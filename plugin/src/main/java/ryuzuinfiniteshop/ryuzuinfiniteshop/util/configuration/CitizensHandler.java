package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;

import java.util.HashMap;
import java.util.UUID;

public class CitizensHandler {
    private static final BiMap<UUID, Shop> npcMap = HashBiMap.create();

    @Getter
    private static boolean loaded = false;

    public static Shop getCitizensShop(Entity entity) {
        return loaded ? npcMap.get(entity.getUniqueId()) : null;
    }

    public static String getCitizensShopString(Entity entity) {
        if(!loaded) return null;
        if(!npcMap.containsKey(entity.getUniqueId())) return null;
        return npcMap.get(entity.getUniqueId()).getID();
    }

    public static boolean isCitizensNPC(UUID uuid) {
        return getByUniqueId(uuid) != null;
    }

    public static NPC getByUniqueId(UUID uuid) {
        return CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
    }

    public static boolean isNPC(Entity entity) {
        return CitizensAPI.getNPCRegistry().isNPC(entity);
    }

    public static UUID getNpcUUID(Entity entity) {
        return CitizensAPI.getNPCRegistry().getNPC(entity).getUniqueId();
    }

    public static void destoryNPC(Shop shop) {
        if(!isCitizensNPC(shop.getCitizen())) return;
        getByUniqueId(shop.getCitizen()).destroy();
        npcMap.remove(shop.getCitizen());
    }

    public static void despawnNPC(Shop shop) {
        if(!isCitizensNPC(shop.getCitizen())) return;
        getByUniqueId(shop.getCitizen()).despawn();
        npcMap.remove(shop.getCitizen());
    }

    public static UUID cloneNpc(Shop shop) {
        return getByUniqueId(shop.getCitizen()).clone().getUniqueId();
    }

    public static UUID createNPC(Shop shop) {
        if(isCitizensNPC(shop.getCitizen())) return shop.getCitizen();
        if(Bukkit.getOfflinePlayer(shop.getCitizen()).getName() == null) return null;
        return CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Bukkit.getOfflinePlayer(shop.getCitizen()).getName()).getUniqueId();
    }

    public static void setEquipment(UUID uuid, EquipmentSlot slot, ItemStack equipment) {
        int slotNumer = slot.ordinal();
        if(slotNumer == 1)
            slotNumer = 5;
        else if(slotNumer == 5)
            slotNumer = 1;
        getByUniqueId(uuid).getOrAddTrait(Equipment.class).set(slotNumer, equipment);
    }

    public static void respawn(Shop shop) {
        despawnNPC(shop);
        spawnNPC(shop);
    }

    public static Entity spawnNPC(Shop shop) {
        NPC citizenNpc = CitizensAPI.getNPCRegistry().getByUniqueId(shop.getCitizen());
        citizenNpc.spawn(shop.getLocation());
        if(!JavaUtil.isEmptyString(shop.getDisplayName())) citizenNpc.setName(shop.getDisplayName());
        citizenNpc.getOrAddTrait(LookClose.class).lookClose(true);
        npcMap.put(citizenNpc.getEntity().getUniqueId(), shop);
        return citizenNpc.getEntity();
    }

    public static void setInstance() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Citizens") == null) return;
        loaded = true;
    }
}
