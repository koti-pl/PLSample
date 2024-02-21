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
class PLV2Triggers(
    val serviceMessenger: Messenger,
    val clientMessenger: Messenger,
) {
    fun sendPostAmount(amount: String) {
        val data = bundleOf(
            V2Trigger.Params.AMOUNT to amount,
        )
        sendTrigger(V2Trigger.POST_AMOUNT, data)
    }

    /**
     * Posts information about a card presentation event to the Payment Loyalty Module (PLM) app if installed on the device.
     *
     * @param cardToken The token representing the user's card information.
     * @param cardType The type of the (ex: Visa)
     * @param amount The amount associated with the card presentation event.
     */
    fun sendPostCard(amount: String, cardToken: String, cardType: String) {
        val data = bundleOf(
            V2Trigger.Params.AMOUNT to amount,
            V2Trigger.Params.CARD_TOKEN to cardToken,
            V2Trigger.Params.CARD_TYPE to cardType
        )
        sendTrigger(V2Trigger.POST_CARD_PRESENT, data)
    }

    /**
     * Posts a transaction to the Payment Loyalty Module (PLM) app if installed on the device.
     *
     * @param cardToken The token representing the user's card information.
     * @param cardType The type of the (ex: visa)
     * @param amount The amount associated with the transaction.
     * @param transactionId The current transaction id.
     * @param transactionStatus The current transaction status. If transaction is success then it is true otherwise false
     */
    fun sendPostTransaction(
        amount: String,
        cardToken: String,
        cardType: String?=null,
        transactionId: String? = null,
        transactionStatus: Boolean
    ) {
        val data = bundleOf(
            V2Trigger.Params.AMOUNT to amount,
            V2Trigger.Params.CARD_TOKEN to cardToken,
            V2Trigger.Params.CARD_TYPE to cardType,
            V2Trigger.Params.TRANSACTION_STATUS to transactionStatus,
            V2Trigger.Params.TRANSACTION_ID to transactionId
        )
        sendTrigger(V2Trigger.POST_TRANSACTION, data)
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

}