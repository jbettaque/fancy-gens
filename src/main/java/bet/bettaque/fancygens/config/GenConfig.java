package bet.bettaque.fancygens.config;

import org.bukkit.Material;
import redempt.redlib.config.annotations.ConfigMappable;

@ConfigMappable
public class GenConfig {
    public int id = 0;
    public String name = "Generator";
    public Material block = Material.STONE;
    public Material product = Material.STONE;
    public double cost = 20;

    public GenConfig() {
    }

    @Override
    public String toString() {
        return this.name;
    }
}