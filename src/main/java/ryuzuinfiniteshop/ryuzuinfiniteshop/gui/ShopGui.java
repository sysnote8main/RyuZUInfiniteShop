package ryuzuinfiniteshop.ryuzuinfiniteshop.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.ShopTrade;

import java.util.ArrayList;
import java.util.List;

public abstract class ShopGui {

    protected List<ShopTrade> trades = new ArrayList<>();
    private final int page;
    private final Shop shop;

    public ShopGui (Shop shop , int page) {
        this.shop = shop;
        this.page = page;
        setTrades(page);
    }

    public int getPage() {
        return page;
    }
    public Shop getShop() {
        return shop;
    }

    public abstract void setTrades(int page);

    public List<ShopTrade> getTrades() {
        return trades;
    }

    public abstract ShopTrade getTrade(int slot);

    public Inventory getInventory(ShopHolder.ShopMode mode) {
        return getInventory(mode);
    }

    public abstract boolean existPage(int page);

}
