package eu.wise_iot.wanderlust.views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.controllers.TourOverviewController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.services.AsyncUITask;
import eu.wise_iot.wanderlust.views.adapters.ToursOverviewRVAdapter;
import eu.wise_iot.wanderlust.views.controls.LoadingDialog;


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
    private TourOverviewController tourOverviewController;
    private int currentPage = 0;
    private ImageController imageController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        imageController = ImageController.getInstance();
        listTours = new LinkedList<>();
        tourOverviewController = new TourOverviewController();
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
        super.onPrepareOptionsMenu(menu);
        getActivity().invalidateOptionsMenu();
        MenuItem menuItem = menu.findItem(R.id.filterIcon);
        if(menuItem != null)
            menuItem.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filterIcon:
                if (BuildConfig.DEBUG) Log.d(TAG,"Filterbutton clicked changing to Filterfragment");
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, FilterFragment.newInstance(), Constants.FILTER_FRAGMENT)
                        .addToBackStack(Constants.FILTER_FRAGMENT)
                        .commit();
                break;
        }
        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentPage = 0;
        //PicassoCache.clearCache(Picasso.with(context)); //https://stackoverflow.com/questions/22016382/invalidate-cache-in-picasso
        View view = inflater.inflate(R.layout.fragment_tour_overview, container, false);
        tvToursAllPlaceholder = view.findViewById(R.id.tvToursAllPlaceholder);
        // set up the RecyclerView Tours
        rvTours = view.findViewById(R.id.rvTouren);
        pbTours = view.findViewById(R.id.pbTouren);
        rvTours.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        adapterRoutes = new ToursOverviewRVAdapter(context, listTours, getActivity());
        adapterRoutes.setClickListener(this::onItemClickImages);
        rvTours.setAdapter(adapterRoutes);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_horizontal));
        rvTours.addItemDecoration(itemDecorator);

        tvToursFavoritePlaceholder = view.findViewById(R.id.tvToursFavoritePlaceholder);
        // set up the RecyclerView favorites
        rvFavorites = view.findViewById(R.id.rvFavorites);
        pbFavorites = view.findViewById(R.id.pbFavorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        adapterFavs = new ToursOverviewRVAdapter(context, favTours, getActivity());
        adapterFavs.setClickListener(this::onItemClickImages);
        rvFavorites.setAdapter(adapterFavs);

        tvToursRecentPlaceholder = view.findViewById(R.id.tvToursRecentPlaceholder);
        // set up the RecyclerView favorites
        rvRecent = view.findViewById(R.id.rvRecent);
        pbRecent = view.findViewById(R.id.pbRecent);
        rvRecent.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        adapterRecent = new ToursOverviewRVAdapter(context, recentTours, getActivity());
        adapterRecent.setClickListener(this::onItemClickImages);
        rvRecent.setAdapter(adapterRecent);

        recentTours.clear();
        recentTours.addAll(tourOverviewController.getRecentTours());
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
        tourOverviewController.getAllTours(currentPage, event -> {
            switch (event.getType()) {
                case OK:
                    //get all needed information from server db
                    currentPage++;
                    listTours.addAll((List<Tour>) event.getModel());

                    if (BuildConfig.DEBUG) Log.d(TAG, "Getting Tours: Server response arrived");

                    adapterRoutes.notifyDataSetChanged();
                    if(adapterRoutes.getItemCount() > 0) {
                        rvTours.setVisibility(View.VISIBLE);
                        tvToursAllPlaceholder.setVisibility(View.GONE);
                        pbTours.setVisibility(View.GONE);
                    } else {
                        rvTours.setVisibility(View.GONE);
                        tvToursAllPlaceholder.setText(getResources().getText(R.string.tour_filter_noresult));
                        tvToursAllPlaceholder.setVisibility(View.VISIBLE);
                        pbTours.setVisibility(View.GONE);
                    }
                    break;
                case NETWORK_ERROR:
                    if(adapterFavs.getItemCount() > 0) {
                        rvTours.setVisibility(View.VISIBLE);
                        tvToursAllPlaceholder.setVisibility(View.GONE);
                        pbTours.setVisibility(View.GONE);
                    } else {
                        rvTours.setVisibility(View.GONE);
                        tvToursAllPlaceholder.setText(getResources().getText(R.string.tour_filter_no_internet));
                        tvToursAllPlaceholder.setVisibility(View.VISIBLE);
                        pbTours.setVisibility(View.GONE);
                    }
                default:
                    if (BuildConfig.DEBUG) Log.d(TAG, "Server response ERROR: " + event.getType().name());

            }
        });

        //get favorized tours
        tourOverviewController.getAllFavoriteTours(controllerEvent -> {
            switch (controllerEvent.getType()) {
                case OK:
                    if (BuildConfig.DEBUG) Log.d(TAG, "refresh Favorites");
                    favTours.clear();
                    favTours.addAll((List<Tour>) controllerEvent.getModel());
                    //getDataFromServer(favTours);
                    if (BuildConfig.DEBUG) Log.d(TAG, favTours.toString());
                    adapterFavs.notifyDataSetChanged();

                    if(adapterFavs.getItemCount() > 0) {
                        rvFavorites.setVisibility(View.VISIBLE);
                        tvToursFavoritePlaceholder.setVisibility(View.GONE);
                        pbFavorites.setVisibility(View.GONE);
                    } else {
                        rvFavorites.setVisibility(View.GONE);
                        tvToursFavoritePlaceholder.setText(getResources().getText(R.string.tour_filter_noresult));
                        tvToursFavoritePlaceholder.setVisibility(View.VISIBLE);
                        pbFavorites.setVisibility(View.GONE);
                    }
                    break;
                case NETWORK_ERROR:
                    if(adapterFavs.getItemCount() > 0) {
                        rvFavorites.setVisibility(View.VISIBLE);
                        tvToursFavoritePlaceholder.setVisibility(View.GONE);
                        pbFavorites.setVisibility(View.GONE);
                    } else {
                        rvFavorites.setVisibility(View.GONE);
                        tvToursFavoritePlaceholder.setText(getResources().getText(R.string.tour_filter_no_internet));
                        tvToursFavoritePlaceholder.setVisibility(View.VISIBLE);
                        pbFavorites.setVisibility(View.GONE);
                    }
                default:
                    if (BuildConfig.DEBUG) Log.d("ERROR", "failed to get Favorites");
            }
        });

        RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (BuildConfig.DEBUG) Log.d(TAG, "The RecyclerView is not scrolling anymore");
                        if ((((LinearLayoutManager) rvTours.getLayoutManager()).findLastVisibleItemPosition() - (25 * (currentPage - 1))) > 15) {
                            tourOverviewController.getAllTours(currentPage, controllerEvent -> {
                                switch (controllerEvent.getType()) {
                                    case OK:
                                        LinkedList<Tour> newList = new LinkedList<>((List<Tour>)controllerEvent.getModel());
                                        currentPage++;
                                        listTours.addAll(newList);
                                        adapterRoutes.notifyItemRangeChanged((listTours.size() - 5),(newList.size() + 5));
                                        if (BuildConfig.DEBUG) Log.d(TAG, "added new page " + currentPage);
                                        break;
                                    default:
                                        if (BuildConfig.DEBUG) Log.d(TAG, "Server response ERROR: " + controllerEvent.getType().name());
                                        Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (BuildConfig.DEBUG) Log.d(TAG, "Scroll idle");
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        if (BuildConfig.DEBUG) Log.d(TAG, "Scrolling now");
                        break;
                }
            }
        };
        rvTours.addOnScrollListener(mScrollListener);
        return view;
    }

    /**
     * handles click on an recycler view item
     * @param view representing the recycler view item
     * @param tour representing the tour of the clicked item
     */
    @SuppressLint("StaticFieldLeak")
    private void onItemClickImages(View view, Tour tour) {
        //distinguish what element was clicked by resource id
        switch (view.getId()) {
            case R.id.tourOVFavoriteButton:
                if (BuildConfig.DEBUG) Log.d(TAG,"Tour Favorite Clicked and event triggered ");
                ImageButton ibFavorite = view.findViewById(R.id.tourOVFavoriteButton);
                if (BuildConfig.DEBUG) Log.d(TAG, "favorite get unfavored: " + tour.getTour_id());
                long favId = tourOverviewController.getTourFavoriteId(tour.getTour_id());
                if(favId != -1) {
                    tourOverviewController.deleteFavorite(favId, controllerEvent -> {
                        switch (controllerEvent.getType()) {
                            case OK:
                                //favorizedTours.remove(tour.getTour_id());
                                if (BuildConfig.DEBUG) Log.d(TAG, "favorite successfully deleted " + tour.getTour_id());
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
                                if (BuildConfig.DEBUG) Log.d(TAG, "favorite failure while deleting " + tour.getTour_id());
                                Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_SHORT);
                        }
                    });
                } else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "favorite gets favored: " + tour.getTour_id());
                    tourOverviewController.setFavorite(tour, controllerEvent -> {
                        switch (controllerEvent.getType()){
                            case OK:
                                if (BuildConfig.DEBUG) Log.d("Touroverview rv", "favorite succesfully added " + tour.getTour_id());
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
                                if (BuildConfig.DEBUG) Log.d("Touroverview rv", "favorite failure while adding " + tour.getTour_id());
                                Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.tourOVSaveButton:
                ImageButton ibSave = view.findViewById(R.id.tourOVSaveButton);
                TourController tourController = new TourController(tour);
                if(tourController.isSaved()){
                    tourController.unsetSaved(getActivity(), controllerEvent -> {
                        switch (controllerEvent.getType()){
                            case OK:
                                ibSave.setColorFilter(ContextCompat.getColor(context, R.color.heading_icon_unselected));
                                break;
                            default:
                                if (BuildConfig.DEBUG) Log.d(TAG , "failed");
                        }
                    });
                }else{
                    tourController.setSaved(getActivity() , controllerEvent -> {
                        switch (controllerEvent.getType()){
                            case OK:
                                ibSave.setColorFilter(ContextCompat.getColor(context, R.color.medium));
                                break;
                            default:
                                if (BuildConfig.DEBUG) Log.d(TAG, "failed");
                        }
                    });
                }
                break;
            case R.id.tourOVShareButton:
                if (BuildConfig.DEBUG) Log.d(TAG,"Tour share");
                shareTour(tour);
                break;
            case R.id.tour_rv_item:
                if (BuildConfig.DEBUG) Log.d(TAG,"Tour ImageInfo Clicked and event triggered ");
                LoadingDialog.getDialog().show(getActivity());
                AsyncUITask.getHandler().queueTask(() -> {
                    switch(EventType.getTypeByCode(tourOverviewController.checkIfTourExists(tour))) {
                        case OK:
                            if (BuildConfig.DEBUG) Log.d(TAG,"Server Response arrived -> OK Tour was found");
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, TourFragment.newInstance(tour), Constants.TOUR_FRAGMENT)
                                    .addToBackStack(Constants.TOUROVERVIEW_FRAGMENT)
                                    .commit();
                            //((AppCompatActivity) getActivity()).getSupportActionBar().show();
                            break;
                        case NOT_FOUND:
                            if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> Tour was not found");
                            Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_tour_not_existing), Toast.LENGTH_LONG).show();
                            recentTours.remove(tour);
                            adapterRecent.notifyDataSetChanged();
                            tourOverviewController.removeRecentTour(tour);
                            break;
                        case SERVER_ERROR:
                            if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> SERVER ERROR");
                            Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_server_error_get_tour), Toast.LENGTH_LONG).show();
                            break;
                        case NETWORK_ERROR:
                            Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_LONG).show();
                            if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> NETWORK ERROR");
                            break;
                        default:
                            if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> UNDEFINED ERROR");
                            Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_general_error), Toast.LENGTH_LONG).show();
                    }

                    LoadingDialog.getDialog().dismiss();
                });
                break;
        }

    }

