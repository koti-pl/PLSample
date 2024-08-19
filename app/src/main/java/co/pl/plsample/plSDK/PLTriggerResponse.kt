package co.pl.plsample.plSDK

/**
 * @file PLTriggerResponse
 * Created by Payment Loyalty on 19/02/2024.
 * Description: trigger response
 */
data class PLTriggerResponse(
    val status : Int,
    val discount:String?,
    val message:String?,
    val couponBase64:String? = null
)
