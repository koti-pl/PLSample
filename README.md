# Payment Loyalty Sample App

## Introduction

The Payment Loyalty Sample App demonstrates the implementation of triggers to manage loyalty points
for customers based on their payment transactions.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Triggers](#triggers)

## Features

- Demonstrate usage triggers using the broadcast intents
- Demonstrate usage triggers using the service

## Installation

1. Clone the repository:

```sh 
git clone https://github.com/koti-pl/PLSample.git
```

## Triggers

1. **Post Amount:** This trigger is activated after the user enters the payment amount. It serves as
   a signal for the Payment Loyalty Manager (PLM) to commence searching for applicable rewards based
   on the merchant's configurations. By sending this trigger, the system initiates the reward
   identification process, ensuring that users are offered relevant benefits corresponding to their
   transaction amount and the merchant's loyalty program.

2. **Post Card:** Upon successful authentication of the user's card, this trigger is need to send to the
   payment loyalty app. It prompts the app to query its available rewards associated with the
   specific card. This ensures that users are promptly notified of any potential rewards or benefits
   they can avail themselves of with their authenticated card, enhancing the overall user experience
   and Motivating card usage.

3. **Post Transaction:** Following the completion of a transaction, this trigger need to send. Its
   purpose is to facilitate various post-transaction activities within the Payment Loyalty ecosystem.
   Specifically, it enables the payment loyalty app to mark the status of any associated coupons or
   rewards, ensuring accurate tracking and management of redeemed offers. Additionally, it allows
   for the seamless capture of transaction details, minimizing the risk of discrepancies or issues
   related to redeemed transactions. By leveraging this trigger, the system ensures a smooth and
   reliable post-transaction process, enhancing user satisfaction and operational efficiency.    


