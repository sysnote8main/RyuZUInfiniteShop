package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.material.Colorable;

import java.util.Arrays;
import java.util.function.Consumer;

//狼、熱帯魚、羊などの染料と同じ色を付けられるもの
public class DyeableShop extends Shop {
    protected DyeColor color;

    public DyeableShop(Location location, EntityType entitytype) {
        super(location, entitytype);
        if(!mythicmob.isPresent())
            setColor(color);
    }

    public DyeColor getColor() {
        return color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
        if (npc instanceof Colorable) ((Colorable) npc).setColor(color);
        if (npc instanceof TropicalFish) ((TropicalFish) npc).setBodyColor(color);
        if (npc instanceof Wolf)  {
            ((Wolf) npc).setTamed(true);
            ((Wolf) npc).setCollarColor(color);
        }
    }

    public DyeColor getNextColor() {
        int nextindex = Arrays.asList(DyeColor.values()).indexOf(color) + 1;
        return nextindex == DyeColor.values().length ?
                DyeColor.values()[0] :
                DyeColor.values()[nextindex];
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("Npc.Options.Color", color.toString());
        });
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.color = DyeColor.valueOf(yaml.getString("Npc.Options.Color" , "WHITE"));
        });
    }

    public Material getColorMaterial() {
        switch (color) {
            case ORANGE:
                return Material.ORANGE_WOOL;
            case MAGENTA:
                return Material.MAGENTA_WOOL;
            case YELLOW:
                return Material.YELLOW_WOOL;
            case LIME:
                return Material.LIME_WOOL;
            case PINK:
                return Material.PINK_WOOL;
            case GRAY:
                return Material.GRAY_WOOL;
            case LIGHT_GRAY:
                return Material.LIGHT_GRAY_WOOL;
            case CYAN:
                return Material.CYAN_WOOL;
            case PURPLE:
                return Material.PURPLE_WOOL;
            case BLUE:
                return Material.BLUE_WOOL;
            case BROWN:
                return Material.BROWN_WOOL;
            case GREEN:
                return Material.GREEN_WOOL;
            case RED:
                return Material.RED_WOOL;
            case BLACK:
                return Material.BLACK_WOOL;
            case WHITE:
            default:
                return Material.WHITE_WOOL;
        }
    }
}
