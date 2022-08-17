package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.trades;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;

public class CancelItemMoveListener implements Listener {
    //ショップのラインナップを変更時以外はインベントリ内のアイテム移動を禁止するように
    @EventHandler
    public void onEdit(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(event.getView().getTopInventory())) return;
        if (!ShopUtil.isTradeMode(event)) return;

        //キャンセルイベント
        event.setCancelled(true);
    }

    //ショップのラインナップを変更時以外はインベントリ内のアイテム移動を禁止するように
    @EventHandler
    public void onEdit(InventoryDragEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event.getInventory());
        if (gui == null) return;
        if (!event.getInventory().equals(event.getView().getTopInventory())) return;
        if (!ShopUtil.isTradeMode(event.getInventory())) return;

        //キャンセルイベント
        event.setCancelled(true);
    }
}
