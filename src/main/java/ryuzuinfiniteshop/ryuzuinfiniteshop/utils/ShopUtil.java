package ryuzuinfiniteshop.ryuzuinfiniteshop.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopEditorMainPage;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.trades.TradeListener;

import java.io.File;

public class ShopUtil {
    public static boolean isShopInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return false;
        Player p = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) return false;
        return isShopInventory(event.getView().getTopInventory());
    }

    public static boolean isShopInventory(Inventory inv) {
        if (inv == null) return false;
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

    public static void playClickEffect(InventoryClickEvent event) {
        if (!isShopInventory(event)) return;
        Player p = (Player) event.getWhoClicked();

        //音を出す
        p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 2);

        //イベントをキャンセル
        event.setCancelled(true);
    }

    public static boolean isAvailableTrade(Inventory inv, int slot, Shop.ShopType type) {
        if (type.equals(Shop.ShopType.TwotoOne))
            return ItemUtil.getItemSet(inv, slot, 2).length != 0 && inv.getItem(slot + 3) != null;
        else
            return ItemUtil.getItemSet(inv, slot, 4).length != 0 && ItemUtil.getItemSet(inv, slot + 5, 4).length != 0;
    }

    public static void loadAllShops() {
        TradeListener.getShops().clear();
        File directory = FileUtil.initializeFolder("shops");
        File[] ItemFiles = directory.listFiles();
        if(ItemFiles == null) return;
        for (File f : ItemFiles) {
            if(f.getName().endsWith(".yml")) new Shop(f);
        }
    }

    public static void removeAllNPC() {
        for(Shop shop : TradeListener.getShops().values()) {
            shop.getNPC().remove();
        }
    }

    public static void saveAllShops() {
        for(Shop shop : TradeListener.getShops().values()) {
            shop.saveYaml();
        }
    }
}
