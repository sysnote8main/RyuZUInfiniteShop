package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.editor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//ショップエディターのメインページ
public class ShopListGui extends ShopGui {

    public ShopListGui(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode, getShop(), this), 9 * 6, "ショップ一覧");

        HashMap<String, Shop> shops = ShopUtil.getSortedShops();
        List<String> keys = new ArrayList<>(shops.keySet());
        for (int i = 0; i < Math.min(shops.size() - (getPage() - 1) * 54, 54); i++) {
            Shop shop = shops.get(keys.get(i + (getPage() - 1) * 54));
            ItemStack item = shop.isLock() ?
                    ItemUtil.getNamedEnchantedItem(shop.getTrades().size() == 0 ? Material.BARRIER : shop.getTrades().get(1).getGiveItems()[0].getType(),
                            JavaUtil.getOrDefault(shop.getNPC().getCustomName(), ChatColor.YELLOW + "<none>"),
                            ChatColor.YELLOW + "座標: " + LocationUtil.toStringFromLocation(shop.getLocation()),
                            "シフトでNPCの位置までテレポート") :
                    ItemUtil.getNamedItem(shop.getTrades().size() == 0 ? Material.BARRIER : shop.getTrades().get(1).getGiveItems()[0].getType(),
                            JavaUtil.getOrDefault(shop.getNPC().getCustomName(), ChatColor.YELLOW + "<none>"),
                            ChatColor.YELLOW + "座標: " + LocationUtil.toStringFromLocation(shop.getLocation()),
                            "シフトでNPCの位置までテレポート");
            item = PersistentUtil.setNMSTag(item , "Shop" , shop.getID());
            inv.setItem(i, item);
        }

        return inv;
    }
}
