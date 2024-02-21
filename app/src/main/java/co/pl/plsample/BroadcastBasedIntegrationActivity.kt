package co.pl.plsample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.lifecycleScope
import co.pl.plsample.plSDK.PLIntentParamKey
import co.pl.plsample.plSDK.PLIntentTrigger
import co.pl.plsample.plSDK.PLStatus
import co.pl.plsample.plSDK.PLTriggerResponse
import co.pl.plsample.plSDK.openPLMApp
import co.pl.plsample.plSDK.sendPostAmountEntered
import co.pl.plsample.plSDK.sendPostCardPresent
import co.pl.plsample.plSDK.sendPostTransaction
import kotlinx.coroutines.launch


/**
 * @file BroadcastBasedIntegrationActivity
 * Created by Payment Loyalty on 20/02/2024.
 * Description: Integration with broadcast [This approach deprecated]
 */
class BroadcastBasedIntegrationActivity : BaseActivity() {

    override fun handleVisibility() {
        lifecycleScope.launch {
            findViewById<Group>(R.id.transactionControls).visibility = View.GONE
        }
    }

    override fun handleAdditionalTransactionsControllers() {
        //Not required for broadcast based approach
    }

    override fun postAmountEntered(amount: String) {

        val status = sendPostAmountEntered(amount)
        if (status) {
            updateTriggerResponse(getBroadCastSuccessResponse("Post amount trigger sent"))
            activeTrigger = PLIntentTrigger.POST_AMOUNT_ENTRY
        } else {
            //continue payment
            showError("Fail to send postAmount trigger")
        }
    }

    override fun postCardPresented(amount: String, cardToken: String, cardType: String?) {
        val status = sendPostCardPresent(cardToken,cardType,amount)
        if (status) {
            updateTriggerResponse(getBroadCastSuccessResponse("Post card present trigger sent"))
            activeTrigger = PLIntentTrigger.POST_CARD_PRESENTED
        } else {
            //continue payment
            showError("Fail to send postCard trigger")
        }
    }

    override fun postTransaction(
        amount: String,
        cardToken: String,
        cardType:String?,
        transactionId: String?,
        transactionStatus: Boolean
    ) {
        val status = sendPostTransaction(
            cardToken=cardToken,
            cardType = cardType,
            amount = amount,
            transactionId = transactionId,
            transactionStatus = transactionStatus
        )
        if (status) {
            updateTriggerResponse(getBroadCastSuccessResponse("Post transaction trigger sent"))
            activeTrigger = PLIntentTrigger.POST_TRANSACTION
        } else {
            //continue payment
            showError("Fail to send postTransaction trigger")
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(statusBroadcastReceiver)
    }

    // Register the confirmation action receiver
    private fun registerReceiver(){
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.payment.confirm")
        registerReceiver(statusBroadcastReceiver, intentFilter)
    }


    private val statusBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            handleLegacy(intent)
        }
    }
    private fun handleLegacy(intent: Intent) {
        when (intent.getIntExtra(PLIntentParamKey.STATUS, PLStatus.NO_ACTION_NEEDED)) {
            PLStatus.REWARD -> {
                //If we receive the any discount need to append and continue to payment
                val discountAmount = (intent.getStringExtra(PLIntentParamKey.DISCOUNT)?: "0").trim()
                adjustTheAmount(discountAmount)
            }

            PLStatus.OPEN_APP -> {
                amount?.let {
                    openPLMApp(
                        launcher = activityResultLauncher,
                        amount = it,
                        cardToken = cardToken,
                        launchFrom = activeTrigger
                    )
                }
            }

            else ->{
                //continue to payment
            }
        }
    }

    private fun getBroadCastSuccessResponse(
        message: String = "Broadcast Sent"
    ) = PLTriggerResponse(
        status = 0,
        discount = "0",
        message = message
    )
}