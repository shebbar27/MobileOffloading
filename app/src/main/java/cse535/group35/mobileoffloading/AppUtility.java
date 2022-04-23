package cse535.group35.mobileoffloading;

import static android.content.DialogInterface.*;
import static cse535.group35.mobileoffloading.R.string.*;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class AppUtility {

    public static void handleException(Exception e, AppCompatActivity activity) {
        e.printStackTrace();
        createExitAlertDialogWithConsentAndExit(
                activity,
                crash_dialog_title,
                crash_dialog_message,
                alert_dialog_ok);
    }

    public static void createConnectionConsentAlert(Context context,
                                                    String deviceName,
                                                    OnClickListener positiveButtonOnClickCallBack,
                                                    OnClickListener negativeButtonOnClickCallBack) {
        createAlertDialogAndShow(context,
                context.getString(connection_consent_alert_title),
                String.format(Locale.US, context.getString(connection_consent_alert_message), deviceName),
                context.getString(alert_dialog_yes),
                positiveButtonOnClickCallBack,
                context.getString(alert_dialog_no),
                negativeButtonOnClickCallBack);
    }

    public static void createTurnOnGPSAlert(AppCompatActivity activity,
                                            OnClickListener positiveButtonOnClickCallBack,
                                            OnClickListener negativeButtonOnClickCallBack) {
        createAlertDialogAndShow(activity,
                turn_on_gps_dialog_title,
                turn_on_gps_message,
                alert_dialog_yes,
                positiveButtonOnClickCallBack,
                alert_dialog_no,
                negativeButtonOnClickCallBack);
    }

    public static void exitApplicationWithDefaultAlert(AppCompatActivity activity) {
        createExitAlertDialogWithConsentAndExit(activity,
                exit_dialog_title,
                exit_dialog_message,
                alert_dialog_yes,
                alert_dialog_no);
    }

    public static void finishAndCloseCurrentActivityWithDefaultAlert(AppCompatActivity activity) {
        createAlertDialogWithConsentAndFinishActivity(activity,
                finish_activity_dialog_title,
                finish_activity_dialog_message,
                alert_dialog_yes,
                alert_dialog_no);
    }

    public static void createExitAlertDialogWithConsentAndExit(AppCompatActivity activity,
                                                               int titleId,
                                                               int messageId,
                                                               int positiveButtonTextId) {
        createAlertDialogAndShow(activity,
                titleId,
                messageId,
                positiveButtonTextId,
                (dialog, which) -> exitApplication(activity),
                empty_string,
                (dialog, which) -> { });
    }

    public static void createExitAlertDialogWithConsentAndExit(AppCompatActivity activity,
                                                               int titleId,
                                                               int messageId,
                                                               int positiveButtonTextId,
                                                               int negativeButtonTextId) {
        createAlertDialogAndShow(activity,
                titleId,
                messageId,
                positiveButtonTextId,
                (dialog, which) -> exitApplication(activity),
                negativeButtonTextId,
                (dialog, which) -> dialog.cancel());
    }

    public static void createAlertDialogWithConsentAndFinishActivity(AppCompatActivity activity,
                                                               int titleId,
                                                               int messageId,
                                                               int positiveButtonTextId,
                                                               int negativeButtonTextId) {
        createAlertDialogAndShow(activity,
                titleId,
                messageId,
                positiveButtonTextId,
                (dialog, which) -> activity.finish(),
                negativeButtonTextId,
                (dialog, which) -> dialog.cancel());
    }

    public static void createAlertDialogAndShow(AppCompatActivity activity,
                                                int titleId,
                                                int messageId,
                                                int positiveButtonTextId,
                                                OnClickListener positiveButtonOnClickCallBack,
                                                int negativeButtonTextId,
                                                OnClickListener negativeButtonOnClickCallBack) {
        createAlertDialogAndShow(activity,
                activity.getString(titleId),
                activity.getString(messageId),
                activity.getString(positiveButtonTextId),
                positiveButtonOnClickCallBack,
                activity.getString(negativeButtonTextId),
                negativeButtonOnClickCallBack);
    }

    public static void createAlertDialogAndShow(AppCompatActivity activity,
                                                String title,
                                                String message,
                                                String positiveButtonText,
                                                OnClickListener positiveButtonOnClickCallBack,
                                                String negativeButtonText,
                                                OnClickListener negativeButtonOnClickCallBack) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton(positiveButtonText, positiveButtonOnClickCallBack);
        if(!negativeButtonText.equals(activity.getString(empty_string))) {
            dialogBuilder.setNegativeButton(negativeButtonText, negativeButtonOnClickCallBack);
        }

        AlertDialog dialog =  dialogBuilder.create();
        dialog.show();

    }

    public static void createAlertDialogAndShow(Context context,
                                                String title,
                                                String message,
                                                String positiveButtonText,
                                                OnClickListener positiveButtonOnClickCallBack,
                                                String negativeButtonText,
                                                OnClickListener negativeButtonOnClickCallBack) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton(positiveButtonText, positiveButtonOnClickCallBack);
        if(!negativeButtonText.equals(context.getString(empty_string))) {
            dialogBuilder.setNegativeButton(negativeButtonText, negativeButtonOnClickCallBack);
        }

        AlertDialog dialog =  dialogBuilder.create();
        dialog.show();
    }

    public static void registerButtonOnClickCallBack(AppCompatActivity activity,
                                                     View.OnClickListener listener,
                                                     ArrayList<Integer> buttonIds) {
        for(Integer id: buttonIds) {
            Button button = activity.findViewById(id);
            if(button != null) {
                button.setOnClickListener(listener);
            } else {
                // log the error for debugging purpose
                Log.d("Button not found",
                        "Button ID: " + id + " failed to register OnClick call back");
            }
        }
    }

    public static void createAndDisplayToast(AppCompatActivity activity,
                                             String message,
                                             int duration) {
        createAndDisplayToast(activity.getApplicationContext(), message, duration);
    }

    public static void createAndDisplayToast(AppCompatActivity activity, String message) {
        createAndDisplayToast(activity.getApplicationContext(), message, Toast.LENGTH_SHORT);
    }

    public static void createAndDisplayToast(Context context,
                                             String message,
                                             int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    public static void createAndDisplayToast(Context context,
                                             String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private static void exitApplication(AppCompatActivity activity) {
        activity.finishAndRemoveTask();
        System.exit(0);
    }
}
