package dev.cromo29.ultraportals;

import dev.cromo29.durkcore.api.DurkPlugin;
import dev.cromo29.durkcore.util.GsonManager;
import dev.cromo29.ultraportals.command.PortalCMD;
import dev.cromo29.ultraportals.listeners.CreatePortalEvent;
import dev.cromo29.ultraportals.listeners.TickEvent;
import dev.cromo29.ultraportals.listeners.UsePortalEvent;
import dev.cromo29.ultraportals.manager.PortalManager;
import dev.cromo29.ultraportals.objects.PortalLink;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UltraPortalsPlugin extends DurkPlugin {

    private Map<String, PortalLink> portalsMap;
    private Map<Player, PortalLink> createPortalMap;

    private GsonManager portalGson;
    private PortalManager portalManager;

    @Override
    public void onStart() {
        this.portalsMap = new HashMap<>();
        this.createPortalMap = new HashMap<>();

        String path = getDataFolder().getPath() + File.separator + "storage";

        this.portalGson = new GsonManager(path, "portals.json").prepareGson();

        registerCommand(new PortalCMD(this));

        setListeners(new CreatePortalEvent(this), new UsePortalEvent(this), new TickEvent(this));

        portalManager = new PortalManager(this);
        portalManager.loadPortals();
    }

    @Override
    public void onStop() {

        portalsMap.values().forEach(PortalLink::removeStand);

    }

    public static UltraPortalsPlugin get() {
        return getPlugin(UltraPortalsPlugin.class);
    }


    public Map<String, PortalLink> getPortalsMap() {
        return portalsMap;
    }

    public Map<Player, PortalLink> getCreatePortalMap() {
        return createPortalMap;
    }

    public GsonManager getPortalGson() {
        return portalGson;
    }

    public PortalManager getPortalManager() {
        return portalManager;
    }

}
