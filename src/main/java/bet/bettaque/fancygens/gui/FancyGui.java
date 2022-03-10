package bet.bettaque.fancygens.gui;

import bet.bettaque.fancygens.helpers.TextHelper;
import io.th0rgal.oraxen.items.OraxenItems;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class FancyGui {
    HashMap<ItemStack, Consumer<InventoryClickEvent>> callbackMap;
    HashMap<ItemStack, Iterable<String>> loreMap;
    InventoryGUI gui;
    FancyGui previous;
    String title;
    List<ItemStack> contents;
    Player player;
    int size;
    boolean center;

    public FancyGui(String title, int size, Player player, FancyGui previous) {
        this.title = title;
        this.size = size;
        this.gui = new InventoryGUI(Bukkit.createInventory(null, size, title));
        this.previous = previous;
        this.player = player;
        this.callbackMap = new HashMap<>();
        this.loreMap = new HashMap<>();
        this.contents = new ArrayList<>();
    }

    public FancyGui(String title, int size, Player player) {
        this(title, size, player ,null);
    }

    public List<ItemStack> getContents() {
        return contents;
    }

    public void setContents(List<ItemStack> contents) {
        this.contents = contents;
        this.populate();
    }

    public InventoryGUI getGui() {
        return gui;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public int getActualSize() {
        if (hasMenuBar()) return size -9;
        return size;
    }

    public boolean hasMenuBar(){
        return previous != null;
    }

    public void populateMenuBar(){
        ItemStack exitIcon = OraxenItems.getItemById("exit_icon").build();
        ItemButton exitButton = ItemButton.create(new ItemBuilder(exitIcon)
                , e -> {
                    previous.populate();
                });
        gui.addButton(exitButton, size - 5);
    }

    public Iterable<String> getLore(ItemStack item){
        Iterable<String> strings = this.loreMap.get(item);
        if (strings == null) return new ArrayList<>();
        return this.loreMap.get(item);
    }

    public Consumer<InventoryClickEvent> getListener(ItemStack item){
        Consumer<InventoryClickEvent> listener = this.callbackMap.get(item);
        if (listener == null) return e -> {};
        return this.callbackMap.get(item);
    }

    public void setCallback(ItemStack item, Consumer<InventoryClickEvent> listener){
        callbackMap.put(item, listener);
    }

    public void setLore(ItemStack item, Iterable<String> lore){
        loreMap.put(item, lore);
    }

    public void populate(){
        size = contents.size() + 9 - (contents.size() % 9);
        if (this.hasMenuBar()) size += 9;
        this.gui = new InventoryGUI(Bukkit.createInventory(null, size, title));
        int c = 0;
        if (center){
            c = (getActualSize() - contents.size()) / 2;
        }

        for (ItemStack item: contents) {
            ItemButton itemButton = ItemButton.create(new ItemBuilder(item)
                    .addLore(getLore(item))
                    , getListener(item));
            gui.addButton(itemButton, c);
            c++;
        }


        if (this.hasMenuBar()) this.populateMenuBar();
        open();
    }

    private void open(){
        this.gui.open(player);
    }
}
