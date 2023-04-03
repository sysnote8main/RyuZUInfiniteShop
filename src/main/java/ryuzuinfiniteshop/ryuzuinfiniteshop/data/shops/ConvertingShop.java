package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;

import java.util.function.Consumer;

public class ConvertingShop extends Shop {
    protected final ConfigurationSection config;

    public ConvertingShop(Location location, EntityType entitytype, ConfigurationSection config) {
        super(location, entitytype);
        this.config = config;
    }

    @Override
    public Consumer<YamlConfiguration> getSyncLoadYamlProcess() {
        return super.getSyncLoadYamlProcess().andThen(yaml -> {
            setNpcMeta(config.getConfigurationSection("object"));
            npc.setCustomName(config.getString("name", "").isEmpty() ? "" : ChatColor.GREEN + config.getString("name"));
        });
    }
}
