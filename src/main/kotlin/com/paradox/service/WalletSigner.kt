package com.paradox.service

import com.google.protobuf.Any
import com.google.protobuf.ByteString
import com.google.protobuf.Message
import cosmos.base.v1beta1.CoinOuterClass
import cosmos.crypto.secp256k1.Keys
import cosmos.tx.v1beta1.ServiceOuterClass
import cosmos.tx.v1beta1.TxOuterClass
import cosmwasm.wasm.v1.Tx
import io.provenance.client.grpc.BaseReqSigner
import io.provenance.client.grpc.GasEstimationMethod
import io.provenance.client.grpc.PbClient
import io.provenance.client.grpc.Signer
import io.provenance.client.protobuf.extensions.getBaseAccount
import io.provenance.hdwallet.bip39.MnemonicWords
import io.provenance.hdwallet.common.hashing.sha256
import io.provenance.hdwallet.signer.BCECSigner
import io.provenance.hdwallet.wallet.Account
import io.provenance.hdwallet.wallet.Wallet
import io.provenance.metadata.v1.MsgWriteScopeRequest
import io.provenance.metadata.v1.Party
import io.provenance.metadata.v1.PartyType
import io.provenance.metadata.v1.ScopeRequest
import java.net.URI
import java.util.UUID

fun Message.toAny() = Any.pack(this, "")

class WalletSigner(networkType: NetworkType, mnemonic: String, passphrase: String = "") : Signer {

    val wallet = Wallet.fromMnemonic(networkType.prefix, passphrase.toCharArray(), MnemonicWords.of(mnemonic))

    val account: Account = wallet[networkType.path]

    override fun address(): String = account.address.value

    override fun pubKey(): Keys.PubKey =
        Keys.PubKey.newBuilder().setKey(ByteString.copyFrom(account.keyPair.publicKey.compressed())).build()

    override fun sign(data: ByteArray): ByteArray = BCECSigner()
        .sign(account.keyPair.privateKey, data.sha256())
        .encodeAsBTC().toByteArray()
}