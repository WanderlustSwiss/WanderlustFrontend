package eu.wise_iot.wanderlust.views;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ProfileController;


public class EditProfileFragment extends Fragment {

    private Toolbar toolbar;

    private ImageView profileImage;
    private TextView changeImage;

    private TextInputLayout nicknameLayout;
    private TextInputLayout emailLayout;

    private EditText nicknameTextfield;
    private EditText emailTextfield;

    private CheckBox easyCheckbox;
    private CheckBox mediumCheckbox;
    private CheckBox hardCheckbox;

    private ProfileController profileController;

    public EditProfileFragment() {
        // Required empty public constructor
        profileController = new ProfileController();
    }


    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //initialize view's
        //toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        profileImage = (ImageView) view.findViewById(R.id.currentImage);

        changeImage = (TextView) view.findViewById(R.id.changeImage);

        nicknameLayout = (TextInputLayout) view.findViewById(R.id.editNicknameLayout);
        emailLayout = (TextInputLayout) view.findViewById(R.id.editEmailLayout);

        nicknameTextfield = (EditText) view.findViewById(R.id.editNicknameField);
        emailTextfield = (EditText) view.findViewById(R.id.editEmailField);

        easyCheckbox = (CheckBox) view.findViewById(R.id.checkboxEasy);
        mediumCheckbox = (CheckBox) view.findViewById(R.id.checkboxMedium);
        hardCheckbox = (CheckBox) view.findViewById(R.id.checkboxHard);

        //initialize current values
        setupToolbar(view);
        setupProfilePicture(view);
        setupNameAndMail(view);
        setupDifficulty(view);

        return view;
    }

    private void setupToolbar(View view) {

    }

    private void setupProfilePicture(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.images);
        //TODO: profile picture from the database
        //Bitmap bitmap1 = BitmapFactory.decodeFile(profileController.getProfilePicture());

        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        drawable.setCircular(true);


        profileImage.setImageDrawable(drawable);
    }


    private void setupNameAndMail(View view) {
        nicknameTextfield.setText(profileController.getNickName());
        emailTextfield.setText(profileController.getEmail());
    }


    private void setupDifficulty(View view) {
    }


}
