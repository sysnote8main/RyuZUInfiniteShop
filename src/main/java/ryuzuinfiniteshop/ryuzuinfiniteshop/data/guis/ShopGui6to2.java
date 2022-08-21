package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;

import java.util.ArrayList;
import java.util.List;

public class ShopGui6to2 extends ShopTradeGui {

    private static final List<Integer> displayslot = new ArrayList<>();

    static {
        for (int i = 0; i < 6; i++) {
            displayslot.add(i * 9 + 4);
        }
    }

    public ShopGui6to2(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        Inventory inv = super.getInventory(mode);

        ItemStack filler = ItemUtil.getNamedItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.BLACK + "");
        for (int i = 0; i < 6; i++) {
            inv.setItem(i * 9 + 6, filler);
        }

        for (int i = 0; i < getTrades().size(); i++) {
            ShopTrade trade = getTrades().get(i);
            int slot = i * 9;
            for (int j = 0; j < trade.take.length; j++) {
                inv.setItem(slot + j, trade.take[j]);
            }
            for (int j = 0; j < trade.give.length; j++) {
                inv.setItem(slot + j + 7, trade.give[j]);
            }
        }

        return inv;
    }

    @Override
    public ShopTrade getTradeFromSlot(int slot) {
        return getTrade(slot / 9 + 1);
    }

    @Override
    public boolean isDisplayItem(int slot) {
        return displayslot.contains(slot);
    }
}
