package eu.wise_iot.wanderlust.models.DatabaseModel;

import java.util.ArrayList;

public class LoginUser {
    static private ArrayList<String> cookies = new ArrayList<>();
    private String identifier;
    private String password;


    public LoginUser(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }


    public static ArrayList<String> getCookies() {
        return cookies;
    }

    public static void setCookies(ArrayList<String> cookiesC) {
        cookies = cookiesC;
    }

    public static void clearCookies() {
        cookies.clear();
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }
}
