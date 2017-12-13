package eu.wise_iot.wanderlust.models.DatabaseModel;

import java.util.ArrayList;
import java.util.HashSet;

import okhttp3.Cookie;

public class LoginUser extends AbstractModel{
    private String identifier;
    private String password;
    static private ArrayList<String> cookies = new ArrayList<>();


    public LoginUser(String identifier, String password){
        this.identifier = identifier;
        this.password = password;
    }


    public static ArrayList<String> getCookies() {
        return cookies;
    }

    public static void setCookies(ArrayList<String> cookiesC) {
        cookies = cookiesC;
    }
    public String getIdentifier(){
        return identifier;
    }

    public String getPassword(){
        return password;
    }
}
