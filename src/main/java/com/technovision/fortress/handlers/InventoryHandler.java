package com.technovision.fortress.handlers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryHandler implements Listener {

    public static final Map<UUID, List<Inventory>> inventoryPages = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();

        // Check if the clicked inventory or item is null
        if (clickedInventory == null || clickedItem == null) {
            return;
        }

        // Check if the clicked inventory is one of the GUI pages
        String title = event.getView().getTitle();
        if (title.startsWith("Groups - Page")) {
            event.setCancelled(true);

            // Get the current page number from the title
            int currentPage = Integer.parseInt(title.split(" ")[3]) - 1;

            // Check if the clicked item is a navigation item
            String displayName = clickedItem.getItemMeta().getDisplayName();
            List<Inventory> pages = inventoryPages.get(player.getUniqueId());
            if (displayName.equals(ChatColor.WHITE + "Next Page")) {
                // Open next page
                player.openInventory(pages.get(currentPage + 1));
            } else if (displayName.equals(ChatColor.WHITE + "Previous Page")) {
                // Open previous page
                player.openInventory(pages.get(currentPage - 1));
            }
        }
    }
}
