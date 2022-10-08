package online.labmaster.taposmartplug.api.inbound;

public class LoginResponse extends TapoResponse {

    public Token result;

    public class Token {
        public String token;
    }

}
