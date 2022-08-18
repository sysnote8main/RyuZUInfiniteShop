package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.function.Consumer;

public class AgeableShop extends Shop {
    protected boolean adult = true;

    public AgeableShop(File file) {
        super(file);
    }

    public AgeableShop(Location location) {
        super(location);
    }

    public AgeableShop(Location location, EntityType entitytype) {
        super(location, entitytype);
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAgeLock(boolean look) {
        this.adult = look;
        if (look)
            ((Ageable) getNPC()).setAdult();
        else
            ((Ageable) getNPC()).setBaby();
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("Adult", adult);
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.adult = yaml.getBoolean("Adult");
        });
    }
}
