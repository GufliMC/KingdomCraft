package com.gufli.kingdomcraft.bukkit.panel;

import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.gui.Inventory;
import com.gufli.kingdomcraft.bukkit.gui.InventoryBuilder;
import com.gufli.kingdomcraft.bukkit.gui.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RankInfoPanel {

    protected static void open(PlatformPlayer player, Rank target) {
        open(player, target, null);
    }

    protected static void open(PlatformPlayer player, Rank target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + target.getName());

        MainPanel.kdc.getPlugin().getScheduler().async().execute(() -> {
            Map<UUID, String> members = target.getMembers();

            List<UUID> sorted = members.keySet().stream()
                    .sorted(Comparator.comparing(uuid -> MainPanel.kdc.getPlayer(uuid) != null))
                    .collect(Collectors.toList());

            for ( UUID uuid : sorted ) {
                String name = members.get(uuid);
                boolean isOnline = MainPanel.kdc.getPlayer(uuid) != null;

                builder.withItem(ItemStackBuilder.of(Material.SKULL_ITEM)
                                .withName(isOnline ? ChatColor.GREEN + name : ChatColor.GRAY + name)
                                .apply(isOnline, (b) -> b.withTexture(uuid))
                                .build(),
                        (player1, clickType) -> {
                            Inventory<?,?> inv = player.getInventory();
                            MainPanel.kdc.getUser(uuid).thenAccept((u) ->
                                    MainPanel.kdc.getPlugin().getScheduler().sync().execute(() ->
                                        PlayerInfoPanel.open(player, u, () -> player.openInventory(inv))));
                            return true;
                        }
                );
            }

            MainPanel.withBack(builder, back);
            MainPanel.kdc.getPlugin().getScheduler().sync().execute(() ->
                    player.openInventory(builder.buildS()));
        });
    }

}
