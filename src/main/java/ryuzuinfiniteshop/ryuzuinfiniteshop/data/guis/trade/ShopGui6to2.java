package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;

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
        Inventory inv = super.getInventory(mode);

        ItemStack filler = ItemUtil.getNamedItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.BLACK + "");
        ItemStack filler2 = mode.equals(ShopHolder.ShopMode.Edit) ? ItemUtil.getNamedItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "シフトクリックでトレードをアイテム化") : filler;
        for (int i = 0; i < 6; i++) {
            inv.setItem(i * 9 + 6, filler2);
        }

        for (int i = 0; i < getTrades().size(); i++) {
            ShopTrade trade = getTrades().get(i);
            int slot = i * 9;
            for (int j = 0; j < trade.getTakeItems().length; j++) {
                inv.setItem(slot + j, trade.getTakeItems()[j]);
            }
            for (int j = 0; j < trade.getGiveItems().length; j++) {
                inv.setItem(slot + j + 7, trade.getGiveItems()[j]);
            }
        }

        return inv;
    }

    @Override
    public ShopTrade getTradeFromSlot(int slot) {
        return getTrade(slot / 9 + 1);
    }
}
