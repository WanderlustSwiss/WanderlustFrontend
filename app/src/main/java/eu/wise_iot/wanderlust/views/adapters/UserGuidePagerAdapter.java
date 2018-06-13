package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.wise_iot.wanderlust.R;

public class UserGuidePagerAdapter extends PagerAdapter {

    private Context mContext;

    public UserGuidePagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return ModelObject.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ModelObject customPagerEnum = ModelObject.values()[position];
        return mContext.getString(customPagerEnum.getTitleResId());
    }

    private enum ModelObject {
        POI(R.string.user_guide_page_poi, R.layout.fragment_user_guide_p_poi),
        SEARCH(R.string.user_guide_page_search, R.layout.fragment_user_guide_p_search),
        FILTER(R.string.user_guide_page_filter, R.layout.fragment_user_guide_p_filter),
        PROFILE(R.string.user_guide_page_profile, R.layout.fragment_user_guide_p_profile),
        DASHBOARD(R.string.user_guide_page_dashboard, R.layout.fragment_user_guide_p_dashboard),
        FINISH(R.string.user_guide_page_start, R.layout.fragment_user_guide_p_finish);

        private int mTitleResId;
        private int mLayoutResId;

        ModelObject(int titleResId, int layoutResId) {
            mTitleResId = titleResId;
            mLayoutResId = layoutResId;
        }

        public int getTitleResId() {
            return mTitleResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }
    }
}
