package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade.ShopGui2to1;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade.ShopGui4to4;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade.ShopGui6to2;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Shop {
    public enum ShopType {TwotoOne, FourtoFour, SixtoTwo}

    protected Entity npc;
    protected Location location;
    protected ShopType type;
    protected List<ShopTrade> trades = new ArrayList<>();
    protected boolean lock = false;
    protected boolean editting = false;
    protected List<ShopEditorGui> editors = new ArrayList<>();
    protected List<ShopTradeGui> pages = new ArrayList<>();
    protected ItemStack[] equipments = new ItemStack[6];

    public Shop(Location location, EntityType entitytype) {
        boolean exsited = new File(RyuZUInfiniteShop.getPlugin().getDataFolder(), "shops/" + LocationUtil.toStringFromLocation(location) + ".yml").exists();
        initializeShop(location, entitytype);
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
        this.location = LocationUtil.toLocationFromString(file.getName().replace(".yml", ""));
        getLoadYamlProcess().accept(config);
    }

    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return yaml -> {
            this.type = ShopType.valueOf(yaml.getString("Shop.Options.ShopType", "TwotoOne"));
            this.lock = yaml.getBoolean("Shop.Status.Lock", false);
            if (yaml.contains("Trades")) {
                this.trades = yaml.getList("Trades").stream().map(tradeconfig -> new ShopTrade((HashMap<String, List<Object>>) tradeconfig)).collect(Collectors.toList());
                updateTradeContents();
            }
            if (yaml.contains("Npc.Options.Equipments")) {
                this.equipments = ((ArrayList<ItemStack>) yaml.getList("Npc.Options.Equipments")).toArray(new ItemStack[0]);
                updateEquipments();
            }
            if (yaml.getString("Npc.DisplayName") != null) npc.setCustomName(yaml.getString("Npc.Options.DisplayName"));
            this.location.setYaw(yaml.getInt("Npc.Status.Yaw", 0));
            npc.teleport(LocationUtil.toBlockLocationFromLocation(location));
            if (npc instanceof LivingEntity)
                ((LivingEntity) npc).setInvisible(!yaml.getBoolean("Npc.Options.Visible", true));
        };
    }

    public void initializeShop(Location location, EntityType entitytype) {
        this.location = location;
        this.type = ShopType.TwotoOne;
        spawnNPC(entitytype);
        Arrays.fill(equipments, new ItemStack(Material.AIR));
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

    public void checkTrades(Inventory inv) {
        ShopHolder holder = ShopUtil.getShopHolder(inv);
        if (holder == null) return;

        //取引を上書きし、取引として成立しないものは削除する
        List<ShopTrade> emptytrades = new ArrayList<>();
        for (int i = 0; i < 9 * 6; i += getShopType().equals(ShopType.TwotoOne) ? 4 : 9) {
            if (getShopType().equals(ShopType.TwotoOne) && i % 9 == 4) i++;
            ShopTrade trade = ((ShopTradeGui) holder.getGui()).getTradeFromSlot(i);
            boolean available = TradeUtil.isAvailableTrade(inv, i, getShopType());
            if (trade == null && available)
                addTrade(inv, i);
            else if (available)
                trade.setTrade(inv, i, getShopType());
            else
                emptytrades.add(trade);

        }
        this.trades.removeAll(emptytrades);

        //ショップを更新する
        updateTradeContents();
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

    //トレードをアイテム化する
    public ItemStack convertTrade(Inventory inv, int slot) {
        if (!((ShopTradeGui) ShopUtil.getShopHolder(inv).getGui()).isConvertSlot(slot)) return null;
        ShopTrade trade = TradeUtil.getTrade(inv, slot - getSubtractSlot(), type);
        if (trade == null) return null;

        ItemStack item = ItemUtil.getNamedEnchantedItem(Material.EMERALD, ChatColor.GREEN + "トレード圧縮宝石", ChatColor.YELLOW + "ショップタイプ: " + getShopTypeDisplay());
        item = PersistentUtil.setNMSTag(item, "ShopType", type.toString());
        item = PersistentUtil.setNMSTag(item, "TradesSize", String.valueOf(1));
        item = PersistentUtil.setNMSTag(item, "Give" + 0, ItemUtil.toStringFromItemStackArray(trade.getGiveItems()));
        item = PersistentUtil.setNMSTag(item, "Take" + 0, ItemUtil.toStringFromItemStackArray(trade.getTakeItems()));
        return item;
    }

    public ItemStack convertTrades() {
        ItemStack item = ItemUtil.getNamedEnchantedItem(Material.EMERALD, ChatColor.GREEN + "トレード圧縮宝石", ChatColor.YELLOW + "ショップタイプ: " + getShopTypeDisplay());
        item = PersistentUtil.setNMSTag(item, "ShopType", type.toString());
        item = PersistentUtil.setNMSTag(item, "TradesSize", String.valueOf(trades.size()));
        for (int i = 0; i < trades.size(); i++) {
            item = PersistentUtil.setNMSTag(item, "Give" + i, ItemUtil.toStringFromItemStackArray(trades.get(i).getGiveItems()));
            item = PersistentUtil.setNMSTag(item, "Take" + i, ItemUtil.toStringFromItemStackArray(trades.get(i).getTakeItems()));
        }
        return item;
    }

    public void loadTrades(ItemStack item) {
        String tag = PersistentUtil.getNMSStringTag(item, "TradesSize");
        if (tag == null) return;
        ShopType shoptype = ShopType.valueOf(PersistentUtil.getNMSStringTag(item, "ShopType"));
        if (!(shoptype.equals(ShopType.TwotoOne) || shoptype.equals(type))) return;
        for (int i = 0; i < Integer.parseInt(tag); i++) {
            trades.add(new ShopTrade(ItemUtil.toItemStackArrayFromString(PersistentUtil.getNMSStringTag(item, "Give" + i)), ItemUtil.toItemStackArrayFromString(PersistentUtil.getNMSStringTag(item, "Take" + i))));
        }
        updateTradeContents();
    }

    public String convertShopToString() {
        saveYaml();
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
                ChatColor.YELLOW + "名前: " + JavaUtil.getOrDefault(npc.getCustomName(), "<none>"));
        return PersistentUtil.setNMSTag(item, "Shop", convertShopToString());
    }

    public void removeShop() {
        npc.remove();
        getFile().delete();
        ShopUtil.removeShop(LocationUtil.toStringFromLocation(location));
    }

    public List<ShopTrade> getTrades() {
        return trades;
    }

    public String getID() {
        return LocationUtil.toStringFromLocation(location);
    }

    public ShopTradeGui getPage(int page) {
        if (page <= 0) return null;
        if (page > pages.size()) return null;
        return pages.get(page - 1);
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
        if (ableCreateEditorNewPage())
            editors.add(new ShopEditorGui(this, getEditorPageCountFromTradesCount() + 1));
    }

    public int getLimitSize() {
        return type.equals(ShopType.TwotoOne) ? 12 : 6;
    }

    public boolean isLimitPage(int page) {
        return getPage(page).getTrades().size() == getLimitSize();
    }

    public int getTradePageCount() {
        return pages.size();
    }

    public int getEditorPageCount() {
        return editors.size();
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
                pages.add(new ShopGui2to1(this, getTradePageCount() + 1));
                break;
            case FourtoFour:
                pages.add(new ShopGui4to4(this, getTradePageCount() + 1));
                break;
            case SixtoTwo:
                pages.add(new ShopGui6to2(this, getTradePageCount() + 1));
                break;
        }
    }

    public boolean ableCreateEditorNewPage() {
        if (editors.isEmpty()) return true;
        return editors.size() < getEditorPageCountFromTradesCount();
    }

    public void createEditorNewPage() {
        if (!ableCreateEditorNewPage()) return;
        editors.add(new ShopEditorGui(this, getEditorPageCount() + 1));
    }

    public void addTrade(Inventory inv, int slot) {
        trades.add(new ShopTrade(inv, slot, type));
    }

    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return yaml -> {
            yaml.set("Npc.Options.EntityType", npc.getType().toString());
            yaml.set("Npc.Options.DisplayName", npc.getCustomName());
            yaml.set("Shop.Options.ShopType", type.toString());
            yaml.set("Npc.Options.Equipments", Arrays.stream(equipments).map(equipment -> JavaUtil.getOrDefault(equipment, new ItemStack(Material.AIR))).collect(Collectors.toList()));
            yaml.set("Npc.Status.Lock", lock);
            yaml.set("Trades", getTradesConfig());
            if (npc instanceof LivingEntity) yaml.set("Npc.Options.Visible", !((LivingEntity) npc).isInvisible());
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
        return FileUtil.initializeFile("shops/" + LocationUtil.toStringFromLocation(location) + ".yml");
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isLock() {
        return lock;
    }

    public void setEditting(boolean editting) {
        this.editting = editting;
    }

    public boolean isEditting() {
        return editting;
    }

    public Entity getNPC() {
        return npc;
    }

    public String getDisplayName() {
        return JavaUtil.getOrDefault(npc.getCustomName(), "ショップ");
    }

    public void spawnNPC(EntityType entitytype) {
        if (npc != null) npc.remove();
        this.location.setPitch(0);
        this.location.setYaw(0);
        Entity npc = location.getWorld().spawnEntity(LocationUtil.toBlockLocationFromLocation(location), entitytype);
        this.npc = npc;
        npc.setSilent(true);
        npc.setInvulnerable(true);
        PersistentUtil.setNMSTag(npc, "Shop", getID());
        initializeLivingEntitiy();
    }

    public void changeInvisible() {
        if (!(npc instanceof LivingEntity)) return;
        LivingEntity livnpc = (LivingEntity) npc;
        livnpc.setInvisible(!livnpc.isInvisible());
    }

    public void changeNPCDirecation() {
        if (!(npc instanceof LivingEntity)) return;
        LivingEntity livnpc = (LivingEntity) npc;
        location.setYaw((location.getYaw() + 45));
        livnpc.teleport(LocationUtil.toBlockLocationFromLocation(location));
    }

    public void initializeLivingEntitiy() {
        if (!(npc instanceof LivingEntity)) return;
        LivingEntity livnpc = (LivingEntity) npc;
        livnpc.setAI(false);
        livnpc.setRemoveWhenFarAway(false);
    }

    public List<ConfigurationSection> getTradesConfig() {
        return getTrades().stream().map(ShopTrade::getConfig).collect(Collectors.toList());
    }

    public ItemStack getEquipmentItem(int slot) {
        return equipments[slot];
    }

    public void setEquipmentItem(ItemStack item, int slot) {
        this.equipments[slot] = item;
        updateEquipments();
    }

    public ItemStack getEquipmentDisplayItem(EquipmentSlot slot) {
        return JavaUtil.getOrDefault(getEquipmentItem(EquipmentUtil.getEquipmentSlotNumber(slot)), EquipmentUtil.getEquipmentDisplayItem(slot));
    }

    public Location getLocation() {
        return location;
    }

    public void updateEquipments() {
        if (npc instanceof LivingEntity) {
            LivingEntity livnpc = ((LivingEntity) npc);
            for (Integer i : EquipmentUtil.getEquipmentsSlot().keySet()) {
                EquipmentSlot slot = EquipmentUtil.getEquipmentSlot(i);
                int number = EquipmentUtil.getEquipmentSlotNumber(slot);
                livnpc.getEquipment().setItem(slot, equipments[number]);
            }
        }
    }

    public boolean isAvailableShop() {
        return !isLock() && !isEditting();
    }

    public boolean isAvailableShop(Player p) {
        if (isLock() && !p.hasPermission("ris.op")) {
            p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.RED + "現在このショップはロックされています");
            SoundUtil.playFailSound(p);
            return false;
        }
        if (isEditting()) {
            p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.RED + "現在このショップは編集中です");
            SoundUtil.playFailSound(p);
            return false;
        }
        if (pages.isEmpty()) {
            p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.RED + "現在このショップには取引が存在しません");
            SoundUtil.playFailSound(p);
            return false;
        }
        return true;
    }
}
