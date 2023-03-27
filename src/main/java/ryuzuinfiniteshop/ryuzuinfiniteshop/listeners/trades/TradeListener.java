package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.trades;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.trade.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ShopUtil;

public class TradeListener implements Listener {

    //ショップで購入する
    @EventHandler
    public void onTrade(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopTradeGui)) return;
        if (!ShopUtil.isTradeMode(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        int slot = event.getSlot();
        ShopTrade trade = ((ShopTradeGui) holder.getGui()).getTradeFromSlot(slot);

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
        ((ShopTradeGui) holder.getGui()).setTradeStatus(p , event.getView().getTopInventory());

        //イベントキャンセル
        event.setCancelled(true);
    }

    //ディスプレイをクリックしたときイベントをキャンセルする
    @EventHandler
    public void changeTradeLimit(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopTradeGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;
        int slot = event.getSlot();
        if (!((ShopTradeGui) holder.getGui()).isDisplaySlot(slot)) return;
        //イベントキャンセル
        event.setCancelled(true);

        Player p = (Player) event.getWhoClicked();
        ShopTrade trade = holder.getShop().getTrade(event.getClickedInventory(), slot);
        //トレードをアイテム化する
        if (event.isShiftClick()) return;
        if (!((ShopTradeGui) holder.getGui()).isConvertSlot(slot)) return;
        if (!(trade.getLimit() == 0 && event.isLeftClick())) {
            event.setCurrentItem(trade.changeLimit(event.isLeftClick() ? -1 : 1));
            SoundUtil.playClickShopSound(p);
        } else
            SoundUtil.playFailSound(p);
    }
}
