package co.pl.plsample

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import co.pl.plsample.plSDK.PLIntentParamKey
import co.pl.plsample.plSDK.PLIntentsFilters
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

    lateinit var couponView: AppCompatImageView

    private var rewardBroadcastRegistered = false
    private val rewardReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "received result on broadcast")
            val status = intent.getIntExtra(PLIntentParamKey.STATUS, PLStatus.NO_ACTION_NEEDED)
            //If we receive coupon base64 string then start print job
            val couponBase64 = intent.getStringExtra(PLIntentParamKey.COUPON_BASE64)
            printTheCoupon(couponBase64)
            if (status == PLStatus.REWARD) {
                val transactionAmount = intent.getStringExtra(PLIntentParamKey.AMOUNT)
                val discount = intent.getStringExtra(PLIntentParamKey.DISCOUNT) ?: "0"
                Log.d(TAG, "reward details :: $transactionAmount")
                Log.d(TAG, "reward details :: $discount")
                Log.d(TAG, "reward details :: $status")
                val result = PLTriggerResponse(
                    status = status,
                    discount = discount,
                    couponBase64 = couponBase64,
                    message = "Result"
                )
                Log.e(TAG, "Receiver reward processing")
                adjustTheAmount(discount)
            } else {
                Log.e(TAG, "Receiver no reward processing")
            }
        }
    }


    /** activity launcher*/
    var activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i(TAG, "received result on launcher")
            if (result.resultCode == Activity.RESULT_OK) {
                if(!rewardBroadcastRegistered) {
                    result.data?.let {
                        //If we receive coupon base64 string then start print job
                        val couponBase64 = it.getStringExtra(PLIntentParamKey.COUPON_BASE64)
                        printTheCoupon(couponBase64)

                        val status = it.getIntExtra(PLIntentParamKey.STATUS, 0)
                        if (status == PLStatus.REWARD) {
                            val discountAmount =
                                (it.getStringExtra(PLIntentParamKey.DISCOUNT) ?: "0").trim()
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
                }

            } else {
                result.data?.let {
                    //If we receive coupon base64 string then start print job
                    val couponBase64 = it.getStringExtra(PLIntentParamKey.COUPON_BASE64)
                    printTheCoupon(couponBase64)
                }
                Log.e(TAG, "result not ok continue payment==>${result.data}")
                // handle error state
            }
        }

    fun adjustTheAmount(discountAmount: String) {
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
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
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
        couponView = findViewById(R.id.couponView)
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
                cardToken = etCardToken.text.toString()
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

    /**
     * Note : If you want receive the result on broadcast uncomment or us the below code
     * */
    /*override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(PLIntentsFilters.PL_FLOW_REWARD_ACTION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(rewardReceiver, filter, RECEIVER_EXPORTED)
        } else registerReceiver(rewardReceiver, filter)
        rewardBroadcastRegistered = true
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(rewardReceiver)
        rewardBroadcastRegistered = false
    }*/

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

    internal fun printTheCoupon(couponBase64: String?) {
        Log.d(TAG, "couponBase64 is available:: ${!couponBase64.isNullOrEmpty()}")
        if (!couponBase64.isNullOrEmpty()) {
           val decodedBytes = Base64.decode(couponBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            bitmap?.let {
                couponView.setImageBitmap(it)
            }?:{
                couponView.setImageBitmap(null)
            }
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