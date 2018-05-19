package eu.wise_iot.wanderlust.views;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.ProfileController;
import eu.wise_iot.wanderlust.views.animations.CircleTransform;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;


public class ProfileEditFragment extends Fragment {
    private static final String TAG = "ProfileEditFragment";

    private ImageView profileImage;

    private TextView changeImage;

    private TextInputLayout emailLayout;

    private EditText emailTextfield;

    private Button buttonOpenGallery;
    private Button buttonDeleteImage;

    private View bottomSheet;

    private BottomSheetBehavior bottomSheetBehavior;

    private CheckBox checkT1;
    private CheckBox checkT2;
    private CheckBox checkT3;
    private CheckBox checkT4;
    private CheckBox checkT5;
    private CheckBox checkT6;
    private CheckBox[] checkBoxes;
    private long difficulty;

    private ImageController imageController;

    private final ProfileController profileController;

    public ProfileEditFragment() {
        // Required empty public constructor
        profileController = new ProfileController();
        imageController = ImageController.getInstance();
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

        buttonDeleteImage = (Button) view.findViewById(R.id.editProfileButtonDeleteImage);
        buttonOpenGallery = (Button) view.findViewById(R.id.editProfileButtonOpenGallery);

        bottomSheet = view.findViewById(R.id.profileEditBottomSheet);

        checkT1 = (CheckBox) view.findViewById(R.id.checkboxT1);
        checkT2 = (CheckBox) view.findViewById(R.id.checkboxT2);
        checkT3 = (CheckBox) view.findViewById(R.id.checkboxT3);
        checkT4 = (CheckBox) view.findViewById(R.id.checkboxT4);
        checkT5 = (CheckBox) view.findViewById(R.id.checkboxT5);
        checkT6 = (CheckBox) view.findViewById(R.id.checkboxT6);
        checkBoxes = new CheckBox[]{checkT1, checkT2, checkT3,
                checkT4, checkT5, checkT6};

        Rect outRect = new Rect();
        view.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN && bottomSheet != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect1 = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect1);

                if (!outRect1.contains((int) event.getRawX(), (int) event.getRawY()))
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            return true;
        });

        //initialize current values
        setupCurrentInfo(view);
        setupDifficulty(view);
        setupActionListener();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //handle keyboard closing
        view.findViewById(R.id.profileEditRootLayout).setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            return true;
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_edit_menu, menu);
        menu.removeItem(R.id.drawer_layout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.checkIcon:
                //save changings
                String newMail = emailTextfield.getText().toString();

                profileController.setEmail(newMail, getActivity(), controllerEvent -> {
                    EventType type = controllerEvent.getType();

                    switch (type) {
                        case OK:
                            ((MainActivity) getActivity()).updateEmailAdress(newMail);
                            Log.d(TAG, "Email wurde geändert.");
                            break;
                        default:
                            Toast.makeText(getActivity(), R.string.err_msg_error_occured,
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Fehler: " + type.toString());
                            break;
                    }
                });

                profileController.setDifficulty(difficulty, getActivity(), controllerEvent -> {
                    EventType type = controllerEvent.getType();

                    switch (type) {
                        case OK:
                            Log.d(TAG, "Difficulty wurde geändert.");
                            break;

                        default:
                            Toast.makeText(getActivity(), R.string.err_msg_error_occured,
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Fehler: " + type.toString());
                            break;
                    }
                });
                Toast.makeText(getActivity(), R.string.profileEditChangesApplied, Toast.LENGTH_SHORT).show();
                ProfileFragment fragment = ProfileFragment.newInstance();
                getFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();
                return true;

            case R.id.cancelIcon:
                //back to profile
                Toast.makeText(getActivity(), R.string.msg_no_changes_done,
                        Toast.LENGTH_SHORT).show();
                ProfileFragment profileFragment = ProfileFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, profileFragment)
                        .commit();
                return true;
        }
        emailTextfield.setText(profileController.getEmail());
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    onActionResultGallery(data);
                    break;
            }
        }
    }

    private void setupCurrentInfo(View view) {
        setupAvatar();
        buttonOpenGallery.setText(R.string.profile_edit_open_gallery);
        buttonDeleteImage.setText(R.string.profile_edit_delete_image);
        buttonDeleteImage.setTextColor(Color.RED);
        emailTextfield.setText(profileController.getEmail());
    }

    private void setupActionListener() {

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        changeImage.setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        buttonOpenGallery.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (profileController.getProfilePicture() == null) {
                    openGallery();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else {
                    profileController.deleteProfilePicture(controllerEvent -> {
                        EventType type = controllerEvent.getType();
                        if (type == EventType.OK) {
                            openGallery();
                        }
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    });
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.msg_picture_not_saved),
                        Toast.LENGTH_SHORT).show();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        buttonDeleteImage.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            profileController.deleteProfilePicture(controllerEvent -> {
                EventType type = controllerEvent.getType();
                if (type == EventType.OK) {
                    setupAvatar();
                }
            });
        });
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

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");

        galleryIntent.putExtra("crop", "true");
        galleryIntent.putExtra("outputX", 170);
        galleryIntent.putExtra("outputY", 170);
        galleryIntent.putExtra("aspectX", 1);
        galleryIntent.putExtra("aspectY", 1);
        galleryIntent.putExtra("scale", true);
        if (galleryIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(galleryIntent, 1000);
        }
    }

    private void onActionResultGallery(Intent data) {

        Uri returnUri = data.getData();

        if (returnUri == null) {
            Bundle extras = data.getExtras();
            returnUri = getImageUri(getActivity().getApplicationContext(), (Bitmap) extras.get("data"));
        }
        try {
            Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);

            String path = imageController.getRealPathFromURI(returnUri, getActivity());
            Bitmap imageBitmap = BitmapFactory.decodeFile(path);
            Uri uri = imageController.getImageUri(getActivity(), imageBitmap);
            ImageController.getInstance().setAndSaveCorrectOrientation(imageBitmap, uri, new File(path));

            bitmapImage = ImageController.getInstance().resize(bitmapImage, 170);
            if (bitmapImage != null) {
                profileController.setProfilePicture(bitmapImage, controllerEvent -> {
                    if (controllerEvent.getType() == EventType.OK) {
                        setupAvatar();
                    } else {
                        Toast.makeText(getActivity(), R.string.err_msg_error_occured,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void setupAvatar() {
        File image = profileController.getProfilePicture();
        if (image != null) {
            Picasso.with(getActivity()).invalidate(image);
            Picasso.with(getActivity()).load(image).placeholder(R.drawable.progress_animation).transform(new CircleTransform()).fit().into(profileImage);
            ((MainActivity) getActivity()).updateProfileImage(image);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            drawable.setCircular(true);
            profileImage.setImageDrawable(drawable);
            ((MainActivity) getActivity()).updateProfileImage(image);
        }

    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
