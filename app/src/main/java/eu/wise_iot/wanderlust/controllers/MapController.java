package eu.wise_iot.wanderlust.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.location.Address;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.MapSearchResult;
import eu.wise_iot.wanderlust.views.MapFragment;

/**
 * MapController: The Map controller which handles requests of the map fragment
 *
 * @author Joshua Meier
 * @license MIT
 */
public class MapController {
    private static final String NOMINATIM_SERVICE_URL = "http://nominatim.openstreetmap.org/";
    private MapFragment fragment;


    /**
     * Create a map contoller
     */
    public MapController(MapFragment fragment) {
        this.fragment = fragment;
    }


    /**
     * Initializes the search bar on the top of the application
     *
     * @param locationName The location name used for the query
     * @param maxResults   The number of results requested within the query
     * @param handler      The fragment handler, which deals with the response
     */
    public void searchPlace(String locationName, int maxResults, final FragmentHandler handler)
            throws IOException {
        String url = NOMINATIM_SERVICE_URL
                + "search?"
                + "q=" + URLEncoder.encode(locationName, "utf-8")
                + "&format=json"
                + "&addressdetails=1"
                + "&limit=" + maxResults
                + "&polygon_geojson=1";

        RequestQueue queue = Volley.newRequestQueue(fragment.getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        JsonParser parser = new JsonParser();
                        JsonElement json = parser.parse(result);
                        JsonArray jResults = json.getAsJsonArray();
                        List<MapSearchResult> list = new ArrayList<>(jResults.size());
                        for (int i = 0; i < jResults.size(); i++) {
                            JsonObject jResult = jResults.get(i).getAsJsonObject();
                            MapSearchResult gAddress = new MapSearchResult();
                            gAddress.setLatitued(jResult.get("lat").getAsDouble());
                            gAddress.setLongitude(jResult.get("lon").getAsDouble());


                            JsonObject jAddress = jResult.get("address").getAsJsonObject();

                            if (jAddress.has("peak")) {
                                gAddress.setLocality(jAddress.get("peak").getAsString());
                            } else if (jAddress.has("river")) {
                                gAddress.setLocality(jAddress.get("river").getAsString());
                            } else if (jAddress.has("water")) {
                                gAddress.setLocality(jAddress.get("water").getAsString());
                            } else if (jAddress.has("hamlet")) {
                                gAddress.setLocality(jAddress.get("hamlet").getAsString());
                            } else if (jAddress.has("city")) {
                                gAddress.setLocality(jAddress.get("city").getAsString());
                            } else if (jAddress.has("town")) {
                                gAddress.setLocality(jAddress.get("town").getAsString());
                            } else if (jAddress.has("village")) {
                                gAddress.setLocality(jAddress.get("village").getAsString());
                            } else if (jAddress.has("state")) {
                                gAddress.setLocality(jAddress.get("state").getAsString());
                            }

                            if (jResult.has("geojson") && !jResult.get("osm_type").getAsString().equals("node")) {
                                JsonObject jPolygonPointsObj = jResult.get("geojson").getAsJsonObject();
                                if (jPolygonPointsObj.get("type").getAsString().equals("MultiPolygon")) {
                                    JsonArray jPolygonPoints = jPolygonPointsObj.get("coordinates").getAsJsonArray();
                                    for (int k = 0; k < jPolygonPoints.size(); ++k) { // go through polygons
                                        JsonArray polygon = jPolygonPoints.get(k).getAsJsonArray();
                                        for (int j = 0; j < polygon.size(); ++j) { // go through multiPolygons
                                            JsonArray subPolygon = polygon.get(j).getAsJsonArray();
                                            ArrayList<GeoPoint> polygonPoints = new ArrayList<>();
                                            for (int v = 0; v < subPolygon.size(); ++v) { // go through cordinates
                                                JsonArray jCoords = subPolygon.get(v).getAsJsonArray();
                                                double lon = jCoords.get(0).getAsDouble();
                                                double lat = jCoords.get(1).getAsDouble();
                                                GeoPoint p = new GeoPoint(lat, lon);
                                                polygonPoints.add(p);
                                            }
                                            gAddress.setPolygon(polygonPoints);
                                        }
                                    }

                                } else if (jPolygonPointsObj.get("type").getAsString().equals("Polygon")) {
                                    ArrayList<GeoPoint> polygonPoints = new ArrayList<>();
                                    JsonArray jPolygonPoints = jPolygonPointsObj.get("coordinates").getAsJsonArray().get(0).getAsJsonArray();
                                    for (int j = 0; j < jPolygonPoints.size(); j++) {
                                        JsonArray jCoords = jPolygonPoints.get(j).getAsJsonArray();
                                        double lon = jCoords.get(0).getAsDouble();
                                        double lat = jCoords.get(1).getAsDouble();
                                        GeoPoint p = new GeoPoint(lat, lon);
                                        polygonPoints.add(p);
                                    }
                                    gAddress.setPolygon(polygonPoints);
                                }


                            }

                            list.add(gAddress);
                        }
                        handler.onResponse(new ControllerEvent(EventType.OK, list));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(fragment.getActivity(), R.string.map_nothing_found, Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }

}
