package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.canceller;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.SpawnShopData;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.entity.EntityUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;

public class CancelAffectNpc implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void cancelDamage(EntityDamageEvent e) {
        cancel(e, e.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void cancelBurn(EntityCombustEvent e) {
        cancel(e, e.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void cancelBurn(CreeperPowerEvent e) {
        cancel(e, e.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void bypassSpawn(EntitySpawnEvent e) {
        if(EntityUtil.entityQueue.isEmpty()) return;
        if(!EntityUtil.entityQueue.peek().contains(e)) return;
        e.setCancelled(false);
        EntityUtil.entityQueue.poll();
    }

    private void cancel(Cancellable e, Entity entity) {
        String id = NBTUtil.getNMSStringTag(entity, "Shop");
        if (id == null) return;
        e.setCancelled(true);
    }
}
