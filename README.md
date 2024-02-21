# Payment Loyalty Sample App

## Introduction

The Payment Loyalty Sample App demonstrates the implementation of triggers to manage loyalty points
for customers based on their payment transactions my mocking the data.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Triggers](#triggers)
- [How To Send Trigger](#howToSendTrigger)

## Features

- Demonstrate usage triggers using the broadcast intents
- Demonstrate usage triggers using the service

## Installation

1. Clone the repository:

```sh 
git clone https://github.com/koti-pl/PLSample.git
```

## Triggers

1. **Post Amount(T1):** This trigger is activated after the user enters the payment amount. It serves as
   a signal for the Payment Loyalty Manager (PLM) to commence searching for applicable rewards based
   on the merchant's configurations. By sending this trigger, the system initiates the reward
   identification process, ensuring that users are offered relevant benefits corresponding to their
   transaction amount and the merchant's loyalty program.

2. **Post Card((T1)):** Upon successful authentication of the user's card, this trigger is need to send to the
   payment loyalty app. It prompts the app to query its available rewards associated with the
   specific card. This ensures that users are promptly notified of any potential rewards or benefits
   they can avail themselves of with their authenticated card, enhancing the overall user experience
   and Motivating card usage.

3. **Post Transaction((T3)):** Following the completion of a transaction, this trigger need to send. Its
   purpose is to facilitate various post-transaction activities within the Payment Loyalty ecosystem.
   Specifically, it enables the payment loyalty app to mark the status of any associated coupons or
   rewards, ensuring accurate tracking and management of redeemed offers. Additionally, it allows
   for the seamless capture of transaction details, minimizing the risk of discrepancies or issues
   related to redeemed transactions. By leveraging this trigger, the system ensures a smooth and
   reliable post-transaction process, enhancing user satisfaction and operational efficiency.    


## How To Send Trigger

We can send Triggers by using 
1. [Broadcast intents](https://developer.android.com/develop/background-work/background-tasks/broadcasts)
2. [Service and Messenger](https://developer.android.com/develop/background-work/services/bound-services)

### [Broadcast intents](https://developer.android.com/develop/background-work/background-tasks/broadcasts) : Use the following code snippets to send T1,T2 and T3 triggers

i. Post Amount Trigger(T1)
```sh
/**
 * Posts the entered amount information to the Payment Loyalty Module (PLM) app if installed on the device.
 *
 * @param amount The amount entered by the user.
 * @return Boolean indicating whether the entered amount information was successfully posted to the PLM app.
 */
fun Context.sendPostAmountEntered(
    amount: String
): Boolean {
    // Check if the PLM app is installed on the device
    return if (isPLMInstalled(packageManager)) {
        // Create an intent to trigger the PLM app with the entered amount details
        val intent = Intent(PLIntentsFilters.TRIGGER_ACTION).apply {
            putExtra(PLIntentParamKey.AMOUNT, amount)
            putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_AMOUNT_ENTRY)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES) // Ensure the intent is delivered to the PLM app
        }

        // Send a broadcast to the PLM app with the entered amount details
        sendBroadcast(intent)

        // Return true indicating successful posting of entered amount information
        true
    } else {
        // Continue with the payment if the PLM app is not installed
        false
    }
}
```

ii. Post Card Present(T2)
```sh
/**
 * Posts information about a card presentation event to the Payment Loyalty Module (PLM) app if installed on the device.
 *
 * @param cardToken The token representing the user's card information.
 * @param cardType The type of the (ex: visa)
 * @param amount The amount associated with the card presentation event.
 * @return Boolean indicating whether the card presentation event was successfully posted to the PLM app.
 */
fun Context.sendPostCardPresent(
    cardToken: String,
    cardType: String? = null,
    amount: String
): Boolean {
    // Check if the PLM app is installed on the device
    return if (isPLMInstalled(packageManager)) {
        // Create an intent to trigger the PLM app with card presentation details
        val intent = Intent(PLIntentsFilters.TRIGGER_ACTION).apply {
            putExtra(PLIntentParamKey.CARD_TOKEN, cardToken)
            putExtra(V2Trigger.Params.CARD_TYPE, cardType)
            putExtra(PLIntentParamKey.AMOUNT, amount)
            putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_CARD_PRESENTED)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }

        // Send a broadcast to the PLM app with the card presentation details
        sendBroadcast(intent)

        // Return true indicating successful card presentation posting
        true
    } else {
        // Return false if the PLM app is not installed
        false
    }
}
```

iii. Post Transaction(T3)
```sh
/**
 * Posts a transaction to the Payment Loyalty Module (PLM) app if installed on the device.
 *
 * @param cardToken The token representing the user's card information.
 * @param cardType The type of the (ex: visa)
 * @param amount The amount associated with the transaction.
 * @param transactionId The current transaction id.
 * @param transactionStatus The current transaction status. If transaction is success then it is true otherwise false
 * @return Boolean indicating whether the transaction was successfully posted to the PLM app.
 */
fun Context.sendPostTransaction(
    cardToken: String,
    cardType: String? = null,
    amount: String,
    transactionId: String? = null,
    transactionStatus: Boolean
): Boolean {
    return if (isPLMInstalled(packageManager)) {
        val intent = Intent(PLIntentsFilters.TRIGGER_ACTION).apply {
            putExtra(PLIntentParamKey.CARD_TOKEN, cardToken)
            putExtra(V2Trigger.Params.CARD_TYPE, cardType)
            putExtra(PLIntentParamKey.AMOUNT, amount)
            putExtra(PLIntentParamKey.LAUNCH_FROM, PLIntentTrigger.POST_TRANSACTION)
            putExtra(V2Trigger.Params.TRANSACTION_STATUS, transactionStatus)
            putExtra(V2Trigger.Params.TRANSACTION_ID, transactionId)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }
        sendBroadcast(intent)
        true
    } else { //If plm app not installed
        false
    }
}
```

