package eu.wise_iot.wanderlust.constants;

import org.osmdroid.util.GeoPoint;

/**
 * Defaults:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public interface Defaults {

    /* ZOOM */
    int ZOOM_STARTUP = 7;
    int ZOOM_SEARCH = 12;
    int ZOOM_ENLARGED = 16;

    /* GEO POINTS */
    GeoPoint GEO_POINT_CENTER_OF_SWITZERLAND = new GeoPoint(46.484, 8.1336);
}
