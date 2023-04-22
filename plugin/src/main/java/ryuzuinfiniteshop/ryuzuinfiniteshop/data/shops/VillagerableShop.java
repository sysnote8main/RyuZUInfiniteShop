package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.JavaUtil;

import java.util.Arrays;
import java.util.function.Consumer;

@Getter
public class VillagerableShop extends AgeableShop {
    protected Villager.Profession profession;
    protected Villager.Type biome;
    protected int level = 1;

    public VillagerableShop(Location location, String entityType, ConfigurationSection config) {
        super(location, entityType, config);
    }

    public void setProfession(Villager.Profession profession) {
        this.profession = profession;
        if(npc == null) return;
        if (npc instanceof Villager)
            ((Villager) npc).setProfession(profession);
        else
            ((ZombieVillager) npc).setVillagerProfession(profession);
//        NBTBuilder.setVillagerData(profession, biome, level);
    }

    public void setBiome(Villager.Type villagertype) {
        this.biome = villagertype;
        if(npc == null) return;
        if (npc instanceof Villager)
            ((Villager) npc).setVillagerType(villagertype);
        else
            ((ZombieVillager) npc).setVillagerType(villagertype);
//        NBTBuilder.setVillagerData(profession, biome, level);
    }

    public void setLevel(int level) {
        this.level = level;
        if(npc == null) return;
        if (npc instanceof Villager)
            ((Villager) npc).setVillagerLevel(level);
//        NBTBuilder.setVillagerData(profession, biome, level);
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
            yaml.set("Npc.Options.Profession", JavaUtil.getOrDefault(profession, Villager.Profession.FARMER).toString());
            if(RyuZUInfiniteShop.VERSION < 14) return;
            yaml.set("Npc.Options.Biome", JavaUtil.getOrDefault(biome, Villager.Type.PLAINS).toString());
            yaml.set("Npc.Options.Level", level);
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.profession = Villager.Profession.valueOf(yaml.getString("Npc.Options.Profession", "NONE"));
            if(RyuZUInfiniteShop.VERSION < 14) return;
            this.biome = Villager.Type.valueOf(yaml.getString("Npc.Options.Biome", "PLAINS"));
            this.level = yaml.getInt("Npc.Options.Level", 1);
        });
    }

    @Override
    public void respawnNPC() {
        super.respawnNPC();
        setProfession(profession);
        if(RyuZUInfiniteShop.VERSION < 14) return;
        setBiome(biome);
        setLevel(level);
    }

    public Material getJobBlockMaterial() {
        if(RyuZUInfiniteShop.VERSION >= 14) {
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
        } else {
            switch (profession.name()) {
                case "NORMAL":
                    return Material.DIRT;
                case "FARMER":
                    return Material.WHEAT;
                case "LIBRARIAN":
                    return Material.BOOKSHELF;
                case "PRIEST":
                    return Material.ROTTEN_FLESH;
                case "BLACKSMITH":
                    return Material.ANVIL;
                case "BUTCHER":
                    return Material.PORKCHOP;
                case "NITWIT":
                    return Material.SLIME_BALL;
                default:
                    return Material.STONE;
            }
        }
    }

    public Material getBiomeMaterial() {
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

    public Material getLevelMaterial() {
        switch (level) {
            case 2:
                return Material.IRON_INGOT;
            case 3:
                return Material.GOLD_INGOT;
            case 4:
                return Material.EMERALD;
            case 5:
                return Material.DIAMOND;
            case 1:
            default:
                return Material.COBBLESTONE;
        }
    }
}
