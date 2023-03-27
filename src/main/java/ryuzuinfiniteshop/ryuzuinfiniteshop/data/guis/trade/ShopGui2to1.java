package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;

public class ShopGui2to1 extends ShopTradeGui {

    static {
        for (int i = 0; i < 6; i++) {
            convertslot.add(i * 9 + 2);
            convertslot.add(i * 9 + 7);
            displayslot.add(i * 9 + 4);
        }
    }

    public ShopGui2to1(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        return getInventory(i -> (i / 2) * 9 + (i % 2 == 1 ? 5 : 0) , mode);
    }

    @Override
    public ShopTrade getTradeFromSlot(int slot) {
        int mod9 = slot % 9;
        if (mod9 == 4) return null;
        int quootient9 = slot / 9;
        int front = mod9 < 4 ? 0 : 1;
        return getTrade(quootient9 * 2 + front + 1);
    }
}
