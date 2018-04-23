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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoCache;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.Duration;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
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
    private ToursOverviewRVAdapter adapterRoutes, adapterFavs, adapterRecent;
    private List<Tour> listTours;
    private final List<Tour> favTours = new ArrayList<>();
    private final List<Tour> recentTours = new ArrayList<>();
    private RecyclerView rvTours, rvFavorites, rvRecent;
    private ProgressBar pbTours, pbFavorites, pbRecent;
    private TextView tvToursAllPlaceholder, tvToursFavoritePlaceholder, tvToursRecentPlaceholder;
    private TourOverviewController toc;
    private int currentPage = 0;
    private ImageController imageController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        imageController = ImageController.getInstance();
        listTours = new LinkedList<>();
        toc = new TourOverviewController();
        setHasOptionsMenu(true);
    }

    public static TourOverviewFragment newInstance() {
        Bundle args = new Bundle();
        TourOverviewFragment fragment = new TourOverviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.tour_filter_menu, menu);
        menu.removeItem(R.id.drawer_layout);
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        getActivity().invalidateOptionsMenu();
        if(menu.findItem(R.id.filterIcon) != null)
            menu.findItem(R.id.filterIcon).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filterIcon:
                Log.d(TAG,"Filterbutton clicked changing to Filterfragment");
                Fragment filterFragment = getFragmentManager().findFragmentByTag(Constants.FILTER_FRAGMENT);
                if(filterFragment == null) filterFragment = FilterFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, filterFragment, Constants.FILTER_FRAGMENT)
                        .commit();
                break;
        }
        return true;
    }

    /**
     * retrieve all images from the database
     * @param tours
     */
    private void getDataFromServer(List<Tour> tours){
        //get given favorites
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
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentPage = 0;
        PicassoCache.clearCache(Picasso.with(context)); //https://stackoverflow.com/questions/22016382/invalidate-cache-in-picasso
        View view = inflater.inflate(R.layout.fragment_tour_overview, container, false);

        tvToursAllPlaceholder = (TextView) view.findViewById(R.id.tvToursAllPlaceholder);
        // set up the RecyclerView Tours
        rvTours = (RecyclerView) view.findViewById(R.id.rvTouren);
        pbTours = (ProgressBar) view.findViewById(R.id.pbTouren);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvTours.setLayoutManager(horizontalLayoutManager);
        adapterRoutes = new ToursOverviewRVAdapter(context, listTours);
        adapterRoutes.setClickListener(this::onItemClickImages);
        rvTours.setAdapter(adapterRoutes);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_horizontal));
        rvTours.addItemDecoration(itemDecorator);

        tvToursFavoritePlaceholder = (TextView) view.findViewById(R.id.tvToursFavoritePlaceholder);
        // set up the RecyclerView favorites
        rvFavorites = (RecyclerView) view.findViewById(R.id.rvFavorites);
        pbFavorites = (ProgressBar) view.findViewById(R.id.pbFavorites);
        LinearLayoutManager horizontalLayoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvFavorites.setLayoutManager(horizontalLayoutManager2);
        adapterFavs = new ToursOverviewRVAdapter(context, favTours);
        adapterFavs.setClickListener(this::onItemClickImages);
        rvFavorites.setAdapter(adapterFavs);

        tvToursRecentPlaceholder = (TextView) view.findViewById(R.id.tvToursRecentPlaceholder);
        // set up the RecyclerView favorites
        rvRecent = (RecyclerView) view.findViewById(R.id.rvRecent);
        pbRecent = (ProgressBar) view.findViewById(R.id.pbRecent);
        LinearLayoutManager horizontalLayoutManager3 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvRecent.setLayoutManager(horizontalLayoutManager3);
        adapterRecent = new ToursOverviewRVAdapter(context, recentTours);
        adapterRecent.setClickListener(this::onItemClickImages);
        rvRecent.setAdapter(adapterRecent);

        recentTours.clear();
        recentTours.addAll(toc.getRecentTours());
        getDataFromServer(recentTours);
        adapterRecent.notifyDataSetChanged();

        if(adapterRecent.getItemCount() > 0) {
            rvRecent.setVisibility(View.VISIBLE);
            tvToursRecentPlaceholder.setVisibility(View.GONE);
            pbRecent.setVisibility(View.GONE);
        } else {
            rvRecent.setVisibility(View.GONE);
            tvToursRecentPlaceholder.setVisibility(View.VISIBLE);
            pbRecent.setVisibility(View.GONE);
        }

        //get all tours
        toc.getAllTours(currentPage, event -> {
            switch (event.getType()) {
                case OK:
                    //get all needed information from server db
                    List<Tour> list = (List<Tour>) event.getModel();
                    currentPage++;
                    for (Tour tour : list)
                        for (ImageInfo imageInfo : tour.getImagePaths())
                            imageInfo.setLocalDir(imageController.getTourFolder());

                    listTours.addAll(list);

                    Log.d(TAG, "Getting Tours: Server response arrived");
                    //get all the images needed and save them on the device
                    getDataFromServer(listTours);

                    adapterRoutes.notifyDataSetChanged();
                    if(adapterRoutes.getItemCount() > 0) {
                        rvTours.setVisibility(View.VISIBLE);
                        tvToursAllPlaceholder.setVisibility(View.GONE);
                        pbTours.setVisibility(View.GONE);
                    } else {
                        rvTours.setVisibility(View.GONE);
                        tvToursAllPlaceholder.setVisibility(View.VISIBLE);
                        pbTours.setVisibility(View.GONE);
                    }
                    break;
                default:
                    Log.d(TAG, "Server response ERROR: " + event.getType().name());
                    Toast.makeText(this.context,getResources().getText(R.string.msg_no_internet), Toast.LENGTH_SHORT);
            }
        });

        //get favorized tours
        toc.getAllFavoriteTours(controllerEvent -> {
            switch (controllerEvent.getType()) {
                case OK:
                    Log.d(TAG, "refresh Favorites");
                    List<Tour> list = (List<Tour>) controllerEvent.getModel();
                    TourOverviewFragment.this.favTours.clear();
                    for (Tour tour : list)
                        for (ImageInfo imageInfo : tour.getImagePaths())
                            imageInfo.setLocalDir(imageController.getTourFolder());

                    TourOverviewFragment.this.favTours.addAll(list);
                    getDataFromServer(favTours);
                    Log.d(TAG, favTours.toString());
                    TourOverviewFragment.this.adapterFavs.notifyDataSetChanged();

                    if(adapterFavs.getItemCount() > 0) {
                        rvFavorites.setVisibility(View.VISIBLE);
                        tvToursFavoritePlaceholder.setVisibility(View.GONE);
                        pbFavorites.setVisibility(View.GONE);
                    } else {
                        rvFavorites.setVisibility(View.GONE);
                        tvToursFavoritePlaceholder.setVisibility(View.VISIBLE);
                        pbFavorites.setVisibility(View.GONE);
                    }
                default:
                    Log.d("ERROR", "failed to get Favorites");
                    Toast.makeText(this.context,getResources().getText(R.string.msg_no_internet), Toast.LENGTH_SHORT);
            }
        });

        RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        Log.d(TAG, "The RecyclerView is not scrolling");
                        int myCellWidth = rvTours.getChildAt(0).getMeasuredWidth();
                        final int offset = rvTours.computeHorizontalScrollOffset();
                        int position = offset / myCellWidth;
                        Log.d(TAG, "Position=" + position + " " + myCellWidth + " " + offset );
                        if (20 < (position - (25*(currentPage-1)))) {
                            toc.getAllTours(currentPage, controllerEvent -> {
                                switch (controllerEvent.getType()) {
                                    case OK:
                                        LinkedList<Tour> newList = new LinkedList<>((List<Tour>)controllerEvent.getModel());
                                        currentPage++;
                                        listTours.addAll(newList);
                                        getDataFromServer(listTours);
                                        adapterRoutes.notifyDataSetChanged();
                                        Log.d(TAG, "added new page " + currentPage);
                                        break;
                                    default:
                                        Log.d(TAG, "Server response ERROR: " + controllerEvent.getType().name());
                                        Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_SHORT);
                                }
                            });
                        }
                        Log.d(TAG, "Scroll idle");
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        Log.d(TAG, "Scrolling now");
                        break;
                }
            }
        };
        rvTours.addOnScrollListener(mScrollListener);
        return view;
    }

    private void onItemClickImages(View view, Tour tour) {
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
                                adapterRecent.notifyDataSetChanged();
                                if(adapterFavs.getItemCount() > 0) {
                                    rvFavorites.setVisibility(View.VISIBLE);
                                    tvToursFavoritePlaceholder.setVisibility(View.GONE);
                                    pbFavorites.setVisibility(View.GONE);
                                } else {
                                    rvFavorites.setVisibility(View.GONE);
                                    tvToursFavoritePlaceholder.setVisibility(View.VISIBLE);
                                    pbFavorites.setVisibility(View.GONE);
                                }
                                break;
                            default:
                                Log.d(TAG, "favorite failure while deleting " + tour.getTour_id());
                                Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_SHORT);
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
                                adapterRoutes.notifyDataSetChanged();
                                adapterFavs.notifyDataSetChanged();
                                adapterRecent.notifyDataSetChanged();
                                if(adapterFavs.getItemCount() > 0) {
                                    rvFavorites.setVisibility(View.VISIBLE);
                                    tvToursFavoritePlaceholder.setVisibility(View.GONE);
                                    pbFavorites.setVisibility(View.GONE);
                                } else {
                                    rvFavorites.setVisibility(View.GONE);
                                    tvToursFavoritePlaceholder.setVisibility(View.VISIBLE);
                                    pbFavorites.setVisibility(View.GONE);
                                }
                                break;
                            default:
                                Log.d("Touroverview rv", "favorite failure while adding " + tour.getTour_id());
                                Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_SHORT);
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
                        adapterRoutes.notifyDataSetChanged();
                        adapterFavs.notifyDataSetChanged();
                        adapterRecent.notifyDataSetChanged();
                    }
                }else{
                    Log.d("SAVED", "nicht saved");
                    boolean set = controller.setSaved();
                    if(set){
                        ibSave.setColorFilter(ContextCompat.getColor(context, R.color.medium));
                        adapterRoutes.notifyDataSetChanged();
                        adapterFavs.notifyDataSetChanged();
                        adapterRecent.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.tour_rv_item:
                Log.d(TAG,"Tour ImageInfo Clicked and event triggered ");
                TourFragment tourFragment = TourFragment.newInstance(tour);
                Fragment oldTourFragment = getFragmentManager().findFragmentByTag(Constants.TOUR_FRAGMENT);
                if(oldTourFragment != null) {
                    getFragmentManager().beginTransaction()
                            .remove(oldTourFragment)
                            .commit();
                }
                getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, tourFragment, Constants.TOUR_FRAGMENT)
                        .addToBackStack(Constants.TOUROVERVIEW_FRAGMENT)
                        .commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                break;

            //the same can be applied to other components in Row_Layout.xml
        }
    }
}
