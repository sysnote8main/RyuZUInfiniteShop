package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.PageableGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopListHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.LocationUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.PersistentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//ショップエディターのメインページ
public class ShopListGui extends PageableGui {
    protected final String name;

    public ShopListGui(int page, String name) {
        super(page);
        this.name = name;
    }

    @Override
    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopListHolder(mode, this, name), 9 * 6, ChatColor.DARK_BLUE + "ショップ一覧 ページ" + getPage());

        HashMap<String, Shop> shops = ShopUtil.getSortedShops(mode, name);
        List<String> keys = new ArrayList<>(shops.keySet());
        for (int i = 0; i < Math.min(shops.size() - (getPage() - 1) * 54, 54); i++) {
            Shop shop = shops.get(keys.get(i + (getPage() - 1) * 54));
            ItemStack item;
            if (mode.equals(ShopMode.Edit))
                item = getDisplayItem(
                        shop.isLock(),
                        shop.getTrades().size() == 0 ? new ItemStack(Material.BARRIER) : shop.getTrades().get(0).getGiveItems()[0],
                        shop.getDisplayNameOrElseNone(),
                        ChatColor.YELLOW + "座標: " + shop.getID(),
                        ChatColor.GREEN + "クリック: ショップの編集画面を開く",
                        ChatColor.GREEN + "シフトクリック: NPCの位置までテレポート"
                );
            else
                item = getDisplayItem(
                        shop.isLock(),
                        shop.getTrades().size() == 0 ? new ItemStack(Material.BARRIER) : shop.getTrades().get(0).getGiveItems()[0],
                        shop.getDisplayNameOrElseNone()
                );
            item = PersistentUtil.setNMSTag(item, "Shop", shop.getID());
            inv.setItem(i, item);
        }

        return inv;
    }


    private ItemStack getDisplayItem(boolean lock, ItemStack item, String name, String... lore) {
        return ItemUtil.getNamedItem(item, name, lock, lore);
    }
}
