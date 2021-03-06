package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Trip;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.TripService;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TripDao:
 *
 * this model represents a self created tour by the user
 *
 * @author Rilind Gashi, Alexander Weinbeck
 * @license MIT
 */


public class TripDao extends DatabaseObjectAbstract {
    private static class Holder {
        private static final TripDao INSTANCE = new TripDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();
    private static TripService service;
    private final Box<Trip> routeBox;
    public static TripDao getInstance() {
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private TripDao() {
        routeBox = BOXSTORE.boxFor(Trip.class);
        service = ServiceGenerator.createService(TripService.class);
    }

    public long count() {
        return routeBox.count();
    }

    public long count(Property searchedColumn, String searchPattern) {
        return find(searchedColumn, searchPattern).size();
    }

    public long count(Property searchedColumn, long searchPattern){
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * Update an existing Trip in the database.
     *
     * @param trip (required).
     */
    public Trip update(Trip trip) {
        routeBox.put(trip);
        return trip;
    }

    /**
     * insert a trip local and remote
     *
     * @param tour a tour, from which the backend creates an trip, which can be inserted to the database
     * @param handler
     */
    public void create(final Tour tour, final FragmentHandler handler) {
        Call<Trip> call = service.createTrip(tour);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Trip trip = response.body();
                    long remoteTripId = trip.getTrip_id();
                    trip.setTrip_id(0);
                    routeBox.put(response.body());
                    trip.setTrip_id(remoteTripId);
                    handler.onResponse(new ControllerEvent<>(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent<Trip>(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                handler.onResponse(new ControllerEvent<Trip>(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * get trip out of the remote database by entity
     *
     * @param id
     * @param handler
     */
    public void retrieve(int id, final FragmentHandler handler) {
        Call<Trip> call = service.retrieveTrip(id);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if (response.isSuccessful()) {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }
    /*
    public void update(int id, final AbstractModel trip, final FragmentHandler handler){
        Call<Trip> call = service.updateTrip(id, (Trip)trip);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if(response.isSuccessful()){
                    routeBox.put((Trip)trip);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR,null));
            }
        });
    }
    */

    public void delete(final AbstractModel trip, final FragmentHandler handler) {
        Trip deletableTrip = (Trip) trip;
        Call<Trip> call = service.deleteTrip((int) deletableTrip.getTrip_id());
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if (response.isSuccessful()) {
                    routeBox.remove((Trip) trip);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public void retrieveAll(final FragmentHandler handler) {
        Call<Trip> call = service.retrieveAllTrips();
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if (response.isSuccessful())
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public List<Trip> find() {
        return routeBox.getAll();
    }

    public Trip findOne(Property searchedColumn, String searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Trip findOne(Property searchedColumn, long searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public List<Trip> find(Property searchedColumn, String searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Trip> find(Property searchedColumn, long searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Trip> find(Property searchedColumn, boolean searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern) {
        routeBox.remove(findOne(searchedColumn, searchPattern));
    }

    public void deleteByPattern(Property searchedColumn, String searchPattern) {
        routeBox.remove(findOne(searchedColumn, searchPattern));
    }
}
