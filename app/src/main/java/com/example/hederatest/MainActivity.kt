package com.example.hederatest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hedera.hashgraph.sdk.AccountBalance
import com.hedera.hashgraph.sdk.AccountBalanceQuery
import com.hedera.hashgraph.sdk.AccountId
import com.hedera.hashgraph.sdk.AccountInfoQuery
import com.hedera.hashgraph.sdk.AddressBookQuery
import com.hedera.hashgraph.sdk.Client
import com.hedera.hashgraph.sdk.FileId
import com.hedera.hashgraph.sdk.Hbar
import com.hedera.hashgraph.sdk.PrivateKey
import com.hedera.hashgraph.sdk.TopicCreateTransaction
import com.hedera.hashgraph.sdk.TransferTransaction
import com.hedera.hashgraph.sdk.logger.LogLevel
import com.hedera.hashgraph.sdk.logger.Logger
import java.math.BigDecimal


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testQuery()

        // Not working on API levels: 28, 29, 30
        testQueryAsync()

        testTransferTransactions()
    }

    private fun testTransferTransactions() {
        val operatorId = AccountId.fromString("0.0.69")
        val operatorKey = PrivateKey.fromString("302e0..")

        val client = Client.forPreviewnet()
        client.setOperator(operatorId, operatorKey)

        val recipientId = AccountId.fromString("0.0.1141")
        val amount = Hbar(BigDecimal(0.1))
        val transactionResponse = TransferTransaction()
            .addHbarTransfer(operatorId, amount.negated())
            .addHbarTransfer(recipientId, amount)
            .setTransactionMemo("transfer test")
            .execute(client)

        TopicCreateTransaction()
            .setTopicMemo("topicMemo")
            .execute(client)

        Log.w("transfer transaction: ", transactionResponse.toString())
    }

    fun testQuery() {
        val operatorId = AccountId.fromString("0.0.69")
        val operatorKey = PrivateKey.fromString("302e0..")

        val client = Client.forPreviewnet()
        client.setOperator(operatorId, operatorKey)

        val nodeAddressBook = AddressBookQuery().setFileId(FileId.ADDRESS_BOOK).execute(client)
        val accountInfo = AccountInfoQuery().setAccountId(operatorId).execute(client)

        Log.w("account info", accountInfo.toString())

        val balance = AccountBalanceQuery()
            .setAccountId(operatorId)
            .execute(client).hbars

        Log.w("account balance: ", balance.toString())
    }

    private fun testQueryAsync() {
        val operatorId = AccountId.fromString("0.0.69")
        val operatorKey = PrivateKey.fromString("302e0..")

        val client = Client.forPreviewnet()
        client.setOperator(operatorId, operatorKey)

        try {
            client.let { client ->
                if (client != null) {
                    AccountBalanceQuery().setAccountId(operatorId).executeAsync(client)
                        .thenAccept {
                            Log.w("Success: ", it.toString())
                            client.close()
                        }
                } else {
                    Log.e( "Error: ", "")
                }
            }
        } catch (e: Exception) {
            Log.w("Error, exception: ", e.message.toString())
        }
    }

}