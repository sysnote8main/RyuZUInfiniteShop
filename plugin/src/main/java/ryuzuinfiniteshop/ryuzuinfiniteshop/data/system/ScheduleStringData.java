package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import lombok.*;
import org.bukkit.inventory.Inventory;

import java.util.function.Consumer;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScheduleStringData extends ScheduleData {
    Consumer<String> successProcess;

    public ScheduleStringData(long time, String id, Inventory inventory, Consumer<String> successProcess) {
        super(time, id, inventory);
        this.successProcess = successProcess;
    }
}
