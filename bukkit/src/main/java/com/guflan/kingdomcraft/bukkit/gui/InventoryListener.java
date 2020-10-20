package com.guflan.kingdomcraft.bukkit.gui;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.gui.AbstractInventory;
import com.guflan.kingdomcraft.api.gui.InventoryClickType;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InventoryListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;

    public InventoryListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent e) {
        PlatformPlayer player = plugin.getKdc().getPlayer(e.getPlayer().getUniqueId());
        handleClose(player);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        PlatformPlayer player = plugin.getKdc().getPlayer(e.getPlayer().getUniqueId());
        handleClose(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if ( !(e.getWhoClicked() instanceof Player) ) return;

        PlatformPlayer player = plugin.getKdc().getPlayer(e.getWhoClicked().getUniqueId());
        AbstractInventory<?> inv = player.getInventory();

        if ( !(inv instanceof BukkitInventory) ) {
            return;
        }

        if ( !((BukkitInventory) inv).getInventory().equals(e.getClickedInventory()) ) {
            player.set(PlatformPlayer.CUSTOM_GUI_KEY, null);
            player.sendMessage("DIFFERENT INVENTORY THAN ONE IN CACHE CLICKED!!!");
            return;
        }

        e.setCancelled(true);

        InventoryClickType type = null;
        switch (e.getClick()) {
            case DOUBLE_CLICK:
            case SHIFT_LEFT:
            case LEFT:
                type = InventoryClickType.LEFT;
                break;
            case SHIFT_RIGHT:
            case RIGHT:
                type = InventoryClickType.RIGHT;
                break;
            case MIDDLE:
                type = InventoryClickType.MIDDLE;
        }

        if ( type == null ) {
            return;
        }

        inv.dispatchClick(player, type, e.getSlot());

        // play a sound
        Sound sound;
        try {
            sound = Sound.valueOf("CLICK"); // 1.8
        } catch (IllegalArgumentException ex) {
            sound = Sound.valueOf("UI_BUTTON_CLICK"); // 1.9 +
        }

        Player p = (Player) e.getWhoClicked();
        p.playSound(p.getLocation().add(0, 1.8, 0), sound, 1f, 1f);
    }

    private void handleClose(PlatformPlayer player) {
        AbstractInventory<?> inv = player.getInventory();
        if ( inv != null ) {
            inv.dispatchClose(player);
            player.set(PlatformPlayer.CUSTOM_GUI_KEY, null);
        }
    }

}
