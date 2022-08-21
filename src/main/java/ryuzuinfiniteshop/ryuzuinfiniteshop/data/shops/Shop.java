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
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Shop {
    public enum ShopType {TwotoOne, FourtoFour}

    public enum ShopNBT {Ageable, Poweredable, Villagerable}

    protected Entity npc;
    protected Location location;
    protected ShopType type;
    protected List<ShopTrade> trades = new ArrayList<>();
    protected boolean lock = false;
    protected boolean editting = false;
    protected List<ShopEditorMainPage> editors = new ArrayList<>();
    protected List<ShopTradeGui> pages = new ArrayList<>();
    protected ItemStack[] equipments = new ItemStack[6];

    public Shop(Location location, EntityType entitytype) {
        boolean exsited = new File(RyuZUInfiniteShop.getPlugin().getDataFolder(), "shops/" + LocationUtil.toStringFromLocation(location) + ".yml").exists();
        initializeShop(location, entitytype);
        if (exsited) loadYamlProcess(getFile());
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
            initializeShop(location, EntityType.valueOf(yaml.getString("EntityType")));
            this.type = ShopType.valueOf(yaml.getString("ShopType"));
            this.lock = yaml.getBoolean("Lock");
            this.trades = yaml.getList("Trades").stream().map(tradeconfig -> new ShopTrade((HashMap<String, ArrayList<ItemStack>>) tradeconfig)).collect(Collectors.toList());
            updateTradeContents();
            this.equipments = ((ArrayList<ItemStack>) yaml.getList("Equipments")).toArray(new ItemStack[0]);
            updateEquipments();
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
        if (type.equals(ShopType.FourtoFour)) trades.clear();
        this.type = type.equals(ShopType.TwotoOne) ? ShopType.FourtoFour : ShopType.TwotoOne;
        updateTradeContents();
    }

    public void checkTrades(Inventory inv) {
        ShopHolder holder = ShopUtil.getShopHolder(inv);
        if (holder == null) return;

        //取引を上書きし、取引として成立しないものは削除する
        List<ShopTrade> emptytrades = new ArrayList<>();
        for (int i = 0; i < 9 * 6; i += getShopType().equals(Shop.ShopType.TwotoOne) ? 4 : 9) {
            if (getShopType().equals(Shop.ShopType.TwotoOne) && i % 9 == 4) i++;
            ShopTrade trade = ((ShopTradeGui) holder.getGui()).getTradeFromSlot(i);
            boolean available = ShopUtil.isAvailableTrade(inv, i, getShopType());
            if (trade == null && available)
                addTrade(inv, i);
            else if (available)
                trade.setTrade(inv, i, getShopType());
            else
                emptytrades.add(trade);
        }
        removeTrades(emptytrades);

        //ショップを更新する
        updateTradeContents();
    }

    public ItemStack convertTrades() {
        ItemStack item = ItemUtil.getNamedEnchantedItem(Material.EMERALD, ChatColor.GREEN + "トレード圧縮宝石");
        item = PersistentUtil.setNMSTag(item, "TradesSize", String.valueOf(trades.size()));
        for (int i = 0; i < trades.size(); i++) {
            item = PersistentUtil.setNMSTag(item, "Give" + i, ItemUtil.toStringFromItemStackArray(trades.get(i).give));
            item = PersistentUtil.setNMSTag(item, "Take" + i, ItemUtil.toStringFromItemStackArray(trades.get(i).take));
        }
        return item;
    }

    public void loadTrades(ItemStack item) {
        String tag = PersistentUtil.getNMSStringTag(item, "TradesSize");
        if (tag == null) return;
        for (int i = 0; i < Integer.parseInt(tag); i++) {
            trades.add(new ShopTrade(ItemUtil.toItemStackArrayFromString(PersistentUtil.getNMSStringTag(item, "Give" + i)), ItemUtil.toItemStackArrayFromString(PersistentUtil.getNMSStringTag(item, "Take" + i))));
        }
        updateTradeContents();
    }

    public ItemStack convertShop() {
        File file = getFile();
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        ItemStack item = ItemUtil.getNamedEnchantedItem(Material.DIAMOND, ChatColor.AQUA + "ショップ圧縮宝石" , "シフトして地面に使用");
        return PersistentUtil.setNMSTag(item, "Shop", config.saveToString());
    }

    public void removeShop() {
        npc.remove();
        getFile().delete();
        ShopUtil.removeShop(LocationUtil.toStringFromLocation(location));
    }

    public void removeTrade(int number) {
        this.trades.remove(number);
    }

    public void removeTrade(ShopTrade trade) {
        this.trades.remove(trade);
    }

    public void removeTrades(Collection<ShopTrade> trades) {
        this.trades.removeAll(trades);
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
            if (type.equals(ShopType.TwotoOne))
                pages.add(new ShopGui2to1(this, i));
            else
                pages.add(new ShopGui4to4(this, i));
        }
    }

    public ShopEditorMainPage getEditor(int page) {
        if (page <= 0) return null;
        if (page > editors.size()) return null;
        return editors.get(page - 1);
    }

    public void setEditors() {
        editors.clear();
        if (pages.isEmpty()) editors.add(new ShopEditorMainPage(this, 1));
        for (int i = 1; i <= getEditorPageCountFromTradesCount(); i++) {
            editors.add(new ShopEditorMainPage(this, i));
        }
        if (ableCreateEditorNewPage())
            editors.add(new ShopEditorMainPage(this, getEditorPageCountFromTradesCount() + 1));
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

    public boolean isNewPage(Inventory inv) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(inv);
        int page = holder.getGui().getPage();
        if (pages.size() < page) return false;
        if (pages.size() + 1 != page) return false;
        return isLimitPage(pages.size());
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
        if (type.equals(ShopType.TwotoOne))
            pages.add(new ShopGui2to1(this, getTradePageCount() + 1));
        else
            pages.add(new ShopGui4to4(this, getTradePageCount() + 1));
    }

    public boolean ableCreateEditorNewPage() {
        if (editors.isEmpty()) return true;
        return editors.size() < getEditorPageCountFromTradesCount();
    }

    public void createEditorNewPage() {
        if (!ableCreateEditorNewPage()) return;
        editors.add(new ShopEditorMainPage(this, getEditorPageCount() + 1));
    }

    public void addTrade(Inventory inv, int slot) {
        trades.add(new ShopTrade(inv, slot, type));
    }

    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return yaml -> {
            yaml.set("EntityType", npc.getType().toString());
            yaml.set("ShopType", type.toString());
            yaml.set("Equipments", Arrays.stream(equipments).map(equipment -> JavaUtil.getOrDefault(equipment, new ItemStack(Material.AIR))).collect(Collectors.toList()));
            yaml.set("Lock", lock);
            yaml.set("Trades", getTradesConfig());
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

    public void removeNPC() {
        this.npc.remove();
    }

    public Entity getNPC() {
        return this.npc;
    }

    public void spawnNPC(EntityType entitytype) {
        if (npc != null) npc.remove();
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
        Location loc = livnpc.getLocation();
        loc.setYaw((livnpc.getLocation().getYaw() + 45));
        livnpc.teleport(loc);
    }

    public void initializeLivingEntitiy() {
        if (!(npc instanceof LivingEntity)) return;
        LivingEntity livnpc = (LivingEntity) npc;
        livnpc.setAI(false);
    }

    public List<ConfigurationSection> getTradesConfig() {
        return getTrades().stream().map(ShopTrade::getConfig).collect(Collectors.toList());
    }

    public ItemStack getEquipmentItem(int slot) {
        return this.equipments[slot];
    }

    public void setEquipmentItem(ItemStack item, int slot) {
        this.equipments[slot] = item;
        updateEquipments();
    }

    public ItemStack getEquipmentDisplayItem(EquipmentSlot slot) {
        return JavaUtil.getOrDefault(getEquipmentItem(EquipmentUtil.getEquipmentSlotNumber(slot)), EquipmentUtil.getEquipmentDisplayItem(slot));
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
