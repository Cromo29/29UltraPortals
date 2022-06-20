package dev.cromo29.ultraportals.objects;

import dev.cromo29.durkcore.specificutils.LocationUtil;
import dev.cromo29.durkcore.util.TXT;
import dev.cromo29.durkcore.util.TextAnimation;
import dev.cromo29.ultraportals.UltraPortalsPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PortalLink {

    private String portalName;
    private Portal origin, destination;
    private Map<Player, Location> blockeds;
    private boolean enabled;

    private TextAnimation textAnimation;

    public PortalLink(String portalName) {
        this.portalName = portalName;
        this.blockeds = new HashMap<>();
        this.enabled = true;

        this.textAnimation = new TextAnimation(new TextAnimation.ColorBlink(
                10,
                5,
                portalName.toUpperCase(),
                "<f>",
                "<l>",
                true));
    }

    public PortalLink(String portalName, Portal origin, Portal destination, boolean enabled) {
        this.portalName = portalName;
        this.origin = origin;
        this.destination = destination;
        this.blockeds = new HashMap<>();
        this.enabled = enabled;


        this.textAnimation = new TextAnimation(new TextAnimation.ColorBlink(
                10,
                5,
                portalName.toUpperCase(),
                "<f>",
                "<l>",
                true));
    }

    public String getPortalName() {
        return portalName;
    }

    public Portal getOrigin() {
        return origin;
    }

    public void setOrigin(Portal origin) {
        this.origin = origin;
    }

    public Portal getDestination() {
        return destination;
    }

    public void setDestination(Portal destination) {
        this.destination = destination;
    }

    public Map<Player, Location> getBlockeds() {
        return blockeds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void saveAsync() {
        UltraPortalsPlugin plugin = UltraPortalsPlugin.get();

        String path = "portals." + portalName.toLowerCase();
        TXT.runAsynchronously(plugin, () -> {
            Map<String, Object> map = new HashMap<>();

            map.put("origin", LocationUtil.serializeLocation(origin.getLocation()));
            map.put("originDirection", LocationUtil.serializeLocation(origin.getDirection()));
            map.put("destination", LocationUtil.serializeLocation(destination.getDirection()));
            map.put("destinationDirection", LocationUtil.serializeLocation(destination.getDirection()));
            map.put("enabled", enabled);

            plugin.getPortalGson().put(path, map);
            plugin.getPortalGson().save();
        });
    }

    public void deleteAsync() {
        UltraPortalsPlugin plugin = UltraPortalsPlugin.get();

        boolean everything = plugin.getPortalGson().getSection("portals")
                .size() <= 1;

        TXT.runAsynchronously(plugin, () -> {
            plugin.getPortalsMap().remove(portalName.toLowerCase());

            if (everything) plugin.getPortalGson().removeAll("portals");
            else plugin.getPortalGson().remove("portals." + portalName.toLowerCase());

            plugin.getPortalGson().save();
        });
    }

    public void update() {
        origin.update(enabled);
        destination.update(enabled);


        if (!origin.getArmorStand().isDead()) {
            if (!origin.getArmorStand().isCustomNameVisible()) origin.getArmorStand().setCustomNameVisible(true);

            origin.getArmorStand().setCustomName(textAnimation.next());
        }

        if (!destination.getArmorStand().isDead()) {
            if (!destination.getArmorStand().isCustomNameVisible()) destination.getArmorStand().setCustomNameVisible(true);

            destination.getArmorStand().setCustomName(textAnimation.next());
        }
    }

    public void createStand() {
        origin.createStand();
        destination.createStand();
    }

    public void removeStand() {
        origin.removeStands();
        destination.removeStands();
    }

}
