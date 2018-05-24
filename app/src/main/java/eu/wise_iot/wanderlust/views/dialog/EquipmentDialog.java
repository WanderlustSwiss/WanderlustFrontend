package eu.wise_iot.wanderlust.views.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.EquipmentController;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.views.animations.CircleTransform;

public class EquipmentDialog extends DialogFragment{

    private static Equipment equipment;
    private static EquipmentController controller;
    private static ImageController imageController;
    private static Context context;


    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView equipment_imageView;
    private Button linkToOnlineShopButtonText;

    public static EquipmentDialog newInstance(Context paramContext, Equipment paramEquipment){
        EquipmentDialog fragment = new EquipmentDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        context = paramContext;

        equipment = paramEquipment;
        imageController = new ImageController();
        controller = new EquipmentController();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_equipment_view, container, false);
        equipment_imageView = (ImageView) view.findViewById(R.id.equipment_image);
        titleTextView = (TextView) view.findViewById(R.id.equipmentTitleTextView);
        descriptionTextView = (TextView) view.findViewById(R.id.equipmentDescriptionTextView);
        linkToOnlineShopButtonText = (Button) view.findViewById(R.id.linkToShopButtonText);

        ImageInfo imagepath = equipment.getImagePath();
        if(imagepath != null){
            File image = imageController.getImage(equipment.getImagePath());
            Picasso.with(context).load(image).placeholder(R.drawable.loader)
                    .fit().centerCrop().transform(new CircleTransform()).into(equipment_imageView);
        }
        titleTextView.setText(equipment.getName());
        descriptionTextView.setText(equipment.getDescription());

        if(equipment.getName().equals("Bargeld")){
            linkToOnlineShopButtonText.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linkToOnlineShopButtonText.setOnClickListener(e -> linkToOnlineShop());
    }

    private void linkToOnlineShop(){
        // todo: add real link here - until then: don't leave unexplained placeholder functionality in a release version...
//        String url = equipment.getAdLink();
//        String url = "http://www.wanderlust-app.ch";
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setData(Uri.parse(url));
//        startActivity(i);
        Toast.makeText(context, R.string.commercial_announcement, Toast.LENGTH_LONG).show();

    }

}
