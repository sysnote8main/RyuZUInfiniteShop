package com.github.ryuzu.sis.v16newer;

import com.github.ryuzu.sis.api.IMythicHandler;
import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;


public class MythicHandlerV5_2_0 implements IMythicHandler {
    private static final HashMap<ItemStack, String> items = new HashMap<>();

//    @EventHandler
//    public void onReload(MythicReloadedEvent event) {
//        reload();
//    }

    @Override
    public void reload(Consumer<Runnable> consumer) {
//        consumer.accept(() -> {
//            items.clear();
//            items.putAll(getMythicMobsInstance().getItemManager().getItems().stream().collect(Collectors.toMap(item -> BukkitAdapter.adapt(item.generateItemStack(1)), MythicItem::getInternalName)));
//        });
    }

    @Override
    public String getID(ItemStack item) {
        if (item == null || item.getType().isAir()) return null;
        ItemStack copy = item.clone();
        copy.setAmount(1);
        return getMythicMobsInstance().getItemManager().getMythicTypeFromItem(copy);
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
        if(item == null) return null;
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

    private MythicBukkit getMythicMobsInstance() {
        return MythicBukkit.inst();
    }
}
