package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui;

import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.JavaUtil;

import java.util.List;

public abstract class ShopTradeGui extends ShopGui {

    public ShopTradeGui(Shop shop, int page) {
        super(shop, page);
        if(!(!existTrades() && shop.getTradePageCount() + 1 == getPage() && shop.getTrades().size() % shop.getLimitSize() == 0)) setTrades();
    }

    public List<ShopTrade> getTrades() {
        return trades;
    }

    public ShopTrade getTrade(int number) {
        if (getTrades().size() < number) return null;
        if (number <= 0) return null;
        return getTrades().get(number - 1);
    }

    public void setTrades() {
        this.trades = JavaUtil.splitList(getShop().getTrades() , getShop().getLimitSize())[getPage() - 1];
    }

    public boolean existTrades() {
        return getTrades().size() > (getPage() - 2) * getShop().getLimitSize();
    }

    public abstract ShopTrade getTradeFromSlot(int slot);

    public abstract boolean isDisplayItem(int slot);
}
