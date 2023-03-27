package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;

public class ShopGui4to4 extends ShopTradeGui {

    static {
        for (int i = 0; i < 6; i++) {
            convertslot.add(i * 9 + 4);
        }
    }

    public ShopGui4to4(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        return getInventory(i -> i * 9 , mode);
    }

    @Override
    public ShopTrade getTradeFromSlot(int slot) {
        return getTrade(slot / 9 + 1);
    }
}
