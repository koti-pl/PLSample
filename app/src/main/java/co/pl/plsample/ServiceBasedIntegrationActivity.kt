package co.pl.plsample

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.lifecycleScope
import co.pl.plsample.plSDK.PLTrigger
import co.pl.plsample.plSDK.PLTriggerResponse
import co.pl.plsample.plSDK.PLIntentTrigger
import co.pl.plsample.plSDK.PLIntentsFilters
import co.pl.plsample.plSDK.PLStatus
import co.pl.plsample.plSDK.V2Trigger
import co.pl.plsample.plSDK.isPLMInstalled
import co.pl.plsample.plSDK.openPLMApp
import kotlinx.coroutines.launch

class ServiceBasedIntegrationActivity : BaseActivity() {
    private val TAG = "ServiceBased"

    private val incomeMessenger: Messenger by lazy {
        Messenger(IncomingHandler(mainLooper))
    }
    private var serverMessenger: Messenger? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i(TAG, "Service : onServiceConnected")
            serverMessenger = Messenger(service)
            handleVisibility()
            updateTriggerResponse(
                PLTriggerResponse(
                    status = PLStatus.RECEIVED_TRIGGER,
                    discount = null,
                    message = "Server Connected"
                )
            )
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "Service : onServiceDisconnected")
            serverMessenger = null
            handleVisibility()
        }
    }

    val plTrigger: (serverMessenger: Messenger, incomeMessenger: Messenger) -> PLTrigger =
        { serverMessenger, incomeMessenger ->
            PLTrigger(serverMessenger, incomeMessenger)
        }

    override fun postAmountEntered(amount: String) {
        if (amount.isNotEmpty()) {
            serverMessenger?.let {
                plTrigger(it, incomeMessenger).sendPostAmount(amount)
                activeTrigger = PLIntentTrigger.POST_AMOUNT_ENTRY
            } ?: run {
                showError(ERROR_SERVICE_NOT_AVAILABLE)
            }
        } else {
            showError("Enter valid amount to continue!")
        }
    }

    override fun postCardPresented(amount: String, cardToken: String, cardType: String?) {
        if (amount.isNotEmpty() && cardToken.isNotEmpty()) {
            serverMessenger?.let {
                plTrigger(it, incomeMessenger).sendPostCard(amount, cardToken, cardType ?: "")
                activeTrigger = PLIntentTrigger.POST_CARD_PRESENTED
            } ?: run {
                showError(ERROR_SERVICE_NOT_AVAILABLE)
            }
        } else {
            showError("Provide valid amount and cardToken to continue!")
        }
    }

    override fun postTransaction(
        amount: String,
        cardToken: String,
        transactionId: String?,
        transactionStatus: Boolean
    ) {
        if (amount.isNotEmpty() && cardToken.isNotEmpty()) {
            serverMessenger?.let {
                plTrigger(it, incomeMessenger).sendPostTransaction(
                    amount,
                    cardToken,
                    transactionId,
                    transactionStatus
                )
                activeTrigger = PLIntentTrigger.POST_TRANSACTION
            } ?: run {
                showError(ERROR_SERVICE_NOT_AVAILABLE)
            }
        } else {
            showError("Invalid data to process the trigger")
        }
    }

    override fun handleVisibility() {
        lifecycleScope.launch {
            findViewById<Group>(R.id.transactionControls).visibility = View.VISIBLE

            serverMessenger?.let {
                btnStartTransaction.visibility = View.GONE
                btnStopTransaction.visibility = View.VISIBLE
            } ?: run {
                btnStopTransaction.visibility = View.VISIBLE
                btnStartTransaction.visibility = View.VISIBLE
            }
        }
    }

    override fun handleAdditionalTransactionsControllers() {

        btnStartTransaction.setOnClickListener {
            startTransaction()
        }

        btnStopTransaction.setOnClickListener {
            stopService()
        }

    }

    private fun startTransaction() {
        if (isPLMInstalled(packageManager)) {
            val intent = Intent(PLIntentsFilters.TRIGGER_ACTION)
            intent.setPackage(PLIntentsFilters.APP_ID)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            showError("PLM Not available")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serverMessenger?.let {
            unbindService(serviceConnection)
        }
    }

    private fun stopService() {
        serverMessenger?.let {
            unbindService(serviceConnection)
            serverMessenger = null
            handleVisibility()
        } ?: run {
            showError("PLM Not available")
        }
    }

    inner class IncomingHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            val serverResponse = msg.data.getPostAmountEntryResponse()
            Log.d("Trigger Received", "response: $serverResponse")
            when (msg.what) {
                PLStatus.RECEIVED_TRIGGER -> {
                    //When we receive the status as RECEIVED_TRIGGER it means payment loyalty app received your request
                    updateTriggerResponse(
                        serverResponse
                    )
                }

                PLStatus.OPEN_APP -> {
                    updateTriggerResponse(serverResponse)
                    amount?.let {
                        openPLMApp(
                            launcher = activityResultLauncher,
                            amount = it,
                            cardToken = cardToken,
                            launchFrom = activeTrigger
                        )
                    }
                }

                PLStatus.REWARD -> {
                    //If there is any reward you can adjust the amount and continue payment
                    processTheReward(serverResponse)
                    updateTriggerResponse(serverResponse)
                }

                PLStatus.FAIL ->{
                    //When we receive the status as FAIL, Means there is no reward or some unexpected error happened, So you can continue with payment
                    updateTriggerResponse(serverResponse)
                    //Continue with payment
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    fun processTheReward(response: PLTriggerResponse) {
        //If we receive the any discount need to append and continue to payment
        val discountAmount = (response.discount ?: "0").trim()
        adjustTheAmount(discountAmount)
    }

}

fun Bundle.getPostAmountEntryResponse(): PLTriggerResponse {
    val status = getInt(V2Trigger.Params.STATUS)
    val message = getString(V2Trigger.Params.MESSAGE)
    val discount = getString(V2Trigger.Params.DISCOUNT_AMOUNT)
    return PLTriggerResponse(
        status = status,
        discount = discount,
        message = message
    )
}