package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;

//ショップのラインナップを変更時以外はインベントリ内のアイテム移動を禁止するように
public class CancelItemMoveListener implements Listener {

    @EventHandler
    public void cancelMoveToOtherInventory(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event.getView().getTopInventory());
        if (gui == null) return;
        if (event.getClickedInventory() == null) return;
        if (!event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) return;
        if (ShopUtil.isEditMode(event.getView().getTopInventory()) && gui instanceof ShopTradeGui) return;

        //キャンセルイベント
        event.setCancelled(true);
    }

    @EventHandler
    public void cancelClickInShop(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (event.getAction().equals(InventoryAction.CLONE_STACK)) return;
        if (ShopUtil.isEditMode(event.getView().getTopInventory()) && gui instanceof ShopTradeGui) return;

        //キャンセルイベント
        event.setCancelled(true);
    }

    @EventHandler
    public void cancelDragToOtherInventory(InventoryDragEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event.getInventory());
        if (gui == null) return;
        if (ShopUtil.isEditMode(event.getInventory()) && gui instanceof ShopTradeGui) return;
        if (event.getRawSlots().stream().noneMatch(i -> i < event.getInventory().getSize())) return;

        //キャンセルイベント
        event.setCancelled(true);
    }
}
