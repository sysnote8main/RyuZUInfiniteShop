package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.admin;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicReloadedEvent;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.stream.Collectors;

public class MythicListener implements Listener {
    private static HashMap<ItemStack, String> items = new HashMap<>();

    @EventHandler
    public void onReload(MythicReloadedEvent event) {
        reload();
    }

    public static void reload() {
        items.clear();
        items.putAll(MythicMobs.inst().getItemManager().getItems().stream().collect(Collectors.toMap(item -> BukkitAdapter.adapt(item.generateItemStack(1)), MythicItem::getInternalName)));
    }

    public static String getID(ItemStack item) {
        ItemStack copy = item.clone();
        return items.getOrDefault(copy , null);
    }
}
