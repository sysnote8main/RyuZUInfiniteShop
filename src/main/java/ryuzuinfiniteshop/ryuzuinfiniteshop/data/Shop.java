package ryuzuinfiniteshop.ryuzuinfiniteshop.data;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopEditorMainPage;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopGui2to1;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.LocationUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.PersistentUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Shop {
    public enum ShopType {TwotoOne, FourtoFour}

    private final Entity npc;
    private final Location location;
    private final ShopType type;
    private final List<ShopTrade> trades;

    private List<ShopEditorMainPage> editors = new ArrayList<>();
    public List<ShopGui> pages = new ArrayList<>();

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
        this.type = ShopType.valueOf(config.getString("ShopType"));
        List<ConfigurationSection> trades = (List<ConfigurationSection>) config.getList("Trades");
        this.trades = trades.stream().map(ShopTrade::new).collect(Collectors.toList());
        setPages();
        setEditors();
        Arrays.fill(equipments , new ItemStack(Material.AIR));
    }

    public List<ShopTrade> getTrades() {
        return trades;
    }

    public String getID() {
        return LocationUtil.toStringFromLocation(location);
    }

    public ShopGui getPage(int page) {
        if (page <= 0) return null;
        if (!pages.get(0).existPage(page)) return null;
        return pages.get(page);
    }

    public void setPages() {
        for (int i = 0; i < getPageCount(); i++) {
            this.pages.add(new ShopGui2to1(this, i));
        }
    }

    public ShopEditorMainPage getEditor(int page) {
        if (page <= 0) return null;
        return editors.get(page);
    }

    public void setEditors() {
        for(int i = 0 ; i < pages.size() / 18 ; i++) {
            this.editors.add(new ShopEditorMainPage(this , i));
        }
    }

    public int getLimitSize() {
        return getShopType().equals(ShopType.TwotoOne) ? 12 : 6;
    }

    public boolean isLimitPage(int page) {
        return getPage(page).getTrades().size() == getLimitSize();
    }

    public int getPageCount() {
        int size = getTrades().size() / getLimitSize();
        if (getTrades().size() % getLimitSize() != 0) size++;
        return size;
    }

    public ShopType getShopType() {
        return type;
    }

    public void saveYaml(File file) {
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

    public void removeNPC() {
        this.npc.remove();
    }

    public Entity getNPC() {
        return this.npc;
    }

    public Entity spawnNPC(YamlConfiguration config) {
        EntityType entityType = EntityType.valueOf(config.getString("EntityType"));
        Entity entity = location.getWorld().spawnEntity(location, entityType);
        PersistentUtil.setNMSTag(entity, "Shop", getID());
        return location.getWorld().spawnEntity(location, entityType);
    }

    public List<ConfigurationSection> getTradesConfig() {
        return getTrades().stream().map(ShopTrade::getConfig).collect(Collectors.toList());
    }

    public ItemStack getEquipmentItem(int slot) {
        return this.equipments[slot];
    }

    public void setEquipmentItem(ItemStack item , int slot) {
        this.equipments[slot] = item;
    }
    public ItemStack getEquipmentDisplayItem(int slot) {
        return getEquipmentItem(slot).getType().equals(Material.AIR) ?
                ItemUtil.getNamedItem(Material.BLACK_STAINED_GLASS_PANE, "ヘルメット") :
                getEquipmentItem(slot);
    }
}
