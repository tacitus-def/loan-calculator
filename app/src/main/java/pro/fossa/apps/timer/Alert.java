package pro.fossa.apps.timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Alert {
	/**
	 * Display a confirm dialog. 
	 * @param activity,
	 * @param title
	 * @param message
	 * @param positiveLabel
	 * @param negativeLabel
	 * @param onPositiveClick runnable to call (in UI thread) if positive button pressed. Can be null
	 * @param onNegativeClick runnable to call (in UI thread) if negative button pressed. Can be null
	 */
	public static final void confirm(
			final Activity activity,
	        final int title, 
	        final int message,
	        final int positiveLabel, 
	        final int negativeLabel,
	        final Runnable onPositiveClick,
	        final Runnable onNegativeClick) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable (false);
        dialog.setPositiveButton(positiveLabel,
                new DialogInterface.OnClickListener () {
            public void onClick (DialogInterface dialog, int buttonId) {
                if (onPositiveClick != null) onPositiveClick.run();
            }
        });
        dialog.setNegativeButton(negativeLabel,
                new DialogInterface.OnClickListener () {
            public void onClick (DialogInterface dialog, int buttonId) {
                if (onNegativeClick != null) onNegativeClick.run();
            }
        });
        dialog.setIcon (android.R.drawable.ic_dialog_alert);
        dialog.show();
    }

}
