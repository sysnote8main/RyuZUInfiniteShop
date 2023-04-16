package com.github.ryuzu.sis.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.function.Consumer;


public interface IMythicListener {
    String getID(ItemStack item);

    void reload(Consumer<Runnable> consumer);

    boolean exsistsMythicMob(String id);

    Collection<String> getMythicMobs();

    ItemStack getMythicItem(String id, int amount);

    Entity spawnMythicMob(String id, Location location);

    boolean isMythicMob(Entity entity);
}
