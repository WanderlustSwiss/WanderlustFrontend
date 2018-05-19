package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserComment;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.views.TourFragment;


/**
 * MyAdapter:
 * provides adapter for recyclerview which is used by the comment list
 *
 * @author Simon Kaspar
 * @license MIT
 */
public class TourCommentRVAdapter extends RecyclerView.Adapter<TourCommentRVAdapter.ViewHolder> {
    private List<UserComment> comments  = Collections.emptyList();
    private final LayoutInflater mInflater;
    private final UserDao userDao;
    private final TourFragment fragment;
    private final User user;
    @SuppressWarnings("WeakerAccess")
    public ItemClickListener mClickListener;
    private final Context context;

    /**
     * data is passed into the constructor, here as a Tour
     * @param context
     * @param paramUserComments
     * @param paramFragment
     */
    public TourCommentRVAdapter(Context context, List<UserComment> paramUserComments,
                                TourFragment paramFragment) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.userDao = UserDao.getInstance();
        this.fragment = paramFragment;
        if (paramUserComments == null) paramUserComments = new ArrayList<>();
        this.user = userDao.getUser();
        this.comments = paramUserComments;
    }

    /**
     * inflates the row layout from xml when needed
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_tour_comment, parent, false);
        return new ViewHolder(view);
    }

    /**
     * binds the data to the view and textview in each row
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserComment userComment = this.comments.get(position);

        holder.tvNickname.setText(userComment.getNickname());
        holder.tvText.setText(userComment.getText());
        holder.tvUpdatedAt.setText(convertToTimeString(userComment.getUpdatedAt()));

        if (userComment.getUser() == user.getUser_id()){
            holder.tvDelete.setEnabled(true);
            holder.tvDelete.setVisibility(View.VISIBLE);
        }else{
            holder.tvDelete.setEnabled(false);
            holder.tvDelete.setVisibility(View.GONE);
        }

        holder.tvDelete.setOnClickListener((View v) -> fragment.deleteComment(userComment));
    }


    /**
     * return total number of rows
      * @return
     */
    @Override
    public int getItemCount() {
        return this.comments.size();
    }

    /**
     * convenience method for getting data at click position
     * @param id
     * @return
     */
    private UserComment getItem(int id) {
        return this.comments.get(id);
    }
    private String convertToTimeString(String date){
        DateTimeFormatter encodef = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DateTime dt = encodef.parseDateTime(date);
        long passedSeconds = (System.currentTimeMillis() - dt.getMillis()) / 1000;
        String before = context.getResources().getString(R.string.tour_comment_before);
        if (passedSeconds < 60){
            String timeMarker = context.getResources().getString(R.string.tour_comment_seconds);
            return before + " " + passedSeconds + " " + timeMarker;
        }else if (passedSeconds >= 60 && passedSeconds < 3600){
            passedSeconds = passedSeconds / 60;
            String timeMarker = context.getResources().getString(R.string.tour_comment_minutes);
            return before + " " + passedSeconds + " " + timeMarker;
        }else if (passedSeconds >= 3600 && passedSeconds < (3600 * 24)){
            passedSeconds = passedSeconds / 3600;
            String timeMarker;
            if (passedSeconds == 1) timeMarker = context.getResources().getString(R.string.tour_comment_hour);
            else timeMarker = context.getResources().getString(R.string.tour_comment_houres);
            return before + " " + passedSeconds + " " + timeMarker;
        }else if (passedSeconds >= (3600 * 24) && passedSeconds < (3600 * 24 * 7)){
            passedSeconds = passedSeconds / (3600 * 24);
            String timeMarker;
            if (passedSeconds == 1) timeMarker = context.getResources().getString(R.string.tour_comment_day);
            else timeMarker = context.getResources().getString(R.string.tour_comment_days);
            return before + " " + passedSeconds + " " + timeMarker;
        } else if (passedSeconds >= (3600 * 24 * 7) && passedSeconds < (3600 * 24 * 365)){
            passedSeconds = passedSeconds / (3600 * 24 * 7);
            String timeMarker;
            if (passedSeconds == 1) timeMarker = context.getResources().getString(R.string.tour_comment_week);
            else timeMarker = context.getResources().getString(R.string.tour_comment_weeks);
            return before + " " + passedSeconds + " " + timeMarker;
        } else {
            passedSeconds = passedSeconds / (3600 * 24 * 365);
            String timeMarker;
            if (passedSeconds == 1) timeMarker = context.getResources().getString(R.string.tour_comment_year);
            else timeMarker = context.getResources().getString(R.string.tour_comment_years);
            return before + " " + passedSeconds + " " + timeMarker;
        }
    }
    /**
     * allows clicks events to be caught
     * @param itemClickListener
     */
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    /**
     * parent activity will implement this interface to respond to click events
     */
    public interface ItemClickListener {
        void onItemClick(View view, UserComment tour);
    }

    /**
     * stores and recycles views as they are scrolled off screen
     * @author Alexander Weinbeck
     * @license MIT
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tvUpdatedAt, tvText, tvNickname;
        private final ImageButton tvDelete;

        /**
         * copy constructor for each element which holds the view
         * @param itemView
         */
        ViewHolder(View itemView) {
            super(itemView);
            tvNickname = (TextView) itemView.findViewById(R.id.tour_comment_nickname);
            tvText = (TextView) itemView.findViewById(R.id.tour_comment_text);
            tvUpdatedAt = (TextView) itemView.findViewById(R.id.tour_comment_updatedat);
            tvDelete = (ImageButton) itemView.findViewById(R.id.tour_comment_delete);
            itemView.setOnClickListener(this);
        }

        /**
         * click event handler
         * @param view
         */
        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getItem(getAdapterPosition()));
        }
    }
}