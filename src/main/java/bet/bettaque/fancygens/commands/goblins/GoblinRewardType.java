package bet.bettaque.fancygens.commands.goblins;

import bet.bettaque.fancygens.FancyResource;
import bet.bettaque.fancygens.helpers.TextHelper;
import bet.bettaque.fancygens.services.FancyEconomy;
import org.bukkit.entity.Player;

import java.util.Random;

public enum GoblinRewardType {
    COINS,
    GEMS,
    SLOTS,
    MULTIPLIER,
//    SELLWAND,
//    GENERATORS,
//    GOBLIN_SCROLL
    ;

    public FancyResource getResource(){
        switch (this){
            case COINS: return FancyResource.COINS;
            case GEMS: return FancyResource.GEMS;
            case SLOTS: return FancyResource.SLOTS;
            case MULTIPLIER: return FancyResource.MULTIPLIER;
        }
        return FancyResource.NULL;
    }

    public double getRandomMultiplier(Random random){

        return random.nextDouble() + 1;
    }

    public void handleReward(Player player, FancyEconomy economy, GoblinTier tier){
        FancyResource resource = this.getResource();
        Random random = new Random();

        switch (resource){
            case COINS: {
                double reward = economy.getBalance(player, resource) / 4 * tier.getMultiplier() * getRandomMultiplier(random);
                economy.add(player, resource, reward);
                player.sendMessage(TextHelper.parseFancyString("&#E91E63-#8E24AA&** " + tier.name() + " Golem Reward** " + resource.formatValue(reward, player)));
                return;
            }
            case GEMS: {
                double reward = 200 * tier.getMultiplier() * getRandomMultiplier(random);
                economy.add(player, resource, reward);
                player.sendMessage(TextHelper.parseFancyString("&#E91E63-#8E24AA&** " + tier.name() + " Golem Reward** " + resource.formatValue(reward, player)));
                return;
            }
            case SLOTS: {
                double reward = 10 * tier.getMultiplier() * getRandomMultiplier(random);
                economy.add(player, resource, reward);
                player.sendMessage(TextHelper.parseFancyString("&#E91E63-#8E24AA&** " + tier.name() + " Golem Reward** " + resource.formatValue(reward, player)));
                return;
            }
            case MULTIPLIER : {
                double reward = 0.025 * tier.getMultiplier() * getRandomMultiplier(random);
                economy.add(player, resource, reward);
                player.sendMessage(TextHelper.parseFancyString("&#E91E63-#8E24AA&** " + tier.name() + " Golem Reward** " + resource.formatValue(reward, player)));
            }
        }
    }
}
