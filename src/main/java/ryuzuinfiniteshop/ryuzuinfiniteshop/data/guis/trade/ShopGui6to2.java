package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;

public class ShopGui6to2 extends ShopTradeGui {

    static {
        for (int i = 0; i < 6; i++) {
            convertslot.add(i * 9 + 4);
        }
    }

    public ShopGui6to2(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        Inventory inv = getInventory(i -> i * 9 , mode);
        for (int i = 0; i < 6; i++) {
            inv.setItem(i * 9 + 6, ShopTrade.getFilter(mode , 0));
        }
        return inv;
    }

    @Override
    public ShopTrade getTradeFromSlot(int slot) {
        return getTrade(slot / 9 + 1);
    }
}
