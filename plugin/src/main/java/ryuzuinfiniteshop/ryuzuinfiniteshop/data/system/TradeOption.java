package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class TradeOption implements ConfigurationSerializable {
    boolean give;
    double money;
    int limit;
    int rate;

    public TradeOption() {
        this.give = false;
        this.money = 0;
        this.limit = 0;
        this.rate = 100;
    }

    public TradeOption(boolean give, double money, int limit, int rate) {
        this.give = give;
        this.money = money;
        this.limit = limit;
        this.rate = rate;
    }

    public boolean isNoData() {
        return money == 0 && limit == 0 && rate == 100;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("give", give);
        result.put("value", money);
        result.put("limit", limit);
        result.put("rate", rate);
        return result;
    }

    public static TradeOption deserialize(Map<String, Object> map) {
        return new TradeOption((boolean) map.get("give"), (int) map.get("value"), (int) map.get("limit"), (int) map.get("rate"));
    }
}
