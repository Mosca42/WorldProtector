package fr.mosca421.worldprotector.command;

public enum Command {
    WP("wp"),
    REGION("region"),
    DIMENSION("dimension"),
    FLAG("flag"),
    HELP("help"),
    ADD("add"),
    ADD_SHORT("+"),
    REMOVE("remove"),
    REMOVE_SHORT("-"),
    REMOVE_ALL("remove-all"),
    ALL("all"),
    INFO("info"),
    NAME("name"),
    EXPAND("expand"),
    VERT("vert"),
    DEFAULT_Y("y_default"),
    LIST("list"),
    DEFINE("define"),
    REDEFINE("redefine"),
    TELEPORT("teleport"),
    TELEPORT_SHORT("tp"),
    ACTIVATE("activate"),
    DEACTIVATE("deactivate"),
    PRIORITY("priority"),
    SET_PRIORITY("set-priority"),
    PLAYER("player"),
    Y1("Y1"),
    Y2("Y2");

    private final String cmdString;

    Command(final String cmdString) {
        this.cmdString = cmdString;
    }

    @Override
    public String toString() {
        return cmdString;
    }
}
