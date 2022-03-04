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
public class PlacedGenerator {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private double x;

    @DatabaseField
    private double y;

    @DatabaseField
    private double z;

    @DatabaseField
    private String world;

    @DatabaseField
    private int generatorId;

    @DatabaseField
    private UUID owner;

    public PlacedGenerator() {
    }

    public PlacedGenerator(Location location, int generatorId, UUID owner) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.generatorId = generatorId;
        this.world = location.getWorld().getName();
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public GenConfig getGenerator(){
        return GensConfig.gens.get(this.generatorId);
    }

    public Material getMaterial() {
        return this.getGenerator().product;
    }

    public Location getLocation(){
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, 0, 0);
    }

    public GenConfig upgradeGenerator(){
        if (GensConfig.gens.get(generatorId + 1) != null){
            generatorId++;
            return this.getGenerator();
        }
        return null;
    }

}
