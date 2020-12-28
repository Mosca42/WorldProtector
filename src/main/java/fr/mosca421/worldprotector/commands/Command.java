package fr.mosca421.worldprotector.commands;

public enum Command {
    REGION("region"),
    FLAG("flag"),
    HELP("help"),
    ADD("add"),
    REMOVE("remove"),
    INFO("info"),
    NAME("name"),
    EXPAND("expand"),
    VERT("vert"),
    LIST("list"),
    DEFINE("define"),
    REDEFINE("redefine"),
    TELEPORT("teleport"),
    PRIORITY("priority"),
    PRIORITY_GET("getpriority"),
    PRIORITY_SET("setpriority"),
    PLAYER("player"),
    PLAYER_REMOVE("removeplayer"),
    PLAYER_ADD("addplayer");

    private final String cmdString;

    Command(final String cmdString){
        this.cmdString = cmdString;
    }

    @Override
    public String toString() {
        return cmdString;
    }
}
