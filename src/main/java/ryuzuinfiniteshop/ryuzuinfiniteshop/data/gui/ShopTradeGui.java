package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui;

import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.JavaUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class ShopTradeGui extends ShopGui {

    public ShopTradeGui(Shop shop, int page) {
        super(shop, page);
        if(shop.getTrades().size() != 0) setTrades();
    }

    public List<ShopTrade> getTrades() {
        return trades;
    }

    public ShopTrade getTrade(int number) {
        if (getTrades().size() <= number) return null;
        if (number < 0) return null;
        return getTrades().get(number);
    }

    public void setTrades() {
        this.trades = JavaUtil.splitList(getShop().getTrades() , getShop().getLimitSize())[getPage() - 1];
    }

    public boolean existTrade() {
        return getTrades().size() > (getPage() - 1) * getShop().getLimitSize();
    }

    public abstract int getTradeNumber(int slot);

    public abstract boolean isDisplayItem(int slot);
}
