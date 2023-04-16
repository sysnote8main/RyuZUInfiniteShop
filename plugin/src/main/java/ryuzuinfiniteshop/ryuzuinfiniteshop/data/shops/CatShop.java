package ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

import java.util.Arrays;
import java.util.function.Consumer;

public class CatShop extends SittableShop {
    protected Cat.Type type;

    public CatShop(Location location, EntityType entitytype, ConfigurationSection config) {
        super(location, entitytype, config);
    }

    public void setCatType(Cat.Type type) {
        this.type = type;
        if(npc == null) return;
        ((Cat) npc).setCatType(type);
    }

    public Cat.Type getNextType() {
        int nextindex = Arrays.asList(Cat.Type.values()).indexOf(type) + 1;
        return nextindex == Cat.Type.values().length ?
                Cat.Type.values()[0] :
                Cat.Type.values()[nextindex];
    }

    @Override
    public Consumer<YamlConfiguration> getSaveYamlProcess() {
        return super.getSaveYamlProcess().andThen(yaml -> yaml.set("Npc.Options.CatType", type.toString()));
    }

    @Override
    public Consumer<YamlConfiguration> getLoadYamlProcess() {
        return super.getLoadYamlProcess().andThen(yaml -> {
            this.type = Cat.Type.valueOf(yaml.getString("Npc.Options.CatType" , "TABBY"));
        });
    }

    @Override
    public void respawnNPC() {
        super.respawnNPC();
        setCatType(type);
    }
}
