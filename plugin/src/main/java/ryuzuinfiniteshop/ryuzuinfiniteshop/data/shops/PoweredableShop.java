package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creeper;

import java.util.function.Consumer;

public class PoweredableShop extends Shop {
    protected boolean powered;

    public PoweredableShop(Location location, String entityType, ConfigurationSection config) {
        super(location, entityType, config);
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
        if (npc == null) return;
        ((Creeper) npc).setPowered(powered);
//        NBTBuilder.setPowered(powered);
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("Npc.Options.Powered", powered);
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.powered = yaml.getBoolean("Npc.Options.Powered", false);
            setPowered(powered);
        });
    }

    @Override
    public void respawnNPC() {
        super.respawnNPC();
        if (isEditableNpc()) setPowered(powered);
    }
}
