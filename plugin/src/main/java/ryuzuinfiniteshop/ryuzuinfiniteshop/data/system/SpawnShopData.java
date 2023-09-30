package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import lombok.Value;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Optional;


@Value
public class SpawnShopData {
    Location location;
    EntityType entityType;

    public boolean contains(EntitySpawnEvent e) {
        return e.getLocation().equals(location) && e.getEntityType().equals(entityType);
    }
}
