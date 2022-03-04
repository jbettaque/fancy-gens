package bet.bettaque.fancygens.config;

import redempt.redlib.config.annotations.ConfigMappable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ConfigMappable
public class GensConfig {
//    public static List<GenConfig> gens = new ArrayList<>();
    public static HashMap<Integer, GenConfig> gens = new HashMap<>();
}
