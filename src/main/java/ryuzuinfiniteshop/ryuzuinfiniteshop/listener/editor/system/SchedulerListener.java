package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ScheduleData;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class SchedulerListener implements Listener {
    private static final HashMap<UUID, ScheduleData> schedulers = new HashMap<>();

    public static void setSchedulers(Player p, String id, Consumer<String> successProcess) {
        schedulers.put(p.getUniqueId(), new ScheduleData(System.currentTimeMillis(), id, successProcess));
        Bukkit.getScheduler().runTaskLater(RyuZUInfiniteShop.getPlugin(), p::closeInventory, 1L);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void changeDisplay(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (!schedulers.containsKey(p.getUniqueId())) return;
        if ((double) (System.currentTimeMillis() - schedulers.get(p.getUniqueId()).getTime()) / 1000 > 20) return;
        ScheduleData data = schedulers.get(p.getUniqueId());
        Shop shop = ShopUtil.getShop(data.getId());
        schedulers.remove(p.getUniqueId());
        event.setCancelled(true);
        if (shop == null) {
            if(data.getId().equals("search"))
                data.getSuccessProcess().accept(event.getMessage());
            else {
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "ショップが見つかりませんでした");
                SoundUtil.playFailSound(p);
            }
        } else if (event.getMessage().equalsIgnoreCase("Cancel")) {
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "キャンセルしました");
            SoundUtil.playClickShopSound(p);
        } else {
            data.getSuccessProcess().accept(event.getMessage());
        }
    }
}
