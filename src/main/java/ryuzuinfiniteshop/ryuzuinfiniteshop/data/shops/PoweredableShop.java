package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.function.Consumer;

public class PoweredableShop extends Shop {
    protected boolean powered = false;

    public PoweredableShop(Location location, EntityType entitytype) {
        super(location, entitytype);
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
        ((Creeper) getNPC()).setPowered(powered);
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("Powered", powered);
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.powered = yaml.getBoolean("Powered");
        });
    }
}
