package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;

import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.LookClose;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;

import java.util.UUID;

public class CitizensHandler {
    @Getter
    private static boolean loaded = false;

    public static boolean isCitizensNPC(UUID uuid) {
        return CitizensAPI.getNPCRegistry().getByUniqueId(uuid) != null;
    }

    public static boolean isNPC(Entity entity) {
        return CitizensAPI.getNPCRegistry().isNPC(entity);
    }

    public static void removeNPC(UUID uuid) {
        if(!isCitizensNPC(uuid)) return;
        getByUniqueId(uuid).destroy();
    }

    public static void despawnNPC(UUID uuid) {
        if(!isCitizensNPC(uuid)) return;
        getByUniqueId(uuid).despawn();
    }

    public static UUID createNPC(UUID uuid, boolean load) {
        if(load)
            return getByUniqueId(uuid).getUniqueId();
        else
            return CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Bukkit.getOfflinePlayer(uuid).getName()).getUniqueId();
    }

    public static UUID getNpcUUID(Entity entity) {
        return CitizensAPI.getNPCRegistry().getNPC(entity).getUniqueId();
    }

    public static NPC getByUniqueId(UUID uuid) {
        return CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
    }

    public static void setEquipment(UUID uuid, EquipmentSlot slot, ItemStack equipment) {
        getByUniqueId(uuid).getOrAddTrait(Equipment.class).set(slot.ordinal(), equipment);
    }

    public static Entity spawnNPC(UUID uuid, Shop shop) {
        NPC citizenNpc = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
        citizenNpc.spawn(shop.getLocation());
        if(!JavaUtil.isEmptyString(shop.getDisplayName())) citizenNpc.setName(shop.getDisplayName());
        citizenNpc.addTrait(LookClose.class);
        return citizenNpc.getEntity();
    }

    public static void setInstance() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Citizens") == null) return;
        loaded = true;
    }
}
