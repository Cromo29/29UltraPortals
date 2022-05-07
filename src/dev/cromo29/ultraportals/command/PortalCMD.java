package dev.cromo29.ultraportals.command;

import dev.cromo29.durkcore.API.DurkCommand;
import dev.cromo29.ultraportals.UltraPortalsPlugin;

import java.util.List;

public class PortalCMD extends DurkCommand {

    private UltraPortalsPlugin plugin;

    public PortalCMD(UltraPortalsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void perform() {

        if (isArgsLength(2)) {

            if (isArgAtIgnoreCase(0, "criar")) {

                plugin.getPortalManager().createPortal(asPlayer(), argAt(1));

            } else if (isArgAtIgnoreCase(0, "deletar")) {

                plugin.getPortalManager().deletePortal(asPlayer(), argAt(1));

            } else if (isArgAtIgnoreCase(0, "editar")) {

                plugin.getPortalManager().changePortal(asPlayer(), argAt(1));

            } else sendHelp();

        } else if (isArgsLength(1)) {

            if (isArgAtIgnoreCase(0, "recarregar")) {

                plugin.getPortalGson().reload();
                plugin.getPortalManager().loadPortals();

                sendMessage(" <b>☾ <7>Você reiniciou os portais!");

            } else if (isArgAtIgnoreCase(0, "lista")) {

                plugin.getPortalManager().portalsList(asPlayer());

            } else sendHelp();

        } else sendHelp();
    }

    @Override
    public boolean canConsolePerform() {
        return false;
    }

    @Override
    public String getPermission() {
        return "29UltraPortals.*";
    }

    @Override
    public String getCommand() {
        return "portal";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    private void sendHelp() {

        sendMessages("",
                " - <e>/portal criar <nome> <f>- <7>Criar um portal.",
                " - <e>/portal deletar <nome> <f>- <7>Excluir um portal.",
                " - <e>/portal editar <nome> <f>- <7>Ativar/Desativar um portal.",
                " - <e>/portal recarregar <f>- <7>Recarregar portais.",
                " - <e>/portal lista <f>- <7>Lista de portais."
                , "");

    }
}
