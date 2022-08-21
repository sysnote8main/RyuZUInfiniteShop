package ryuzuinfiniteshop.ryuzuinfiniteshop.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;

public class TradeUtil {
    public static boolean isAvailableTrade(Inventory inv, int slot, Shop.ShopType type) {
        switch (type) {
            case TwotoOne:
                return ItemUtil.getItemSet(inv, slot, 2).length != 0 && inv.getItem(slot + 3) != null;
            case FourtoFour:
                return ItemUtil.getItemSet(inv, slot, 4).length != 0 && ItemUtil.getItemSet(inv, slot + 5, 4).length != 0;
            case SixtoTwo:
                return ItemUtil.getItemSet(inv, slot, 6).length != 0 && ItemUtil.getItemSet(inv, slot + 7, 2).length != 0;
        }
        return false;
    }

    public static ShopTrade getTrade(Inventory inv, int slot, Shop.ShopType type) {
        if (!isAvailableTrade(inv, slot, type)) return null;
        switch (type) {
            case TwotoOne:
                return new ShopTrade(new ItemStack[]{inv.getItem(slot + 3)}, ItemUtil.getItemSet(inv, slot, 2));
            case FourtoFour:
                return new ShopTrade(ItemUtil.getItemSet(inv, slot + 5, 4), ItemUtil.getItemSet(inv, slot, 4));
            case SixtoTwo:
                return new ShopTrade(ItemUtil.getItemSet(inv, slot + 7, 2), ItemUtil.getItemSet(inv, slot, 6));
        }
        return null;
    }
}
