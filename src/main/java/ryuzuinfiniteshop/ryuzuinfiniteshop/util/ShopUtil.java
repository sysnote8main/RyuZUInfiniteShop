package ryuzuinfiniteshop.ryuzuinfiniteshop.util;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.TradeListener;

import java.util.HashMap;

public class ShopUtil {
    private static final HashMap<Integer , Integer> equipmentslots = new HashMap<>();

    static {
        equipmentslots.put(2 * 9 + 2 , 1);
        equipmentslots.put(3 * 9 + 2 , 2);
        equipmentslots.put(4 * 9 + 2 , 3);
        equipmentslots.put(5 * 9 + 2 , 4);
        equipmentslots.put(3 * 9 + 1 , 0);
        equipmentslots.put(3 * 9 + 3 , 5);
    }
    public static boolean isShopInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return false;
        Player p = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) return false;
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (holder == null) return false;
        ShopHolder shopholder = (ShopHolder) holder;
        if (!shopholder.mode.equals(ShopHolder.ShopMode.Trade)) return false;
        Shop shop = TradeListener.getShop(shopholder.tags.get(0));
        return shop != null;
    }
    public static boolean isShopTradeInventory(InventoryClickEvent event) {
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        return isShopInventory(event) && shopholder.mode.equals(ShopHolder.ShopMode.Trade);
    }

    public static boolean isShopEditInventory(InventoryClickEvent event) {
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        return isShopInventory(event) && shopholder.mode.equals(ShopHolder.ShopMode.Edit);
    }

    public static HashMap<Integer , Integer> getEquipmentsSlot() {
        return equipmentslots;
    }
}
