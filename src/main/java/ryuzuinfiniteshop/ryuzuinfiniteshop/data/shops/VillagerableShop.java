package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;

import java.io.File;
import java.util.function.Consumer;

public class VillagerableShop extends AgeableShop {
    protected Villager.Profession profession = Villager.Profession.FARMER;
    protected Villager.Type villagertype = Villager.Type.PLAINS;

    public VillagerableShop(File file) {
        super(file);
    }

    public VillagerableShop(Location location) {
        super(location);
    }

    public VillagerableShop(Location location, EntityType entitytype) {
        super(location, entitytype);
    }

    public Villager.Profession getProfession() {
        return profession;
    }

    public Villager.Type getVillagertype() {
        return villagertype;
    }

    public void setProfession(Villager.Profession profession) {
        this.profession = profession;
        if (npc instanceof Villager)
            ((Villager) getNPC()).setProfession(profession);
        else
            ((ZombieVillager) getNPC()).setVillagerProfession(profession);
    }

    public void setVillagerType(Villager.Type villagertype) {
        this.villagertype = villagertype;
        if (npc instanceof Villager)
            ((Villager) getNPC()).setVillagerType(villagertype);
        else
            ((ZombieVillager) getNPC()).setVillagerType(villagertype);

    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("Profession", profession);
            yaml.set("VillagerType", villagertype);
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.profession = Villager.Profession.valueOf(yaml.getString("Profession"));
            this.villagertype = Villager.Type.valueOf(yaml.getString("VillagerType"));
        });
    }
}
