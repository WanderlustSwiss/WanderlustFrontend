package eu.wise_iot.wanderlust.views;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ResultFilterController;
import eu.wise_iot.wanderlust.controllers.TourOverviewController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.views.adapters.ResultFilterRVAdapter;

/**
 * Fragment that contains the filtering functionality for all tours
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class ResultFilterFragment extends Fragment {
    private static final String TAG = "ResultFilterFragment";
    private static FilterFragment.FilterSetting settingsSet;
    private ResultFilterController resultFilterController;
    private Context context;
    private ResultFilterRVAdapter adapterRoutes;
    private final LinkedList<Tour> listFilteredTours = new LinkedList<>();
    private ProgressBar pbToursFiltered;
    private RecyclerView rvToursFiltered;
    private TextView tvToursFilteredPlaceholder;
    private int currentPage = 0;

    public static ResultFilterFragment newInstance(FilterFragment.FilterSetting setting) {
        settingsSet = setting;
        Bundle args = new Bundle();
        ResultFilterFragment fragment = new ResultFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        resultFilterController = new ResultFilterController();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tour_filter_result, container, false);

        // set up the RecyclerView 1
        pbToursFiltered = rootView.findViewById(R.id.pbTourResult);
        rvToursFiltered = rootView.findViewById(R.id.rvFilteredTours);
        tvToursFilteredPlaceholder = rootView.findViewById(R.id.tvToursFilteredPlaceholder);

        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvToursFiltered.setLayoutManager(verticalLayoutManager);
        adapterRoutes = new ResultFilterRVAdapter(context, listFilteredTours);
        adapterRoutes.setClickListener(this::onItemClickImages);
        rvToursFiltered.setAdapter(adapterRoutes);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_vertical));
        rvToursFiltered.addItemDecoration(itemDecorator);

        resultFilterController.getFilteredTours(event -> {
            switch (event.getType()) {
                case OK:
                    //get all needed information from server db
                    List<Tour> list  = new LinkedList<>((List<Tour>) event.getModel());
                    listFilteredTours.addAll(list);
                    currentPage++;
                    if (BuildConfig.DEBUG) Log.d(TAG, "Getting filtered Tours: Server response arrived");
                    //get all the images needed and save them on the device
                    //getDataFromServer(listFilteredTours);
                    adapterRoutes.notifyDataSetChanged();
                    if(adapterRoutes.getItemCount() > 0) {
                        rvToursFiltered.setVisibility(View.VISIBLE);
                        tvToursFilteredPlaceholder.setVisibility(View.GONE);
                        pbToursFiltered.setVisibility(View.GONE);
                    } else {
                        rvToursFiltered.setVisibility(View.GONE);
                        tvToursFilteredPlaceholder.setVisibility(View.VISIBLE);
                        pbToursFiltered.setVisibility(View.GONE);
                    }


                    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            switch (newState) {
                                case RecyclerView.SCROLL_STATE_IDLE:
                                    if (BuildConfig.DEBUG) Log.d(TAG,"The RecyclerView is not scrolling");
                                    int myCellWidth = rvToursFiltered.getChildAt(0).getMeasuredHeight();
                                    final int offset = rvToursFiltered.computeHorizontalScrollOffset();
                                    int position = offset / myCellWidth;
                                    //if (BuildConfig.DEBUG) Log.d(TAG, "pos: "+position);
                                    if ((position - (10 * currentPage)) > 5) {
                                        resultFilterController.getFilteredTours(controllerEvent -> {
                                            switch (controllerEvent.getType()) {
                                                case OK:
                                                    //get all needed information from server db
                                                    //if (BuildConfig.DEBUG) Log.d(TAG,"added new page " + currentPage);
                                                    LinkedList<Tour> newList = new LinkedList<>((List<Tour>)controllerEvent.getModel());
                                                    currentPage++;
                                                    listFilteredTours.addAll(newList);
                                                    //getDataFromServer(listFilteredTours);
                                                    adapterRoutes.notifyDataSetChanged();
                                                    break;
                                                default:
                                                    if (BuildConfig.DEBUG) Log.d(TAG,"Server response ERROR: " + controllerEvent.getType().name());
                                                    break;
                                            }
                                        },settingsSet.rating, settingsSet.distanceS, settingsSet.distanceE, currentPage ,settingsSet.durationS,
                                                settingsSet.durationE, settingsSet.region,
                                                settingsSet.name,
                                                resultFilterController.getDifficultiesByArray(settingsSet.cbT1, settingsSet.cbT2,settingsSet.cbT3,settingsSet.cbT4,settingsSet.cbT5,settingsSet.cbT6));
                                    }
                                    if (BuildConfig.DEBUG) Log.d(TAG,"Scroll idle");
                                    break;
                                case RecyclerView.SCROLL_STATE_DRAGGING:
                                    if (BuildConfig.DEBUG) Log.d(TAG,"Scrolling now");
                                    break;
                            }
                        }
                    };
                    rvToursFiltered.addOnScrollListener(mScrollListener);

                    break;
            }
        },settingsSet.rating, settingsSet.distanceS, settingsSet.distanceE, currentPage ,settingsSet.durationS,
                settingsSet.durationE, settingsSet.region,
                settingsSet.name, resultFilterController.getDifficultiesByArray(settingsSet.cbT1, settingsSet.cbT2,settingsSet.cbT3,settingsSet.cbT4,settingsSet.cbT5,settingsSet.cbT6));

        return rootView;
    }
    /**
     * handles click in Recyclerview item
     * @param view
     * @param tour
     */
    protected void onItemClickImages(View view, Tour tour) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Tour ImageInfo Clicked and event triggered ");

        Fragment fragment = getFragmentManager().findFragmentByTag(Constants.TOUR_FRAGMENT);
        if(fragment.isAdded()) {
            getFragmentManager().beginTransaction()
                    .hide(this)
                    .remove(getFragmentManager().findFragmentByTag(Constants.TOUR_FRAGMENT))
                    .add(R.id.content_frame,TourFragment.newInstance(tour))
                    //.addToBackStack(Constants.TOUR_FRAGMENT)
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .hide(this)
                    .add(R.id.content_frame,TourFragment.newInstance(tour))
                    //.addToBackStack(Constants.TOUR_FRAGMENT)
                    .commit();
        }
        //((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    /**
     * retrieve all images from the database
     * @param tours
     */
    private static void getDataFromServer(List<Tour> tours){
        TourOverviewController toc = new TourOverviewController();
        //get thumbnail for each tour
        for(Tour ut : tours){
            try {
                toc.downloadThumbnail(ut.getTour_id(), 1, controllerEvent -> {
                    switch (controllerEvent.getType()) {
                        case OK:
                            if (BuildConfig.DEBUG) Log.d(TAG, "Server response thumbnail downloading: " + controllerEvent.getType().name());
                            break;
                        default:
                            if (BuildConfig.DEBUG) Log.d(TAG, "Server response thumbnail ERROR: " + controllerEvent.getType().name());
                    }
                });
            } catch (Exception e){
                if (BuildConfig.DEBUG) Log.d(TAG, "Server response ERROR: " + e.getMessage());
            }
        }
    }
}
