package eu.wise_iot.wanderlust.views.controls;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import eu.wise_iot.wanderlust.R;

/**
 * Helper class for showing and hiding a loading dialog
 *
 * @author Alexander Weinbeck
 * @license GPL-3.0
 */
public class LoadingDialog {
    private Dialog dialog;
    private static LoadingDialog mInstance;

    /**
     * Threadsafe singleton initialization
     * @return instance for dialog
     */
    public static synchronized LoadingDialog getDialog() {
        if (mInstance == null) {
            mInstance = new LoadingDialog();
        }
        return mInstance;
    }

    /**
     * Show the dialog
     * @param context from where it is used
     */
    public void show(Context context) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_processing);
        dialog.getWindow().setBackgroundDrawableResource(R.color.ap_transparent);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * Hide the Dialog
     */
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog.dismiss();
    }
}