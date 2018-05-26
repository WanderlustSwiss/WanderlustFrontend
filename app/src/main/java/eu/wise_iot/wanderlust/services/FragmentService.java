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
 * provides appwide Fragmentservice
 */
public class FragmentService extends Application {
    private static final String TAG = "FragmentService";

    private static Activity activityUsed;
    private static final Stack<Fragment> fragmentBackStack = new Stack<>();
    private static FragmentService fragmentService;
    private FragmentTransaction transaction;
    private static final List<String> fragmentsWithAppBar = Arrays.asList(  Constants.TOUROVERVIEW_FRAGMENT,
                                                                            Constants.PROFILE_FRAGMENT,
                                                                            Constants.MAP_FRAGMENT,
                                                                            Constants.FILTER_FRAGMENT,
                                                                            Constants.RESULT_FILTER_FRAGMENT);
    private String lastManipulatedTag;

    private final List<String> drawerFragments = Arrays.asList(Constants.DISCLAIMER_FRAGMENT, Constants.MAP_FRAGMENT,
                                                                Constants.PROFILE_FRAGMENT, Constants.TOUROVERVIEW_FRAGMENT,
                                                                Constants.USER_GUIDE_FRAGMENT);

    public static synchronized FragmentService getInstance(Activity activity){
        if (fragmentService == null) {
            fragmentService = new FragmentService();
            activityUsed = activity;
        }
        return fragmentService;
    }
    public synchronized void pushBackStack(Fragment fragment){
        fragmentBackStack.push(fragment);
    }
    public synchronized Fragment popBackStack (){
        return fragmentBackStack.pop();
    }
    public synchronized boolean hasElements(){
        return !fragmentBackStack.empty();
    }
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

            //show or add the new Fragment
            if (fm.findFragmentByTag(targetFragmentTag) != null && !isDynamicTargetFragment) {
                ft.show(targetFragment);
            } else {
                lastManipulatedTag = targetFragmentTag;
                ft.add(R.id.content_frame, targetFragmentInstance, targetFragmentTag);
            }

            ft.hide(currentFragmentInstance);
            //add tracing with adding it to stack
            fragmentBackStack.push(currentFragmentInstance);
            if (!activityUsed.isFinishing() && !activityUsed.isDestroyed()) {
                //commit changes
                ft.commit();
                setAppbar(targetFragmentTag);
            }
    }
    public synchronized void performTransaction(boolean isDynamicTarget, String targetTag, Fragment targetInstance, Fragment currentInstance, boolean killBackStack){

            FragmentManager fm = activityUsed.getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment targetFragment = fm.findFragmentByTag(targetTag);

            //if dynamic content is inside of the targetFragment re-render it completely
            //by removing and then adding the target fragment
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
            //hide
            ft.hide(currentInstance);

            //commit changes
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
    public synchronized void clearStack(){
        fragmentBackStack.clear();
    }
    public synchronized boolean hasAppbar(String fragment){
        return (fragmentsWithAppBar.contains(fragment)) ? true : false;
    }
    public synchronized void setAppbar(String targetTag){
        if(hasAppbar(targetTag)){
            android.support.v7.app.ActionBar actionbar = ((AppCompatActivity) activityUsed).getSupportActionBar();
            if(actionbar != null) actionbar.show();
        }
    }
    public synchronized String getLastManipulated(){
        return lastManipulatedTag;
    }
    public synchronized void setLastManipulated(String tag){
        lastManipulatedTag = tag;
    }
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
    public void performSwitchInActivity(boolean isDynamicTarget, String targetTag, Fragment targetInstance){
        if (!activityUsed.isFinishing() && !activityUsed.isDestroyed()) {
        FragmentManager fm = activityUsed.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment targetFragment = fm.findFragmentByTag(targetTag);
        /*
        //hide all other fragments
        for(String drawerFragment : drawerFragments) {
            Fragment fragmentFind = fm.findFragmentByTag(drawerFragment);
            if ((fragmentFind != null) && fragmentFind.isAdded() && (fragmentFind != targetInstance)) {
                if (BuildConfig.DEBUG) Log.d(TAG, "hiding fragment: " + fragmentFind.getTag());
                ft.hide(fragmentFind);
            }
        }
        */
        //if dynamic content is inside of the targetFragment re-render it completely
        //by removing and then adding the target fragment
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

        //commit changes

            ft.commit();
            fm.executePendingTransactions();
            setAppbar(targetTag);
        } else {
            return;
        }
    }
}
