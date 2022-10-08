package online.labmaster.taposmartplug.api.outbound;

import online.labmaster.taposmartplug.api.LoginParam;

public class LoginRequest extends TapoRequest {

    private static final String LOGIN_DEVICE_METHOD = "login_device";

    private final LoginParam params;

    public LoginRequest(String username, String password) {
        super(LOGIN_DEVICE_METHOD);
        this.params = new LoginParam(username, password);
    }

    public LoginParam getParams() {
        return params;
    }
}
