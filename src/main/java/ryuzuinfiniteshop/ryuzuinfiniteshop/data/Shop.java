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
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
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

    private final Entity npc;
    private final Location location;
    private final ShopType type;
    private List<ShopTrade> trades;
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
        spawnNPC(config);
        this.location = LocationUtil.toLocationFromString(file.getName());
        this.npc = spawnNPC(config);
        initializeLivingEntitiy();
        this.type = ShopType.valueOf(config.getString("ShopType"));
        List<ConfigurationSection> trades = (List<ConfigurationSection>) config.getList("Trades");
        this.trades = trades.stream().map(ShopTrade::new).collect(Collectors.toList());
        updateTradeContents();
        Arrays.fill(equipments, new ItemStack(Material.AIR));
        TradeListener.addShop(getID() , this);
    }

    public Shop(Location location) {
        File file = FileUtil.initializeFile("shops/" + LocationUtil.toStringFromLocation(location) + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        spawnNPC(config);
        this.location = location;
        this.npc = spawnNPC(config);
        this.type = ShopType.TwotoOne;
        Arrays.fill(equipments, new ItemStack(Material.AIR));
        saveYaml();
        TradeListener.addShop(getID() , this);
    }

    public void updateTradeContents() {
        setTradePages();
        setEditors();
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
        if (!pages.get(0).existTrade(page)) return null;
        return pages.get(page);
    }

    public void setTradePages() {
        for (int i = 0; i < getTradePageCount(); i++) {
            if (type.equals(ShopType.TwotoOne))
                pages.add(new ShopGui2to1(this, i));
            else
                pages.add(new ShopGui4to4(this, i));
        }
    }

    public ShopEditorMainPage getEditor(int page) {
        if (page <= 0) return null;
        return editors.get(page);
    }

    public void setEditors() {
        for (int i = 0; i < pages.size() / 18; i++) {
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
        int size = getTrades().size() / getLimitSize();
        if (getTrades().size() % getLimitSize() != 0) size++;
        return size;
    }

    public int getEditorPageCount() {
        int size = getTradePageCount() / 18;
        if (getTrades().size() % getLimitSize() != 0) size++;
        return size;
    }

    public ShopType getShopType() {
        return type;
    }

    public boolean ableCreateNewPage() {
        if (trades.isEmpty()) return true;
        return isLimitPage(pages.size() - 1);
    }

    public void addTradePage() {
        if (!ableCreateNewPage()) return;
        if (type.equals(ShopType.TwotoOne))
            pages.add(new ShopGui2to1(this, getTradePageCount() + 1));
        else
            pages.add(new ShopGui4to4(this, getTradePageCount() + 1));
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

    public Entity spawnNPC(YamlConfiguration config) {
        EntityType entityType = EntityType.valueOf(config.getString("EntityType", "Villager"));
        Entity entity = location.getWorld().spawnEntity(LocationUtil.toBlockLocationFromLocation(location), entityType);
        entity.setSilent(true);
        entity.setInvulnerable(true);
        PersistentUtil.setNMSTag(entity, "Shop", getID());
        return location.getWorld().spawnEntity(location, entityType);
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
