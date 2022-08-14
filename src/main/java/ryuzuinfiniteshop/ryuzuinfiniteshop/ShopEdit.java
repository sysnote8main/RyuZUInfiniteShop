package ryuzuinfiniteshop.ryuzuinfiniteshop;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.PersistentUtil;

public class ShopEdit implements Listener {
    @EventHandler
    public void openShopEditor(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player p = event.getPlayer();
        String id = PersistentUtil.getNMSStringTag(entity , "Shop");
        if(id == null) return;
        Shop shop = ShopSystem.getShop(id);
        p.openInventory(shop.getPage(1).getInventory(ShopHolder.ShopMode.Edit));
    }
}
