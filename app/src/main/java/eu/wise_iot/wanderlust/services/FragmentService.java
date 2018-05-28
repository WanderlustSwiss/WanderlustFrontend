package eu.wise_iot.wanderlust.services;


import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.views.DisclaimerFragment;
import eu.wise_iot.wanderlust.views.MapFragment;
import eu.wise_iot.wanderlust.views.ProfileFragment;
import eu.wise_iot.wanderlust.views.StartupLoginFragment;
import eu.wise_iot.wanderlust.views.StartupRegistrationFragment;
import eu.wise_iot.wanderlust.views.TourOverviewFragment;
import eu.wise_iot.wanderlust.views.UserGuideFragment;


/**
 * Provides app-wide fragment handling
 * see method description for usage description
 * for correct usage also set a new fragment in the activity under all fragments
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class FragmentService extends Application {
    private static final String TAG = "FragmentService";
    private static Activity activityUsed;
    private static final Stack<Fragment> fragmentBackStack = new Stack<>();
    private static FragmentService fragmentService;

    /**
     * Define all fragments with an appbar,
     * if not specified completely the service will NOT work
     */
    private static final List<String> fragmentsWithAppBar = Arrays.asList(  Constants.TOUROVERVIEW_FRAGMENT,
                                                                            Constants.PROFILE_FRAGMENT,
                                                                            Constants.MAP_FRAGMENT,
                                                                            Constants.FILTER_FRAGMENT,
                                                                            Constants.RESULT_FILTER_FRAGMENT);
    /**
     * last used fragment need for back-stack internally
     */
    private String lastManipulatedTag;

    /**
     * all fragments that are inside the drawer, maybe needed in further development
     * keep this class even if unused
     */
    private final List<String> drawerFragments = Arrays.asList(Constants.DISCLAIMER_FRAGMENT,
                                                                Constants.MAP_FRAGMENT,
                                                                Constants.PROFILE_FRAGMENT,
                                                                Constants.TOUROVERVIEW_FRAGMENT,
                                                                Constants.USER_GUIDE_FRAGMENT);

    /**
     * singleton factory
     * @param activity mostly passed in via this (activity) or getActivity() (fragment)
     * @return service
     */
    public static synchronized FragmentService getInstance(Activity activity){
        if (fragmentService == null) {
            fragmentService = new FragmentService();
            activityUsed = activity;
        }
        return fragmentService;
    }

    /**
     * push given fragment to back-stack, only used internally
     * @param fragment
     */
    public synchronized void pushBackStack(Fragment fragment){
        fragmentBackStack.push(fragment);
    }

    /**
     * get fragment from back-stack
     * @return Fragment on the back-stack
     */
    public synchronized Fragment popBackStack (){
        return fragmentBackStack.pop();
    }

    /**
     * check if the back-stack has elements useful in some cases
     * @return boolean has elements
     */
    public synchronized boolean hasElements(){
        return !fragmentBackStack.empty();
    }

    /**
     * perform a transition from one fragment to another
     * use this method if you want to put the fragment on the back-stack and you can return to it
     * so do not provide an end fragment (such as tour fragment)
     *
     * @param isDynamicTargetFragment boolean if the target needs to be completely re-rendered
     * @param targetFragmentTag Tag of the fragment that is the target of the transition
     * @param targetFragmentInstance Instance of target
     * @param currentFragmentInstance current Fragment (this inside of fragment)
     */
    public synchronized void performTraceTransaction(boolean isDynamicTargetFragment, String targetFragmentTag, Fragment targetFragmentInstance, Fragment currentFragmentInstance){

        FragmentManager fm = activityUsed.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment targetFragment = fm.findFragmentByTag(targetFragmentTag);

        //if dynamic content is inside of the targetFragment re-render it completely
        //by removing and then adding the target fragment
        if (isDynamicTargetFragment) {
            Fragment oldFragment = fm.findFragmentByTag(targetFragmentTag);
            if (oldFragment != null) {
                ft.remove(oldFragment);
            }
        }

        //show or add the new Fragment depending on dynamic or static content
        if (fm.findFragmentByTag(targetFragmentTag) != null && !isDynamicTargetFragment) {
            ft.show(targetFragment);
        } else {
            lastManipulatedTag = targetFragmentTag;
            ft.add(R.id.content_frame, targetFragmentInstance, targetFragmentTag);
        }

        //hide the current fragment
        ft.hide(currentFragmentInstance);
        //add tracing with adding it to stack
        fragmentBackStack.push(currentFragmentInstance);
        //commit changes only if activity is available
        if (!activityUsed.isFinishing() && !activityUsed.isDestroyed()) {
            //commit changes
            ft.commit();
            setAppbar(targetFragmentTag);
        }
    }

    /**
     * provides a method to do a fragment transition without remembering the source fragment
     * if needed kill the backstack of fragmentservice to prevent backbutton press in source fragment
     *
     * @param isDynamicTarget boolean if the target needs to be completely rerendered
     * @param targetTag Tag of the fragment that is the target of the transition
     * @param targetInstance Instance of target
     * @param currentInstance current Fragment (this inside of fragment)
     * @param killBackStack if the backstack should be deleted
     */
    public synchronized void performTransaction(boolean isDynamicTarget, String targetTag, Fragment targetInstance, Fragment currentInstance, boolean killBackStack){

        FragmentManager fm = activityUsed.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment targetFragment = fm.findFragmentByTag(targetTag);

        //if dynamic content is inside of the targetFragment re-render it completely
        //by removing the fragment tag
        if (isDynamicTarget) {
            Fragment oldFragment = fm.findFragmentByTag(targetTag);
            if (oldFragment != null) {
                ft.remove(oldFragment);
            }
        }

        //show or add the new Fragment
        if (fm.findFragmentByTag(targetTag) != null && !isDynamicTarget) {
            ft.show(targetFragment);
        } else {
            ft.add(R.id.content_frame, targetInstance, targetTag);
        }
        //hide the current fragment
        ft.hide(currentInstance);

        //commit changes only if activity is available
        if (!activityUsed.isFinishing() && !activityUsed.isDestroyed()) {
            ft.commit();
            //fm.executePendingTransactions();
            setAppbar(targetTag);
        } else {
            return;
        }
        //clear backstack
        if(killBackStack) clearStack();
    }

    /**
     * clear the backstack so no tracing or backbutton usage possible
     */
    public synchronized void clearStack(){
        fragmentBackStack.clear();
    }

    /**
     * helper method which checks if appbar is needed
     * @param fragment to check if appbar needed
     * @return boolean wheter the fragment has an appbar or not
     */
    public synchronized boolean hasAppbar(String fragment){
        return (fragmentsWithAppBar.contains(fragment)) ? true : false;
    }

    /**
     * set the appbar for given target fragment tag
     * @param targetTag to set the appbar
     */
    public synchronized void setAppbar(String targetTag){
        if(hasAppbar(targetTag)){
            android.support.v7.app.ActionBar actionbar = ((AppCompatActivity) activityUsed).getSupportActionBar();
            if(actionbar != null) actionbar.show();
        }
    }

    /**
     * get current visible fragment tag / shown fragment tag
     * @return fragment tag
     */
    public synchronized String getLastManipulated(){
        return lastManipulatedTag;
    }

    /**
     * set last visible fragment tag / shown fragment tag
     * used internally
     *
     * @return fragment tag
     */
    public synchronized void setLastManipulated(String tag){
        lastManipulatedTag = tag;
    }
    /**
     * handle the press on the back button for the given fragment
     *
     * @param fragment to handle
     */
    public synchronized void handleBackstackPress(Fragment fragment){
        FragmentManager fm = activityUsed.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //Don't do anything with back button if user is on login or registration screen
        if ((fragment instanceof StartupRegistrationFragment)
                || (fragment instanceof StartupLoginFragment)
                || (fragment instanceof MapFragment)
                || (fragment instanceof TourOverviewFragment)
                || (fragment instanceof ProfileFragment)
                || (fragment instanceof DisclaimerFragment)
                || (fragment instanceof UserGuideFragment)) {

            clearStack();
        }
        //use backstack to go back
        else if(hasElements()) {
            Fragment targetFragment = popBackStack();

            if(BuildConfig.DEBUG) Log.d(TAG, "entered Backstack state: fragment from stack: " + targetFragment.getTag() );
            if(BuildConfig.DEBUG) Log.d(TAG, "entered Backstack state: fragment from ui: " + fragment.getTag() );
            setLastManipulated(targetFragment.getTag());
            ft.hide(fragment)
                    .show(targetFragment)
                    .commit();
            //check if there is an appbar needed if it was removed
            if(hasAppbar(targetFragment.getTag())){
                ((AppCompatActivity) activityUsed).getSupportActionBar().show();
            }
        }
    }
}


