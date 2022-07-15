package com.paradox.service

enum class NetworkType(
    /**
     * The hrp (Human Readable Prefix) of the network address
     */
    val prefix: String,
    /**
     * The HD wallet path
     */
    val path: String
) {
    TESTNET("tp", "m/44'/1'/0'/0/0"),
    TESTNET_HARDENED("tp", "m/44'/1'/0'/0/0'"),
    MAINNET("pb", "m/44'/505'/0'/0/0")
}