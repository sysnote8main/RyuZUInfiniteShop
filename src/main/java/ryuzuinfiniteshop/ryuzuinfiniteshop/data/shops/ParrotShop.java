package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;

import java.util.Arrays;
import java.util.function.Consumer;

public class ParrotShop extends Shop {
    protected Parrot.Variant color;

    public ParrotShop(Location location, EntityType entitytype) {
        super(location, entitytype);
    }

    public ParrotShop(Location location, EntityType entitytype, ConfigurationSection config) {
        super(location, entitytype, config);
    }

    public void setColor(Parrot.Variant color) {
        this.color = color;
        if(npc == null) return;
        ((Parrot) npc).setVariant(color);
//        NBTBuilder.setVariant(color.ordinal());
    }

    public Parrot.Variant getNextColor() {
        int nextindex = Arrays.asList(Parrot.Variant.values()).indexOf(color) + 1;
        return nextindex == Parrot.Variant.values().length ?
                Parrot.Variant.values()[0] :
                Parrot.Variant.values()[nextindex];
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> yaml.set("Npc.Options.Color", color.toString()));
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.color = Parrot.Variant.valueOf(yaml.getString("Npc.Options.Color" , "RED"));
        });
    }

    @Override
    public void respawnNPC() {
        super.respawnNPC();
        setColor(color);
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
