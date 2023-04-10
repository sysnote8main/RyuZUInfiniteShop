package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ScheduleData;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;
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
        if(FileUtil.isSaveBlock(p)) return;
        ScheduleData data = schedulers.get(p.getUniqueId());
        Shop shop = ShopUtil.getShop(data.getId());
        schedulers.remove(p.getUniqueId());
        event.setCancelled(true);
        if (event.getMessage().equalsIgnoreCase("Cancel")) {
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + LanguageKey.MESSAGE_CANCELLED.getMessage());
            SoundUtil.playClickShopSound(p);
        } else {
            if (shop == null) {
                if(data.getId().equals("search"))
                    Bukkit.getScheduler().runTask(RyuZUInfiniteShop.getPlugin(), () -> data.getSuccessProcess().accept(event.getMessage()));
                else {
                    p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.ERROR_SHOP_NOT_FOUND.getMessage());
                    SoundUtil.playFailSound(p);
                }
            } else
                Bukkit.getScheduler().runTask(RyuZUInfiniteShop.getPlugin(), () -> data.getSuccessProcess().accept(event.getMessage()));
        }
    }
}
