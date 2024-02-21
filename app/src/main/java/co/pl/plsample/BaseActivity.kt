package co.pl.plsample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import co.pl.plsample.plSDK.PLIntentParamKey
import co.pl.plsample.plSDK.PLStatus
import co.pl.plsample.plSDK.PLTriggerResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * @file BaseActivity
 * Created by Payment Loyalty on 20/02/2024.
 * Description: Base Activity of payment app
 */
abstract class BaseActivity : AppCompatActivity() {

    private val TAG = "BaseActivity"

    var cardToken: String = "1234"
    var amount: String? = null

    var activeTrigger: String = ""


    //views

    lateinit var tvTriggerAcknowledgment: AppCompatTextView

    lateinit var etAmount: AppCompatEditText
    lateinit var etCardToken: AppCompatEditText

    //transaction control
    lateinit var btnStartTransaction: AppCompatButton
    lateinit var btnStopTransaction: AppCompatButton

    //transaction status control
    lateinit var btnPostAmount: AppCompatButton
    lateinit var btnPostCardPresent: AppCompatButton
    lateinit var btnPostTransaction: AppCompatButton


    /** activity launcher*/
    var activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val status = it.getIntExtra(PLIntentParamKey.STATUS, 0)
                    if (status == PLStatus.REWARD) {
                        val discountAmount = (it.getStringExtra(PLIntentParamKey.DISCOUNT)?: "0").trim()
                        adjustTheAmount(discountAmount)
                    } else {
                        Log.e(TAG, "continue payment==>")
                        // Process with actual amount (no reward)
                        // You might want to include your actual amount processing logic here
                    }

                } ?: run {
                    Log.e(TAG, "continue payment==>")
                    // Process with actual amount (no reward)
                    // You might want to include your actual amount processing logic here
                }

            } else {
                // handle error state
            }
        }

    fun adjustTheAmount(discountAmount: String){
        lifecycleScope.launch {
            val discount = discountAmount.toDouble()
            if (discount > 0) {
                val totalAmount = (amount ?: "0").toDouble()
                val adjustedAmount = (totalAmount - discount).toString()
                Log.i(TAG, "Adjusted amount $adjustedAmount")
                etAmount.setText(adjustedAmount)
            } else {
                Log.e(TAG, "continue payment==>")
                // Process with actual amount (no reward)
                // You might want to include your actual amount processing logic here
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_main)
        initViews()
        handleVisibility()
        handleAdditionalTransactionsControllers()
    }

    private fun initViews() {

        findViewById<AppCompatImageButton>(R.id.terminateApp).setOnClickListener {
            finishAffinity()
        }

        tvTriggerAcknowledgment = findViewById(R.id.tvResponse)

        etAmount = findViewById(R.id.etAmount)
        etCardToken = findViewById<AppCompatEditText>(R.id.etCardToken).apply {
            setText(cardToken)
        }

        //transaction controls
        btnStartTransaction = findViewById(R.id.startTransaction)
        btnStopTransaction = findViewById(R.id.stopTransaction)

        btnPostAmount = findViewById<AppCompatButton>(R.id.postAmount).apply {
            setOnClickListener {
                amount = etAmount.text.toString()
                postAmountEntered(etAmount.text.toString())
            }
        }
        btnPostCardPresent = findViewById<AppCompatButton>(R.id.postcard).apply {
            setOnClickListener {
                amount = etAmount.text.toString()
                postCardPresented(
                    amount = etAmount.text.toString(),
                    cardToken = etCardToken.text.toString(),
                    cardType = "Visa"
                )
            }
        }
        btnPostTransaction = findViewById<AppCompatButton>(R.id.postTransactions).apply {
            setOnClickListener {
                amount = etAmount.text.toString()
                postTransaction(
                    amount = etAmount.text.toString(),
                    cardToken = etCardToken.text.toString(),
                    cardType = "Visa",
                    transactionId = "19071992",
                    transactionStatus = true
                )
            }
        }

        findViewById<AppCompatButton>(R.id.resetData).setOnClickListener {
            resetTransaction()
        }

    }

    private fun resetTransaction() {
        activeTrigger = ""
        cardToken = "1234"

        etAmount.setText("")
        etCardToken.setText(cardToken)
        tvTriggerAcknowledgment.setText(R.string.trigger_status_will_be_updated_here)
    }

    fun showError(message: String) {
        Snackbar.make(
            this,
            tvTriggerAcknowledgment,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    //handle this methode as needed
    fun updateTriggerResponse(response: PLTriggerResponse) {
        lifecycleScope.launch {
            tvTriggerAcknowledgment.text = "Received : ${response}"
        }
    }

    abstract fun handleVisibility()

    abstract fun handleAdditionalTransactionsControllers()

    abstract fun postAmountEntered(
        amount: String,      //Mandatory :: it should be always available and more than zero(0)
    )

    abstract fun postCardPresented(
        amount: String,      //Mandatory :: it should be always available and more than zero(0)
        cardToken: String,   //Mandatory :: unique identifier of the card to identify card to check the reward availability. it should be same always for the same card
        cardType: String?   //Not mandatory :: The value is helpfully to providing detailed analytics to the user
    )

    abstract fun postTransaction(
        amount: String,      //Mandatory :: it should be always available and more than zero(0)
        cardToken: String,   //Mandatory :: unique identifier of the card to identify card to check the reward availability. it should be same always for the same card
        cardType: String?,   //Not mandatory :: The value is helpfully to providing detailed analytics to the user
        transactionId: String?, //Not mandatory :: The value is helpfully to providing detailed analytics to the user
        transactionStatus: Boolean = true, // Mandatory :: By default we consider it as successful transaction if this value is not passed
    )


    companion object {
        val ERROR_SERVICE_NOT_AVAILABLE = "Service not started, Please Start!"
    }
}