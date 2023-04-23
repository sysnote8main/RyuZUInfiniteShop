package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import lombok.Value;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ModeGui;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Value
public class ScheduleData {
    long time;
    String id;
    Consumer<String> successProcess;
    Inventory inventory;
}
