package dev.cromo29.ultraportals.listeners;

import dev.cromo29.durkcore.util.MakeItem;
import dev.cromo29.durkcore.util.TXT;
import dev.cromo29.ultraportals.objects.Portal;
import dev.cromo29.ultraportals.objects.PortalLink;
import dev.cromo29.ultraportals.UltraPortalsPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CreatePortalEvent implements Listener {

    private final UltraPortalsPlugin plugin;

    public CreatePortalEvent(UltraPortalsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void quitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getPortalManager().isCreating(player)) {
            plugin.getCreatePortalMap().remove(player);
            player.getInventory().remove(new MakeItem(Material.STICK).setName("<e>Gerador de portais").build());
        }
    }

    @EventHandler
    public void dropEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (plugin.getPortalManager().isCreating(player)) {
            if (event.getItemDrop().getItemStack().isSimilar(new MakeItem(Material.STICK).setName("<e>Gerador de portais").build()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void createPortal(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.STICK) return;
        if (!item.hasItemMeta()
                || !item.getItemMeta().hasDisplayName()
                || !item.getItemMeta().getDisplayName().equals(TXT.parse("<e>Gerador de portais"))) return;

        if (!plugin.getPortalManager().isCreating(player)) return;

        PortalLink portalLink = plugin.getCreatePortalMap().get(player);
        Location location = player.getLocation().add(0, 1.5, 0);

        List<PortalLink> portalLinkList = plugin.getPortalManager().getClosestPortalLink(location);

        if (!portalLinkList.isEmpty()) {
            sendMessage(player, " <c><disapprove> <e>Você está muito próximo de outros portais!");
            return;
        }

        if (portalLink.getOrigin() == null && portalLink.getDestination() == null) {

            portalLink.setOrigin(new Portal(location, player.getEyeLocation(), Color.fromRGB(159, 168, 241)));

            sendMessage(player, " <b>☾ <7>Você setou a <b>primeira <7>posição do portal!");

        } else if (portalLink.getOrigin() != null && portalLink.getDestination() == null) {

            if (plugin.getPortalManager().compareWorld(portalLink.getOrigin().getLocation(), location)) {

                if (portalLink.getOrigin().getLocation().distance(location) < 5) {
                    sendMessage(player, " <c><disapprove> <e>Você precisa estar a mais de 5 blocos do outro portal.");
                    return;
                }

            }

            portalLink.setDestination(new Portal(location, player.getEyeLocation(), Color.fromRGB(27, 38, 49)));
            
            sendMessage(player, " <b>☾ <7>Você setou a <b>segunda <7>posição do portal!");

            plugin.getPortalManager().finishPortal(player);
        }
    }

    void sendMessage(Player player, String... texts) {
        TXT.sendMessages(player, texts);
    }
}
