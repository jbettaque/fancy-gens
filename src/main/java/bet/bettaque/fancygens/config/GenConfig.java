package bet.bettaque.fancygens.config;

import org.bukkit.Material;
import redempt.redlib.config.annotations.ConfigMappable;

@ConfigMappable
public class GenConfig {
    public int id = 0;
    public String name = "Generator";
    public Material block = Material.STONE;
    public Material product = Material.STONE;

    public double rescale(double value, double inMin, double inMax, double outMin, double outMax){
        return outMin + (value - inMin) * ((outMax - outMin) / (inMax - inMin));
    }

    public double getCost() {
        if (id > 79) return Math.pow(2.5, id) * 150 * (1 + id);
        return Math.pow(2, id) * 150 * (1 + id);
    }

    public double getCost(int boost) {
        int id = this.id * (boost +1);
        if (id > 79) return Math.pow(2.5, id) * 150 * (1 + id);
        return Math.pow(2, id) * 150 * (1 + id);
    }

    public double getUpgradeCost(){
        int id = this.id + 1;
        if (id > 79) return Math.pow(2.5, id) * 150 * (1 + id);
        return Math.pow(2, id) * 150 * (1 + id);
    }

    public double getUpgradeCost(int boost){
        int id = this.id * (boost +1) + 1;
        if (id > 79) return Math.pow(2.5, id) * 150 * (1 + id);
        return Math.pow(2, id) * 150 * (1 + id);
    }

    public double getProductPrice(){
        if (id > 79) return getCost() / (150 + id * 3);
        return getCost() / (150 + id * 2);
    }

    public double getProductPrice(int boost){
        int id = this.id * (boost +1);
        if (id > 79) return getCost() / (150 + id * 3);
        return getCost() / (150 + id * 2);
    }

    public GenConfig() {
    }

    @Override
    public String toString() {
        return this.name;
    }
}
