package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.function.Consumer;

public class PoweredableShop extends Shop {
    protected boolean powered;

    public PoweredableShop(Location location, EntityType entitytype) {
        super(location, entitytype);
        if(!mythicmob.isPresent())
            ((Creeper) npc).setPowered(powered);
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
        ((Creeper) npc).setPowered(powered);
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
            this.powered = yaml.getBoolean("Npc.Options.Powered" , false);
        });
    }
}
