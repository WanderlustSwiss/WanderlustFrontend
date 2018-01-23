package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ProfileController;


public class ProfileEditFragment extends Fragment {

    private ImageView profileImage;
    private TextView changeImage;

    private TextInputLayout emailLayout;

    private EditText emailTextfield;

    private CheckBox checkT1;
    private CheckBox checkT2;
    private CheckBox checkT3;
    private CheckBox checkT4;
    private CheckBox checkT5;
    private CheckBox checkT6;
    private CheckBox[] checkBoxes;
    private long difficulty;

    private ProfileController profileController;

    public ProfileEditFragment() {
        // Required empty public constructor
        profileController = new ProfileController();
    }

    public static ProfileEditFragment newInstance() {
        ProfileEditFragment fragment = new ProfileEditFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        //initialize view's
        profileImage = (ImageView) view.findViewById(R.id.currentImage);

        changeImage = (TextView) view.findViewById(R.id.changeImage);

        emailLayout = (TextInputLayout) view.findViewById(R.id.editEmailLayout);
        emailTextfield = (EditText) view.findViewById(R.id.editEmailField);

        checkT1 = (CheckBox) view.findViewById(R.id.checkboxT1);
        checkT2 = (CheckBox) view.findViewById(R.id.checkboxT2);
        checkT3 = (CheckBox) view.findViewById(R.id.checkboxT3);
        checkT4 = (CheckBox) view.findViewById(R.id.checkboxT4);
        checkT5 = (CheckBox) view.findViewById(R.id.checkboxT5);
        checkT6 = (CheckBox) view.findViewById(R.id.checkboxT6);
        checkBoxes = new CheckBox[]{checkT1, checkT2, checkT3,
                checkT4, checkT5, checkT6};

        //initialize current values
        setupCurrentInfo(view);
        setupDifficulty(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_edit_menu, menu);
        menu.removeItem(R.id.drawer_layout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.checkIcon:
                //save changings
                String newMail = emailTextfield.getText().toString();

                profileController.setEmail(newMail, getActivity(), new FragmentHandler() {
                    @Override
                    public void onResponse(ControllerEvent controllerEvent) {
                        EventType type = controllerEvent.getType();

                        switch (type) {
                            case OK:
                                Toast.makeText(getActivity(), R.string.msg_email_edit_successful,
                                        Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getActivity(), R.string.err_msg_error_occured,
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });

                profileController.setDifficulty(difficulty, getActivity(), new FragmentHandler() {
                    @Override
                    public void onResponse(ControllerEvent controllerEvent) {
                        EventType type = controllerEvent.getType();

                        switch (type) {
                            case OK:
                                Toast.makeText(getActivity(),
                                        getString(R.string.msg_difficulty_level_changed_to_1) + " " + difficulty + " " + getString(R.string.msg_difficulty_level_changed_to_2),
                                        Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                Toast.makeText(getActivity(), R.string.err_msg_error_occured,
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                return true;

            case R.id.cancelIcon:
                //back to profile
                Toast.makeText(getActivity(), R.string.msg_no_changes_done,
                        Toast.LENGTH_SHORT).show();
                ProfileFragment profileFragment = new ProfileFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, profileFragment)
                        .commit();
                return true;
        }
        emailTextfield.setText(profileController.getEmail());
        return true;
    }

    private void setupCurrentInfo(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.images);
        //TODO: profile picture from the database
        //Bitmap bitmap1 = BitmapFactory.decodeFile(profileController.getProfilePicture());

        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        drawable.setCircular(true);

        profileImage.setImageDrawable(drawable);

        changeImage.setOnClickListener(v -> {
            Toast.makeText(getActivity(), R.string.msg_no_action_defined,
                    Toast.LENGTH_SHORT).show();
        });

        emailTextfield.setText(profileController.getEmail());
    }

    private void setupDifficulty(View view) {
        //setup current difficulty level
        difficulty = profileController.getDifficulty();
        checkBoxes[(int) difficulty - 1].setChecked(true);

        //checkbox listener to change difficulty of profile
        View.OnClickListener listener1 = v -> {
            CheckBox box = (CheckBox) v;
            for (int i = 0; i < checkBoxes.length; i++) {
                if (checkBoxes[i].equals(box)) {
                    difficulty = i + 1;
                } else {
                    checkBoxes[i].setChecked(false);
                }
            }
        };

        for (CheckBox checkBox : checkBoxes) {
            checkBox.setOnClickListener(listener1);
        }

    }


}
