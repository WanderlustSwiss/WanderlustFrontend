package eu.wise_iot.wanderlust.controllers;

import android.util.Log;

import java.io.File;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;

/**
 * Poi command class for adding POIs
 *
 * @author TODO ?
 * @license MIT
 */
public class AddPoiCommand implements QueueCommand {

    private final PoiController controller;
    private Poi poi;
    private final FragmentHandler<Poi> successHandler;
    private int numberOfTries = 0;
    private static final String TAG = "AddPoiCommand";
    private final File imagePath;

    public AddPoiCommand(Poi poi, File imagePath) {
        controller = new PoiController();
        this.poi = poi;
        this.imagePath = imagePath;

        successHandler = event -> {
            if(event.getType() == EventType.OK){
                Log.i(TAG, "Foto wurde erfolgreich hinzugefügt.");
            } else {
                Log.i(TAG, "Foto upload fehlgeschlagen.");
            }
        };
    }

    @Override
    public void execute(FragmentHandler handler) {
        Log.i(TAG, "Poi wird gespeichert.");
        controller.saveNewPoi(poi, handler);
        ++ numberOfTries;
    }

    @Override
    public void executeAfterSuccess(Object obj) {
        if(obj instanceof Poi){
            poi = (Poi) obj;
            Log.i(TAG, "Foto wird gespeichert.");
            controller.uploadImage(imagePath, poi, successHandler);
        }
    }

    @Override
    public boolean isExecutable() {
        return imagePath.exists() && numberOfTries < 100;
    }
}
