package dev.cromo29.ultraportals.listeners;

import dev.cromo29.durkcore.updater.UpdateType;
import dev.cromo29.durkcore.updater.event.UpdaterEvent;
import dev.cromo29.ultraportals.UltraPortalsPlugin;
import dev.cromo29.ultraportals.objects.PortalLink;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Iterator;

public class TickEvent implements Listener {

    private final UltraPortalsPlugin plugin;

    public TickEvent(UltraPortalsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTick(UpdaterEvent event) {
        if (event.getType() != UpdateType.FASTEST) return;

        Iterator<PortalLink> portalLinkIterator = plugin.getPortalsMap().values().iterator();

        portalLinkIterator.forEachRemaining(PortalLink::update);
    }
}
