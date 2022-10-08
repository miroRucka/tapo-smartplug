package online.labmaster.taposmartplug.api;

public class LoginParam {

    private final String username;
    private final String password;

    public LoginParam(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
