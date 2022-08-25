package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.JavaUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.LocationUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

//ショップエディターのメインページ
public class ShopListGui extends ShopGui {

    public ShopListGui(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode, getShop(), this), 9, "ショップ一覧");

        HashMap<String, Shop> shops = ShopUtil.getSortedShops();
        List<String> keys = new ArrayList<>(shops.keySet());
        for (int i = 0; i < Math.min(shops.size() - (getPage() - 1) * 54, 54); i++) {
            Shop shop = shops.get(keys.get(i + (getPage() - 1) * 54));
            inv.setItem(i,
                    ItemUtil.getNamedItem(shop.getTrades().size() == 0 ? Material.BARRIER : shop.getTrades().get(1).give[0].getType(),
                            JavaUtil.getOrDefault(shop.getNPC().getCustomName(), ChatColor.YELLOW + "<none>"),
                    ChatColor.YELLOW + "座標: " + LocationUtil.toStringFromLocation(shop.getLocation()),
                            "シフトでNPCの位置までテレポート"
                    ));
        }

        return inv;
    }
}
