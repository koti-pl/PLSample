package co.pl.plsample.plSDK

import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import androidx.core.os.bundleOf

/**
 * @file PLTrigger
 * Created by Payment Loyalty on 19/02/2024.
 * Description: Helper class to send triggers to Payment Loyalty
 */
class PLTrigger(
    val serviceMessenger: Messenger,
    val clientMessenger: Messenger,
) {
    fun sendPostAmount(amount: String) {
        val data = bundleOf(
            Trigger.Params.AMOUNT to amount,
        )
        sendTrigger(Trigger.POST_AMOUNT, data)
    }

    fun sendPostCard(amount: String, cardToken: String, cardType: String) {
        val data = bundleOf(
            Trigger.Params.AMOUNT to amount,
            Trigger.Params.CARD_TOKEN to cardToken,
            Trigger.Params.CARD_TYPE to cardType
        )
        sendTrigger(Trigger.POST_CARD_PRESENT, data)
    }

    fun sendPostTransaction(
        amount: String,
        cardToken: String,
        transactionId: String? = null,
        transactionStatus: Boolean
    ) {
        val data = bundleOf(
            Trigger.Params.AMOUNT to amount,
            Trigger.Params.CARD_TOKEN to cardToken,
            Trigger.Params.TRANSACTION_STATUS to transactionStatus,
            Trigger.Params.TRANSACTION_ID to transactionId
        )
        sendTrigger(Trigger.POST_TRANSACTION, data)
    }


    private fun sendTrigger(
        trigger: Int,
        bundle: Bundle
    ) {
        try {
            val msg = Message.obtain(null, trigger).apply {
                data = bundle
                replyTo = clientMessenger
            }
            serviceMessenger.send(msg)
            Log.i("Trigger", "SENT $trigger with data ${bundle}")
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    companion object {

        val TRIGGER_ACTION = "co.paymentLoyalty.action.trigger"
        val APP_PACKAGE = "com.winloyalty"

        object Trigger {

            const val NONE = 0
            const val POST_AMOUNT = 1
            const val POST_CARD_PRESENT = 2
            const val POST_TRANSACTION = 3

            object Status {
                const val ERROR = 0
                const val LAUNCH_LOYALTY = 1    //launch payment loyalty app to launch reward UI
                const val RECEIVED_TRIGGER = 3  //Acknowledgement of each trigger
                const val REWARD = 5  // user has some reward
            }

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
    }


}