package co.pl.plsample.plSDK

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import co.pl.plsample.BuildConfig

/**
 * @file PLMManager
 * Created by Payment Loyalty on 20/12/2023.
 * Description: Triggers
 */


/**
 * Posts the entered amount information to the Payment Loyalty Module (PLM) app if installed on the device.
 *
 * @param amount The amount entered by the user.
 * @param shareCouponBitmap to enable receiving the generated coupon as bitmap to print using the printer or to show on screen. Default value is true.
 * @return Boolean indicating whether the entered amount information was successfully posted to the PLM app.
 */
fun Context.sendPostAmountEntered(
    amount: String,
    shareCouponBitmap: Boolean = true
): Boolean {
    // Check if the PLM app is installed on the device
    return if (isPLMInstalled(packageManager)) {
        // Create an intent to trigger the PLM app with the entered amount details
        val intent = Intent(PLIntentsFilters.TRIGGER_ACTION).apply {
            setPackage(PLIntentsFilters.PAYMENT_LOYALTY_APP_ID)
            putExtra(PLIntentParamKey.APP_IDENTIFIER, BuildConfig.APPLICATION_ID)
            putExtra(PLIntentParamKey.COUPON_SHARING_ENABLED,shareCouponBitmap)
            putExtra(PLIntentParamKey.AMOUNT, amount)
            putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_AMOUNT_ENTRY)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES) // Ensure the intent is delivered to the PLM app
        }

        // Send a broadcast to the PLM app with the entered amount details
        sendBroadcast(intent)

        // Return true indicating successful posting of entered amount information
        true
    } else {
        // Continue with the payment if the PLM app is not installed
        false
    }
}


/**
 * Posts information about a card presentation event to the Payment Loyalty Module (PLM) app if installed on the device.
 *
 * @param cardToken The token representing the user's card information.
 * @param cardType The type of the (ex: visa)
 * @param amount The amount associated with the card presentation event.
 * @return Boolean indicating whether the card presentation event was successfully posted to the PLM app.
 */
fun Context.sendPostCardPresent(
    cardToken: String,
    cardType: String? = null,
    amount: String
): Boolean {
    // Check if the PLM app is installed on the device
    return if (isPLMInstalled(packageManager)) {
        // Create an intent to trigger the PLM app with card presentation details
        val intent = Intent(PLIntentsFilters.TRIGGER_ACTION).apply {
            setPackage(PLIntentsFilters.PAYMENT_LOYALTY_APP_ID)
            putExtra(PLIntentParamKey.APP_IDENTIFIER,BuildConfig.APPLICATION_ID)
            putExtra(PLIntentParamKey.CARD_TOKEN, cardToken)
            putExtra(V2Trigger.Params.CARD_TYPE, cardType) //optional
            putExtra(PLIntentParamKey.AMOUNT, amount)
            putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_CARD_PRESENTED)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }

        // Send a broadcast to the PLM app with the card presentation details
        sendBroadcast(intent)

        // Return true indicating successful card presentation posting
        true
    } else {
        // Return false if the PLM app is not installed
        false
    }
}


/**
 * Posts a transaction to the Payment Loyalty Module (PLM) app if installed on the device.
 *
 * @param cardToken The token representing the user's card information.
 * @param cardType The type of the (ex: visa)
 * @param amount The amount associated with the transaction.
 * @param transactionId The current transaction id.
 * @param transactionStatus The current transaction status. If transaction is success then it is true otherwise false
 * @return Boolean indicating whether the transaction was successfully posted to the PLM app.
 */
fun Context.sendPostTransaction(
    cardToken: String,
    cardType: String? = null,
    amount: String,
    transactionId: String? = null,
    transactionStatus: Boolean
): Boolean {
    return if (isPLMInstalled(packageManager)) {
        val intent = Intent(PLIntentsFilters.TRIGGER_ACTION).apply {
            setPackage(PLIntentsFilters.PAYMENT_LOYALTY_APP_ID)
            putExtra(PLIntentParamKey.APP_IDENTIFIER,BuildConfig.APPLICATION_ID)
            putExtra(PLIntentParamKey.CARD_TOKEN, cardToken)
            putExtra(V2Trigger.Params.CARD_TYPE, cardType) //Optional
            putExtra(PLIntentParamKey.AMOUNT, amount)
            putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_TRANSACTION)
            putExtra(V2Trigger.Params.TRANSACTION_STATUS, transactionStatus) //Optional
            putExtra(V2Trigger.Params.TRANSACTION_ID, transactionId) //Optional
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }
        sendBroadcast(intent)
        true
    } else { //If plm app not installed
        false
    }
}


/**
 * Opens the Payment Loyalty Module (PLM) app with specified parameters.
 *
 * @param launcher The activity result launcher for handling the result of the PLM app launch.
 * @param amount The amount associated with the transaction.
 * @param cardToken The token representing the user's card information.
 * @param launchFrom A string indicating the context or origin of the app launch.
 * @return Boolean indicating whether the PLM app launch was successful or not.
 */
fun openPLMApp(
    launcher: ActivityResultLauncher<Intent>,
    amount: String,
    cardToken: String,
    launchFrom: String
): Boolean {
    try {
        val intent = Intent(PLIntentsFilters.OPEN_PLM_ACTION).apply {
            flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
            setPackage(PLIntentsFilters.PAYMENT_LOYALTY_APP_ID)
            putExtra(PLIntentParamKey.AMOUNT, amount)
            putExtra(PLIntentParamKey.CARD_TOKEN, cardToken)
            putExtra(PLIntentParamKey.LAUNCH_FROM, launchFrom)
        }
        launcher.launch(intent)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

/**
 * Opens the Payment Loyalty Module (PLM) app with specified parameters.
 *
 * @param launcher The activity result launcher for handling the result of the PLM app launch.
 * @return Boolean indicating whether the PLM app launch was successful or not.
 */
fun openBillBoardApp(
    launcher: ActivityResultLauncher<Intent>
): Boolean {
    try {
        val intent = Intent(PLIntentsFilters.OPEN_BILLBOARD_ACTION).apply {
            flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
            setPackage(PLIntentsFilters.BILLBOARD_APP_ID)
        }
        launcher.launch(intent)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}


/**
 * Checks if the Payment Loyalty Module (PLM) app is installed on the device.
 *
 * @param packageManager The PackageManager instance used to query information about installed packages.
 * @return Boolean indicating whether the PLM app is installed on the device.
 */
/**
 * Note : Declare PL app package to whitelist app to query
 *     <queries>
 *         <package android:name="com.winloyalty" />
 *     </queries>
 * */
fun isPLMInstalled(packageManager: PackageManager): Boolean {
    return packageManager.getLaunchIntentForPackage(PLIntentsFilters.PAYMENT_LOYALTY_APP_ID) != null
}

/**
 * Checks if the Bill Board app is installed on the device.
 *
 * @param packageManager The PackageManager instance used to query information about installed packages.
 * @return Boolean indicating whether the BILL Board app is installed on the device.
 */
/**
 * Note : Declare Bill Board app package to whitelist app to query
 *     <queries>
 *         <package android:name="co.pl.billboard" />
 *     </queries>
 * */
fun isBillBoardInstalled(packageManager: PackageManager): Boolean {
    return packageManager.getLaunchIntentForPackage(PLIntentsFilters.BILLBOARD_APP_ID) != null
}