package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.Inventory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleData {
    long time;
    String id;
    Inventory inventory;
}
