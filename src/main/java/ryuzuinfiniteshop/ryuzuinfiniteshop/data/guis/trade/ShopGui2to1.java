package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;

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
        Inventory inv = super.getInventory(mode);

        ItemStack filler = ItemUtil.getNamedItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.BLACK + "");
        ItemStack filler2 = mode.equals(ShopHolder.ShopMode.Edit) ? ItemUtil.getNamedItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "シフトクリックでトレードをアイテム化") : filler;

        for (int i = 0; i < 6; i++) {
            inv.setItem(i * 9 + 2, filler2);
            inv.setItem(i * 9 + 4, filler);
            inv.setItem(i * 9 + 7, filler2);
        }

        for (int i = 0; i < getTrades().size(); i++) {
            ShopTrade trade = getTrades().get(i);
            int slot = (i / 2) * 9 + (i % 2 == 1 ? 5 : 0);
            for (int k = 0; k < trade.getTakeItems().length; k++) {
                inv.setItem(slot + k, trade.getTakeItems()[k]);
            }
            inv.setItem(slot + 3, trade.getGiveItems()[0]);
        }

        return inv;
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
