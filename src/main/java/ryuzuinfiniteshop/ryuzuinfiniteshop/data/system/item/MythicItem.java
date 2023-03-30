package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item;

import lombok.Value;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.MythicInstanceProvider;

import java.util.LinkedHashMap;
import java.util.Map;

@Value
public class MythicItem implements ConfigurationSerializable {
    String id;
    int amount;

    public ItemStack convertItemStack() {
        return MythicInstanceProvider.getInstance().getMythicItem(id, amount);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("amount", amount);
        return result;
    }

    public static MythicItem deserialize(Map<String, Object> map) {
        return new MythicItem(map.get("id").toString(), Integer.parseInt(map.get("amount").toString()));
    }
}
