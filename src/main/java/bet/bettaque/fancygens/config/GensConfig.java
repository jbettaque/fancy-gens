package bet.bettaque.fancygens.config;

import org.bukkit.Material;
import redempt.redlib.config.annotations.ConfigMappable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ConfigMappable
public class GensConfig {
//    public static List<GenConfig> gens = new ArrayList<>();
    public static HashMap<Integer, GenConfig> gens = new HashMap<>();
    public static HashMap<Material, Double> shopItems = new HashMap<>();
    public static List<MineConfig> mines = new ArrayList<>();
    public static HashMap<Material, Double> miningItems = new HashMap<>();
}
