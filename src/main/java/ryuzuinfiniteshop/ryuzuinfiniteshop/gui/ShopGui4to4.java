package ryuzuinfiniteshop.ryuzuinfiniteshop.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.ShopTrade;

public class ShopGui4to4 extends ShopGui {

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(new ShopHolder(ShopHolder.ShopType.fourtofour), 9 * 6);

        ItemStack filler = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        for (int i = 0; i < 6; i++) {
            inventory.setItem(i * 9 + 4, filler);
        }

        for (int i = 0; i < trades.size(); i++) {
            ShopTrade trade = trades.get(i);
            int slot = (i / 2) * 9 + (i % 2 == 1 ? 5 : 0);
            for (int j = 0; j < 4; j++) {
                inventory.setItem(slot + j, trade.give[j]);
                inventory.setItem(slot + j + 5, trade.take[j]);
            }
        }

        return inventory;
    }
}
