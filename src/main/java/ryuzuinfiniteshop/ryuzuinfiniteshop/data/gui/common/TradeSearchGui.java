package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.TradesGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.SeachTradeHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

import java.util.*;

public class TradeSearchGui extends TradesGui {
    protected final LinkedHashMap<ShopTrade, Shop> searchedTrade;
    protected final List<Shop> shops;
    protected final Player player;

    public TradeSearchGui(int page, Player p, LinkedHashMap<ShopTrade, Shop> searchedTrade) {
        super(page);
        this.searchedTrade = searchedTrade;
        this.player = p;
        this.trades = new ArrayList<>(searchedTrade.keySet());
        this.shops = new ArrayList<>(searchedTrade.values());
    }

    @Override
    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new SeachTradeHolder(mode, this, player, searchedTrade), 9 * 6, ChatColor.DARK_BLUE + "トレード サーチ ページ" + getPage());

        for (int i = (getPage() - 1) * 6; i < getPage() * 6; i++) {
            if (i >= trades.size()) {
                for (int j = 0; j < 9; j++) {
                    inv.setItem((i % 6) * 9 + j, ItemUtil.getNamedItem(Material.BLACK_STAINED_GLASS_PANE, ""));
                }
            } else {
                Shop shop = shops.get(i);
                for (int j = 0; j < 9; j++) {
                    inv.setItem((i % 6) * 9 + j, trades.get(i).getTradeItems(shop.getShopType(), shop.getID(), player)[j]);
                }
            }
        }

        return inv;
    }
}
