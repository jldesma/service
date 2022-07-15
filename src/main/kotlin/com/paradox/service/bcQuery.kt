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

fun bcQuery(data: ByteString, client: PbClient, CONTRACT_ADDRESS: String): String {
    val queryMatchMsg = QuerySmartContractStateRequest
        .newBuilder()
        .setAddress(CONTRACT_ADDRESS)
        .setQueryData(data)
        .build()

    client.wasmClient.queryWasm(queryMatchMsg).also {
        return String(it.data.toByteArray())
    }
}