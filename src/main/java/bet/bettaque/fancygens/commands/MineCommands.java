package bet.bettaque.fancygens.commands;

import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.config.MineConfig;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.helpers.PersistanceHelper;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import io.th0rgal.oraxen.items.OraxenItems;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;
import redempt.redlib.misc.EventListener;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class MineCommands {
    ConfigManager gensConfig;
    Plugin plugin;
    HashMap<UUID, Location> playerSelectionCache = new HashMap<>();
    Dao<GeneratorPlayer, String> generatorPlayerDao;

    public MineCommands(Plugin plugin, ConfigManager gensConfig, Dao<GeneratorPlayer, String> generatorPlayerDao) {
        this.plugin = plugin;
        this.gensConfig = gensConfig;
        this.generatorPlayerDao = generatorPlayerDao;
    }

    @CommandHook("createmine")
    public void createMine(Player player, String name, int pointsRequirement, String customItemName){
        if (GensConfig.mines.stream().anyMatch(mineConfig -> mineConfig.getName().equals(name))) {
            player.spigot().sendMessage(TextHelper.parseFancyComponents("&cThis mine already exists!"));
            return;
        }

        ItemStack selectBlock = new ItemBuilder(Material.LAPIS_BLOCK).setName("Selector Block");

        ItemUtils.give(player, selectBlock, 2);

        new EventListener<>(plugin, BlockPlaceEvent.class, (listener, event) -> {
            if (event.getPlayer() != player) return;
            if (event.getBlockPlaced().getType() != Material.LAPIS_BLOCK) return;
            event.setCancelled(true);
            Location selectedLocation = event.getBlockPlaced().getLocation();
            player.sendMessage(selectedLocation.toString());
            if (playerSelectionCache.containsKey(player.getUniqueId())){
                Location selection1 = playerSelectionCache.get(player.getUniqueId());
                playerSelectionCache.remove(player.getUniqueId());

                int mineId = GensConfig.mines.size();

                player.sendMessage(customItemName);
                ItemStack mineIcon = OraxenItems.getItemById(customItemName).build();

                MineConfig mine = new MineConfig(mineId, name, selection1, selectedLocation, pointsRequirement, mineIcon);
                GensConfig.mines.add(mine);
                gensConfig.save();
                player.sendMessage(ChatColor.GREEN + "You have created a new mine!");
                listener.unregister();
                ItemUtils.countAndRemove(player.getInventory(), selectBlock);

            } else {
                playerSelectionCache.put(player.getUniqueId(), selectedLocation);
                player.sendMessage(ChatColor.GREEN + "You have selected a corner. Move on to the next corner!");
            }

        });

    }

    @CommandHook("adminmines")
    public void admin(Player player){
        InventoryGUI gui = new InventoryGUI(Bukkit.createInventory(null, 27, "Admin Mines"));

        for (MineConfig mine : GensConfig.mines) {
            ItemButton button = ItemButton.create(new ItemBuilder(mine.getIcon())
                            .setName(mine.getName())
                    , e -> {
                        mineAdmin(player, mine);
                    });
            gui.addButton(button, mine.getId());
        }

        gui.open(player);

    }

    public void mineAdmin(Player player, MineConfig mine){
        InventoryGUI gui = new InventoryGUI(Bukkit.createInventory(null, 54, "Admin Mines " + mine.getName()));

        renderOres(mine, gui, player);

        gui.open(player);

        EventListener<InventoryClickEvent> clickEventEventListener = new EventListener<>(plugin, InventoryClickEvent.class, (listener, event) -> {
            if (gui.getInventory().firstEmpty() == -1) return;
            ItemStack item = event.getCurrentItem();
            Inventory inventory = event.getClickedInventory();
            if(item != null && item.getType().isBlock() && inventory.getType() == InventoryType.PLAYER) {
                item.setAmount(1);
                mine.getOres().add(item);
                gensConfig.save();
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

                renderOres(mine, gui, player);
                gui.update();
            }
        });
        BukkitRunnable cleanup = new BukkitRunnable(){
            @Override
            public void run() {
                clickEventEventListener.unregister();
            }
        };

        gui.setDestroyOnClose(true);
        gui.setOnDestroy(cleanup);

    }

    private void renderOres(MineConfig mine, InventoryGUI gui, Player player) {
        gui.clear();
        int c = 0;
        for (ItemStack ore : mine.getOres()) {
            ItemButton button = ItemButton.create(new ItemBuilder(ore)
                            .setName(mine.getName())
                    , e -> {
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 6);
                        mine.getOres().remove(ore);
                        gensConfig.save();
                        renderOres(mine, gui, player);
                        gui.update();
                    });
            gui.addButton(button, c);
            c++;
        }
    }

    @CommandHook("clearmine")
    public void clearMine(Player player, MineConfig mine){
        player.sendMessage(ChatColor.GREEN + "Mine cleared: " + mine.getName());
        mine.clear();
    }

    @CommandHook("deletemine")
    public void deleteMine(Player player, MineConfig mine){
        mine.clear();
        PersistanceHelper.removeMineRunnable(mine);
        GensConfig.mines.remove(mine);
        gensConfig.save();
        player.sendMessage(ChatColor.GREEN + "Mine deleted: " + mine.getName());
    }

    public void tpToMines(Player player, MineConfig mine){
        try {
            String mineName = TextHelper.parseFancyString("&#64B5F6-#80CBC4&" + mine.getName());
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            if (generatorPlayer.getPrestige() >= mine.getPrestigeRequirement()) {
                player.teleport(mine.getTpLocation());
                player.spigot().sendMessage(TextHelper.parseFancyComponents("&aYou have been sent to: " + mineName));
//                player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(10000, 5));
//                player.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(10000, 1));
            } else {
                player.spigot().sendMessage(TextHelper.parseFancyComponents("&cYou need to gain more prestige in order to access: " + mineName));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
