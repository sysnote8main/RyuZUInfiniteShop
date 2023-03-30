package ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Colorable;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ModeHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.MythicInstanceProvider;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.JavaUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.LocationUtil;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public class ShopUtil {
    private static HashMap<String, Shop> shops = new HashMap<>();

    public static ShopHolder getShopHolder(InventoryClickEvent event) {
        return getShopHolder(getSecureInventory(event));
    }

    public static ShopHolder getShopHolder(Inventory inv) {
        ModeHolder holder = getModeHolder(inv);
        if (holder == null) return null;
        if (!(holder instanceof ShopHolder)) return null;
        return (ShopHolder) holder;
    }

    public static ModeHolder getModeHolder(InventoryClickEvent event) {
        return getModeHolder(getSecureInventory(event));
    }

    public static ModeHolder getModeHolder(Inventory inv) {
        if (inv == null) return null;
        if (inv instanceof PlayerInventory) return null;
        InventoryHolder holder = inv.getHolder();
        if (holder == null) return null;
        if (!(holder instanceof ModeHolder)) return null;
        return (ModeHolder) holder;
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

    public static boolean isSearchMode(InventoryClickEvent event) {
        return isTradeMode(getSecureInventory(event));
    }

    public static boolean isEditMode(Inventory inv) {
        if (getModeHolder(inv) == null) return false;
        return ((ShopHolder) inv.getHolder()).getMode().equals(ShopMode.Edit);
    }

    public static boolean isTradeMode(Inventory inv) {
        if (getModeHolder(inv) == null) return false;
        return ((ShopHolder) inv.getHolder()).getMode().equals(ShopMode.Trade);
    }

    public static boolean isSearchMode(Inventory inv) {
        if (getModeHolder(inv) == null) return false;
        return ((ShopHolder) inv.getHolder()).getMode().equals(ShopMode.Search);
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
            Optional<String> mmid = Optional.ofNullable(config.getString("Npc.Options.MythicMob"));
            if(mmid.isPresent() && MythicInstanceProvider.isLoaded() && MythicInstanceProvider.getInstance().getMythicMob(mmid.get()) != null)
                createNewShop(LocationUtil.toLocationFromString(f.getName().replace(".yml", "")), mmid.get());
            else
                createNewShop(LocationUtil.toLocationFromString(f.getName().replace(".yml", "")), EntityType.valueOf(config.getString("Npc.Options.EntityType", "VILLAGER")));
        }
    }

    public static void removeAllNPC() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
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
        shops.keySet().stream().sorted(Comparator.naturalOrder()).forEach(key -> sorted.put(key, shops.get(key)));
        return sorted;
    }

    public static Shop getShop(String id) {
        return shops.get(id);
    }

    public static void addShop(String id, Shop shop) {
        shops.put(id, shop);
    }

    public static Shop createNewShop(Location location, String mmid) {
        return new Shop(location, mmid);
    }

    public static Shop createNewShop(Location location, EntityType type) {
        if (type.equals(EntityType.VILLAGER) || type.equals(EntityType.ZOMBIE_VILLAGER)) {
            return new VillagerableShop(location, type);
        }
        if (type.equals(EntityType.CREEPER)) {
            return new PoweredableShop(location, type);
        }
        if (Colorable.class.isAssignableFrom(type.getEntityClass()) || type.equals(EntityType.WOLF) || type.equals(EntityType.TROPICAL_FISH)) {
            return new DyeableShop(location, type);
        }
        if (type.equals(EntityType.PARROT)) {
            return new ParrotShop(location, type);
        }
        if (type.equals(EntityType.HORSE)) {
            return new HorseShop(location, type);
        }
        if (Ageable.class.isAssignableFrom(type.getEntityClass())) {
            return new AgeableShop(location, type);
        }
        return new Shop(location, type);
    }

    public static void removeShop(String id) {
        shops.remove(id);
    }

    public static Shop reloadShop(Shop shop) {
        return reloadShop(shop.getLocation(), shop.convertShopToString(), config -> {});
    }

    public static Shop reloadShop(Location location, String data) {
        return reloadShop(location, data, config -> {});
    }

    private static Shop reloadShop(Location location, String data, Consumer<YamlConfiguration> consumer) {
        File file = FileUtil.initializeFile("shops/" + LocationUtil.toStringFromLocation(location) + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(data);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        consumer.accept(config);
        String stringlocation = LocationUtil.toStringFromLocation(location);
        if (shops.containsKey(stringlocation)) shops.get(stringlocation).removeShop();

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        EntityType type = EntityType.valueOf(config.getString("Npc.Options.EntityType", "VILLAGER"));
        return createNewShop(location, type);
    }

    public static Shop overwriteShop(Location location, String data, EntityType type) {
        return reloadShop(location, data, config -> {
            config.set("Npc.Options.MythicMob", null);
            config.set("Npc.Options.EntityType", type.toString());
        });
    }

    public static Shop overwriteShop(Location location, String data, String mmid) {
        return reloadShop(location, data, config -> config.set("Npc.Options.MythicMob", mmid));
    }

    public static ShopHolder closeShopTradeInventory(Player p) {
        if (p.getOpenInventory().getTopInventory().getHolder() instanceof ShopHolder) {
            ShopHolder holder = (ShopHolder) p.getOpenInventory().getTopInventory().getHolder();
            if (holder.getMode().equals(ShopMode.Trade)) {
                p.closeInventory();
                return holder;
            }
        }
        return null;
    }

    public static void closeShopTradeInventory(Player p, Shop shop) {
        if (p.getOpenInventory().getTopInventory().getHolder() instanceof ShopHolder) {
            ShopHolder holder = (ShopHolder) p.getOpenInventory().getTopInventory().getHolder();
            if (holder.getMode().equals(ShopMode.Trade) && holder.getShop().equals(shop))
                p.closeInventory();
        }
    }

    public static void closeAllShopTradeInventory(Shop shop) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            closeShopTradeInventory(p, shop);
        }
    }

    public static void reloadAllShopTradeInventory(Runnable runnable) {
        HashMap<Player, ShopHolder> holders = new HashMap<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            ShopHolder holder = closeShopTradeInventory(p);
            if (holder != null) holders.put(p, holder);
        }
        runnable.run();
        for (Player p : holders.keySet()) {
            ShopHolder holder = holders.get(p);
            p.openInventory(holder.getInventory());
        }
    }
}
