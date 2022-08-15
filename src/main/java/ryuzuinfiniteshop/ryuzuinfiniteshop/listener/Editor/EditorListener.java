package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.Editor;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.TradeListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.PersistentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ShopUtil;

public class EditorListener implements Listener {
    //ショップの編集画面を開く
    @EventHandler
    public void openShopEditor(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player p = event.getPlayer();
        String id = PersistentUtil.getNMSStringTag(entity, "Shop");
        if (id == null) return;
        Shop shop = TradeListener.getShop(id);
        p.openInventory(shop.getPage(1).getInventory(ShopHolder.ShopMode.Edit));
    }
}
