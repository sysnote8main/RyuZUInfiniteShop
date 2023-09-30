package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Snowman;

import java.util.function.Consumer;

@Getter

public class SnowmanShop extends Shop {
    protected boolean derp;

    public SnowmanShop(Location location, String entityType, ConfigurationSection config) {
        super(location, entityType, config);
    }

    public void setDerp(boolean derp) {
        this.derp = derp;
        if (npc == null) return;
        ((Snowman) npc).setDerp(derp);
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> yaml.set("Npc.Options.Derp", derp));
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.derp = yaml.getBoolean("Npc.Options.Derp", false);
        });
    }

    @Override
    public void respawnNPC() {
        super.respawnNPC();
        if (isEditableNpc()) setDerp(derp);
    }
}
