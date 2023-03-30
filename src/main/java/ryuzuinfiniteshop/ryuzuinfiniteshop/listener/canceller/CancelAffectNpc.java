package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.canceller;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.PersistentUtil;

public class CancelAffectNpc implements Listener {
    //ダメージを無効化する
    @EventHandler(priority = EventPriority.HIGH)
    public void cancelDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        String id = PersistentUtil.getNMSStringTag(entity, "Shop");
        if (id == null) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void cancelBurn(EntityCombustEvent event) {
        Entity entity = event.getEntity();
        String id = PersistentUtil.getNMSStringTag(entity, "Shop");
        if (id == null) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void cancelBurn(CreeperPowerEvent event) {
        Entity entity = event.getEntity();
        String id = PersistentUtil.getNMSStringTag(entity, "Shop");
        if (id == null) return;
        event.setCancelled(true);
    }
}
