package eu.wise_iot.wanderlust.views.controls;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import eu.wise_iot.wanderlust.R;

public class LoadingDialog {
    private Dialog dialog;
    private static LoadingDialog mInstance;

    public static synchronized LoadingDialog getDialog() {
        if (mInstance == null) {
            mInstance = new LoadingDialog();
        }
        return mInstance;
    }

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

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}