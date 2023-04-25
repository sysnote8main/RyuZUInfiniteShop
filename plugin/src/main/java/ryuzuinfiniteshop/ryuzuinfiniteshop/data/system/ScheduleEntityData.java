package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;

import java.util.function.Consumer;

@Value
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
public class ScheduleEntityData extends ScheduleData {
    Consumer<Entity> successProcess;

    public ScheduleEntityData(long time, String id, Inventory inventory, Consumer<Entity> successProcess) {
        super(time, id, inventory);
        this.successProcess = successProcess;
    }
}
