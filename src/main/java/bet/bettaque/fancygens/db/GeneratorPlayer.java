package bet.bettaque.fancygens.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "players")
public class GeneratorPlayer {

    @DatabaseField(id = true)
    private String uuid;

    @DatabaseField
    private int maxGens;

    @DatabaseField
    private int usedGens;

    public GeneratorPlayer() {
    }

    public GeneratorPlayer(String uuid, int maxGens, int usedGens) {
        this.uuid = uuid;
        this.maxGens = maxGens;
        this.usedGens = usedGens;
    }

    public String getUuid() {
        return uuid;
    }

    public void incrementUsedGens(){
        this.usedGens++;
    }

    public void decrementUsedGens(){
        this.usedGens--;
    }

    public int getMaxGens() {
        return maxGens;
    }

    public void setMaxGens(int maxGens) {
        this.maxGens = maxGens;
    }

    public int getUsedGens() {
        return usedGens;
    }

    public void setUsedGens(int usedGens) {
        this.usedGens = usedGens;
    }
}
