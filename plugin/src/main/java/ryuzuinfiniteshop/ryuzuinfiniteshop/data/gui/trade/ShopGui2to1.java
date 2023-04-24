package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.trade;

import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

import java.util.ArrayList;
import java.util.List;

public class ShopGui2to1 extends ShopTradeGui {
    private static List<Integer> convertSlot = new ArrayList<>();
    private static List<Integer> displaySlot = new ArrayList<>();

    static {
        for (int i = 0; i < 6; i++) {
            convertSlot.add(i * 9 + 2);
            convertSlot.add(i * 9 + 7);
            displaySlot.add(i * 9 + 4);
        }
    }

    public ShopGui2to1(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopMode mode) {
        Inventory inv = getInventory(i -> (i / 2) * 9 + (i % 2 == 1 ? 5 : 0) , mode);
        for (int i = 0; i < 6; i++) {
            ItemUtil.setItemIfEmpyty(inv , i * 9 + 2 , ShopTrade.getFilter(0));
            ItemUtil.setItemIfEmpyty(inv , i * 9 + 4 , ShopTrade.getFilter());
            ItemUtil.setItemIfEmpyty(inv , i * 9 + 7 , ShopTrade.getFilter(0));
        }
        return inv;
    }

    @Override
    public ShopTrade getTradeFromSlot(int slot) {
        int mod9 = slot % 9;
        if (mod9 == 4) return null;
        int quootient9 = slot / 9;
        int front = mod9 < 4 ? 0 : 1;
        return getTrade(quootient9 * 2 + front);
    }

    @Override
    public List<Integer> getDisplaySlot() {
        return displaySlot;
    }

    @Override
    public List<Integer> getConvertSlot() {
        return convertSlot;
    }
}
