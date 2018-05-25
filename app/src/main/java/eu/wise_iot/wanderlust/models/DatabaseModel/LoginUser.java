package eu.wise_iot.wanderlust.models.DatabaseModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class LoginUser {

    static private ArrayList<String> cookies = new ArrayList<>();

    private final String identifier;
    private final String password;
    private String osVersion;
    private String deviceModel;
    private String resolution;
    private String serialNumber;

    public String  firstCookie;


    public LoginUser(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public static ArrayList<String> getCookies() {
        return cookies;
    }

    public void setDeviceStatistics(String osVersion, String deviceModel, String resolution, String serialNumber){
        this.osVersion = osVersion;
        this.deviceModel = deviceModel;
        this.resolution = resolution;
        this.serialNumber = serialNumber;
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

    public String getDeviceStatisticsUrl(){
        try {
            String osVersionString = "osVersion=" + URLEncoder.encode(osVersion, "UTF-8");
            String deviceModelString = "deviceModel=" + URLEncoder.encode(deviceModel, "UTF-8");
            String resolutionString = "resolution=" + URLEncoder.encode(resolution, "UTF-8");
            String serialNumberString = "serialNumber=" + URLEncoder.encode(serialNumber, "UTF-8");

            return '?' + osVersionString + '&' + deviceModelString + '&'
                    + resolutionString + '&' + serialNumberString;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
