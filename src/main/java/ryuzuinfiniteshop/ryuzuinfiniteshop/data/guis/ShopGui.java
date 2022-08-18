package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class ShopGui {

    protected List<ShopTrade> trades = new ArrayList<>();
    private final int page;
    private final Shop shop;

    public ShopGui(Shop shop, int page) {
        this.shop = shop;
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public Shop getShop() {
        return shop;
    }

    public abstract Inventory getInventory(ShopHolder.ShopMode mode);
}
