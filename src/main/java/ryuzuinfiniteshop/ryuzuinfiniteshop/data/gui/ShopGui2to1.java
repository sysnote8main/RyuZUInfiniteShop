package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.JavaUtil;

public class ShopGui2to1 extends ShopGui {

    public ShopGui2to1(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode , getPage()), 9 * 6 , "ショップ ページ" + getPage());

        ItemStack filler = ItemUtil.getNamedItem(Material.BLACK_STAINED_GLASS_PANE , "");

        for (int i = 0; i < 6; i++) {
            inv.setItem(i * 9 + 2, filler);
            inv.setItem(i * 9 + 4, filler);
            inv.setItem(i * 9 + 7, filler);
        }

        for (int i = 0; i < getTrades().size(); i++) {
            ShopTrade trade = getTrades().get(i);
            int slot = (i / 2) * 9 + (i % 2 == 1 ? 5 : 0);
            inv.setItem(slot, trade.give[0]);
            inv.setItem(slot + 1, trade.give[1]);
            inv.setItem(slot + 3, trade.take[0]);
        }

        return inv;
    }

    @Override
    public ShopTrade getTrade(int slot) {
        int mod9 = slot % 9;
        if (mod9 == 4) return null;
        int quootient9 = slot / 9;
        int front = mod9 / 4;
        int tradenumber = quootient9 * 2 + front;
        if (getTrades().size() - 1 <= tradenumber) return null;
        return getTrades().get(tradenumber);
    }

    @Override
    public void setTrades(int page) {
        this.trades = JavaUtil.splitList(getShop().getTrades() , 12)[page - 1];
    }

    @Override
    public boolean existPage(int page) {
        return getTrades().size() > (page - 1) * 12;
    }
}
