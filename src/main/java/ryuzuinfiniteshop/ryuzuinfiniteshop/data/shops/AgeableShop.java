package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;

import java.util.function.Consumer;

public class AgeableShop extends Shop {
    protected boolean adult = true;

    public AgeableShop(Location location, EntityType entitytype) {
        super(location, entitytype);
        setAgeLook(adult);
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAgeLook(boolean look) {
        this.adult = look;
        if (look)
            ((Ageable) npc).setAdult();
        else
            ((Ageable) npc).setBaby();
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
            this.adult = yaml.getBoolean("Adult" , true);
        });
    }
}
