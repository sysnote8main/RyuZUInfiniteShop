package com.github.ryuzu.searchableinfiniteshop.v16older;

import com.github.ryuzu.searchableinfiniteshop.api.IMythicHandler;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicReloadedEvent;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class MythicHandlerV4_12_0 implements IMythicHandler, Listener {
    private static final HashMap<ItemStack, String> items = new HashMap<>();
    private final Consumer<Runnable> reloadProcessor;

    public MythicHandlerV4_12_0(JavaPlugin plugin, Consumer<Runnable> reloadProcessor) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.reloadProcessor = reloadProcessor;
    }

    @EventHandler
    public void onReload(MythicReloadedEvent event) {
        reload(reloadProcessor);
    }

    @Override
    public void reload(Consumer<Runnable> consumer) {
        consumer.accept(() -> {
            items.clear();
            items.putAll(getMythicMobsInstance().getItemManager().getItems().stream().collect(Collectors.toMap(item -> BukkitAdapter.adapt(item.generateItemStack(1)), MythicItem::getInternalName)));
        });
    }

    @Override
    public String getID(ItemStack item) {
        if (item == null || item.getType().isAir()) return null;
        ItemStack copy = item.clone();
        copy.setAmount(1);
        String id = getMythicMobsInstance().getVolatileCodeHandler().getItemHandler().getNBTData(copy).getString("MYTHIC_TYPE");
        return id == null || id.isEmpty() ? items.get(copy) : id;
    }

    @Override
    public boolean exsistsMythicMob(String id) {
        return getMythicMobsInstance().getAPIHelper().getMythicMob(id) != null;
    }

    @Override
    public Collection<String> getMythicMobs() {
        return getMythicMobsInstance().getMobManager().getMobNames();
    }

    @Override
    public ItemStack getMythicItem(String id, int amount) {
        ItemStack item = getMythicMobsInstance().getItemManager().getItemStack(id);
        if (item == null) return null;
        item.setAmount(amount);
        return item;
    }

    @Override
    public Entity spawnMythicMob(String id, Location location) {
        try {
            return getMythicMobsInstance().getAPIHelper().spawnMythicMob(id, location);
        } catch (InvalidMobTypeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isMythicMob(Entity entity) {
        return getMythicMobsInstance().getAPIHelper().isMythicMob(entity);
    }

    private MythicMobs getMythicMobsInstance() {
        return MythicMobs.inst();
    }
}
