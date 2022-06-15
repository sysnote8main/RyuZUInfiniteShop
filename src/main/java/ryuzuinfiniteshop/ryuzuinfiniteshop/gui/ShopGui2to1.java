package ryuzuinfiniteshop.ryuzuinfiniteshop.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.ShopTrade;

public class ShopGui2to1 extends ShopGui {

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(new ShopHolder(ShopHolder.ShopType.twotoone), 9 * 6);

        ItemStack filler1 = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemStack filler2 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        for (int i = 0; i < 6; i++) {
            inventory.setItem(i * 9 + 4, filler1);
            inventory.setItem(i * 9 + 2, filler2);
            inventory.setItem(i * 9 + 7, filler2);
        }

        for (int i = 0; i < trades.size(); i++) {
            ShopTrade trade = trades.get(i);
            int slot = (i / 2) * 9 + (i % 2 == 1 ? 5 : 0);
            inventory.setItem(slot, trade.give[0]);
            inventory.setItem(slot + 1, trade.give[1]);
            inventory.setItem(slot + 3, trade.take[0]);
        }

        return inventory;
    }
}
