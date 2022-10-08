package online.labmaster.taposmartplug.api.inbound;

import online.labmaster.taposmartplug.api.LoginParam;

public class LoginRequest {

    public static final String LOGIN_DEVICE_METHOD = "login_device";

    private final String method;
    private final LoginParam params;

    public LoginRequest(String method, String username, String password) {
        this.method = method;
        this.params = new LoginParam(username, password);
    }

    public String getMethod() {
        return method;
    }

    public LoginParam getParams() {
        return params;
    }
}
