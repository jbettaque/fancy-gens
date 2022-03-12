package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.FancyResource;
import bet.bettaque.fancygens.commands.goblins.GoblinTier;
import bet.bettaque.fancygens.commands.goblins.GoblinRewardType;
import bet.bettaque.fancygens.helpers.TextHelper;
import bet.bettaque.fancygens.services.FancyEconomy;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDespawnEvent;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import redempt.redlib.itemutils.ItemUtils;

import java.util.Random;

public class ScrollListener implements Listener {
    Plugin plugin;
    FancyEconomy economy;

    public ScrollListener(Plugin plugin, FancyEconomy economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    @EventHandler
    public void onScrollUse(PlayerInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack usedItem = event.getItem();
        String oraxenId = OraxenItems.getIdByItem(usedItem);
        if (oraxenId != null && oraxenId.startsWith("summoning_scroll")){
            String tierString = oraxenId.substring(17);
            GoblinTier tier = GoblinTier.get(tierString);

            BukkitAPIHelper mythicAPIHelper = MythicMobs.inst().getAPIHelper();
            try {
                Entity goblin = mythicAPIHelper.spawnMythicMob("event_goblin", event.getClickedBlock().getLocation());
                goblin.setCustomName(TextHelper.parseFancyString("&#F44336-#E91E63&**Common Treasure Goblin** "));
                goblin.setCustomNameVisible(true);
                goblin.setMetadata("goblins", new FixedMetadataValue(plugin, tier));
                ItemUtils.remove(event.getPlayer().getInventory(), usedItem, 1);
            } catch (InvalidMobTypeException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onMythicDespawn(MythicMobDespawnEvent event){
        Random random = new Random();
        Entity entity = event.getEntity();
        if (entity.hasMetadata("goblins")){
            GoblinTier rarity = (GoblinTier) entity.getMetadata("goblins").get(0).value();
            BukkitAPIHelper mythicAPIHelper = MythicMobs.inst().getAPIHelper();
            try {
                for (int i = 0; i < random.nextInt(5) + 3; i++) {
                    Location randomLocation = event.getEntity().getLocation().add((random.nextInt(i + 1)) - (random.nextInt(i + 1)), 0, (random.nextInt(i + 1)) - (random.nextInt(i + 1)));

                    while(randomLocation.add(0, -5, 0).getBlock().getType() == Material.AIR){
                        randomLocation.setY(randomLocation.getY() - 1);
                    }
                    Entity box = mythicAPIHelper.spawnMythicMob("event_box", randomLocation.add(0, 1, 0));
                    box.setMetadata("box", new FixedMetadataValue(plugin, rarity));
                }

            } catch (InvalidMobTypeException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onBoxLooted(MythicMobDeathEvent event){
        Entity entity = event.getEntity();
        if (entity.hasMetadata("box")){
            GoblinTier rarity = (GoblinTier) entity.getMetadata("box").get(0).value();
            Random random = new Random();
            int rewardIndex = random.nextInt(GoblinRewardType.values().length);
            GoblinRewardType goblinRewardType = GoblinRewardType.values()[rewardIndex];
            if(event.getKiller() instanceof Player){
                Player player = (Player) event.getKiller();
                goblinRewardType.handleReward(player, economy, rarity);



            }
        }
    }

}
