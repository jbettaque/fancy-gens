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
import redempt.redlib.itemutils.ItemUtils;

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
    int page;

    public FancyGui(String title, int size, Player player, FancyGui previous) {
        this.title = title;
        this.size = size;
        this.gui = new InventoryGUI(Bukkit.createInventory(null, size, title));
        this.previous = previous;
        this.player = player;
        this.callbackMap = new HashMap<>();
        this.loreMap = new HashMap<>();
        this.contents = new ArrayList<>();
        this.page = 0;
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

    public void fillSlots(int start, int end){
        ItemStack fillIcon = OraxenItems.getItemById("filler").setDisplayName(ChatColor.RESET.toString()).build();
        gui.fill(start, end, fillIcon);
    }

    public boolean hasMenuBar(){
        return previous != null;
    }

    public void populateMenuBar(){
        ItemStack exitIcon = OraxenItems.getItemById("back").build();
        ItemUtils.setName(exitIcon, exitIcon.getItemMeta().getDisplayName() + " to main menu");
        ItemButton exitButton = ItemButton.create(new ItemBuilder(exitIcon)
                , e -> {
                    previous.populate();
                });
        gui.addButton(exitButton, size - 5);
        fillSlots(size - 4, size);
        fillSlots(size - 9, size - 5);

        if (page > 0){
            gui.clearSlot(size - 7);
            ItemStack prevPageIcon = OraxenItems.getItemById("arrow_left").build();
            ItemButton prevPageButton = ItemButton.create(new ItemBuilder(prevPageIcon)
                    , e -> {
                        page -= 1;
                        populate();
                    });
            gui.addButton(prevPageButton, size - 7);
        }


        if (contents.size() > (page * 45) + 45){
            gui.clearSlot(size - 3);
            ItemStack nextPageIcon = OraxenItems.getItemById("arrow_right").build();
            ItemButton nextPageButton = ItemButton.create(new ItemBuilder(nextPageIcon)
                    , e -> {
                        page += 1;
                        populate();
                    });
            gui.addButton(nextPageButton, size - 3);
        } else {
            fillSlots(size - 3, size - 3);
        }


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
        size = contents.size() - (contents.size() % 9);
        if (size > 9) size +=9;
        if (this.hasMenuBar()) size += 9;
        if (size > 54) size = 54;


        int c = 0;
        boolean singleRowTransformed = false;
        if (size == 9 || (this.hasMenuBar() && size == 18)){
            size = 45;
            singleRowTransformed = true;

            c = 12;
        }

        this.gui = new InventoryGUI(Bukkit.createInventory(null, size, title));

//        if (center){
//            c = (getActualSize() - contents.size()) / 2;
//        }
        int pageOffset = page * 45;
        int endOffset = 45 + pageOffset;
        if (endOffset > contents.size()) endOffset = contents.size();
        for (ItemStack item: contents.subList(pageOffset, endOffset)) {
            if (c >= 45) break;
            if (contents.size() == 1){
                c = 22;
            }
            ItemButton itemButton = ItemButton.create(new ItemBuilder(item)
                    .addLore(getLore(item))
                    , getListener(item));
            gui.addButton(itemButton, c);
            c++;
            if (singleRowTransformed){
                if (c == 15) c = 21;
                if (c == 24) c = 30;
//                if (c == 33) c = 42;
            }
//            if (c +1 % 9 != c +1) c = c + 6;
        }

        if (singleRowTransformed){
            if (contents.size() == 1){
                fillSlots(0, 22);
                fillSlots(23, 45);
            } else {
                fillSlots(0, 12);
                fillSlots(15, 21);
                fillSlots(24, 30);
                fillSlots(33, 45);
            }
        }


        if (this.hasMenuBar()) this.populateMenuBar();
        open();
    }

    private void open(){
        this.gui.open(player);
    }
}
