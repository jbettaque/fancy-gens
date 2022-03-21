package bet.bettaque.fancygens.db;

import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.UUID;

@DatabaseTable(tableName = "placed_generators")
public class PlacedGenerator extends PlacedCustomBlock {

    @DatabaseField
    private int generatorId;

    @DatabaseField
    private int boost;

    public PlacedGenerator() {
    }

    public PlacedGenerator(Location location, int generatorId, UUID owner, int boost) {
        super(location, owner);
        this.generatorId = generatorId;
        this.boost = boost;
    }

    public int getGeneratorId(){
        return this.generatorId;
    }

    public GenConfig getGenerator(){
        return GensConfig.gens.get(this.generatorId);
    }

    public void setGeneratorId(int generatorId) {
        this.generatorId = generatorId;
    }

    public int getBoost() {
        return boost;
    }

    public void setBoost(int boost) {
        this.boost = boost;
    }

    public double getCost() {
        return getGenerator().getCost(boost);
    }

    public double getUpgradeCost(){
        return getGenerator().getUpgradeCost(boost);
    }

    public double getProductPrice(){
        return getGenerator().getProductPrice(boost);
    }

    public Material getMaterial() {
        return this.getGenerator().product;
    }

    public GenConfig upgradeGenerator(){
        if (GensConfig.gens.get(generatorId + 1) != null){
            generatorId++;
        } else {
            generatorId = 1;
            boost += 1;
        }
        upgradeBlock();
        return this.getGenerator();
    }

    public void upgradeBlock(){
        Block block = getLocation().getBlock();
        Player owner = Bukkit.getPlayer(getOwner());
        block.setType(getMaterial());
        if (owner != null){
            owner.playSound(owner.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            owner.spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation(), 20, 0.5, 0.5, 0.5, 0.4);
        }
    }

}
