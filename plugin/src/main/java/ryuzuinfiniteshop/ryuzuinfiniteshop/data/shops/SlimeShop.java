package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;

import java.util.function.Consumer;

public class SlimeShop extends Shop {
    @Getter
    protected int size;

    public SlimeShop(Location location, String entityType, ConfigurationSection config) {
        super(location, entityType, config);
    }

    public void setSize(int size) {
        this.size = size;
        if (npc == null) return;
        ((Slime) npc).setSize(size);
//        NBTBuilder.setIsBaby(!look);
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("Npc.Options.Size", size);
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.size = yaml.getInt("Npc.Options.Size", 1);
        });
    }

    @Override
    public void respawnNPC() {
        super.respawnNPC();
        setSize(size);
    }
}
