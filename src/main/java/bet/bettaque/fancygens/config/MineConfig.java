package bet.bettaque.fancygens.config;

import bet.bettaque.fancygens.helpers.PersistanceHelper;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import redempt.redlib.config.annotations.ConfigMappable;
import redempt.redlib.region.CuboidRegion;
import redempt.redlib.region.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@ConfigMappable
public class MineConfig {
    int id;
    String name;

    Location regionStart;
    Location regionEnd;

    List<ItemStack> ores = new ArrayList<>();
    int prestigeRequirement;

    ItemStack icon;

    public MineConfig() {
    }

    public MineConfig(int id, String name, Location regionStart, Location regionEnd, int prestigeRequirement, ItemStack icon) {
        this.id = id;
        this.name = name;
        this.regionStart = regionStart;
        this.regionEnd = regionEnd;
        this.start(PersistanceHelper.getPlugin());
        this.prestigeRequirement = prestigeRequirement;
        this.icon = icon;
    }

    public CuboidRegion getRegion(){
        CuboidRegion region = new CuboidRegion(regionStart, regionEnd);
        Location start = region.getStart();
        Location end = region.getEnd();
        region.expand(BlockFace.UP, 1);
        if (end.getX() > start.getX()) region.expand(BlockFace.EAST, 1);
        else region.expand(BlockFace.WEST, 1);

        if (end.getZ() > start.getZ()) region.expand(BlockFace.SOUTH, 1);
        else region.expand(BlockFace.NORTH, 1);
        return region;
    }

    public Location getTpLocation(){
        Region region = getRegion();
        double height = region.getDimensions()[1];
        return region.getCenter().add(0, (height / 2) + 1 , 0);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrestigeRequirement() {
        return prestigeRequirement;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getRegionStart() {
        return regionStart;
    }

    public void setRegionStart(Location regionStart) {
        this.regionStart = regionStart;
    }

    public Location getRegionEnd() {
        return regionEnd;
    }

    public void setRegionEnd(Location regionEnd) {
        this.regionEnd = regionEnd;
    }

    public List<ItemStack> getOres() {
        return ores;
    }

    public void setOres(ArrayList<ItemStack> ores) {
        this.ores = ores;
    }

    private int random(int max){
        if (max == 0) return 0;
        Random random = new Random();
        return random.nextInt(max);
    }

    private ItemStack selectRandomOre(){
        return this.ores.get(random(this.ores.size()));
    }

    public void regenerate(Block block){
        PersistentDataContainer container = new CustomBlockData(block, PersistanceHelper.getPlugin());
        container.set(PersistanceHelper.oreKey, PersistentDataType.INTEGER, 1);
        block.setType(this.selectRandomOre().getType());
    }

    public void start(Plugin plugin){
        CuboidRegion region = getRegion();
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                List<Block> blockList = region.stream().filter(block -> block.getType() == Material.AIR).collect(Collectors.toList());
                if (ores.size() > 0){
                    int batchSize = blockList.size() / 200;
                    if (batchSize < 1) batchSize = 1;
                    for (int i = 0; i < batchSize; i++) {
                        if (blockList.size() > 0) {
                            regenerate(blockList.remove(random(blockList.size() -1)));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
        PersistanceHelper.setMineRunnable(this, task);
    }

    public void clear(){
        this.getRegion().forEachBlock((block -> {
            PersistentDataContainer container = new CustomBlockData(block, PersistanceHelper.getPlugin());
            block.setType(Material.AIR);
            container.remove(PersistanceHelper.oreKey);
        }));
    }
}
