package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;

import java.util.UUID;
import java.util.function.Consumer;

public class PlayerableShop extends Shop {
    protected UUID uuid = null;

    public PlayerableShop(Location location, EntityType entitytype) {
        super(location, entitytype);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setPowered(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("UUID", uuid);
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.uuid = UUID.fromString(yaml.getString("UUID"));
        });
    }
}
