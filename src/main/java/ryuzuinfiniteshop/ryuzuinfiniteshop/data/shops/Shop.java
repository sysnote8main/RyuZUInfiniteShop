package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item.ObjectItems;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.trade.ShopGui2to1;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.trade.ShopGui4to4;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.trade.ShopGui6to2;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.trade.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.entity.EntityNBTBuilder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.entity.EquipmentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.TradeUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Shop {
    public enum ShopType {TwotoOne, FourtoFour, SixtoTwo}

    @Getter
    protected Entity npc;
    @Getter
    protected EntityNBTBuilder NBTBuilder;
    @Getter
    @Setter
    protected Location location;
    protected Optional<String> mythicmob = Optional.empty();
    protected EntityType entityType;
    protected ShopType type;
    protected List<ShopTrade> trades = new ArrayList<>();
    @Setter
    @Getter
    protected boolean lock = false;
    @Setter
    @Getter
    protected boolean searchable = false;

    @Setter
    @Getter
    protected boolean invisible = false;

    @Setter
    @Getter
    protected boolean editting = false;
    protected List<ShopEditorGui> editors = new ArrayList<>();
    protected List<ShopTradeGui> pages = new ArrayList<>();
    protected ObjectItems equipments;

    public Shop(Location location, EntityType entityType) {
        boolean exsited = new File(RyuZUInfiniteShop.getPlugin().getDataFolder(), "shops/" + LocationUtil.toStringFromLocation(location) + ".yml").exists();
        initializeShop(location);
        this.entityType = entityType;
        loadYamlProcess(getFile());
        if (!exsited) {
            createEditorNewPage();
            saveYaml();
        }
    }

    public Shop(Location location, String mmid) {
        boolean exsited = new File(RyuZUInfiniteShop.getPlugin().getDataFolder(), "shops/" + LocationUtil.toStringFromLocation(location) + ".yml").exists();
        initializeShop(location);
        this.mythicmob = Optional.of(mmid);
        loadYamlProcess(getFile());
        this.NBTBuilder = new EntityNBTBuilder(npc);
        if (!exsited) {
            createEditorNewPage();
            saveYaml();
        }
    }

    public void loadYamlProcess(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        getLoadYamlProcess().accept(config);
    }

    private Consumer<YamlConfiguration> getLoadYamlProcess() {
        return yaml -> {
            getAsyncLoadYamlProcess().accept(yaml);
            Bukkit.getScheduler().runTask(RyuZUInfiniteShop.getPlugin(), () -> getSyncLoadYamlProcess().accept(yaml));
        };
    }

    protected Consumer<YamlConfiguration> getSyncLoadYamlProcess() {
        return yaml -> {
            if (!mythicmob.isPresent()) this.mythicmob = Optional.ofNullable(yaml.getString("Npc.Options.MythicMob"));
            if (mythicmob.isPresent() && MythicInstanceProvider.getInstance().getMythicMob(mythicmob.get()) != null) {
                if (npc != null) npc.remove();
                npc = MythicInstanceProvider.getInstance().spawnMythicMob(mythicmob.get(), location);
                setNpcMeta();
            } else {
                spawnNPC(entityType);
                this.NBTBuilder = new EntityNBTBuilder(npc);
                if (yaml.contains("Npc.Options.Equipments")) {
                    this.equipments = new ObjectItems(yaml.get("Npc.Options.Equipments"));
                    updateEquipments();
                }
                if (yaml.getString("Npc.Options.DisplayName") != null)
                    npc.setCustomName(yaml.getString("Npc.Options.DisplayName"));
                if (npc instanceof LivingEntity) {
                    this.invisible = yaml.getBoolean("Npc.Options.Invisible", false);
                    NBTBuilder.setInvisible(invisible);
                }
//                    ((LivingEntity) npc).setInvisible(!yaml.getBoolean("Npc.Options.Invisible", true));
            }
            this.location.setYaw(yaml.getInt("Npc.Status.Yaw", 0));
            npc.teleport(LocationUtil.toBlockLocationFromLocation(location));
        };
    }

    private Consumer<YamlConfiguration> getAsyncLoadYamlProcess() {
        return yaml -> {
            this.type = ShopType.valueOf(yaml.getString("Shop.Options.ShopType", "TwotoOne"));
            this.lock = yaml.getBoolean("Npc.Status.Lock", false);
            this.searchable = yaml.getBoolean("Npc.Status.Searchable", true);
            if (yaml.contains("Trades")) {
                this.trades = yaml.getList("Trades").stream().map(tradeconfig -> new ShopTrade((HashMap<String, Object>) tradeconfig)).collect(Collectors.toList());
                updateTradeContents();
            }
        };
    }

    private void initializeShop(Location location) {
        this.location = location;
        this.type = ShopType.TwotoOne;
        equipments = new ObjectItems(IntStream.range(0, 6).mapToObj(i -> new ItemStack(Material.AIR)).collect(Collectors.toList()));
        ShopUtil.addShop(getID(), this);
    }

    public void updateTradeContents() {
        setTradePages();
        setEditors();
    }

    public void createNewPage() {
        createTradeNewPage();
        createEditorNewPage();
    }

    public void changeShopType() {
        if (!type.equals(ShopType.TwotoOne)) trades.clear();
        this.type = getNextShopType();
        updateTradeContents();
    }

    public ShopType getNextShopType() {
        switch (type) {
            case TwotoOne:
                return ShopType.FourtoFour;
            case FourtoFour:
                return ShopType.SixtoTwo;
            case SixtoTwo:
                return ShopType.TwotoOne;
        }
        return ShopType.TwotoOne;
    }

    // 重複している取引があればtrueを返す
    public boolean checkTrades(Inventory inv) {
        ShopHolder holder = ShopUtil.getShopHolder(inv);
        if (holder == null) return false;

        //取引を上書きし、取引として成立しないものと重複しているものは削除する
        boolean duplication = false;
        HashSet<ShopTrade> emptyTrades = new HashSet<>();
        HashSet<ShopTrade> onTrades = new HashSet<>();
        for (int i = 0; i < 9 * 6; i += getShopType().equals(ShopType.TwotoOne) ? 4 : 9) {
            if (getShopType().equals(ShopType.TwotoOne) && i % 9 == 4) i++;
            int limitSlot = 0;
            if (getShopType().equals(ShopType.TwotoOne)) limitSlot = i + 2;
            else if (getShopType().equals(ShopType.FourtoFour)) limitSlot = i + 4;
            else if (getShopType().equals(ShopType.SixtoTwo)) limitSlot = i + 6;
            ShopTrade trade = ((ShopTradeGui) holder.getGui()).getTradeFromSlot(i);
            ShopTrade expectedTrade = TradeUtil.getTrade(inv, i, getShopType());
            boolean available = TradeUtil.isAvailableTrade(inv, i, getShopType());
            String limitString = NBTUtil.getNMSStringTag(inv.getItem(limitSlot), "TradeLimit");
            int limit = limitString == null ? 0 : Integer.parseInt(limitString);
            limit = limit == 0 && expectedTrade != null && expectedTrade.getLimit() > 0 ? expectedTrade.getLimit() : limit;
            if (available && this.trades.contains(expectedTrade) && !expectedTrade.equals(trade)) duplication = true;

            // 編集画面上に重複した取引が存在するかチェックする
            if (expectedTrade == null) continue;
            if (onTrades.contains(expectedTrade)) duplication = true;
            onTrades.add(expectedTrade);

            // 取引を追加、上書き、削除する
            if (trade == null && available) {
                addTrade(inv, i, limit);
                LogUtil.log(LogUtil.LogType.ADDTRADE, inv.getViewers().stream().findFirst().map(HumanEntity::getName).orElse("null"), getID(), expectedTrade, expectedTrade.getLimit());
            } else if (available) {
                if (!trade.equals(expectedTrade))
                    LogUtil.log(LogUtil.LogType.REPLACETRADE, inv.getViewers().stream().findFirst().map(HumanEntity::getName).orElse("null"), getID(), trade, expectedTrade, trade.getLimit(), limit);
                trade.setTrade(inv, i, getShopType());
                trade.setTradeLimits(limit, true);
            } else if (trade != null) {
                emptyTrades.add(trade);
                LogUtil.log(LogUtil.LogType.REMOVETRADE, inv.getViewers().stream().findFirst().map(HumanEntity::getName).orElse("null"), getID(), trade, trade.getLimit());
            }
        }
        this.trades.removeAll(emptyTrades);
        if (duplication) this.trades = trades.stream().distinct().collect(Collectors.toList());

        //ショップを更新する
        updateTradeContents();
        return duplication;
    }

    public String getShopTypeDisplay() {
        switch (type) {
            case TwotoOne:
                return ChatColor.GREEN + "2 -> 1";
            case FourtoFour:
                return ChatColor.GREEN + "4 -> 4";
            case SixtoTwo:
                return ChatColor.GREEN + "6 -> 2";
        }
        return "";
    }

    public int getSubtractSlot() {
        switch (type) {
            case TwotoOne:
                return 2;
            case FourtoFour:
                return 4;
            case SixtoTwo:
                return 6;
        }
        return 0;
    }

    public ShopTrade getTrade(Inventory inv, int slot) {
        if (!((ShopTradeGui) ShopUtil.getShopHolder(inv).getGui()).isConvertSlot(slot)) return null;
        return TradeUtil.getTrade(inv, slot - getSubtractSlot(), type);
    }

    //トレードをアイテム化する
    public ItemStack convertTrade(Inventory inv, int slot) {
        ShopTrade trade = getTrade(inv, slot);
        if (trade == null) return null;

        ItemStack item = ItemUtil.getNamedEnchantedItem(Material.EMERALD, ChatColor.GREEN + "トレード圧縮宝石", ChatColor.YELLOW + "ショップタイプ: " + getShopTypeDisplay());
        item = NBTUtil.setNMSTag(item, "ShopType", type.toString());
        item = NBTUtil.setNMSTag(item, "TradesSize", String.valueOf(1));
        item = NBTUtil.setNMSTag(item, "Give" + 0, ItemUtil.toStringFromItemStackArray(trade.getGiveItems()));
        item = NBTUtil.setNMSTag(item, "Take" + 0, ItemUtil.toStringFromItemStackArray(trade.getTakeItems()));
        return item;
    }

    public ItemStack convertTrades() {
        ItemStack item = ItemUtil.getNamedEnchantedItem(Material.EMERALD, ChatColor.GREEN + "トレード圧縮宝石", ChatColor.YELLOW + "ショップタイプ: " + getShopTypeDisplay());
        item = NBTUtil.setNMSTag(item, "ShopType", type.toString());
        item = NBTUtil.setNMSTag(item, "TradesSize", String.valueOf(trades.size()));
        for (int i = 0; i < trades.size(); i++) {
            item = NBTUtil.setNMSTag(item, "Give" + i, ItemUtil.toStringFromItemStackArray(trades.get(i).getGiveItems()));
            item = NBTUtil.setNMSTag(item, "Take" + i, ItemUtil.toStringFromItemStackArray(trades.get(i).getTakeItems()));
        }
        return item;
    }

    public boolean loadTrades(ItemStack item, Player p) {
        List<ShopTrade> temp = getTrades(item);
        if (temp == null) return false;
        boolean duplication = temp.stream().anyMatch(trade -> trades.contains(trade));
        trades.addAll(temp);
        temp.forEach(trade -> LogUtil.log(LogUtil.LogType.ADDTRADE, p.getName(), getID(), trade, trade.getLimit()));
        if (duplication) trades = trades.stream().distinct().collect(Collectors.toList());
        updateTradeContents();
        return duplication;
    }

    public List<ShopTrade> getTrades(ItemStack item) {
        String tag = NBTUtil.getNMSStringTag(item, "TradesSize");
        if (tag == null) return null;
        Shop.ShopType shoptype = Shop.ShopType.valueOf(NBTUtil.getNMSStringTag(item, "ShopType"));
        if (!(shoptype.equals(Shop.ShopType.TwotoOne) || shoptype.equals(type))) return null;
        List<ShopTrade> temp = new ArrayList<>();
        for (int i = 0; i < Integer.parseInt(tag); i++) {
            temp.add(new ShopTrade(ItemUtil.toItemStackArrayFromString(NBTUtil.getNMSStringTag(item, "Give" + i)), ItemUtil.toItemStackArrayFromString(NBTUtil.getNMSStringTag(item, "Take" + i))));
        }
        return temp;
    }

    public String convertShopToString() {
        File file = getFile();
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return config.saveToString();
    }

    public ItemStack convertShopToItemStack() {
        ItemStack item = ItemUtil.getNamedEnchantedItem(Material.DIAMOND, ChatColor.AQUA + "ショップ圧縮宝石",
                                                        "シフトして地面に使用",
                                                        ChatColor.YELLOW + "ショップタイプ: " + getShopTypeDisplay(),
                                                        ChatColor.YELLOW + "名前: " + JavaUtil.getOrDefault(npc.getCustomName(), "<none>")
        );
        return NBTUtil.setNMSTag(item, "ShopData", convertShopToString());
    }

    public void removeShop() {
        npc.remove();
        getFile().delete();
        ShopUtil.removeShop(getID());
    }

    public List<ShopTrade> getTrades() {
        return trades;
    }

    public void setTrades(List<ShopTrade> trades) {
        this.trades = trades;
        updateTradeContents();
    }

    public String getID() {
        return LocationUtil.toStringFromLocation(location);
    }

    public ShopTradeGui getPage(int page) {
        if (page <= 0) return null;
        if (page > pages.size()) return null;
        return pages.get(page - 1);
    }

    public int getPage(ShopTrade trade) {
        if (!trades.contains(trade)) return -1;
        return (int) Math.ceil((double) (trades.indexOf(trade) + 1) / getLimitSize());
    }

    public void setTradePages() {
        pages.clear();
        for (int i = 1; i <= getTradePageCountFromTradesCount(); i++) {
            switch (type) {
                case TwotoOne:
                    pages.add(new ShopGui2to1(this, i));
                    break;
                case FourtoFour:
                    pages.add(new ShopGui4to4(this, i));
                    break;
                case SixtoTwo:
                    pages.add(new ShopGui6to2(this, i));
                    break;
            }
        }
    }

    public ShopEditorGui getEditor(int page) {
        if (page <= 0) return null;
        if (page > editors.size()) return null;
        return editors.get(page - 1);
    }

    public void setEditors() {
        editors.clear();
        if (pages.isEmpty()) editors.add(new ShopEditorGui(this, 1));
        for (int i = 1; i <= getEditorPageCountFromTradesCount(); i++) {
            editors.add(new ShopEditorGui(this, i));
        }
        HashMap<String, List<Player>> map = new HashMap<>();
        if (ableCreateEditorNewPage())
            editors.add(new ShopEditorGui(this, getEditorPageCountFromTradesCount() + 1));
    }

    public int getLimitSize() {
        return type.equals(ShopType.TwotoOne) ? 12 : 6;
    }

    public boolean isLimitPage(int page) {
        return getPage(page).getTrades().size() == getLimitSize();
    }

    public int getPageCount() {
        return pages.size();
    }

    public int getTradePageCountFromTradesCount() {
        int size = trades.size() / getLimitSize();
        if (trades.size() % getLimitSize() != 0) size++;
        return size;
    }

    public int getEditorPageCountFromTradesCount() {
        int size = getTradePageCountFromTradesCount() / 18;
        //if (getTradePageCountFromTradesCount() % 18 != 0) size++;
        return size + 1;
    }

    public ShopType getShopType() {
        return type;
    }

    public boolean ableCreateNewPage() {
        if (trades.isEmpty()) return true;
        return isLimitPage(pages.size());
    }

    public void createTradeNewPage() {
        if (!ableCreateNewPage()) return;
        switch (type) {
            case TwotoOne:
                pages.add(new ShopGui2to1(this, getPageCount() + 1));
                break;
            case FourtoFour:
                pages.add(new ShopGui4to4(this, getPageCount() + 1));
                break;
            case SixtoTwo:
                pages.add(new ShopGui6to2(this, getPageCount() + 1));
                break;
        }
    }

    public boolean ableCreateEditorNewPage() {
        if (editors.isEmpty()) return true;
        return editors.size() < getEditorPageCountFromTradesCount();
    }

    public void createEditorNewPage() {
        if (!ableCreateEditorNewPage()) return;
        editors.add(new ShopEditorGui(this, getPageCount() + 1));
    }

    public void addTrade(Inventory inv, int slot, int limit) {
        trades.add(new ShopTrade(inv, slot, type, limit));
    }

    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return yaml -> {
            mythicmob.ifPresent(mythicmob -> yaml.set("Npc.Options.MythicMob", mythicmob));
            if (npc != null) {
                yaml.set("Npc.Options.EntityType", npc.getType().toString());
                yaml.set("Npc.Options.DisplayName", npc.getCustomName());
                if (npc instanceof LivingEntity) yaml.set("Npc.Options.Invisible", invisible);
            }
            yaml.set("Shop.Options.ShopType", type.toString());
            yaml.set("Npc.Options.Equipments", equipments.getObjects());
            yaml.set("Npc.Status.Lock", lock);
            yaml.set("Npc.Status.Searchable", searchable);
            yaml.set("Trades", getTrades().stream().map(ShopTrade::serialize).collect(Collectors.toList()));
            yaml.set("Npc.Status.Yaw", location.getYaw());
        };
    }

    public void saveYaml() {
        File file = getFile();
        YamlConfiguration yaml = new YamlConfiguration();
        getSaveYamlProcess().accept(yaml);
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return FileUtil.initializeFile("shops/" + getID() + ".yml");
    }

    public String getDisplayName() {
        return JavaUtil.getOrDefault(npc.getCustomName(), "ショップ");
    }

    public boolean containsDisplayName(String name) {
        if (npc.getCustomName() == null || npc.getCustomName().isEmpty()) return false;
        if (name == null || name.isEmpty()) return false;
        return ChatColor.stripColor(npc.getCustomName().toUpperCase()).contains(name.toUpperCase());
    }

    public String getDisplayNameOrElseNone() {
        return JavaUtil.getOrDefault(npc.getCustomName(), ChatColor.YELLOW + "<none>");
    }

    private void spawnNPC(EntityType entitytype) {
        this.location.setPitch(0);
        this.location.setYaw(0);
        Entity npc = location.getWorld().spawnEntity(LocationUtil.toBlockLocationFromLocation(location), entitytype);
        this.npc = npc;
        setNpcMeta();
    }

    public void setNpcMeta() {
        npc.setSilent(true);
        npc.setInvulnerable(true);
        npc.setGravity(false);
        NBTUtil.setNMSTag(npc, "Shop", getID());
        initializeLivingEntitiy();
        if (npc.getType().equals(EntityType.ENDER_CRYSTAL))
            ((EnderCrystal) npc).setShowingBottom(false);
//        NBTBuilder.setSilent(true);
//        NBTBuilder.setInvulnerable(true);
//        NBTBuilder.setNoGravity(true);
//        NBTUtil.setNMSTag(npc, "Shop", getID());
//        initializeLivingEntitiy();
//        if (npc.getType().equals(EntityType.ENDER_CRYSTAL))
//            NBTBuilder.setSilent(true);
    }

    public void setNpcMeta(ConfigurationSection section) {
        if (this instanceof AgeableShop)
            ((AgeableShop) this).setAgeLook(section.getBoolean("baby", false));
        if (this instanceof PoweredableShop)
            ((PoweredableShop) this).setPowered(section.getBoolean("powered", false));
        if (this instanceof HorseShop) {
            ((HorseShop) this).setColor(Horse.Color.valueOf(section.getString("color")));
            ((HorseShop) this).setStyle(Horse.Style.valueOf(section.getString("style")));
        }
        if (this instanceof ParrotShop)
            ((ParrotShop) this).setColor(Parrot.Variant.valueOf(section.getString("parrotVariant")));
        if (this instanceof DyeableShop) {
            ((DyeableShop) this).setColor(DyeColor.valueOf(section.getString("color", "WHITE")));
            ((DyeableShop) this).setOptionalInfo(
                    (
                            section.contains("angry") ? section.getBoolean("angry") :
                                    (section.contains("sitting") ? section.getBoolean("sitting") :
                                            (section.getBoolean("shaved", false)))
                    )
            );
        }
    }

    public void initializeLivingEntitiy() {
        if (!(npc instanceof LivingEntity)) return;
        LivingEntity livnpc = (LivingEntity) npc;
        livnpc.setAI(false);
        livnpc.setRemoveWhenFarAway(false);
//        NBTBuilder.setNoAI(true);
//        NBTBuilder.setPersistenceRequired(true);
    }

    public void changeInvisible() {
        if (!(npc instanceof LivingEntity)) return;
        LivingEntity livnpc = (LivingEntity) npc;
//        livnpc.setInvisible(!invisible);
        NBTBuilder.setInvisible(invisible);
    }

    public void changeNPCDirecation() {
        if (!(npc instanceof LivingEntity)) return;
        LivingEntity livnpc = (LivingEntity) npc;
        location.setYaw((location.getYaw() + 45));
        livnpc.teleport(LocationUtil.toBlockLocationFromLocation(location));
    }

    public ItemStack getEquipmentItem(int slot) {
        return equipments.toItemStacks()[slot];
    }

    public void setEquipmentItem(ItemStack item, int slot) {
        equipments.setObject(item, slot);
        updateEquipments();
    }

    public ItemStack getEquipmentDisplayItem(EquipmentSlot slot) {
        return JavaUtil.getOrDefault(getEquipmentItem(slot.ordinal()), EquipmentUtil.getEquipmentDisplayItem(slot));
    }

    public void updateEquipments() {
        if (npc instanceof LivingEntity) {
            LivingEntity livnpc = ((LivingEntity) npc);
            for (Integer i : EquipmentUtil.getEquipmentsSlot().keySet()) {
                EquipmentSlot slot = EquipmentUtil.getEquipmentSlot(i);
                livnpc.getEquipment().setItem(slot, equipments.toItemStacks()[slot.ordinal()]);
            }
        }
    }

    public boolean isAvailableShop() {
        return !isLock() && !isEditting();
    }

    public boolean isAvailableShop(Player p) {
        if (isLock() && !p.hasPermission("sis.op")) {
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "現在このショップはロックされています");
            SoundUtil.playFailSound(p);
            return false;
        }
        if (isEditting()) {
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "現在このショップは編集中です");
            SoundUtil.playFailSound(p);
            return false;
        }
        if (pages.isEmpty()) {
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "現在このショップには取引が存在しません");
            SoundUtil.playFailSound(p);
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Shop) {
            Shop shop = (Shop) obj;
            return shop.getID().equals(getID());
        }
        return false;
    }
}
