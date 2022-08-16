package ryuzuinfiniteshop.ryuzuinfiniteshop.data;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopEditorMainPage;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopGui2to1;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopGui4to4;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.TradeListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.LocationUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.PersistentUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Shop {
    public enum ShopType {TwotoOne, FourtoFour}

    private Entity npc;
    private Location location;
    private ShopType type;
    private List<ShopTrade> trades = new ArrayList<>();
    private boolean lock = false;
    private boolean editting = false;
    private List<ShopEditorMainPage> editors = new ArrayList<>();
    private List<ShopTradeGui> pages = new ArrayList<>();
    public ItemStack[] equipments = new ItemStack[6];

    public Shop(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        this.location = LocationUtil.toLocationFromString(file.getName().replace(".yml", ""));
        spawnNPC(config);
        this.type = ShopType.valueOf(config.getString("ShopType"));
        List<ConfigurationSection> trades = (List<ConfigurationSection>) config.getList("Trades");
        this.trades = trades.stream().map(ShopTrade::new).collect(Collectors.toList());
        updateTradeContents();
        Arrays.fill(equipments, new ItemStack(Material.AIR));
        TradeListener.addShop(getID(), this);
    }

    public Shop(Location location) {
        File file = FileUtil.initializeFile("shops/" + LocationUtil.toStringFromLocation(location) + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        this.location = location;
        this.type = ShopType.TwotoOne;
        spawnNPC(config);
        Arrays.fill(equipments, new ItemStack(Material.AIR));
        saveYaml();
        createFirstPage();
        TradeListener.addShop(getID(), this);
    }

    public void updateTradeContents() {
        if (trades.size() == 0)
            createFirstPage();
        else
            setGuis();
    }

    public void setGuis() {
        setTradePages();
        setEditors();
    }

    public void createFirstPage() {
        pages.clear();
        editors.clear();
        createNewPage();
    }

    public void createNewPage() {
        createTradeNewPage();
        createEdotorNewPage();
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
        return editors.get(page - 1);
    }

    public void setEditors() {
        editors.clear();
        for (int i = 1; i <= getEditorPageCountFromTradesCount(); i++) {
            editors.add(new ShopEditorMainPage(this, i));
        }
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
        if (getTradePageCountFromTradesCount() % 18 != 0) size++;
        return size;
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

    public boolean ableEditorNewPage() {
        if (editors.isEmpty()) return true;
        return editors.size() < getEditorPageCountFromTradesCount();
    }

    public void createEdotorNewPage() {
        if (!ableEditorNewPage()) return;
        editors.add(new ShopEditorMainPage(this, getEditorPageCount() + 1));
    }

    public void addTrade(Inventory inv, int slot) {
        trades.add(new ShopTrade(inv, slot, type));
    }

    public void saveYaml() {
        File file = FileUtil.initializeFile("shops/" + LocationUtil.toStringFromLocation(location) + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("EntityType", npc.getType().toString());
        config.set("ShopType", type.toString());
        config.set("Trades", getTradesConfig());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void spawnNPC(YamlConfiguration config) {
        EntityType entityType = EntityType.valueOf(config.getString("EntityType", "VILLAGER"));
        Entity npc = location.getWorld().spawnEntity(LocationUtil.toBlockLocationFromLocation(location), entityType);
        this.npc = npc;
        npc.setSilent(true);
        npc.setInvulnerable(true);
        PersistentUtil.setNMSTag(npc, "Shop", getID());
        initializeLivingEntitiy();
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

    public ItemStack getEquipmentDisplayItem(int slot) {
        String name = null;
        switch (slot) {
            case 0:
                name = "メインハンド";
                break;
            case 1:
                name = "ヘルメット";
                break;
            case 2:
                name = "チェストプレート";
                break;
            case 3:
                name = "レギンス";
                break;
            case 4:
                name = "ブーツ";
                break;
            case 5:
                name = "オフハンド";
                break;
        }

        return getEquipmentItem(slot).getType().equals(Material.AIR) ?
                ItemUtil.getNamedItem(Material.BLACK_STAINED_GLASS_PANE, name) :
                getEquipmentItem(slot);
    }

    public void updateEquipments() {
        if (npc instanceof LivingEntity) ((LivingEntity) npc).getEquipment().setArmorContents(equipments);
    }

    public boolean isAvailableShop() {
        return !isLock() && !isEditting();
    }

    public boolean isAvailableShop(Player p) {
        if (isLock() && !p.hasPermission("ris.op")) {
            p.sendMessage(ChatColor.RED + "現在このショップはロックされています");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 2);
            return false;
        }
        if (isEditting()) {
            p.sendMessage(ChatColor.RED + "現在このショップは編集中です");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 2);
            return false;
        }
        return true;
    }
}
