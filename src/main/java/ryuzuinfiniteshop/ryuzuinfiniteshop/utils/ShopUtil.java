package ryuzuinfiniteshop.ryuzuinfiniteshop.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Colorable;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;

public class ShopUtil {
    private static HashMap<String, Shop> shops = new HashMap<>();

    public static ShopHolder getShopHolder(InventoryClickEvent event) {
        return getShopHolder(getSecureInventory(event));
    }

    public static ShopHolder getShopHolder(Inventory inv) {
        if (inv == null) return null;
        if (inv instanceof PlayerInventory) return null;
        InventoryHolder holder = inv.getHolder();
        if (holder == null) return null;
        if (!(holder instanceof ShopHolder)) return null;
        return (ShopHolder) holder;
    }

    public static Inventory getSecureInventory(InventoryClickEvent event) {
        return JavaUtil.getOrDefault(event.getClickedInventory(), event.getView().getTopInventory());
    }

    public static boolean isEditMode(InventoryClickEvent event) {
        return isEditMode(getSecureInventory(event));
    }

    public static boolean isTradeMode(InventoryClickEvent event) {
        return isTradeMode(getSecureInventory(event));
    }

    public static boolean isEditMode(Inventory inv) {
        if (getShopHolder(inv) == null) return false;
        return ((ShopHolder) inv.getHolder()).getShopMode().equals(ShopHolder.ShopMode.Edit);
    }

    public static boolean isTradeMode(Inventory inv) {
        if (getShopHolder(inv) == null) return false;
        return ((ShopHolder) inv.getHolder()).getShopMode().equals(ShopHolder.ShopMode.Trade);
    }

    public static void loadAllShops() {
        getShops().clear();
        File directory = FileUtil.initializeFolder("shops");
        File[] ItemFiles = directory.listFiles();
        if (ItemFiles == null) return;
        for (File f : ItemFiles) {
            if (!f.getName().endsWith(".yml")) continue;
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(f);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            createShop(LocationUtil.toLocationFromString(f.getName().replace(".yml", "")), EntityType.valueOf(config.getString("EntityType")));
        }
    }

    public static void removeAllNPC() {
        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                String id = PersistentUtil.getNMSStringTag(entity, "Shop");
                if (id != null) entity.remove();
            }
        }
    }

    public static void saveAllShops() {
        for (Shop shop : getShops().values()) {
            shop.saveYaml();
        }
    }

    public static HashMap<String, Shop> getShops() {
        return shops;
    }

    public static HashMap<String, Shop> getSortedShops() {
        HashMap<String, Shop> sorted = new HashMap<>();
        shops.keySet().stream().sorted(Comparator.naturalOrder()).forEach(key -> sorted.put(key , shops.get(key)));
        return sorted;
    }

    public static Shop getShop(String id) {
        return shops.get(id);
    }

    public static void addShop(String id, Shop shop) {
        shops.put(id, shop);
    }

    public static void createShop(Location location, EntityType type) {
        if (type.equals(EntityType.VILLAGER) || type.equals(EntityType.ZOMBIE_VILLAGER)) {
            new VillagerableShop(location, type);
            return;
        }
        if (type.equals(EntityType.CREEPER)) {
            new PoweredableShop(location, type);
            return;
        }
        if (Colorable.class.isAssignableFrom(type.getEntityClass()) || type.equals(EntityType.WOLF) || type.equals(EntityType.TROPICAL_FISH)) {
            new DyeableShop(location, type);
            return;
        }
        if (type.equals(EntityType.PARROT)) {
            new ParrotShop(location, type);
            return;
        }
        if (type.equals(EntityType.HORSE)) {
            new HorseShop(location, type);
            return;
        }
        if (Ageable.class.isAssignableFrom(type.getEntityClass())) {
            new AgeableShop(location, type);
            return;
        }
        new Shop(location, type);
    }

    public static void removeShop(String id) {
        shops.remove(id);
    }

    public static void createShop(Location location, String data) {
        File file = FileUtil.initializeFile("shops/" + LocationUtil.toStringFromLocation(location) + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(data);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String stringlocation = LocationUtil.toStringFromLocation(location);
        if (shops.containsKey(stringlocation)) shops.get(stringlocation).removeShop();

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        EntityType type = EntityType.valueOf(config.getString("EntityType"));
        createShop(location, type);
    }

    public static void createShop(Location location, String data, EntityType type) {
        File file = FileUtil.initializeFile("shops/" + LocationUtil.toStringFromLocation(location) + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(data);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        config.set("EntityType", type.toString());

        String stringlocation = LocationUtil.toStringFromLocation(location);
        if (shops.containsKey(stringlocation)) shops.get(stringlocation).removeShop();

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        createShop(location, type);
    }
}
