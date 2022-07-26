package com.paradox.service

import com.google.gson.Gson
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.net.URI
import java.util.UUID


@RestController
@RequestMapping("api/blockchain")
class BlockchainController {
    val CONTRACT_ADDRESS = "tp1jpuk5d0auylc7c7dkmds0auzadt7d39tw85fkudzs94y53mavvxs0jg5sr"
    val client = PbClient("pio-testnet-1", URI("grpcs://grpc.test.provenance.io:443"), GasEstimationMethod.MSG_FEE_CALCULATION)
    val gson = Gson()

    @GetMapping("GetByToken/{token_id}")
    fun GetByToken(@PathVariable token_id: String): CurrentAskForTokenResponse {
        val result = bcQuery(ByteString.copyFromUtf8("""{"current_ask_for_token": { "token_id" : "${token_id}" }}"""), client, CONTRACT_ADDRESS);

        return gson.fromJson(result, CurrentAskForTokenResponse::class.java);
    }

    @GetMapping("GetByOwner/{address}")
    fun GetByOwner(@PathVariable address:String): Array<MyMintMsg> {
        val result =  bcQuery(ByteString.copyFromUtf8("""{"tokens_of": { "owner" : "${address}" }}"""), client, CONTRACT_ADDRESS);

        return gson.fromJson(result, Array<MyMintMsg>::class.java);
    }

    @PostMapping
    fun Insert(@RequestBody invoice:InvoiceNTF): String {
        val data = MintMsg (
            MyMintMsg(
                token_id = UUID.randomUUID().toString(),
                owner = invoice.account_address,
                token_uri = "",
                extension = invoice
            ),
            Coin("token",invoice.invoice.toString())
        )

        val json:String = gson.toJson(data)
        val result =  bcExecute(ByteString.copyFromUtf8("""{"mint":{ "mint": ${json} }}"""), "century draft give hazard assault swing attract civil rescue enable model annual session alcohol income utility alley urge play stove silver practice stumble jewel", client, CONTRACT_ADDRESS);

        return result
    }

    @PutMapping
    fun Update(@RequestBody invoice:InvoiceNTF): String {
        val json:String = gson.toJson(invoice)
        val result =  bcExecute(ByteString.copyFromUtf8("""{"update_data":{ "obj": ${json} }}"""), "century draft give hazard assault swing attract civil rescue enable model annual session alcohol income utility alley urge play stove silver practice stumble jewel", client, CONTRACT_ADDRESS);

        return result
    }
}


