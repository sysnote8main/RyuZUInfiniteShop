package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor;

import lombok.Getter;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;

import java.util.List;

@Getter
public abstract class ShopGui extends TradesGui {
    protected final Shop shop;

    public ShopGui(Shop shop, int page) {
        super(page);
        this.shop = shop;
    }

    @Override
    public List<ShopTrade> getTrades() {
        return shop.getTrades(page);
    }
}
