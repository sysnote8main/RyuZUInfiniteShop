package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopListHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//ショップエディターのメインページ
public class ShopListGui extends PageableGui {
    protected final HashMap<String, Shop> shops;

    public ShopListGui(int page, HashMap<String, Shop> shops) {
        super(page);
        this.shops = shops;
    }

    @Override
    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopListHolder(mode, this, shops), 9 * 6, ChatColor.DARK_BLUE + "ショップ一覧 ページ" + getPage());

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
                        ChatColor.YELLOW + "検索可否: " + (shop.isSearchable() ? ChatColor.GREEN + "可" : ChatColor.RED + "不可"),
                        ChatColor.YELLOW + "ロック: " + (shop.isLock() ? ChatColor.RED + "ロック" : ChatColor.GREEN + "アンロック"),
                        ChatColor.GREEN + "クリック: 取引画面を開く",
                        ChatColor.GREEN + "シフトクリック: 編集画面を開く"
                );
            else
                item = getDisplayItem(
                        shop.isLock(),
                        shop.getTrades().size() == 0 ? new ItemStack(Material.BARRIER) : shop.getTrades().get(0).getGiveItems()[0],
                        shop.getDisplayNameOrElseNone()
                );
            item = NBTUtil.setNMSTag(item, "Shop", shop.getID());
            inv.setItem(i, item);
        }

        return inv;
    }

    private ItemStack getDisplayItem(boolean lock, ItemStack item, String name, String... lore) {
        return ItemUtil.getNamedItem(item, name, lock, lore);
    }
}
