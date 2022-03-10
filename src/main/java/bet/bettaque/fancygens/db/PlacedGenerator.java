package bet.bettaque.fancygens.db;

import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

@DatabaseTable(tableName = "placed_generators")
public class PlacedGenerator extends PlacedCustomBlock {

    @DatabaseField
    private int generatorId;

    public PlacedGenerator() {
    }

    public PlacedGenerator(Location location, int generatorId, UUID owner) {
        super(location, owner);
        this.generatorId = generatorId;
    }

    public int getGeneratorId(){
        return this.generatorId;
    }

    public GenConfig getGenerator(){
        return GensConfig.gens.get(this.generatorId);
    }

    public Material getMaterial() {
        return this.getGenerator().product;
    }

    public GenConfig upgradeGenerator(){
        if (GensConfig.gens.get(generatorId + 1) != null){
            generatorId++;
            return this.getGenerator();
        }
        return null;
    }

}
