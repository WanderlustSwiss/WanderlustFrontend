package eu.wise_iot.wanderlust.views;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;

/**
 * Fragment that contains the filtering functionality for all tours
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class FilterFragment extends Fragment {


    public static FilterFragment newInstance() {
        Bundle args = new Bundle();
        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_filtertours, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        SeekBar seekBar = (SeekBar)view.findViewById(R.id.seekbar);
//        seekBar.setProgress(0);
//        seekBar.incrementProgressBy(10);
//        seekBar.setMax(200);
//        TextView seekBarValue = (TextView)view.findViewById(R.id.seekbarvalue);
//        seekBarValue.setText(tvRadius.getText().toString().trim());
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                progress = progress / 10;
//                progress = progress * 10;
//                seekBarValue.setText(String.valueOf(progress));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
    }

}
