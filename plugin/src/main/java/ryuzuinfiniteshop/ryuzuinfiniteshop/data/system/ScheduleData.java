package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import lombok.Value;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Value
public class ScheduleData {
    long time;
    String id;
    Consumer<String> successProcess;
}