//    public void performSwitchInActivity(boolean isDynamicTarget, String targetTag, Fragment targetInstance){
//        if (!activityUsed.isFinishing() && !activityUsed.isDestroyed()) {
//        FragmentManager fm = activityUsed.getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//
//        Fragment targetFragment = fm.findFragmentByTag(targetTag);
//        /*
//        //hide all other fragments
//        for(String drawerFragment : drawerFragments) {
//            Fragment fragmentFind = fm.findFragmentByTag(drawerFragment);
//            if ((fragmentFind != null) && fragmentFind.isAdded() && (fragmentFind != targetInstance)) {
//                if (BuildConfig.DEBUG) Log.d(TAG, "hiding fragment: " + fragmentFind.getTag());
//                ft.hide(fragmentFind);
//            }
//        }
//        */
//        //if dynamic content is inside of the targetFragment re-render it completely
//        //by removing and then adding the target fragment
//        if (isDynamicTarget) {
//            Fragment oldFragment = fm.findFragmentByTag(targetTag);
//            if (oldFragment != null) {
//                ft.remove(oldFragment);
//            }
//        }
//
//        //show or add the new Fragment
//        if (fm.findFragmentByTag(targetTag) != null && !isDynamicTarget) {
//            ft.show(targetFragment);
//        } else {
//            ft.add(R.id.content_frame, targetInstance, targetTag);
//        }
//
//        //commit changes
//
//            ft.commit();
//            fm.executePendingTransactions();
//            setAppbar(targetTag);
//        } else {
//            return;
//        }
//    }