package eu.wise_iot.wanderlust.services;

/**
 * Created by truee on 11/24/2017.
 */

public class LoginUser {
    private String identifier;
    private String password;

    LoginUser(String identifier, String password){
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier(){
        return identifier;
    }

    public String getPassword(){
        return password;
    }
}
