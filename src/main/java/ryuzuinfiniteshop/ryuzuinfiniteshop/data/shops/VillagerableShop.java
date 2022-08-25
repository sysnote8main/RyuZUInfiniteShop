package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.JavaUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class VillagerableShop extends AgeableShop {
    protected Villager.Profession profession = Villager.Profession.FARMER;
    protected Villager.Type biome = Villager.Type.PLAINS;

    public VillagerableShop(Location location, EntityType entitytype) {
        super(location, entitytype);
        ((Villager) npc).setProfession(profession);
        ((ZombieVillager) npc).setVillagerProfession(profession);
        ((Villager) npc).setRecipes(new ArrayList<>());
    }

    public Villager.Profession getProfession() {
        return profession;
    }

    public Villager.Type getBiome() {
        return biome;
    }

    public void setProfession(Villager.Profession profession) {
        this.profession = profession;
        if (npc instanceof Villager)
            ((Villager) npc).setProfession(profession);
        else
            ((ZombieVillager) npc).setVillagerProfession(profession);
        ((Villager) npc).setRecipes(new ArrayList<>());
    }

    public void setBiome(Villager.Type villagertype) {
        this.biome = villagertype;
        if (npc instanceof Villager)
            ((Villager) npc).setVillagerType(villagertype);
        else
            ((ZombieVillager) npc).setVillagerType(villagertype);
    }

    public Villager.Profession getNextProfession() {
        int nextindex = Arrays.asList(Villager.Profession.values()).indexOf(profession) + 1;
        return nextindex == Villager.Profession.values().length ?
                Villager.Profession.values()[0] :
                Villager.Profession.values()[nextindex];
    }

    public Villager.Type getNextBiome() {
        int nextindex = Arrays.asList(Villager.Type.values()).indexOf(biome) + 1;
        return nextindex == Villager.Type.values().length ?
                Villager.Type.values()[0] :
                Villager.Type.values()[nextindex];
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("Profession", JavaUtil.getOrDefault(profession, Villager.Profession.FARMER).toString());
            yaml.set("Biome", JavaUtil.getOrDefault(biome, Villager.Type.PLAINS).toString());
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.profession = Villager.Profession.valueOf(yaml.getString("Profession" , "FARMER"));
            this.biome = Villager.Type.valueOf(yaml.getString("Biome" , "PLAINS"));
        });
    }

    public Material getJobBlockMaterial() {
        switch (profession) {
            case NITWIT:
                return Material.GREEN_STAINED_GLASS;
            case ARMORER:
                return Material.BLAST_FURNACE;
            case BUTCHER:
                return Material.SMOKER;
            case CARTOGRAPHER:
                return Material.CARTOGRAPHY_TABLE;
            case CLERIC:
                return Material.BREWING_STAND;
            case FARMER:
                return Material.COMPOSTER;
            case FISHERMAN:
                return Material.BARREL;
            case FLETCHER:
                return Material.FLETCHING_TABLE;
            case LEATHERWORKER:
                return Material.CAULDRON;
            case LIBRARIAN:
                return Material.LECTERN;
            case MASON:
                return Material.STONECUTTER;
            case SHEPHERD:
                return Material.LOOM;
            case TOOLSMITH:
                return Material.SMITHING_TABLE;
            case WEAPONSMITH:
                return Material.GRINDSTONE;
            case NONE:
            default:
                return Material.WHITE_STAINED_GLASS;
        }
    }

    public Material getBiomeImageMaterial() {
        switch (biome) {
            case DESERT:
                return Material.SAND;
            case JUNGLE:
                return Material.JUNGLE_LOG;
            case SAVANNA:
                return Material.TERRACOTTA;
            case SNOW:
                return Material.SNOW_BLOCK;
            case SWAMP:
                return Material.LILY_PAD;
            case TAIGA:
                return Material.DARK_OAK_LOG;
            case PLAINS:
            default:
                return Material.GRASS_BLOCK;
        }
    }
}
