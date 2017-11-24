package eu.wise_iot.wanderlust.constants;

import org.osmdroid.util.GeoPoint;

/**
 * Defaults:
 * @author Fabian Schwander
 * @license MIT
 */
public interface Defaults {

    /* ZOOM */
    int ZOOM_STARTUP = 7;
    int ZOOM_ENLARGED = 16;

    /* GEO POINTS */
    GeoPoint GEO_POINT_CENTER_OF_SWITZERLAND = new GeoPoint(46.484, 8.1336);
//    GeoPoint GEO_POINT_POI = new GeoPoint(47.27010, 9.40180); // Schäffler
    GeoPoint GEO_POINT_POI = new GeoPoint(46.58022299, 9.78543498); // Lai da Palpuogna
//    GeoPoint GEO_POINT_POI = new GeoPoint(47.48073, 8.21205); // FHNW at Brugg

    /* SERVER URL */
//    String URL_SERVER =  "http://172.20.10.3:8080/api/";      // Fabian local iPhone
//    String URL_SERVER =  "http://192.168.1.133:8080/api/";    // Fabian local home
//    String URL_SERVER =  "http://10.207.2.197:8080/api/";       // FHNW local
    String URL_SERVER = "http://77.109.171.251:8889/api/";
}
