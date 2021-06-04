package fr.mosca421.worldprotector.command;

public enum Command {
    WP("wp"),
    REGION("region"),
    DIMENSION("dimension"),
    FLAG("flag"),
    HELP("help"),
    ADD("add"),
    REMOVE("remove"),
    REMOVE_ALL("remove-all"),
    INFO("info"),
    NAME("name"),
    EXPAND("expand"),
    VERT("vert"),
    DEFAULT_Y("y_default"),
    LIST("list"),
    DEFINE("define"),
    REDEFINE("redefine"),
    TELEPORT("teleport"),
    ACTIVATE("activate"),
    DEACTIVATE("deactivate"),
    PRIORITY("priority"),
    PRIORITY_GET("getpriority"),
    PRIORITY_SET("setpriority"),
    PLAYER("player"),
    PLAYER_REMOVE("removeplayer"),
    PLAYER_ADD("addplayer"),
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
