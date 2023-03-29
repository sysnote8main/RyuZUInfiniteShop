package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder;

import lombok.Getter;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.TradeSearchGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;

import java.util.LinkedHashMap;

@Getter
public class SeachTradeHolder extends PageableHolder {
    protected final LinkedHashMap<ShopTrade, Shop> trades;

    public SeachTradeHolder(ShopMode mode, TradeSearchGui gui, LinkedHashMap<ShopTrade, Shop> trades) {
        super(mode, gui);
        this.trades = trades;
    }

    public SeachTradeHolder(ShopMode mode, TradeSearchGui gui, LinkedHashMap<ShopTrade, Shop> trades, ModeHolder before) {
        super(mode, gui, before);
        this.trades = trades;
    }

    public int getMaxPage() {
        return (int) Math.ceil((double) trades.size() / 6);
    }

    @Override
    public TradeSearchGui getGui() {
        return (TradeSearchGui) super.getGui();
    }
}
