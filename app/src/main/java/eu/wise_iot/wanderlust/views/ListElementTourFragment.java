package eu.wise_iot.wanderlust.views;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import eu.wise_iot.wanderlust.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListElementTourFragment interface
 * to handle interaction events.
 * Use the {@link ListElementTourFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListElementTourFragment extends Fragment {

    private ImageView imageView;
    private TextView title;
    private TextView description;
    private TextView difficulty;
    private TextView time;
    private ImageView editIcon;

    public ListElementTourFragment() {
        // Required empty public constructor
    }


    public static ListElementTourFragment newInstance() {
        ListElementTourFragment fragment = new ListElementTourFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_element_tour, container, false);
        imageView = (ImageView) view.findViewById(R.id.ListTourImageView);
        title = (TextView) view.findViewById(R.id.ListTourTitle);
        description = (TextView) view.findViewById(R.id.ListTourDescription);
        difficulty = (TextView) view.findViewById(R.id.ListTourDifficulty);
        time = (TextView) view.findViewById(R.id.ListTourTime);
        editIcon = (ImageView) view.findViewById(R.id.ListTourEdit);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
