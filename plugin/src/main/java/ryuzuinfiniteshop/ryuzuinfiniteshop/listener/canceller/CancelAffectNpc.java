package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.canceller;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.MerchantInventory;
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
    @EventHandler
    public void cancelCut(PlayerShearEntityEvent e) {
        cancel(e, e.getEntity());
    }
    @EventHandler
    public void cancelDye(PlayerInteractEntityEvent e) {
        cancel(e, e.getRightClicked());
    }
    @EventHandler
    public void cancelChangePose(PlayerLeashEntityEvent e) {
        cancel(e, e.getEntity());
    }


    private void cancel(Cancellable e, Entity entity) {
        String id = NBTUtil.getNMSStringTag(entity, "Shop");
        if (id == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void cancelOpenVillagerInventory(InventoryOpenEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.MERCHANT)) return;
        Villager villager = (Villager) event.getInventory().getHolder();
        if(villager == null) return;
        String id = NBTUtil.getNMSStringTag(villager, "Shop");
        if (id == null) return;
        event.setCancelled(true);
    }
}
