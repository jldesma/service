package com.paradox.service

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.google.protobuf.Any
import com.google.protobuf.ByteString
import com.google.protobuf.Message
import cosmos.base.v1beta1.CoinOuterClass
import cosmos.crypto.secp256k1.Keys
import cosmos.tx.v1beta1.ServiceOuterClass
import cosmos.tx.v1beta1.TxOuterClass
import cosmwasm.wasm.v1.Tx
import cosmwasm.wasm.v1.QueryOuterClass.QuerySmartContractStateRequest
import io.provenance.client.grpc.BaseReqSigner
import io.provenance.client.grpc.GasEstimationMethod
import io.provenance.client.grpc.PbClient
import io.provenance.client.grpc.Signer
import io.provenance.client.protobuf.extensions.getBaseAccount
import io.provenance.client.protobuf.extensions.queryWasm
import io.provenance.hdwallet.bip39.MnemonicWords
import io.provenance.hdwallet.common.hashing.sha256
import io.provenance.hdwallet.signer.BCECSigner
import io.provenance.hdwallet.wallet.Account
import io.provenance.hdwallet.wallet.Wallet
import io.provenance.metadata.v1.MsgWriteScopeRequest
import io.provenance.metadata.v1.Party
import io.provenance.metadata.v1.PartyType
import io.provenance.metadata.v1.ScopeRequest
import io.provenance.marker.v1.QueryAccessRequest
import java.net.URI
import java.util.UUID

fun bcExecute(data: ByteString, mnemonic: String, client: PbClient, CONTRACT_ADDRESS: String): String {
    val adminMnemonic = mnemonic;

    val adminSigner = WalletSigner(NetworkType.TESTNET, adminMnemonic)
    val adminAccount = client.authClient.getBaseAccount(adminSigner.address())
    var adminOffset = 0

    val executeMatchMsg = Tx.MsgExecuteContract.newBuilder()
        .setContract(CONTRACT_ADDRESS)
        .setMsg(data)
        .setSender(adminSigner.address())
        .build().toAny()

    client.estimateAndBroadcastTx(
        TxOuterClass.TxBody.newBuilder().addMessages(executeMatchMsg).build(),
        listOf(BaseReqSigner(adminSigner, adminOffset, adminAccount)),
        ServiceOuterClass.BroadcastMode.BROADCAST_MODE_BLOCK).also {
        if (it.txResponse.code != 0) {
            throw Exception("Error matching scope ask/bid, code: ${it.txResponse.code}, message: ${it.txResponse.rawLog}")
        }

        adminOffset++
        return it.txResponse.txhash
    }
}