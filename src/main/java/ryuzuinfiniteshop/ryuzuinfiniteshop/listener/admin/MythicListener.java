package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.admin;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicReloadedEvent;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.JavaUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

import java.util.HashMap;
import java.util.stream.Collectors;


public class MythicListener implements Listener {
    private static final HashMap<ItemStack, String> items = new HashMap<>();

    @EventHandler
    public void onReload(MythicReloadedEvent event) {
        reload();
    }

    public void reload() {
        ShopUtil.reloadAllShopTradeInventory(() -> {
            items.clear();
            items.putAll(getMythicMobsInstance().getItemManager().getItems().stream().collect(Collectors.toMap(item -> BukkitAdapter.adapt(item.generateItemStack(1)), MythicItem::getInternalName)));
        });
    }

    public String getID(ItemStack item) {
        ItemStack copy = item.clone();
        copy.setAmount(1);
        return JavaUtil.getOrDefault(getMythicMobsInstance().getVolatileCodeHandler().getItemHandler().getNBTData(item).getString("MYTHIC_TYPE") , items.get(copy));
    }

    public MythicMob getMythicMob(String id) {
        return getMythicMobsInstance().getAPIHelper().getMythicMob(id);
    }

    public ItemStack getMythicItem(String id) {
        return getMythicItem(id, 1);
    }

    public ItemStack getMythicItem(String id , int amount) {
        ItemStack item = getMythicMobsInstance().getItemManager().getItemStack(id);
        item.setAmount(amount);
        return item;
    }

    public Entity spawnMythicMob(String id, Location location) {
        try {
            return getMythicMobsInstance().getAPIHelper().spawnMythicMob(id, location);
        } catch (InvalidMobTypeException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isMythicMob(Entity entity) {
        return getMythicMobsInstance().getAPIHelper().isMythicMob(entity);
    }

    public MythicMobs getMythicMobsInstance() {
        return MythicMobs.inst();
    }

    public MythicListener() {
        RyuZUInfiniteShop.getPlugin().getServer().getPluginManager().registerEvents(this, RyuZUInfiniteShop.getPlugin());
    }
}
