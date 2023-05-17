package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.TradesGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.SeachTradeHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

import java.util.*;

public class SearchTradeGui extends TradesGui {
    protected final LinkedHashMap<ShopTrade, Shop> searchedTrade;
    protected final List<Shop> shops;
    protected final Player player;

    public SearchTradeGui(int page, Player p, LinkedHashMap<ShopTrade, Shop> searchedTrade) {
        super(page);
        this.searchedTrade = searchedTrade;
        this.player = p;
        this.trades = new ArrayList<>(searchedTrade.keySet());
        this.shops = new ArrayList<>(searchedTrade.values());
    }

    @Override
    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new SeachTradeHolder(mode, this, player, searchedTrade), 9 * 6, ChatColor.DARK_BLUE + LanguageKey.INVENTORY_SEARCH_TRADE.getMessage(getPage()) + " " + LanguageKey.INVENTORY_PAGE.getMessage(String.valueOf(getPage())));

        for (int i = (getPage() - 1) * 6; i < getPage() * 6; i++) {
            if (i >= trades.size()) {
                for (int j = 0; j < 9; j++) {
                    inv.setItem((i % 6) * 9 + j, ShopTrade.getFilter());
                }
            } else {
                Shop shop = shops.get(i);
                ItemStack[] items = trades.get(i).getTradeItems(shop.getShopType(), shop.getID(), player);
                for (int j = 0; j < 9; j++) {
                    inv.setItem((i % 6) * 9 + j, items[j]);
                }
            }
        }

        return inv;
    }
}
