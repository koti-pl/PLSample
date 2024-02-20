package co.pl.plsample

import android.view.View
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.lifecycleScope
import co.pl.plsample.plSDK.PLTriggerResponse
import co.pl.plsample.plSDK.postCardPresent
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
        val status = co.pl.plsample.plSDK.postAmountEntered(this,amount)
        if(status){
            updateTriggerResponse(getBroadCastSuccessResponse("Post amount trigger sent"))
        }else{
            //continue payment
        }
    }

    override fun postCardPresented(amount: String, cardToken: String, cardType: String?) {
        val status = postCardPresent(this,cardToken,amount)
        if(status){
            updateTriggerResponse(getBroadCastSuccessResponse("Post card present trigger sent"))
        }else{
            //continue payment
        }
    }

    override fun postTransaction(
        amount: String,
        cardToken: String,
        transactionId: String?,
        transactionStatus: Boolean
    ) {
        val status = co.pl.plsample.plSDK.postTransaction(this,cardToken, amount)
        if(status){
            updateTriggerResponse(getBroadCastSuccessResponse("Post transaction trigger sent"))
        }else{
            //continue payment
        }
    }

    private fun getBroadCastSuccessResponse(
        message:String = "Broadcast Sent"
    ) = PLTriggerResponse(
        status = 0,
        discount = "0",
        message = "Broadcast Sent"
    )
}