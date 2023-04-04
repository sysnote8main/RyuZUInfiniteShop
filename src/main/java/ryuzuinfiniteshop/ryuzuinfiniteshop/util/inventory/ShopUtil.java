package ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Colorable;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ModeHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    public static boolean loadAllShops() {
        getShops().clear();
        File directory = FileUtil.initializeFolder("shops");
        File[] ItemFiles = directory.listFiles();
        if (ItemFiles == null) return false;
        boolean converted = false;
        for (File f : ItemFiles) {
            try {
                if (!f.getName().endsWith(".yml")) continue;
                if (f.getName().equals("save.yml")) {
                    converted = convertAllShopkeepers(f);
                    continue;
                }
                YamlConfiguration config = new YamlConfiguration();
                try {
                    config.load(f);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                Optional<String> mmid = Optional.ofNullable(config.getString("Npc.Options.MythicMob"));
                if (mmid.isPresent() && MythicInstanceProvider.isLoaded() && MythicInstanceProvider.getInstance().getMythicMob(mmid.get()) != null)
                    createNewShop(LocationUtil.toLocationFromString(f.getName().replace(".yml", "")), mmid.get());
                else
                    createNewShop(LocationUtil.toLocationFromString(f.getName().replace(".yml", "")), EntityType.valueOf(config.getString("Npc.Options.EntityType", "VILLAGER")));
            } catch (Exception e) {
                throw new RuntimeException("ShopID: " + f.getName() + " の読み込み中にエラーが発生しました", e);
            }
        }
        return converted;
    }

    private static boolean convertAllShopkeepers(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        Set<String> keys = new HashSet<>();
        for (String key : config.getKeys(false)) {
            String base = key + ".";
            try {
                if (key.equals("data-version")) continue;
                EntityType type;
                try {
                    type = EntityType.valueOf(config.getString(key + ".object.type", "VILLAGER").toUpperCase());
                } catch (IllegalArgumentException e) {
                    continue;
                }
                if (!config.getString(base + "type", "none").equals("admin")) continue;
                if (Bukkit.getWorld(config.getString(base + ".world")) == null) continue;
                Location location = LocationUtil.toLocationFromString(config.getString(base + ".world") + "," + config.getString(base + "x") + "," + config.getString(base + "y") + "," + config.getString(base + "z"));
                Shop shop = createNewShop(location, type, config.getConfigurationSection(key));
                List<ShopTrade> trades = new ArrayList<>();
                for (String recipe : config.getConfigurationSection(base + "recipes").getKeys(false)) {
                    boolean hasItem2 = config.contains(base + "recipes." + recipe + ".item2");
                    ItemStack[] items = new ItemStack[hasItem2 ? 2 : 1];
                    ItemStack[] results = new ItemStack[1];
                    results[0] = config.getItemStack(base + "recipes." + recipe + ".resultItem");
                    items[0] = config.getItemStack(base + "recipes." + recipe + ".item1");
                    if (hasItem2) items[1] = config.getItemStack(base + "recipes." + recipe + ".item2");
                    trades.add(new ShopTrade(results, items));
                }
                shop.setTrades(trades);
                keys.add(key);
            } catch (Exception e) {
                throw new RuntimeException("ShopkeepersID: " + key + " SISID: " + (config.getString(base + ".world") + "," + config.getString(base + "x") + "," + config.getString(base + "y") + "," + config.getString(base + "z")) + " のShopkeepersからのコンバート中にエラーが発生しました", e);
            }
        }

        keys.forEach(key -> config.set(key, null));

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keys.size() > 0;
    }

    public static void removeAllNPC() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                String id = NBTUtil.getNMSStringTag(entity, "Shop");
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

    public static HashMap<String, Shop> getSortedShops(ShopMode mode, String name) {
        HashMap<String, Shop> sorted = new HashMap<>();
        if (mode.equals(ShopMode.Edit))
            shops.keySet().stream().sorted(Comparator.naturalOrder()).forEach(key -> sorted.put(key, shops.get(key)));
        else
            shops.keySet().stream().sorted(Comparator.naturalOrder()).filter(key -> shops.get(key).isSearchable() && shops.get(key).containsDisplayName(name)).forEach(key -> sorted.put(key, shops.get(key)));

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

    public static Shop createNewShop(Location location, EntityType type, ConfigurationSection config) {
        if (type.equals(EntityType.VILLAGER) || type.equals(EntityType.ZOMBIE_VILLAGER)) {
            return new VillagerableShop(location, type, config);
        }
        if (type.equals(EntityType.CREEPER)) {
            return new PoweredableShop(location, type, config);
        }
        if (Colorable.class.isAssignableFrom(type.getEntityClass()) || type.equals(EntityType.WOLF) || type.equals(EntityType.TROPICAL_FISH)) {
            return new DyeableShop(location, type, config);
        }
        if (type.equals(EntityType.PARROT)) {
            return new ParrotShop(location, type, config);
        }
        if (type.equals(EntityType.HORSE)) {
            return new HorseShop(location, type, config);
        }
        if (Ageable.class.isAssignableFrom(type.getEntityClass())) {
            return new AgeableShop(location, type, config);
        }
        return new Shop(location, type, config);
    }

    public static Shop createNewShop(Location location, EntityType type) {
        return createNewShop(location, type, null);
    }

    public static void removeShop(String id) {
        shops.remove(id);
    }

    public static Shop reloadShop(Shop shop) {
        return reloadShop(shop.getLocation(), shop.convertShopToString(), TradeUtil.convertTradesToList(shop.convertTradesToMap()), config -> {});
    }

    public static Shop reloadShop(Location location, String data, List<ShopTrade> trades) {
        return reloadShop(location, data, trades, config -> {});
    }

    private static Shop reloadShop(Location location, String data, List<ShopTrade> trades, Consumer<YamlConfiguration> consumer) {
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
        config.set("Trades", trades.stream().map(ShopTrade::serialize).collect(Collectors.toList()));

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        EntityType type = EntityType.valueOf(config.getString("Npc.Options.EntityType", "VILLAGER"));
        String mythicmob = config.getString("Npc.Options.MythicMob");
        if (mythicmob != null && MythicInstanceProvider.getInstance().getMythicMob(mythicmob) != null)
            return createNewShop(location, mythicmob);
        else
            return createNewShop(location, type);
    }

    public static HashMap<String, String> mergeShop(ItemStack item, Shop shop, Player p) {
        YamlConfiguration config = new YamlConfiguration();
        String data = NBTUtil.getNMSStringTag(item, "ShopData");
        try {
            config.loadFromString(data);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        List<ShopTrade> trades = TradeUtil.convertTradesToList(item);
        trades.addAll(shop.getTrades());
        LogUtil.log(LogUtil.LogType.MERGESHOP, p.getName(), shop.getID());
        shop.removeShop();
        HashMap<String, String> shopData = new HashMap<>();
        shopData.put("ShopData", NBTUtil.getNMSStringTag(item, "ShopData"));
        shopData.putAll(TradeUtil.convertTradesToMap(item, trades.stream().distinct().collect(Collectors.toList())));
        return shopData;
    }

    public static Shop overwriteShop(Location location, String data, HashMap<String, String> trades, EntityType type) {
        return reloadShop(location, data, TradeUtil.convertTradesToList(trades), config -> {
            config.set("Npc.Options.MythicMob", null);
            config.set("Npc.Options.EntityType", type.toString());
        });
    }

    public static Shop overwriteShop(Location location, String data, HashMap<String, String> trades, String mmid) {
        return reloadShop(location, data, TradeUtil.convertTradesToList(trades), config -> config.set("Npc.Options.MythicMob", mmid));
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

    public static ShopHolder closeShopInventory(Player p) {
        if (p.getOpenInventory().getTopInventory().getHolder() instanceof ShopHolder) {
            ShopHolder holder = (ShopHolder) p.getOpenInventory().getTopInventory().getHolder();
            p.closeInventory();
            return holder;
        }
        return null;
    }

    public static HashMap<Player, ShopHolder> getAllShopInventoryViewer() {
        HashMap<Player, ShopHolder> holders = new HashMap<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            ShopHolder holder = closeShopInventory(p);
            if (holder != null) holders.put(p, holder);
        }
        return holders;
    }

    public static void openAllShopInventory(HashMap<Player, ShopHolder> holders) {
        for (Player p : holders.keySet()) {
            ShopHolder holder = holders.get(p);
            p.openInventory(holder.getInventory());
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
