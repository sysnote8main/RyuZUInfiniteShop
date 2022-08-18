package ryuzuinfiniteshop.ryuzuinfiniteshop.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
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

    public static boolean isShopInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return false;
        Player p = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) return false;
        return isShopInventory(event.getView().getTopInventory());
    }

    public static boolean isShopInventory(Inventory inv) {
        if (inv == null) return false;
        if (inv instanceof PlayerInventory) return false;
        InventoryHolder holder = inv.getHolder();
        if (holder == null) return false;
        if (!(holder instanceof ShopHolder)) return false;
        ShopHolder shopholder = (ShopHolder) holder;
        Shop shop = shopholder.getShop();

        return shop != null;
    }

    public static Inventory getSecureInventory(InventoryClickEvent event) {
        return event.getClickedInventory() == null ? event.getView().getTopInventory() : event.getClickedInventory();
    }

    public static ShopGui getShopGui(InventoryClickEvent event) {
        return getShopGui(getSecureInventory(event));
    }

    public static ShopGui getShopGui(Inventory inv) {
        if (!isShopInventory(inv)) return null;
        ShopHolder holder = (ShopHolder) inv.getHolder();
        if (holder.getTags().get(0).equals(ShopEditorMainPage.class.getName()))
            return holder.getShop().getEditor(holder.getPage());
        else
            return holder.getShop().getPage(holder.getPage());
    }

    public static boolean isEditMode(InventoryClickEvent event) {
        return isEditMode(getSecureInventory(event));
    }

    public static boolean isTradeMode(InventoryClickEvent event) {
        return isTradeMode(getSecureInventory(event));
    }

    public static boolean isEditMode(Inventory inv) {
        if (!isShopInventory(inv)) return false;
        return ((ShopHolder) inv.getHolder()).getShopMode().equals(ShopHolder.ShopMode.Edit);
    }

    public static boolean isTradeMode(Inventory inv) {
        if (!isShopInventory(inv)) return false;
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
            if (f.getName().endsWith(".yml"))  {
                YamlConfiguration config = new YamlConfiguration();
                try {
                    config.load(f);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                createShop(LocationUtil.toLocationFromString(f.getName().replace(".yml", "")) , EntityType.valueOf(config.getString("EntityType")));
            }
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
        if(type.equals(EntityType.VILLAGER) || type.equals(EntityType.ZOMBIE_VILLAGER)) {
            new VillagerableShop(location, type);
            return;
        }
        if(type.equals(EntityType.CREEPER)) {
            new PoweredableShop(location, type);
            return;
        }
        if(Ageable.class.isAssignableFrom(type.getEntityClass())) {
            new AgeableShop(location, type);
            return;
        }
        new Shop(location, type);
    }
}
