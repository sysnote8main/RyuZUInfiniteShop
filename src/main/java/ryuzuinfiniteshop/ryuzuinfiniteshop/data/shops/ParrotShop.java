package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Villager;

import java.util.Arrays;
import java.util.function.Consumer;

public class ParrotShop extends Shop {
    protected Parrot.Variant color = Parrot.Variant.RED;

    public ParrotShop(Location location, EntityType entitytype) {
        super(location, entitytype);
        ((Parrot) npc).setVariant(color);
    }

    public Parrot.Variant setColor() {
        return color;
    }

    public void setColor(Parrot.Variant color) {
        this.color = color;
        ((Parrot) npc).setVariant(color);
    }

    public Parrot.Variant getNextColor() {
        int nextindex = Arrays.asList(Parrot.Variant.values()).indexOf(color) + 1;
        return nextindex == Parrot.Variant.values().length ?
                Parrot.Variant.values()[0] :
                Parrot.Variant.values()[nextindex];
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("Color", color.toString());
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.color = Parrot.Variant.valueOf(yaml.getString("Color" , "RED"));
        });
    }

    public Material getColorMaterial() {
        switch (color) {
            case BLUE:
                return Material.BLUE_WOOL;
            case GREEN:
                return Material.GREEN_WOOL;
            case CYAN:
                return Material.CYAN_WOOL;
            case GRAY:
                return Material.GRAY_WOOL;
            case RED:
            default:
                return Material.RED_WOOL;
        }
    }
}
