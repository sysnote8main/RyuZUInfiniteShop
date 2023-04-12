package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.Config;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
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

    @Getter
    protected Entity npc;
    @Getter
    protected EntityNBTBuilder NBTBuilder;
    @Getter
    protected String displayName;
    @Getter
    @Setter
    protected Location location;
    @Getter
    protected String mythicmob;
    protected NPC citizen;
    protected EntityType entityType;
    protected ShopType type;
    protected List<ShopTrade> trades = new ArrayList<>();
    protected ConfigurationSection shopkeepersConfig;
    protected int yaw;

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

    public Shop(Location location, EntityType entityType, ConfigurationSection config) {
        boolean exsited = new File(RyuZUInfiniteShop.getPlugin().getDataFolder(), "shops/" + LocationUtil.toStringFromLocation(location) + ".yml").exists();
        this.location = location;
        this.entityType = entityType;
        this.shopkeepersConfig = config;
        ShopUtil.addShop(getID(), this);
        loadYamlProcess(getFile());
        if (!exsited) {
            createEditorNewPage();
            if (config == null) saveYaml();
        } else
            loadYamlProcess(getFile());
    }

    public Shop(Location location, EntityType entityType, String name) {
        boolean exsited = new File(RyuZUInfiniteShop.getPlugin().getDataFolder(), "shops/" + LocationUtil.toStringFromLocation(location) + ".yml").exists();
        this.citizen = CitizensAPI.getNPCRegistry().createNPC(entityType, name);
        this.location = location;
        this.entityType = entityType;
        ShopUtil.addShop(getID(), this);
        loadYamlProcess(getFile());
        if (!exsited) {
            createEditorNewPage();
            saveYaml();
        }
    }

    public Shop(Location location, String mmid) {
        boolean exsited = new File(RyuZUInfiniteShop.getPlugin().getDataFolder(), "shops/" + LocationUtil.toStringFromLocation(location) + ".yml").exists();
        this.location = location;
        this.mythicmob = mmid;
        ShopUtil.addShop(getID(), this);
        loadYamlProcess(getFile());
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

    protected Consumer<YamlConfiguration> getLoadYamlProcess() {
        return yaml -> {
            this.mythicmob = yaml.getString("Npc.Options.MythicMob");
            if(MythicInstanceProvider.isLoaded() && MythicInstanceProvider.getInstance().getMythicMob(mythicmob) == null)
                new RuntimeException(LanguageKey.ERROR_MYTHICMOBS_INVALID_ID.getMessage(mythicmob)).printStackTrace();
            this.displayName = yaml.getString("Npc.Options.DisplayName");
            this.invisible = yaml.getBoolean("Npc.Options.Invisible", false);
            this.yaw = yaml.getInt("Npc.Status.Yaw", 0);
            this.location.setYaw(yaw);
            this.type = ShopType.valueOf(yaml.getString("Shop.Options.ShopType", "TwotoOne"));
            this.lock = yaml.getBoolean("Npc.Status.Lock", false);
            this.searchable = yaml.getBoolean("Npc.Status.Searchable", true);
            this.equipments = new ObjectItems(yaml.get("Npc.Options.Equipments", IntStream.range(0, 6).mapToObj(i -> new ItemStack(Material.AIR)).collect(Collectors.toList())));
            this.trades = yaml.getList("Trades", new ArrayList<>()).stream().map(tradeconfig -> new ShopTrade((HashMap<String, Object>) tradeconfig)).collect(Collectors.toList());
            updateTradeContents();

            if (shopkeepersConfig == null) return;
            setNpcMeta(shopkeepersConfig.getConfigurationSection("object"));
            setDisplayName(shopkeepersConfig.getString("name", "").isEmpty() ? "" : ChatColor.GREEN + shopkeepersConfig.getString("name"));
        };
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
        ShopTradeGui gui = getPage(holder.getGui().getPage());
        if (gui == null) return false;

        //取引を上書きし、取引として成立しないものと重複しているものは削除する
        boolean duplication = false;
        HashSet<ShopTrade> emptyTrades = new HashSet<>();
        List<ShopTrade> onTrades = new ArrayList<>(getTrades());
        gui.getTrades().forEach(onTrades::remove);
        for (int i = 0; i < 9 * 6; i += getShopType().equals(ShopType.TwotoOne) ? 4 : 9) {
            if (getShopType().equals(ShopType.TwotoOne) && i % 9 == 4) i++;
            int limitSlot = 0;
            if (getShopType().equals(ShopType.TwotoOne)) limitSlot = i + 2;
            else if (getShopType().equals(ShopType.FourtoFour)) limitSlot = i + 4;
            else if (getShopType().equals(ShopType.SixtoTwo)) limitSlot = i + 6;
            ShopTrade trade = gui.getTradeFromSlot(i);
            ShopTrade expectedTrade = TradeUtil.getTrade(inv, i, getShopType());
            boolean available = TradeUtil.isAvailableTrade(inv, i, getShopType());
            String limitString = NBTUtil.getNMSStringTag(inv.getItem(limitSlot), "TradeLimit");
            int limit = limitString == null ? 0 : Integer.parseInt(limitString);
            limit = limit == 0 && expectedTrade != null && expectedTrade.getLimit() > 0 ? expectedTrade.getLimit() : limit;

            // 編集画面上に重複した取引が存在するかチェックする
            if (available && onTrades.contains(expectedTrade)) duplication = true;
            onTrades.add(expectedTrade);

            // 取引を追加、上書き、削除する
            if (trade == null && available) {
                // 取引を追加
                addTrade(inv, i, limit);
                LogUtil.log(LogUtil.LogType.ADDTRADE, inv.getViewers().stream().findFirst().map(HumanEntity::getName).orElse("null"), getID(), expectedTrade, expectedTrade.getLimit());
            } else if (available) {
                // 取引を上書き
                if (!trade.equals(expectedTrade))
                    LogUtil.log(LogUtil.LogType.REPLACETRADE, inv.getViewers().stream().findFirst().map(HumanEntity::getName).orElse("null"), getID(), trade, expectedTrade, trade.getLimit(), limit);
                trade.setTrade(inv, i, getShopType());
                trade.setTradeLimits(limit, true);
            } else if (trade != null) {
                // 取引を削除する
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

    public ShopTrade getTrade(Inventory inv, int slot) {
        if (!((ShopTradeGui) ShopUtil.getShopHolder(inv).getGui()).isConvertSlot(slot)) return null;
        return TradeUtil.getTrade(inv, slot - ShopUtil.getSubtractSlot(type), type);
    }

    //トレードをアイテム化する
    public ItemStack convertTrade(Inventory inv, int slot) {
        ShopTrade trade = getTrade(inv, slot);
        if (trade == null) return null;

        ItemStack item = ItemUtil.getNamedEnchantedItem(Material.EMERALD, ChatColor.GREEN + LanguageKey.ITEM_TRADE_COMPRESSION_GEM.getMessage(), ChatColor.YELLOW + LanguageKey.ITEM_SHOP_COMPRESSION_GEM_TYPE.getMessage() + getShopTypeDisplay());
        item = NBTUtil.setNMSTag(item, "ShopType", type.toString());
        item = NBTUtil.setNMSTag(item, "TradesSize", String.valueOf(1));
        item = NBTUtil.setNMSTag(item, "Give" + 0, ItemUtil.toStringFromItemStackArray(trade.getGiveItems()));
        item = NBTUtil.setNMSTag(item, "Take" + 0, ItemUtil.toStringFromItemStackArray(trade.getTakeItems()));
        return item;
    }


    public HashMap<String, String> convertTradesToMap() {
        HashMap<String, String> trades = new HashMap<>();
        trades.put("ShopType", type.toString());
        trades.put("TradesSize", String.valueOf(this.trades.size()));
        for (int i = 0; i < this.trades.size(); i++) {
            trades.put("Give" + i, ItemUtil.toStringFromItemStackArray(this.trades.get(i).getGiveItems()));
            trades.put("Take" + i, ItemUtil.toStringFromItemStackArray(this.trades.get(i).getTakeItems()));
        }
        return trades;
    }

    public ItemStack convertTradesToItemStack() {
        ItemStack item = ItemUtil.getNamedEnchantedItem(Material.EMERALD, ChatColor.GREEN + LanguageKey.ITEM_TRADE_COMPRESSION_GEM.getMessage(), ChatColor.YELLOW + LanguageKey.ITEM_SHOP_COMPRESSION_GEM_TYPE.getMessage() + getShopTypeDisplay());
        item = NBTUtil.setNMSTag(item, convertTradesToMap());
        return item;
    }

    public boolean loadTrades(ItemStack item, Player p) {
        List<ShopTrade> temp = TradeUtil.convertTradesToList(item);
        if (temp == null) return false;
        boolean duplication = temp.stream().anyMatch(trade -> trades.contains(trade));
        trades.addAll(temp);
        temp.forEach(trade -> LogUtil.log(LogUtil.LogType.ADDTRADE, p.getName(), getID(), trade, trade.getLimit()));
        if (duplication) trades = trades.stream().distinct().collect(Collectors.toList());
        updateTradeContents();
        return duplication;
    }

    public HashMap<String, String> convertShopToMap() {
        HashMap<String, String> shop = new HashMap<>();
        shop.put("ShopData", convertShopToString());
        shop.putAll(convertTradesToMap());
        return shop;
    }

    public String convertShopToString() {
        YamlConfiguration yaml = saveYaml();
        yaml.set("Trades", null);
        return saveYaml().saveToString();
    }

    public ItemStack convertShopToItemStack() {
        ItemStack item = ItemUtil.getNamedEnchantedItem(Material.DIAMOND, ChatColor.AQUA + LanguageKey.ITEM_SHOP_COMPRESSION_GEM.getMessage() + ChatColor.GREEN + getDisplayNameOrElseNone(),
                ChatColor.YELLOW + LanguageKey.ITEM_SHOP_COMPRESSION_GEM_CLICK.getMessage() + ChatColor.GREEN + LanguageKey.ITEM_SHOP_COMPRESSION_GEM_MEARGE.getMessage(),
                ChatColor.YELLOW + LanguageKey.ITEM_SHOP_COMPRESSION_GEM_PLACELORE.getMessage() + ChatColor.GREEN + LanguageKey.ITEM_SHOP_COMPRESSION_GEM_PLACE.getMessage(),
                ChatColor.YELLOW + LanguageKey.ITEM_SHOP_COMPRESSION_GEM_TYPE.getMessage() + getShopTypeDisplay()
        );
        item = NBTUtil.setNMSTag(item, convertShopToMap());
        return item;
    }

    public void removeShop() {
        if (npc != null) npc.remove();
        getFile().delete();
        ShopUtil.removeShop(getID());
    }

    public void removeShop(Player p) {
        LogUtil.log(LogUtil.LogType.REMOVESHOP, p.getName(), getID());
        removeShop();
    }

    public List<ShopTrade> getTrades() {
        return trades;
    }

    public List<ShopTrade> getTrades(int page) {
        List<ShopTrade>[] trades = JavaUtil.splitList(getTrades(), getLimitSize());
        if (trades.length == page - 1) return new ArrayList<>();
        return trades[page - 1];
    }

    public void setTrades(List<ShopTrade> trades) {
        this.trades = trades;
        updateTradeContents();
    }

    public void addAllTrades(List<ShopTrade> trades) {
        this.trades.addAll(trades);
        this.trades = this.trades.stream().distinct().collect(Collectors.toList());
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
            yaml.set("Npc.Options.MythicMob", mythicmob);
            yaml.set("Npc.Options.DisplayName", displayName);
            yaml.set("Npc.Options.EntityType", entityType.toString());
            yaml.set("Npc.Options.Invisible", invisible);
            yaml.set("Shop.Options.ShopType", type.toString());
            yaml.set("Npc.Options.Equipments", equipments.getObjects());
            yaml.set("Npc.Status.Lock", lock);
            yaml.set("Npc.Status.Searchable", searchable);
            yaml.set("Trades", getTrades().stream().map(ShopTrade::serialize).collect(Collectors.toList()));
            yaml.set("Npc.Status.Yaw", location.getYaw());
        };
    }

    public YamlConfiguration saveYaml() {
        File file = getFile();
        YamlConfiguration yaml = new YamlConfiguration();
        getSaveYamlProcess().accept(yaml);
        try {
            yaml.save(file);
        } catch (IOException e) {
            if(!Config.readOnlyIgnoreIOException) throw new RuntimeException(LanguageKey.ERROR_FILE_SAVING.getMessage(file.getName()), e);
        }
        return yaml;
    }

    public File getFile() {
        return FileUtil.initializeFile("shops/" + getID() + ".yml");
    }

    public String getDisplayNameOrElseShop() {
        return JavaUtil.getOrDefault(displayName, LanguageKey.INVENTORY_DEFAULT_SHOP.getMessage());
    }

    public void setDisplayName(String name) {
        this.displayName = name;
        if (npc != null) npc.setCustomName(name);
    }

    public boolean containsDisplayName(String name) {
        return JavaUtil.containsIgnoreCase(displayName, name);
    }

    public String getDisplayNameOrElseNone() {
        return JavaUtil.getOrDefault(displayName, ChatColor.YELLOW + "<none>");
    }

    private void spawnNPC(EntityType entitytype) {
        this.location.setPitch(0);
        this.npc = location.getWorld().spawnEntity(LocationUtil.toBlockLocationFromLocation(location), entitytype);
        setNpcMeta();
    }

    public void setNpcMeta() {
        npc.setSilent(true);
        npc.setInvulnerable(true);
        npc.setGravity(false);
        npc.setPersistent(false);
        NBTUtil.setNMSTag(npc, "Shop", getID());
        initializeLivingEntitiy();
        if (entityType.equals(EntityType.ENDER_CRYSTAL))
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
            ((AgeableShop) this).setAgeLook(!section.getBoolean("baby", false));
        if (this instanceof PoweredableShop)
            ((PoweredableShop) this).setPowered(section.getBoolean("powered", false));
        if (this instanceof HorseShop) {
            ((HorseShop) this).setColor(Horse.Color.valueOf(section.getString("color")));
            ((HorseShop) this).setStyle(Horse.Style.valueOf(section.getString("style")));
        }
        if (this instanceof VillagerableShop) {
            ((VillagerableShop) this).setProfession(Villager.Profession.valueOf(section.getString("profession")));
            ((VillagerableShop) this).setBiome(Villager.Type.valueOf(section.getString("villagerType")));
            ((VillagerableShop) this).setLevel(section.getInt("villagerLevel"));
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
        if (!Config.followPlayer && citizen != null)
            citizen.addTrait(LookClose.class);
        livnpc.setRemoveWhenFarAway(true);
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
        if (citizen == null) {
            if (npc instanceof LivingEntity) {
                LivingEntity livnpc = ((LivingEntity) npc);
                for (EquipmentSlot slot : EquipmentUtil.getEquipmentsSlot().values()) {
                    livnpc.getEquipment().setItem(slot, equipments.toItemStacks()[slot.ordinal()]);
                }
            }
        } else {
            for (EquipmentSlot slot : EquipmentUtil.getEquipmentsSlot().values()) {
                citizen.getOrAddTrait(Equipment.class).set(slot.ordinal(), equipments.toItemStacks()[slot.ordinal()]);
            }
        }
    }

    public boolean isAvailableShop() {
        return !isLock() && !isEditting();
    }

    public boolean isAvailableShop(Player p) {
        if (isLock() && !p.hasPermission("sis.op")) {
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_SHOP_LOCKED.getMessage());
            SoundUtil.playFailSound(p);
            return false;
        }
        if (isEditting()) {
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_SHOP_EDITING.getMessage());
            SoundUtil.playFailSound(p);
            return false;
        }
        if (pages.isEmpty()) {
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_SHOP_NO_TRADES.getMessage());
            SoundUtil.playFailSound(p);
            return false;
        }
        return true;
    }

    public void setNpcType(EntityType entityType) {
        if (npc != null) npc.remove();
        this.entityType = entityType;
        this.mythicmob = null;
        if (npc == null) respawnNPC();
    }

    public void setMythicType(String mythicType) {
        if (npc != null) npc.remove();
        this.mythicmob = mythicType;
        respawnNPC();
    }

    public void respawnNPC() {
        if (FileUtil.isSaveBlock()) return;
        if (npc != null && npc.isValid()) return;
        if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) return;
        if (entityType == null) return;
        if (mythicmob != null && MythicInstanceProvider.getInstance().getMythicMob(mythicmob) != null) {
            if (npc != null) npc.remove();
            npc = MythicInstanceProvider.getInstance().spawnMythicMob(mythicmob, location);
            setNpcMeta();
        } else if (citizen != null) {
            if (npc != null) citizen.despawn();
            citizen.spawn(location);
            citizen.setName(displayName);
            npc = citizen.getEntity();
            setNpcMeta();
        } else {
            spawnNPC(entityType);
            npc.setCustomName(displayName);
            npc.getPassengers().forEach(Entity::remove);
            Optional.ofNullable(npc.getVehicle()).ifPresent(Entity::remove);
            if (npc instanceof LivingEntity)
                updateEquipments();
        }
        this.NBTBuilder = new EntityNBTBuilder(npc);
        Block block = location.clone().subtract(0, -1, 0).getBlock();
        if (block.getBlockData() instanceof Slab && ((Slab) block.getBlockData()).getType().equals(Slab.Type.BOTTOM))
            npc.teleport(location.clone().add(0, -0.5, 0));
        if (npc instanceof LivingEntity)
            NBTBuilder.setInvisible(invisible);
        this.location.setYaw(yaw);
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
