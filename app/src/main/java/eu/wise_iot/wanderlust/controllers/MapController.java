package eu.wise_iot.wanderlust.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.AddressPoint;
import eu.wise_iot.wanderlust.models.DatabaseModel.GeoObject;
import eu.wise_iot.wanderlust.models.DatabaseModel.HashtagResult;
import eu.wise_iot.wanderlust.models.DatabaseModel.MapSearchResult;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PublicTransportPoint;
import eu.wise_iot.wanderlust.services.HashtagService;

import eu.wise_iot.wanderlust.services.SacService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.views.MapFragment;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * MapController: The Map controller which handles requests of the map fragment
 *
 * @author Joshua Meier
 */
public class MapController {
    private final String NOMINATIM_SERVICE_URL = "http://nominatim.openstreetmap.org/";
    private final String SBB_SERVICE_URL = "https://data.sbb.ch/api/records/1.0/search/";
    private final String OPENPOI_SERVICE_URL = "http://openpois.net/poiquery.php?";
    private static final String TAG = "MapController";
    private final Fragment fragment;


    /**
     * Create a map contoller
     */
    public MapController(Fragment fragment) {
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
                result -> {
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
                }, error -> Toast.makeText(fragment.getActivity(), R.string.map_nothing_found, Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);

    }


    public void serachHashtag(int hashtagId, GeoPoint point1, GeoPoint point2, FragmentHandler<List<Poi>> fragmentHandler) {
        HashtagService service = ServiceGenerator.createService(HashtagService.class);
        Call<List<Poi>> call = service.retrievePoisByArea(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(), point2.getLongitude(), hashtagId);
        call.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(@NonNull Call<List<Poi>> call, @NonNull retrofit2.Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    fragmentHandler.onResponse(new ControllerEvent<>(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    fragmentHandler.onResponse(new ControllerEvent<>(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Poi>> call, @NonNull Throwable t) {
                fragmentHandler.onResponse(new ControllerEvent<>(EventType.NETWORK_ERROR));
            }
        });
    }

    public void suggestHashtags(String query, FragmentHandler<List<HashtagResult>> fragmentHandler) {
        HashtagService service = ServiceGenerator.createService(HashtagService.class);
        Call<List<HashtagResult>> call = service.retrievePoisByTag(query.substring(1));
        call.enqueue(new Callback<List<HashtagResult>>() {
            @Override
            public void onResponse(@NonNull Call<List<HashtagResult>> call, @NonNull retrofit2.Response<List<HashtagResult>> response) {
                int x = 3;
                fragmentHandler.onResponse(new ControllerEvent<>(EventType.getTypeByCode(response.code()), response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<List<HashtagResult>> call, @NonNull Throwable t) {
                fragmentHandler.onResponse(new ControllerEvent<>(EventType.NETWORK_ERROR));
            }
        });

    }

    public void searchExternalPOIGeoObjects(GeoPoint centerGeoPoint, int radius, final FragmentHandler handler) {
        String url = OPENPOI_SERVICE_URL
                + "lat=" + centerGeoPoint.getLatitude() +
                "&lon=" + centerGeoPoint.getLongitude() +
                "&radius=" + radius +
                "&maxfeatures=25" +
                "&format=application/json";

        RequestQueue queue = Volley.newRequestQueue(fragment.getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            JsonArray jRecords = jsonObject.get("records").getAsJsonArray();

            List<PublicTransportPoint> publicTransportPoints = new ArrayList<>(jRecords.size());

            for (int i = 0; i < jRecords.size(); i++) {
                JsonObject jField = jRecords.get(i).getAsJsonObject().get("fields").getAsJsonObject();
                String busStopDescription = jField.get("name").getAsString();
                JsonArray jGeoPoints = jField.get("geopos").getAsJsonArray();
                GeoPoint geoPoint = new GeoPoint(jGeoPoints.get(0).getAsDouble(), jGeoPoints.get(1).getAsDouble());
                int id = jField.get("nummer").getAsInt();
                PublicTransportPoint gPublicTransportPoint = new PublicTransportPoint(geoPoint, busStopDescription, id);
                publicTransportPoints.add(gPublicTransportPoint);
            }
            handler.onResponse(new ControllerEvent<>(EventType.OK, publicTransportPoints));



        }, error -> handler.onResponse(new ControllerEvent<List<PublicTransportPoint>>(EventType.NETWORK_ERROR)));

        queue.add(stringRequest);

    }

    public void searchPublicTransportStations(GeoPoint centerGeoPoint, int rows, int radius, final FragmentHandler handler) {
        String url = SBB_SERVICE_URL
                + "?dataset=didok-liste"
                + "&geofilter.distance=" + centerGeoPoint.getLatitude() + "," + centerGeoPoint.getLongitude() + "," + radius + ""
                + "&rows=" + rows;

        RequestQueue queue = Volley.newRequestQueue(fragment.getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            JsonArray jRecords = jsonObject.get("records").getAsJsonArray();

            List<PublicTransportPoint> publicTransportPoints = new ArrayList<>(jRecords.size());

            for (int i = 0; i < jRecords.size(); i++) {
                JsonObject jField = jRecords.get(i).getAsJsonObject().get("fields").getAsJsonObject();
                String busStopDescription = jField.get("name").getAsString();
                JsonArray jGeoPoints = jField.get("geopos").getAsJsonArray();
                GeoPoint geoPoint = new GeoPoint(jGeoPoints.get(0).getAsDouble(), jGeoPoints.get(1).getAsDouble());
                int id = jField.get("nummer").getAsInt();
                PublicTransportPoint gPublicTransportPoint = new PublicTransportPoint(geoPoint, busStopDescription, id);

                publicTransportPoints.add(gPublicTransportPoint);
            }
            handler.onResponse(new ControllerEvent<>(EventType.OK, publicTransportPoints));



        }, error -> handler.onResponse(new ControllerEvent<List<PublicTransportPoint>>(EventType.NETWORK_ERROR)));

        queue.add(stringRequest);

    }

    public void searchSac(GeoPoint point1, GeoPoint point2, FragmentHandler<List<GeoObject>> fragmentHandler) {
        SacService service = ServiceGenerator.createService(SacService.class);
        Call<List<GeoObject>> call = service.retrieveSacPoisByArea(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(), point2.getLongitude());
        call.enqueue(new Callback<List<GeoObject>>() {
            @Override
            public void onResponse(@NonNull Call<List<GeoObject>> call, @NonNull retrofit2.Response<List<GeoObject>> response) {
                if (response.isSuccessful()) {
                    fragmentHandler.onResponse(new ControllerEvent<>(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    fragmentHandler.onResponse(new ControllerEvent<>(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GeoObject>> call, @NonNull Throwable t) {
                fragmentHandler.onResponse(new ControllerEvent<>(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * Search address by coodinates
     *
     * @param latitude     Latitude
     * @param longitude    Longitude
     * @param maxResults   The number of results requested within the query
     * @param handler      The fragment handler, which deals with the response
     */
    public void searchCoordinates(double latitude, double longitude,
                                  int maxResults, final FragmentHandler handler){
        try {
            String url = NOMINATIM_SERVICE_URL
                    + "search?"
                    + "q=" + URLEncoder.encode(Double.toString(latitude)
                    + "," + Double.toString(longitude), "utf-8")
                    + "&format=json"
                    + "&addressdetails=1"
                    + "&limit=" + maxResults;
            RequestQueue queue = Volley.newRequestQueue(fragment.getActivity());

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                JsonParser parser = new JsonParser();
                JsonElement json = parser.parse(response);
                JsonArray jResults = json.getAsJsonArray();
          //      JsonObject jsonObject = new Gson().fromJson(response, JsonArray.class);
           //     JsonArray jResults = jsonObject.getAsJsonArray();
                JsonObject jResult = jResults.get(0).getAsJsonObject();
                AddressPoint gAddress = new AddressPoint();
                JsonObject jAddress = jResult.get("address").getAsJsonObject();

                if (jAddress.has("path")) {
                    gAddress.setName(jAddress.get("path").getAsString());
                }else{
                    if (jAddress.has("suburb")) {
                        gAddress.setName(jAddress.get("suburb").getAsString());
                    }
                }
                if (jAddress.has("city")) {
                    gAddress.setCity(jAddress.get("city").getAsString());
                }

                if(jAddress.has("state")){
                    gAddress.setState(jAddress.get("state").getAsString());
                }

                handler.onResponse(new ControllerEvent<>(EventType.OK, gAddress));



            }, error -> handler.onResponse(new ControllerEvent<AddressPoint>(EventType.NETWORK_ERROR)));

            queue.add(stringRequest);
        }catch(IOException ex){
            handler.onResponse(new ControllerEvent<AddressPoint>(EventType.NETWORK_ERROR));
        }

    }
}
