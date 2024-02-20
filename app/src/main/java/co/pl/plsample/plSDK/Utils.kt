package co.pl.plsample.plSDK

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager


/**
 * @file Utils
 * Created by Payment Loyalty on 19/12/2023.
 * Description:
 */

object PLIntentsFilters{
    //payment app package id
    const val APP_ID = "com.winloyalty"

    //intent filter to send intents to loyalty application
    const val PL_TRIGGER_INTENT_ACTION = "com.nominateloyalty.confirm"

    //intent filter to open the loyalty app on specified trigger
    const val OPEN_PLM_ACTION = "com.plm.OPEN_APP"

    //Payment loyalty broadcast status(PLStatus) of loyalty execution
    const val PL_CONFIRMATION_ACTION = "com.payment.confirm"
}

object PLIntentTrigger{
    const val POST_AMOUNT_ENTRY = "post_amount_entry"
    const val POST_CARD_PRESENTED = "post_card_presented"
    const val POST_TRANSACTION = "post_transaction"
}

object PLIntentParamKey {
    const val CARD_TOKEN = "card_token"
    const val AMOUNT = "amount"
    const val DISCOUNT = "discount_amount"
    const val LAUNCH_FROM = "launch_from"
    const val STATUS = "status"
    const val TRANSACTION_ID = "transaction_id"
}

object PLStatus {
    const val NO_ACTION_NEEDED = 0
    const val FAIL = 3
    const val DENIED = 4
    const val REWARD = 5
    const val OPEN_APP = 6
}

object PLIntent {
    val PL_OPEN_APP = "co.pl.action.openApp"
    val PL_FAIL = "co.pl.action.fail"
    val PL_REWARD = "co.pl.action.reward"
    val PL_DENIED = "co.pl.action.denied"
    val PL_NO_ACTION_NEEDED = "co.pl.action.silent"
}