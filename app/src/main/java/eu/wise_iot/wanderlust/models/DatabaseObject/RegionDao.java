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

    private RegionDao() {
        regionBox = BOXSTORE.boxFor(Region.class);
        service = ServiceGenerator.createService(RegionService.class);
    }

    /**
     * get all region in the database.
     */
    public void retrieveAll() {
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

    public List<Region> find() {
        return regionBox.getAll();
    }

    public List<Region> find(Property searchedColumn, String searchPattern) {
        return regionBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public Region findOne(Property searchedColumn, String searchPattern) {
        List<Region> list = regionBox.query().filter(entity -> entity.getName().toLowerCase().contains(searchPattern.toLowerCase()) || searchPattern.toLowerCase().contains(entity.getName().toLowerCase())).build().find();
        if(list.size() >= 1){
            return list.get(0);
        } else{
            return null;
        }
    }

    public Region findOne(Property searchedColumn, long searchPattern) {
        return regionBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }
}
