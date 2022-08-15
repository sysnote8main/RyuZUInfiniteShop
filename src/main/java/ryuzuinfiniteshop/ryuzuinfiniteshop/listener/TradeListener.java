package ryuzuinfiniteshop.ryuzuinfiniteshop.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ShopUtil;

import java.util.HashMap;

public class TradeListener implements Listener {
    private static HashMap<String , Shop> shops = new HashMap<>();

    public static Shop getShop(String id) {
        return shops.get(id);
    }

    //ショップで購入する
    @EventHandler
    public void onTrade(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        if (event.getClickedInventory() == null) return;
        if(!ShopUtil.isShopTradeInventory(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = TradeListener.getShop(shopholder.tags.get(0));

        //取引
        int times = 1;
        switch (type) {
            case SHIFT_RIGHT:
            case SHIFT_LEFT:
                p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 2);
                times = 10;
                break;
            case MIDDLE:
                times = 100;
                break;
        }
        shop.getPage(shopholder.page).getTrade(shopholder.page).trade(p, times);
        event.setCancelled(true);
    }
}