//
//    private class AsyncCheckTourExists extends AsyncTask<Tour, Void, Tour> {
//        private Integer responseCode;
//        private final TimingLogger t1 = new TimingLogger("TIMINGS","check tour exists");
//        private final Activity activity;
//
//        AsyncCheckTourExists(Activity activity){
//            this.activity = activity;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            LoadingDialog.getDialog().show(activity);
//            t1.addSplit("showing progress");
//        }
//        @Override
//        protected Tour doInBackground(Tour... params) {
//            TimingLogger t1 = new TimingLogger("TIMINGS","check tour sequential");
//            responseCode = tourOverviewController.checkIfTourExists(params[0]);
//            t1.addSplit("got response");
//            return params[0];
//        }
//        @Override
//        protected void onPostExecute(Tour tour) {
//            t1.addSplit("handle response");
//
//            switch(EventType.getTypeByCode(responseCode)) {
//                case OK:
//                    if (BuildConfig.DEBUG) Log.d(TAG,"Server Response arrived -> OK Tour was found");
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.content_frame, TourFragment.newInstance(tour), Constants.TOUR_FRAGMENT)
//                            .addToBackStack(Constants.TOUROVERVIEW_FRAGMENT)
//                            .commit();
//                    //((AppCompatActivity) getActivity()).getSupportActionBar().show();
//                    break;
//                case NOT_FOUND:
//                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> Tour was not found");
//                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_tour_not_existing), Toast.LENGTH_LONG).show();
//                    recentTours.remove(tour);
//                    adapterRecent.notifyDataSetChanged();
//                    tourOverviewController.removeRecentTour(tour);
//                    break;
//                case SERVER_ERROR:
//                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> SERVER ERROR");
//                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_server_error_get_tour), Toast.LENGTH_LONG).show();
//                    break;
//                case NETWORK_ERROR:
//                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_LONG).show();
//                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> NETWORK ERROR");
//                    break;
//                default:
//                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> UNDEFINED ERROR");
//                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_general_error), Toast.LENGTH_LONG).show();
//            }
//
//            LoadingDialog.getDialog().dismiss();
//            t1.dumpToLog();
//        }
//    }
    /**
     * shares the tour with other apps
     */
    private void shareTour(Tour tour){
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        String description = tour.getDescription() + getResources().getString(R.string.app_domain);
        shareIntent.putExtra(Intent.EXTRA_TEXT, description);
        shareIntent.putExtra(Intent.EXTRA_TITLE, tour.getTitle());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, tour.getTitle());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title_tour)));
    }
}
