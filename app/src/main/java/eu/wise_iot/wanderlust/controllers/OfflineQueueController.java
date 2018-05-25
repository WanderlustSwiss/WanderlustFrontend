package eu.wise_iot.wanderlust.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;

public class OfflineQueueController {

    private final ArrayList<QueueCommand> list = new ArrayList<>();
    private static OfflineQueueController offlineQueueController;
    private static final String TAG = "OfflineQueueController";
    private boolean isRunning = false;

    private OfflineQueueController() {
    }

    public static OfflineQueueController getInstance() {
        if (offlineQueueController == null) {
            offlineQueueController = new OfflineQueueController();
        }
        return offlineQueueController;
    }

    public void addCommand(QueueCommand command) {
        list.add(command);
    }

    private boolean isRunning() {
        return isRunning;
    }

    private void executeCommands() {
        isRunning = true;
        ArrayList<QueueCommand> copiedList = new ArrayList<>(list);
        for (QueueCommand cmd : copiedList) {
            if (cmd.isExecutable()) {

                FragmentHandler handler = controllerEvent -> {
                    switch (controllerEvent.getType()) {
                        case OK:
                            cmd.executeAfterSuccess(controllerEvent.getModel());
                            list.remove(cmd);
                            break;
                        default:
                            break;
                    }
                };

                cmd.execute(handler);

            } else {
                list.remove(cmd);
            }
        }
        isRunning = false;
    }


    public static class NetworkChangeReceiver extends BroadcastReceiver {

        private final OfflineQueueController offlineQueueController;

        public NetworkChangeReceiver() {
            offlineQueueController = OfflineQueueController.getInstance();
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            boolean status = NetworkUtil.getConnectivityStatusString(context);
            Log.i(TAG, "Received Intent from NetworkChangeReceiver with Status " + status);

            if (status) {
                if (!offlineQueueController.isRunning()) {
                    offlineQueueController.executeCommands();

                }
            }
        }
    }


    private static class NetworkUtil {

        private static boolean getConnectivityStatusString(Context context) {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return  activeNetwork != null && activeNetwork.isConnected();
        }
    }

}
