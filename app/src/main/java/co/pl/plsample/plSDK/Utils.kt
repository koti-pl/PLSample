package co.pl.plsample.plSDK

/**
 * @file Utils
 * Created by Payment Loyalty on 19/12/2023.
 * Description:
 */

object PLIntentsFilters{
    //payment app package id
    const val APP_ID = "com.winloyalty"


    //Payment loyalty trigger Action [works for both broadcast and service]
    val TRIGGER_ACTION = "co.paymentLoyalty.action.trigger"

    //intent filter to send intents to loyalty application [Deprecated only works with broadcast]
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
}

object PLStatus {
    const val NO_ACTION_NEEDED = 0
    const val RECEIVED_TRIGGER = 1
    const val FAIL = 3
    const val REWARD = 5
    const val OPEN_APP = 6
}

object V2Trigger {
    const val POST_AMOUNT = 1
    const val POST_CARD_PRESENT = 2
    const val POST_TRANSACTION = 3

    object Params {
        const val AMOUNT = "amount"
        const val CARD_TOKEN = "cardToken"
        const val CARD_TYPE = "cardType"
        const val TRANSACTION_ID = "transactionId"
        const val TRANSACTION_STATUS = "transactionStatus"
        const val DISCOUNT_AMOUNT = "discount_amount"
        const val STATUS = "status"
        const val MESSAGE = "message"
    }
}