package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ModeGui;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleData {
    long time;
    String id;
    Inventory inventory;
}
