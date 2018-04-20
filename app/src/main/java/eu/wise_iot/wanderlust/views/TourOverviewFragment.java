package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.controllers.TourOverviewController;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.views.adapters.ToursOverviewRVAdapter;


/**
 * TourOverviewFragment:
 *
 * shows favorized tours as well as all other tours which are available from the database
 * @author Fabian Schwander, Alexander Weinbeck
 * @license MIT
 */
public class TourOverviewFragment extends Fragment {
    private static final String TAG = "TourOverviewFragment";
    private Context context;
    private ToursOverviewRVAdapter adapterRoutes;
    private ToursOverviewRVAdapter adapterFavs;
    private LinkedList<Tour> listTours;
    private final List<Tour> favTours = new ArrayList<>();
    private RecyclerView rvTouren;
    private RecyclerView rvFavorites;
    private int currentPage = 0;
    private ImageController imageController;

    /**
     * Constructor of view
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        imageController = ImageController.getInstance();
    }
    /**
     * Static instance constructor.
     *
     * @return Fragment: TourOverviewFragment
     */
    public static TourOverviewFragment newInstance() {
        Bundle args = new Bundle();
        TourOverviewFragment fragment = new TourOverviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * retrieve all images from the database
     * @param tours
     */
    private static void getDataFromServer(List<Tour> tours){
        TourOverviewController toc = new TourOverviewController();
        //get given favorites
        toc.downloadDifficultyTypes();
        toc.downloadFavorites(controllerEvent -> {
            switch (controllerEvent.getType()) {
                case OK:
                    Log.d(TAG, "Server response getting favorites: " + controllerEvent.getType().name());
                    break;
                default:
                    Log.d(TAG, "Download favorites: Server response ERROR: " + controllerEvent.getType().name());
            }
        });
        //get thumbnail for each tour
        for(Tour ut : tours){
            try {
                toc.downloadThumbnail(ut.getTour_id(), 1, controllerEvent -> {
                    switch (controllerEvent.getType()) {
                        case OK:
                            Log.d(TAG, "Server response thumbnail downloading: " + controllerEvent.getType().name());
                            break;
                        default:
                            Log.d(TAG, "Server response thumbnail ERROR: " + controllerEvent.getType().name());
                    }
                });
            } catch (Exception e){
                Log.d(TAG, "Server response ERROR: " + e.getMessage());
            }
        }

    }

    /**
     * upon view creation
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View of holder
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TourOverviewController toc = new TourOverviewController();
        View view = inflater.inflate(R.layout.fragment_toursoverview, container, false);
        //fetch from db actual tours to feed recyclerview
        toc.getAllTours(new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent event) {
                switch (event.getType()) {
                    case OK:
                        //get all needed information from server db
                        listTours  = new LinkedList<>((List<Tour>)event.getModel());
                        currentPage++;
                        Log.d(TAG,"Getting Tours: Server response arrived");
                        //get all the images needed and save them on the device
                        getDataFromServer(listTours);

                        // set up the RecyclerView 1
                        rvTouren = (RecyclerView) view.findViewById(R.id.rvTouren);
                        rvTouren.setPadding(5,5,5,5);
                        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        rvTouren.setLayoutManager(horizontalLayoutManager);
                        adapterRoutes = new ToursOverviewRVAdapter(context, listTours);
                        adapterRoutes.setClickListener(this::onItemClickImages);
                        rvTouren.setAdapter(adapterRoutes);

                        DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
                        itemDecorator.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_horizontal));
                        rvTouren.addItemDecoration(itemDecorator);

                        //get favorited tours
                        toc.getAllFavoriteTours(controllerEvent -> {
                            switch (controllerEvent.getType()) {
                                case OK:
                                    Log.d("INFO","refresh Favorites");
                                    List<Tour> list = (List<Tour>) controllerEvent.getModel();

                                    for (Tour tour : list)
                                        for (ImageInfo imageInfo : tour.getImagePaths())
                                            imageInfo.setLocalDir(imageController.getTourFolder());

                                    TourOverviewFragment.this.favTours.addAll(list);
                                    getDataFromServer(favTours);
                                    Log.d("INFO",favTours.toString());
                                    TourOverviewFragment.this.adapterFavs.notifyDataSetChanged();
                                default:
                                    Log.d("ERROR","failed to get Favorites");
                                    break;
                            }
                        });

                        // set up the RecyclerView favorites
                        rvFavorites = (RecyclerView) view.findViewById(R.id.rvFavorites);
                        LinearLayoutManager horizontalLayoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        rvFavorites.setLayoutManager(horizontalLayoutManager2);
                        adapterFavs = new ToursOverviewRVAdapter(context, favTours);
                        adapterFavs.setClickListener(this::onItemClickImages);
                        rvFavorites.setAdapter(adapterFavs);

                        RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                switch (newState) {
                                    case RecyclerView.SCROLL_STATE_IDLE:
                                        Log.d(TAG,"The RecyclerView is not scrolling");
                                        int myCellWidth = rvTouren.getChildAt(0).getMeasuredWidth();
                                        final int offset = rvTouren.computeHorizontalScrollOffset();
                                        int position = offset / myCellWidth;
                                        //Log.d(TAG, "pos: "+position);
                                        if (5 < (position - (10*currentPage))) {
                                            toc.getAllTours(controllerEvent -> {
                                                switch (controllerEvent.getType()) {
                                                    case OK:
                                                        //get all needed information from server db
                                                        //Log.d(TAG,"added new page " + currentPage);
                                                        LinkedList<Tour> newList = new LinkedList<>((List<Tour>)controllerEvent.getModel());
                                                        currentPage++;
                                                        listTours.addAll(newList);
                                                        getDataFromServer(listTours);
                                                        adapterRoutes.notifyDataSetChanged();
                                                        break;
                                                    default:
                                                        Log.d(TAG,"Server response ERROR: " + controllerEvent.getType().name());
                                                        break;
                                                }
                                            },currentPage);
                                        }
                                        Log.d(TAG,"Scroll idle");
                                        break;
                                    case RecyclerView.SCROLL_STATE_DRAGGING:
                                        Log.d(TAG,"Scrolling now");
                                        break;
                                }
                            }
                        };
                        rvTouren.addOnScrollListener(mScrollListener);
                        break;
                    default:
                        Log.d(TAG,"Server response ERROR: " + event.getType().name());
                        //do nothing
                }
            }

            /**
             * handles click in Recyclerview
             * @param view
             * @param tour
             */
            public void onItemClickImages(View view, Tour tour) {
                switch (view.getId()) {
                    case R.id.favoriteButton:
                        Log.d(TAG,"Tour Favorite Clicked and event triggered ");
                        ImageButton ibFavorite = (ImageButton)view.findViewById(R.id.favoriteButton);
                        Log.d(TAG, "favorite get unfavored: " + tour.getTour_id());
                        long favId = toc.getTourFavoriteId(tour.getTour_id());
                        if(favId != -1) {
                            toc.deleteFavorite(favId, controllerEvent -> {
                                switch (controllerEvent.getType()) {
                                    case OK:
                                        //favorizedTours.remove(tour.getTour_id());
                                        Log.d(TAG, "favorite successfully deleted " + tour.getTour_id());
                                        ibFavorite.setColorFilter(ContextCompat.getColor(context, R.color.heading_icon_unselected));
                                        //remove tour from adapter dataset
                                        for(Tour tmpTour : favTours)
                                            if(tmpTour.getTour_id() == tour.getTour_id())
                                                favTours.remove(tmpTour);
                                        //notify observer of adapters
                                        adapterFavs.notifyDataSetChanged();
                                        adapterRoutes.notifyDataSetChanged();
                                        break;
                                    default:
                                        Log.d(TAG, "favorite failure while deleting " + tour.getTour_id());
                                }
                            });
                        } else {
                            Log.d(TAG, "favorite gets favored: " + tour.getTour_id());
                            toc.setFavorite(tour, controllerEvent -> {
                                switch (controllerEvent.getType()){
                                    case OK:
                                        Log.d("Touroverview rv", "favorite succesfully added " + tour.getTour_id());
                                        ibFavorite.setColorFilter(ContextCompat.getColor(context, R.color.highlight_main));
                                        //add tour to adapter dataset
                                        favTours.add(tour);
                                        //notify adapter of dataset change
                                        adapterFavs.notifyDataSetChanged();
                                        break;
                                    default:
                                        Log.d("Touroverview rv", "favorite failure while adding " + tour.getTour_id());
                                }
                            });
                        }
                        break;
                    case R.id.saveButton:
                        ImageButton ibSave = (ImageButton) view.findViewById(R.id.saveButton);
                        TourController controller = new TourController(tour);
                        Log.d("SAVE", "klickt...");
                        boolean saved = controller.isSaved();
                        if(saved){
                            Log.d("SAVE", "saved");
                            boolean unset = controller.unsetSaved();
                            if(unset){
                                ibSave.setColorFilter(ContextCompat.getColor(context, R.color.heading_icon_unselected));
                            }
                        }else{
                            Log.d("SAVED", "nicht saved");
                            boolean set = controller.setSaved();
                            if(set){
                                ibSave.setColorFilter(ContextCompat.getColor(context, R.color.medium));
                            }
                        }
                        break;
                    default:
                        Log.d(TAG,"Tour ImageInfo Clicked and event triggered ");
                        TourFragment tourFragment = TourFragment.newInstance(tour);
                        getFragmentManager().beginTransaction()
                                .add(R.id.content_frame, tourFragment, Constants.TOUR_FRAGMENT)
                                .addToBackStack(Constants.TOUR_FRAGMENT)
                                .commit();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                        break;
                    //the same can be applied to other components in Row_Layout.xml
                }
            }
        }, currentPage);

        return view;
    }

}
