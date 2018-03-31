package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Region;
import eu.wise_iot.wanderlust.services.RegionService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegionDao extends DatabaseObjectAbstract {

    private static class Holder {
        private static final RegionDao INSTANCE = new RegionDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static RegionDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private static RegionService service;
    private final Box<Region> regionBox;

    /**
     * Constructor.
     */

    private RegionDao() {
        regionBox = BOXSTORE.boxFor(Region.class);
        service = ServiceGenerator.createService(RegionService.class);
    }

    /**
     * Update all region in the database.
     */
    public void retrive() {
        Call<List<Region>> call = service.retrieveAllRegions();
        call.enqueue(new Callback<List<Region>>() {
            @Override
            public void onResponse(Call<List<Region>> call, Response<List<Region>> response) {
                if (response.isSuccessful()) {
                    regionBox.removeAll();
                    for (Region region : response.body()) {
                        regionBox.put(region);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Region>> call, Throwable t) {

            }
        });
    }

    /**
     * Return a list with all devices
     *
     * @return List<Region>
     */
    public List<Region> find() {
        return regionBox.getAll();
    }

    /**
     * Searching for a single region with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return Region which match to the search pattern in the searched columns
     */
    public Region findOne(Property searchedColumn, String searchPattern) {
        return regionBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Region findOne(Property searchedColumn, long searchPattern) {
        return regionBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }
}
