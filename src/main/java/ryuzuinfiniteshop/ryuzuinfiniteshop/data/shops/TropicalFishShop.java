package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish;

import java.util.function.Consumer;

public class TropicalFishShop extends Shop {
    protected DyeColor bodyColor;
    protected DyeColor patternColor;
    protected TropicalFish.Pattern pattern;

    public TropicalFishShop(Location location, EntityType entitytype) {
        super(location, entitytype);
    }

    public TropicalFishShop(Location location, EntityType entitytype, ConfigurationSection config) {
        super(location, entitytype, config);
    }

    public void setBodyColor(DyeColor bodyColor) {
        this.bodyColor = bodyColor;
        if (npc == null) return;
        ((TropicalFish) npc).setBodyColor(bodyColor);
    }

    public void setPatternColor(DyeColor patternColor) {
        this.patternColor = patternColor;
        if (npc == null) return;
        ((TropicalFish) npc).setBodyColor(patternColor);
    }

    public void setPattern(TropicalFish.Pattern pattern) {
        this.pattern = pattern;
        if (npc == null) return;
        ((TropicalFish) npc).setPattern(pattern);
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("Npc.Options.BodyColor", bodyColor.name());
            yaml.set("Npc.Options.PatternColor", patternColor.name());
            yaml.set("Npc.Options.Pattern", pattern.name());
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.bodyColor = DyeColor.valueOf(yaml.getString("Npc.Options.BodyColor", "RED"));
            this.patternColor = DyeColor.valueOf(yaml.getString("Npc.Options.PatternColor", "RED"));
            this.pattern = TropicalFish.Pattern.valueOf(yaml.getString("Npc.Options.Pattern", "KOB"));
        });
    }

    @Override
    public void respawnNPC() {
        super.respawnNPC();
        setBodyColor(bodyColor);
        setPatternColor(patternColor);
        setPattern(pattern);
    }
}
