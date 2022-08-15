package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.Editor;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.TradeListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ShopUtil;

//ショップのNPCの装備を変更する
public class ChangeEquipmentListener implements Listener {
    @EventHandler
    public void changeEquipment(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        if (event.getClickedInventory() != null) return;
        if (!ShopUtil.isShopEditInventory(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = TradeListener.getShop(shopholder.tags.get(0));
        int slot = event.getSlot();

        if((type.isRightClick() || type.isLeftClick()) && !type.isShiftClick()) {
            if(ShopUtil.getEquipmentsSlot().containsValue(slot)) {
                event.setCurrentItem(event.getCursor());
                shop.setEquipmentItem(event.getCursor() , ShopUtil.getEquipmentsSlot().get(slot));
                p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 2);
            }
        }
        event.setCancelled(true);
    }
}
