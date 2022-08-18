package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.trades;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;

public class TradeListener implements Listener {

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
        Inventory inv = event.getView().getTopInventory();
        ShopHolder shopholder = (ShopHolder) inv.getHolder();
        Shop shop = shopholder.getShop();
        int slot = event.getSlot();
        ShopTrade trade = ((ShopTradeGui) gui).getTradeFromSlot(slot);

        if (trade == null) return;

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

        //ステータスの更新
        ShopUtil.setTradeStatus(p, inv, shop, (ShopTradeGui) gui);

        //イベントキャンセル
        event.setCancelled(true);
    }
}
