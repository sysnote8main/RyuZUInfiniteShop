package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.TradesGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.SeachTradeHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;

import java.util.*;

public class TradeSearchGui extends TradesGui {
    protected final LinkedHashMap<ShopTrade, Shop> searchedTrade;
    protected final List<Shop> shops;

    public TradeSearchGui(int page, LinkedHashMap<ShopTrade, Shop> searchedTrade) {
        super(page);
        this.searchedTrade = searchedTrade;
        this.trades = new ArrayList<>(searchedTrade.keySet());
        this.shops = new ArrayList<>(searchedTrade.values());
    }

    @Override
    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new SeachTradeHolder(mode, this, searchedTrade), 9 * 6, "トレード サーチ ページ" + getPage());

        for(int i = (getPage() - 1) * 6 ; i < Math.min(getPage() * 6 , trades.size()) ; i++) {
            Shop shop = shops.get(i);
            for(int j = 0 ; j < 9 ; j++) {
                inv.setItem(i * 9 + j , trades.get(i).getTradeItems(shop.getShopType(), shop.getID())[j]);
            }
        }

        return inv;
    }
}
