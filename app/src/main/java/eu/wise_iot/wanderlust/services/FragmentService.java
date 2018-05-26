package eu.wise_iot.wanderlust.services;


import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;


/**
 * provides appwide Fragmentservice
 */
public class FragmentService extends Application {
    private static final String TAG = "FragmentService";

    private static Activity activityUsed;
    private static final Stack<Fragment> fragmentBackStack = new Stack<>();
    private static FragmentService fragmentService;
    private FragmentTransaction transaction;
    private static final List<String> fragmentsWithAppBar = Arrays.asList(Constants.TOUROVERVIEW_FRAGMENT,Constants.PROFILE_FRAGMENT, Constants.MAP_FRAGMENT, Constants.FILTER_FRAGMENT);
    private String lastManipulatedTag;

    public static synchronized FragmentService getInstance(Activity activity){
        if (fragmentService == null) {
            fragmentService = new FragmentService();
            activityUsed = activity;
        }
        return fragmentService;
    }
    public void pushBackStack(Fragment fragment){
        fragmentBackStack.push(fragment);
    }
    public Fragment popBackStack (){
        return fragmentBackStack.pop();
    }
    public boolean hasElements(){
        return !fragmentBackStack.empty();
    }
    public void performTraceTransaction(boolean isDynamicTargetFragment, String targetFragmentTag, Fragment targetFragmentInstance, Fragment currentFragmentInstance){

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
    public void performTransaction(boolean isDynamicTarget, String targetTag, Fragment targetInstance, Fragment currentInstance, boolean killBackStack){
        if (!activityUsed.isFinishing()) {
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
            ft.commit();
            setAppbar(targetTag);
        }
        //clear backstack
        if(killBackStack) clearStack();
    }
    public void clearStack(){
        fragmentBackStack.clear();
    }
    public boolean hasAppbar(String fragment){
        return (fragmentsWithAppBar.contains(fragment)) ? true : false;
    }
    public void setAppbar(String targetTag){
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
}
