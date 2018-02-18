package imt.logofinder.Model;

/**
 * Created by Tom on 18/02/2018.
 */

public class ServerOptions {
    String serverName = null;
    String serverPath = null;
    int isDeletable = 1;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public int getIsDeletable() {
        return isDeletable;
    }

    public void setIsDeletable(int isDeletable) {
        this.isDeletable = isDeletable;
    }

    public ServerOptions() {

    }

    public ServerOptions(String serverName, String serverPath, int isDeletable) {

        this.serverName = serverName;
        this.serverPath = serverPath;
        this.isDeletable = isDeletable;
    }

    public ServerOptions(String serverName, String serverPath) {
        this.serverName = serverName;
        this.serverPath = serverPath;
    }
}
