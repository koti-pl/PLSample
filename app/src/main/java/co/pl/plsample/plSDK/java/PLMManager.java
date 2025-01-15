package co.pl.plsample.plSDK.java;

 import android.content.Context;
 import android.content.Intent;
 import android.content.pm.PackageManager;
 import androidx.activity.result.ActivityResultLauncher;
 import co.pl.plsample.BuildConfig;
 import co.pl.plsample.plSDK.PLIntentParamKey;
 import co.pl.plsample.plSDK.PLIntentTrigger;
 import co.pl.plsample.plSDK.PLIntentsFilters;
 import co.pl.plsample.plSDK.V2Trigger;

/**
 * @file PLMManager
 * Created by Payment Loyalty on 20/12/2023.
 * Description: Triggers
 */

public class PLMManager {

    /**
     * Posts the entered amount information to the Payment Loyalty Module (PLM) app if installed on the device.
     *
     * @param context            The application context.
     * @param amount             The amount entered by the user.
     * @param shareCouponBitmap  Enables receiving the generated coupon as a bitmap. Default value is true.
     * @return Boolean indicating whether the entered amount information was successfully posted to the PLM app.
     */
    public static boolean sendPostAmountEntered(Context context, String amount, boolean shareCouponBitmap) {
        if (isPLMInstalled(context.getPackageManager())) {
            Intent intent = new Intent(PLIntentsFilters.TRIGGER_ACTION);
            intent.setPackage(PLIntentsFilters.PAYMENT_LOYALTY_APP_ID);
            intent.putExtra(PLIntentParamKey.APP_IDENTIFIER, BuildConfig.APPLICATION_ID);
            intent.putExtra(PLIntentParamKey.COUPON_SHARING_ENABLED, shareCouponBitmap);
            intent.putExtra(PLIntentParamKey.AMOUNT, amount);
            intent.putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_AMOUNT_ENTRY);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.sendBroadcast(intent);
            return true;
        } else {
            return false;
        }
    }

    public static boolean sendPostAmountEntered(Context context, String amount) {
        return sendPostAmountEntered(context, amount, true);
    }

    /**
     * Posts information about a card presentation event to the Payment Loyalty Module (PLM) app if installed on the device.
     *
     * @param context   The application context.
     * @param cardToken The token representing the user's card information.
     * @param cardType  The type of the card (e.g., Visa).
     * @param amount    The amount associated with the card presentation event.
     * @return Boolean indicating whether the card presentation event was successfully posted to the PLM app.
     */
    public static boolean sendPostCardPresent(Context context, String cardToken, String cardType, String amount) {
        if (isPLMInstalled(context.getPackageManager())) {
            Intent intent = new Intent(PLIntentsFilters.TRIGGER_ACTION);
            intent.setPackage(PLIntentsFilters.PAYMENT_LOYALTY_APP_ID);
            intent.putExtra(PLIntentParamKey.APP_IDENTIFIER, BuildConfig.APPLICATION_ID);
            intent.putExtra(PLIntentParamKey.CARD_TOKEN, cardToken);
            intent.putExtra(V2Trigger.Params.CARD_TYPE, cardType);
            intent.putExtra(PLIntentParamKey.AMOUNT, amount);
            intent.putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_CARD_PRESENTED);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.sendBroadcast(intent);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Posts a transaction to the Payment Loyalty Module (PLM) app if installed on the device.
     *
     * @param context            The application context.
     * @param cardToken          The token representing the user's card information.
     * @param cardType           The type of the card (e.g., Visa).
     * @param amount             The amount associated with the transaction.
     * @param transactionId      The current transaction ID.
     * @param transactionStatus  The current transaction status. If the transaction is successful, it's true; otherwise, false.
     * @return Boolean indicating whether the transaction was successfully posted to the PLM app.
     */
    public static boolean sendPostTransaction(Context context, String cardToken, String cardType, String amount,
                                              String transactionId, boolean transactionStatus) {
        if (isPLMInstalled(context.getPackageManager())) {
            Intent intent = new Intent(PLIntentsFilters.TRIGGER_ACTION);
            intent.setPackage(PLIntentsFilters.PAYMENT_LOYALTY_APP_ID);
            intent.putExtra(PLIntentParamKey.APP_IDENTIFIER, BuildConfig.APPLICATION_ID);
            intent.putExtra(PLIntentParamKey.CARD_TOKEN, cardToken);
            intent.putExtra(V2Trigger.Params.CARD_TYPE, cardType);
            intent.putExtra(PLIntentParamKey.AMOUNT, amount);
            intent.putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_TRANSACTION);
            intent.putExtra(V2Trigger.Params.TRANSACTION_STATUS, transactionStatus);
            intent.putExtra(V2Trigger.Params.TRANSACTION_ID, transactionId);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.sendBroadcast(intent);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Opens the Payment Loyalty Module (PLM) app with specified parameters.
     *
     * @param launcher  The activity result launcher for handling the result of the PLM app launch.
     * @param amount    The amount associated with the transaction.
     * @param cardToken The token representing the user's card information.
     * @param launchFrom A string indicating the context or origin of the app launch.
     * @return Boolean indicating whether the PLM app launch was successful or not.
     */
    public static boolean openPLMApp(ActivityResultLauncher<Intent> launcher, String amount, String cardToken, String launchFrom) {
        try {
            Intent intent = new Intent(PLIntentsFilters.OPEN_PLM_ACTION);
            intent.setPackage(PLIntentsFilters.PAYMENT_LOYALTY_APP_ID);
            intent.putExtra(PLIntentParamKey.AMOUNT, amount);
            intent.putExtra(PLIntentParamKey.CARD_TOKEN, cardToken);
            intent.putExtra(PLIntentParamKey.LAUNCH_FROM, launchFrom);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            launcher.launch(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Opens the Bill Board app with specified parameters.
     *
     * @param launcher The activity result launcher for handling the result of the Bill Board app launch.
     * @return Boolean indicating whether the Bill Board app launch was successful or not.
     */
    public static boolean openBillBoardApp(ActivityResultLauncher<Intent> launcher) {
        try {
            Intent intent = new Intent(PLIntentsFilters.OPEN_BILLBOARD_ACTION);
            intent.setPackage(PLIntentsFilters.BILLBOARD_APP_ID);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            launcher.launch(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if the Payment Loyalty Module (PLM) app is installed on the device.
     *
     * @param packageManager The PackageManager instance used to query information about installed packages.
     * @return Boolean indicating whether the PLM app is installed on the device.
     */
    public static boolean isPLMInstalled(PackageManager packageManager) {
        return packageManager.getLaunchIntentForPackage(PLIntentsFilters.PAYMENT_LOYALTY_APP_ID) != null;
    }

    /**
     * Checks if the Bill Board app is installed on the device.
     *
     * @param packageManager The PackageManager instance used to query information about installed packages.
     * @return Boolean indicating whether the Bill Board app is installed on the device.
     */
    public static boolean isBillBoardInstalled(PackageManager packageManager) {
        return packageManager.getLaunchIntentForPackage(PLIntentsFilters.BILLBOARD_APP_ID) != null;
    }
}

