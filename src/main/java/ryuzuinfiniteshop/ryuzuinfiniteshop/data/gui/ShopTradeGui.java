package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui;

import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;

import java.util.ArrayList;
import java.util.List;

public abstract class ShopTradeGui extends ShopGui {

    public ShopTradeGui(Shop shop , int page) {
        super(shop , page);
        setTrades(page);
    }

    public List<ShopTrade> getTrades() {
        return trades;
    }

    public ShopTrade getTrade(int number) {
        if(getShop().getTrades().size() <= number) return null;
        return getTrades().get(getTradeNumber(number));
    }

    public abstract int getTradeNumber(int slot);

    public abstract void setTrades(int page);

    public abstract boolean isDisplayItem(int slot);
}
