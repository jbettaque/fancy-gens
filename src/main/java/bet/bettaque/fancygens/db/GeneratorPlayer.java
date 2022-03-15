package bet.bettaque.fancygens.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.UUID;

@DatabaseTable(tableName = "players")
public class GeneratorPlayer {

    @DatabaseField(id = true)
    private String uuid;

    @DatabaseField
    private int maxGens;

    @DatabaseField
    private int usedGens;

    @DatabaseField
    private double score;

    @DatabaseField
    private int prestige;

    @DatabaseField
    private double multiplier;

    @DatabaseField
    private int timesPurchasedTokens;

    @DatabaseField
    private double gems;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<Double> lastSells;

    public GeneratorPlayer() {
    }

    public GeneratorPlayer(String uuid, int maxGens, int usedGens) {
        this.uuid = uuid;
        this.maxGens = maxGens;
        this.usedGens = usedGens;
        this.score = 0;
        this.prestige = 0;
        this.multiplier = 1;
        this.timesPurchasedTokens = 0;
        this.lastSells = new ArrayList<>();
    }

    public String getUuid() {
        return uuid;
    }

    public UUID getUUID(){
        return UUID.fromString(uuid);
    }

    public void incrementUsedGens(){
        this.usedGens++;
    }

    public void incrementScore(double amount){
        this.score += amount;
    }

    public void addMaxGens(int amount){
        this.maxGens += amount;
    }

    public void decrementUsedGens(){
        this.usedGens--;
    }

    public void incrementPrestige(){
        this.prestige++;
    }

    public void resetScore(){
        this.score = 0;
    }

    public void addGems(double amount){
        this.gems += amount;
    }

    public void incrementTimesPurchasedTokens(){
        this.timesPurchasedTokens ++;
    }

    public void incrementMultiplier(double amount){
        this.multiplier += amount;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    public int getTimesPurchasedTokens() {
        return this.timesPurchasedTokens;
    }

    public void depositGems(double amount){
        this.gems += amount;
    }

    public void withdrawGems(double amount){
        this.gems -= amount;
    }

    public double getGems() {
        return this.gems;
    }

    public void setGems(double gems) {
        this.gems = gems;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setPrestige(int prestige) {
        this.prestige = prestige;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public int getPrestige() {
        return prestige;
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

    public void addSell(double price){
        if (this.lastSells == null) this.lastSells = new ArrayList<>();
        this.lastSells.add(0,price);
        if (lastSells.size() > 20) {
            lastSells.remove(lastSells.size() -1);
        }
    }

    public double getLastSellsAvrg(){
        double total = 0;
        if (this.lastSells != null){
            for (double sell: this.lastSells){
                total += sell;
            }
        } else {
            lastSells = new ArrayList<>();
        }


        return Math.round(total / lastSells.size());
    }

    public ArrayList<Double> getLastSells() {
        return lastSells;
    }
}
