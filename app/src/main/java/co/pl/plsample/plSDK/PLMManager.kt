package co.pl.plsample.plSDK

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher

/**
 * @file PLMManager
 * Created by Payment Loyalty on 20/12/2023.
 * Description:
 */


/**
 * Posts the entered amount information to the Payment Loyalty Module (PLM) app if installed on the device.
 *
 * @param context The context of the calling component.
 * @param amount The amount entered by the user.
 * @return Boolean indicating whether the entered amount information was successfully posted to the PLM app.
 */
fun postAmountEntered(
    context: Context,
    amount: String
): Boolean {
    // Check if the PLM app is installed on the device
    return if (isPLMInstalled(context.packageManager)) {
        // Create an intent to trigger the PLM app with the entered amount details
        val intent = Intent(PLIntentsFilters.TRIGGER_ACTION).apply {
            putExtra(PLIntentParamKey.AMOUNT, amount)
            putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_AMOUNT_ENTRY)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES) // Ensure the intent is delivered to the PLM app
        }

        // Send a broadcast to the PLM app with the entered amount details
        context.sendBroadcast(intent)

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
 * @param context The context of the calling component.
 * @param cardToken The token representing the user's card information.
 * @param amount The amount associated with the card presentation event.
 * @return Boolean indicating whether the card presentation event was successfully posted to the PLM app.
 */
fun postCardPresent(
    context: Context,
    cardToken: String,
    amount: String
): Boolean {
    // Check if the PLM app is installed on the device
    return if (isPLMInstalled(context.packageManager)) {
        // Create an intent to trigger the PLM app with card presentation details
        val intent = Intent(PLIntentsFilters.TRIGGER_ACTION).apply {
            putExtra(PLIntentParamKey.CARD_TOKEN, cardToken)
            putExtra(PLIntentParamKey.AMOUNT, amount)
            putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_CARD_PRESENTED)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }

        // Send a broadcast to the PLM app with the card presentation details
        context.sendBroadcast(intent)

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
 * @param context The context of the calling component.
 * @param cardToken The token representing the user's card information.
 * @param amount The amount associated with the transaction.
 * @return Boolean indicating whether the transaction was successfully posted to the PLM app.
 */
fun postTransaction(
    context: Context,
    cardToken: String,
    cardType: String? = null,
    amount: String,
    transactionId: String? = null,
    transactionStatus: Boolean
): Boolean {
    return if (isPLMInstalled(context.packageManager)) {
        val intent = Intent(PLIntentsFilters.TRIGGER_ACTION).apply {
            putExtra(PLIntentParamKey.CARD_TOKEN, cardToken)
            putExtra(PLIntentParamKey.AMOUNT, amount)
            putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_TRANSACTION)
            putExtra(V2Trigger.Params.TRANSACTION_STATUS , transactionStatus)
            putExtra(V2Trigger.Params.TRANSACTION_ID , transactionId)
            putExtra(V2Trigger.Params.CARD_TYPE , cardType)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }
        context.sendBroadcast(intent)
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
            flags = 0
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
 * Checks if the Payment Loyalty Module (PLM) app is installed on the device.
 *
 * @param packageManager The PackageManager instance used to query information about installed packages.
 * @return Boolean indicating whether the PLM app is installed on the device.
 */
fun isPLMInstalled(packageManager: PackageManager): Boolean {
    return packageManager.getLaunchIntentForPackage(PLIntentsFilters.APP_ID) != null
}