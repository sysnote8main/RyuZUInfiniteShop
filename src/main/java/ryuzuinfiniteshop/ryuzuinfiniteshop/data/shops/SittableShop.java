package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sittable;

import java.util.function.Consumer;

public class SittableShop extends Shop {
    @Getter
    protected boolean sitting;

    public SittableShop(Location location, EntityType entitytype, ConfigurationSection config) {
        super(location, entitytype, config);
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
        if(npc == null) return;
        ((Sittable) npc).setSitting(sitting);
//        NBTBuilder.setIsBaby(!look);
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("Npc.Options.Sitting", sitting);
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.sitting = yaml.getBoolean("Npc.Options.Sitting" , false);
        });
    }

    @Override
    public void respawnNPC() {
        super.respawnNPC();
        setSitting(sitting);
    }
}
