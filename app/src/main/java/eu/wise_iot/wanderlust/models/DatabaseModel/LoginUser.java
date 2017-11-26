package eu.wise_iot.wanderlust.models.DatabaseModel;

import java.util.HashSet;

import okhttp3.Cookie;

public class LoginUser {
    private String identifier;
    private String password;
    static private HashSet<String> cookies;


    public LoginUser(String identifier, String password){
        this.identifier = identifier;
        this.password = password;
    }


    public static HashSet<String> getCookies() {
        return cookies;
    }

    public static void setCookies(HashSet<String> cookiesC) {
        cookies = cookiesC;
    }
    public String getIdentifier(){
        return identifier;
    }

    public String getPassword(){
        return password;
    }
}
