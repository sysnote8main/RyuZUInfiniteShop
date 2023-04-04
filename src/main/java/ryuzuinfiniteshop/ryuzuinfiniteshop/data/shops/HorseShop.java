package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Parrot;
import org.bukkit.material.Colorable;

import java.util.Arrays;
import java.util.function.Consumer;

public class HorseShop extends AgeableShop {
    protected Horse.Color color = Horse.Color.WHITE;
    protected Horse.Style style = Horse.Style.NONE;

    public HorseShop(Location location, EntityType entitytype) {
        super(location, entitytype);
    }

    public HorseShop(Location location, EntityType entitytype, ConfigurationSection config) {
        super(location, entitytype, config);
    }

    public void setStyle(Horse.Style style) {
        this.style = style;
        ((Horse) npc).setStyle(style);
    }

    public void setColor(Horse.Color color) {
        this.color = color;
        ((Horse) npc).setColor(color);
//        NBTBuilder.setVariant(color.name().toLowerCase(), style.name().toLowerCase());
    }

    public Horse.Style getNextStyle() {
        int nextindex = Arrays.asList(Horse.Style.values()).indexOf(style) + 1;
        return nextindex == Horse.Style.values().length ?
                Horse.Style.values()[0] :
                Horse.Style.values()[nextindex];
    }

    public Horse.Color getNextColor() {
        int nextindex = Arrays.asList(Horse.Color.values()).indexOf(color) + 1;
        return nextindex == Horse.Color.values().length ?
                Horse.Color.values()[0] :
                Horse.Color.values()[nextindex];
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> {
            yaml.set("Npc.Options.Color", color.toString());
            yaml.set("Npc.Options.Style", style.toString());
        });
    }

    @Override
    public Consumer<YamlConfiguration> getSyncLoadYamlProcess() {
        return super.getSyncLoadYamlProcess().andThen(yaml -> {
            this.color = Horse.Color.valueOf(yaml.getString("Npc.Options.Color" , "WHITE"));
            this.style = Horse.Style.valueOf(yaml.getString("Npc.Options.Style" , "NONE"));
            setColor(color);
            setStyle(style);
        });
    }

    public Material getColorMaterial() {
        switch (color) {
            case CREAMY:
                return Material.BIRCH_PLANKS;
            case CHESTNUT:
                return Material.OAK_PLANKS;
            case DARK_BROWN:
                return Material.DARK_OAK_PLANKS;
            case GRAY:
                return Material.GRAY_WOOL;
            case BROWN:
                return Material.BROWN_WOOL;
            case WHITE:
            default:
                return Material.WHITE_WOOL;
        }
    }
}
