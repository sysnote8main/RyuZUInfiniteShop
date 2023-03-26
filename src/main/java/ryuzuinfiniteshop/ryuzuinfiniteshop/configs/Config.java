package ryuzuinfiniteshop.ryuzuinfiniteshop.configs;

import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

public class Config {
    public void loadConfig() throws IOException, InvalidConfigurationException {
        DisplayPanel.loadConfig();
    }
}
