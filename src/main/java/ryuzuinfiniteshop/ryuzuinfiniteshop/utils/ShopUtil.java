package ryuzuinfiniteshop.ryuzuinfiniteshop.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.AgeableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.PoweredableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopEditorMainPage;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.VillagerableShop;

import java.io.File;
import java.io.IOException;
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

    public static boolean isAvailableTrade(Inventory inv, int slot, Shop.ShopType type) {
        if (type.equals(Shop.ShopType.TwotoOne))
            return ItemUtil.getItemSet(inv, slot, 2).length != 0 && inv.getItem(slot + 3) != null;
        else
            return ItemUtil.getItemSet(inv, slot, 4).length != 0 && ItemUtil.getItemSet(inv, slot + 5, 4).length != 0;
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
        for (Shop shop : getShops().values()) {
            shop.getNPC().remove();
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

    public static Shop getShop(String id) {
        return shops.get(id);
    }

    public static void addShop(String id, Shop shop) {
        shops.put(id, shop);
    }

    public static void createShop(Location location, EntityType type) {
        if (ShopUtil.getShops().containsKey(LocationUtil.toStringFromLocation(location))) return;
        if (type.equals(EntityType.VILLAGER) || type.equals(EntityType.ZOMBIE_VILLAGER)) {
            new VillagerableShop(location, type);
            return;
        }
        if (type.equals(EntityType.CREEPER)) {
            new PoweredableShop(location, type);
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
        if (ShopUtil.getShops().containsKey(LocationUtil.toStringFromLocation(location))) return;

        File file = FileUtil.initializeFile("shops/" + LocationUtil.toStringFromLocation(location) + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(data);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        EntityType type = EntityType.valueOf(config.getString("EntityType"));

        if (type.equals(EntityType.VILLAGER) || type.equals(EntityType.ZOMBIE_VILLAGER)) {
            new VillagerableShop(location, type);
            return;
        }
        if (type.equals(EntityType.CREEPER)) {
            new PoweredableShop(location, type);
            return;
        }
        if (Ageable.class.isAssignableFrom(type.getEntityClass())) {
            new AgeableShop(location, type);
            return;
        }
        new Shop(location, type);
    }

    public static void setTradeStatus(Player p, Inventory inventory, Shop shop, ShopTradeGui gui) {
        ItemStack status1 = ItemUtil.getNamedItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "購入可能");
        ItemStack status2 = ItemUtil.getNamedItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "アイテムが足りません");
        ItemStack status3 = ItemUtil.getNamedItem(Material.YELLOW_STAINED_GLASS_PANE, ChatColor.YELLOW + "インベントリに十分な空きがありません");

        int addslot = shop.getShopType().equals(Shop.ShopType.TwotoOne) ? 2 : 4;
        for (int i = 0; i < gui.getTrades().size(); i++) {
            int baseslot = shop.getShopType().equals(Shop.ShopType.TwotoOne) ?
                    (i / 2) * 9 + (i % 2 == 1 ? 5 : 0) :
                    i * 9;
            ShopTrade trade = gui.getTradeFromSlot(baseslot);
            ShopTrade.Result result = trade.getResult(p);
            switch (result) {
                case notAfford:
                    inventory.setItem(baseslot + addslot, status2);
                    break;
                case Full:
                    inventory.setItem(baseslot + addslot, status3);
                    break;
                case Success:
                    inventory.setItem(baseslot + addslot, status1);
                    break;
            }
        }
    }
}
