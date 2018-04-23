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


    public TextView titleTextView;
    public TextView descriptionTextView;
    public ImageView equipment_imageView;
    public ImageButton linkToOnlineShopButtonIcon;
    public Button linkToOnlineShopButtonText;

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
        titleTextView = (TextView) view.findViewById(R.id.equipment_title_text_view);
        descriptionTextView = (TextView) view.findViewById(R.id.equipment_description_text_view);
        linkToOnlineShopButtonIcon = (ImageButton) view.findViewById(R.id.link_to_shop_button_icon);
        linkToOnlineShopButtonText = (Button) view.findViewById(R.id.link_to_shop_button_text);

        ImageInfo imagepath = equipment.getImagePath();
        if(imagepath != null){
            File image = imageController.getImage(equipment.getImagePath());
            Picasso.with(context).load(image).placeholder(R.drawable.loader)
                    .fit().centerCrop().transform(new CircleTransform()).into(equipment_imageView);
        }
        titleTextView.setText(equipment.getName());
        descriptionTextView.setText(equipment.getDescription());

        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linkToOnlineShopButtonIcon.setOnClickListener(e ->{ linkToOnlineShop(); });
        linkToOnlineShopButtonText.setOnClickListener(e ->{ linkToOnlineShop(); });
    }

    public void linkToOnlineShop(){
        //String url = equipment.getUrl();
        String url = "http://www.wanderlust-app.ch";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

}
