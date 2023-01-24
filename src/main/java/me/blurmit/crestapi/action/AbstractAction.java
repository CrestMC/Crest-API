package me.blurmit.crestapi.action;

import me.blurmit.crestapi.server.Connection;

public abstract class AbstractAction {

    private String[] aliases;
    protected ActionManager actionManager;

    public AbstractAction() {
        aliases = new String[0];
        actionManager = null;
    }

    public AbstractAction(String... aliases) {
        this.aliases = aliases;
        this.actionManager = null;
    }

    public AbstractAction(ActionManager actionManager, String... aliases) {
        this.aliases = aliases;
        this.actionManager = actionManager;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String... aliases) {
        this.aliases = aliases;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public void setActionManager(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    public abstract String getName();

    public abstract void handleConnection(Connection connection) throws Exception;

}
