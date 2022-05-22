package dev.cromo29.ultraportals.listeners;

import dev.cromo29.durkcore.util.ParticleEffect;
import dev.cromo29.durkcore.util.ParticleMaker;
import dev.cromo29.ultraportals.objects.Portal;
import dev.cromo29.ultraportals.objects.PortalLink;
import dev.cromo29.ultraportals.UltraPortalsPlugin;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class UsePortalEvent implements Listener {

    private final UltraPortalsPlugin plugin;

    public UsePortalEvent(UltraPortalsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void enterPortal(PlayerMoveEvent event) {

        if (event.isCancelled()) return;

        Location movedFrom = event.getFrom();
        Location movedTo = event.getTo();

        if (movedFrom.getBlockX() == movedTo.getBlockX()
                && movedFrom.getBlockY() == movedTo.getBlockY()
                && movedFrom.getBlockZ() == movedTo.getBlockZ()) return;

        Player player = event.getPlayer();
        Location location = player.getLocation();

        List<PortalLink> portalLinkList = plugin.getPortalManager().getClosestPortalLink(location);

        portalLinkList.forEach(portalLink -> {
            Portal origin = portalLink.getOrigin();
            Portal destination = portalLink.getDestination();

            if (!canTeleport(player, portalLink, origin.getLocation())
                    || !canTeleport(player, portalLink, destination.getLocation())) return;

            if (plugin.getPortalManager().compareWorld(location, origin.getLocation())) {

                if (location.distance(origin.getLocation()) < 2.25) teleport(player, portalLink, destination);
            }

            if (plugin.getPortalManager().compareWorld(location, destination.getLocation())) {

                if (location.distance(destination.getLocation()) < 2.25) teleport(player, portalLink, origin);
            }

        });

    }

    private void teleport(Player player, PortalLink portalLink, Portal destination) {
        Location location = destination.getLocation();
        Location toTeleport = destination.getDirection();

        player.teleport(toTeleport);
        player.playSound(toTeleport, Sound.ENDERMAN_TELEPORT, 1, 1);

        player.setVelocity(toTeleport.getDirection().multiply(0.2).setY(0.3));

        portalLink.getBlockeds().put(player, location);

        destination.getCircle().forEach(loc -> {
            Vector vector = location.toVector().subtract(loc.toVector());

            ParticleMaker.sendParticle(ParticleEffect.CLOUD, loc, vector.multiply(2.5), 0.1F, 30);
        });
    }

    private boolean canTeleport(Player player, PortalLink portalLink, Location location) {

        if (!portalLink.getBlockeds().containsKey(player)) return true;

        Location blockedLocation = portalLink.getBlockeds().get(player);

        if (!compareLocation(blockedLocation, location)) return true;

        if (!plugin.getPortalManager().compareWorld(blockedLocation, player.getLocation())) return false;

        if (blockedLocation.distance(player.getLocation()) > 2.9) {
            portalLink.getBlockeds().remove(player);
            return true;
        }

        return false;
    }

    private boolean compareLocation(Location a, Location b) {
        return a.getWorld() == b.getWorld()
                && a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }
}
