package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.trades;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.LocationUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;

import java.util.HashMap;

public class TradeListener implements Listener {
    private static HashMap<String, Shop> shops = new HashMap<>();

    public static HashMap<String, Shop> getShops() {
        return shops;
    }

    public static Shop getShop(String id) {
        return shops.get(id);
    }

    public static void addShop(String id, Shop shop) {
        shops.put(id, shop);
    }

    public static void createNewShop(Player p) {
        Location loc = p.getLocation();
        if (shops.containsKey(LocationUtil.toStringFromLocation(loc))) {
            p.sendMessage(ChatColor.RED + "既にその場所にはショップが存在します");
            return;
        }
        new Shop(loc);
    }

    public static void createNewShop(Player p , EntityType entitytype) {
        Location loc = p.getLocation();
        if (shops.containsKey(LocationUtil.toStringFromLocation(loc))) {
            p.sendMessage(ChatColor.RED + "既にその場所にはショップが存在します");
            return;
        }
        new Shop(loc , entitytype);
    }

    //ショップで購入する
    @EventHandler
    public void onTrade(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (!(gui instanceof ShopTradeGui)) return;
        if (!ShopUtil.isTradeMode(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = shopholder.getShop();
        int slot = event.getSlot();
        ShopTrade trade = ((ShopTradeGui) gui).getTradeFromSlot(slot);

        if(trade == null) return;

        //取引
        int times = 1;
        switch (type) {
            case SHIFT_RIGHT:
            case SHIFT_LEFT:
                times = 10;
                break;
            case MIDDLE:
                times = 100;
                break;
        }
        trade.trade(p, times);

        //イベントキャンセル
        event.setCancelled(true);
    }
}
