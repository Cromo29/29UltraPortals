package dev.cromo29.ultraportals.manager;

import dev.cromo29.durkcore.specificutils.LocationUtil;
import dev.cromo29.durkcore.specificutils.StringUtil;
import dev.cromo29.durkcore.util.MakeItem;
import dev.cromo29.durkcore.util.TXT;
import dev.cromo29.ultraportals.objects.Portal;
import dev.cromo29.ultraportals.objects.PortalLink;
import dev.cromo29.ultraportals.UltraPortalsPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PortalManager {

    private final UltraPortalsPlugin plugin;

    public PortalManager(UltraPortalsPlugin plugin) {
        this.plugin = plugin;
    }

    public void createPortal(Player player, String name) {

        if (isCreating(player)) {
            sendMessage(player, " <c><disapprove> <e>Você já está criando outro portal!");
            return;
        }

        if (getPortalByName(name) != null) {
            sendMessage(player, " <c><disapprove> <e>Já existe um portal com esse nome.");
            return;
        }

        plugin.getCreatePortalMap().put(player, new PortalLink(name));

        player.getInventory().addItem(new MakeItem(Material.STICK).setName("<e>Gerador de portais").build());

        sendMessage(player, " <b>☾ <7>Use o <b>stick <7>para salvar sua posição.");
    }

    public void finishPortal(Player player) {
        PortalLink portalLink = plugin.getCreatePortalMap().get(player);

        player.getInventory().remove(new MakeItem(Material.STICK).setName("<e>Gerador de portais").build());

        plugin.getPortalsMap().put(portalLink.getPortalName().toLowerCase(), portalLink);
        plugin.getCreatePortalMap().remove(player);

        portalLink.createStand();
        portalLink.saveAsync();

        sendMessage(player, " <b>☾ <7>Você criou o portal <b>" + portalLink.getPortalName() + "<7>!");
    }

    public void deletePortal(Player player, String name) {
        PortalLink portalLink = getPortalByName(name);

        if (portalLink == null) {
            sendMessage(player, " <c><disapprove> <e>O portal " + name + " não existe!");
            return;
        }

        portalLink.deleteAsync();
        portalLink.removeStand();

        sendMessage(player, " <b>☾ <7>Você deletou o portal <b>" + portalLink.getPortalName() + "<7>!");
    }

    public void portalsList(Player player) {
        List<String> portals = new ArrayList<>();

        plugin.getPortalsMap().values().forEach(portalLink -> portals.add(StringUtils.capitalize(portalLink.getPortalName())));

        String portalsText = TXT.createString(portals.toArray(new String[0]), 0, "<7>, <b>");

        sendMessage(player, " <b>☾ <7>Portais: <b>" + portalsText + "<7>.");

    }

    public void changePortal(Player player, String name) {
        PortalLink portalLink = getPortalByName(name);

        if (portalLink == null) {
            sendMessage(player, " <c><disapprove> <e>O portal " + name + " não existe!");
            return;
        }

        portalLink.setEnabled(!portalLink.isEnabled());
        portalLink.saveAsync();

        if (portalLink.isEnabled()) {

            sendMessage(player,
                    "",
                    StringUtil.getCentered(" <b>☾ <7>Você desativou o portal <b>" + portalLink.getPortalName() + "<7>!"),
                    "");

            // portalLink.removeStand();

        } else {

            sendMessage(player,
                    "",
                    StringUtil.getCentered(" <b>☾ <7>Você ativou o portal <b>" + portalLink.getPortalName() + "<7>!"),
                    "");

            // portalLink.createStand();

        }

        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);

    }

    public void loadPortals() {

        plugin.getPortalsMap().values().forEach(PortalLink::removeStand);

        plugin.getPortalsMap().clear();
        plugin.getCreatePortalMap().clear();

        if (plugin.getPortalGson().getSection("portals") == null) return;

        plugin.getPortalGson().getSection("portals").forEach(portalName -> {
            Map<String, Object> map = plugin.getPortalGson().get("portals." + portalName).asMap();

            Location originLocation = LocationUtil.unserializeLocation((String) map.get("origin"));
            Location originDirection = LocationUtil.unserializeLocation((String) map.get("originDirection"));
            Location destinationLocation = LocationUtil.unserializeLocation((String) map.get("destination"));
            Location destinationDirection = LocationUtil.unserializeLocation((String) map.get("destinationDirection"));
            boolean enabled = (boolean) map.get("enabled");

            if (originLocation == null
                    || originDirection == null
                    || destinationLocation == null
                    || destinationDirection == null) {
                plugin.logs(
                        "",
                        " <c>Alguma localizacao nao existe!");
                return;
            }

            Portal origin = new Portal(originLocation, originDirection, enabled ? Color.fromRGB(159, 168, 241) : Color.fromRGB(255, 153, 0));
            Portal destination = new Portal(destinationLocation, destinationDirection, enabled ? Color.fromRGB(27, 38, 49) : Color.fromRGB(255, 153, 0));

            if (origin.getLocation().getWorld() == null || destination.getLocation().getWorld() == null) {
                plugin.logs(
                        "",
                        " <c>O mundo do portal <f>" + portalName + " <c>nao foi encontrado!");
                return;
            }

            PortalLink portalLink = new PortalLink(portalName, origin, destination, enabled);
            portalLink.createStand();

            plugin.getPortalsMap().put(portalName.toLowerCase(), portalLink);
        });
    }

    public boolean isCreating(Player player) {
        return plugin.getCreatePortalMap().containsKey(player);
    }

    public PortalLink getPortalByName(String portalName) {
        return plugin.getPortalsMap().get(portalName.toLowerCase());
    }

    public List<PortalLink> getClosestPortalLink(Location location) {
        List<PortalLink> portalLinkList = new ArrayList<>();

        plugin.getPortalsMap().values().forEach(portalLink -> {

            if (!portalLink.isEnabled()) return;

            Portal origin = portalLink.getOrigin();
            Portal destination = portalLink.getDestination();

            if (compareWorld(location, origin.getLocation())) {

                if (location.distance(origin.getLocation()) < 5) portalLinkList.add(portalLink);
            }

            if (compareWorld(location, destination.getLocation())) {

                if (location.distance(destination.getLocation()) < 5) portalLinkList.add(portalLink);
            }
        });

        return portalLinkList;
    }

    public boolean compareWorld(Location a, Location b) {
        return a.getWorld().getName().equalsIgnoreCase(b.getWorld().getName());
    }

    void sendMessage(Player player, String... messages) {
        TXT.sendMessages(player, messages);
    }
}
