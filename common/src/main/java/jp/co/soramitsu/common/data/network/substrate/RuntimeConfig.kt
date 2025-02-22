/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.common.data.network.substrate

import jp.co.soramitsu.common.BuildConfig
import jp.co.soramitsu.common.R
import jp.co.soramitsu.common.data.network.dto.TokenInfoDto
import jp.co.soramitsu.common.data.network.substrate.runtime.RuntimeHolder
import jp.co.soramitsu.common.util.ext.addHexPrefix
import jp.co.soramitsu.fearless_utils.encrypt.EncryptionType
import jp.co.soramitsu.fearless_utils.extensions.fromHex
import jp.co.soramitsu.fearless_utils.runtime.RuntimeSnapshot
import jp.co.soramitsu.fearless_utils.runtime.metadata.module
import jp.co.soramitsu.fearless_utils.runtime.metadata.storage
import jp.co.soramitsu.fearless_utils.runtime.metadata.storageKey
import jp.co.soramitsu.fearless_utils.ss58.SS58Encoder.toAccountId
import jp.co.soramitsu.fearless_utils.ss58.SS58Encoder.toAddress
import java.math.BigInteger

object OptionsProvider {
    var CURRENT_VERSION_CODE: Int = 0
    var APPLICATION_ID: String = ""
    val fileProviderAuthority: String get() = "$APPLICATION_ID.soraFileProvider"
    const val dexId: Int = 0
    val DEFAULT_ICON: Int = R.drawable.ic_token_default
    const val url: String = BuildConfig.WS_HOST_URL
    const val hash: String = BuildConfig.GENESIS_HASH
    const val typesFilePath: String = BuildConfig.TYPES_FILE_PATH
    const val soraScanUrl: String = BuildConfig.SORA_SCAN_HOST_URL
    const val mortalEraLength = 64
    val encryptionType = EncryptionType.SR25519
    val existentialDeposit: BigInteger = BigInteger.ZERO
    const val substrate = "substrate"
    const val feeAssetId = "0x0200000000000000000000000000000000000000000000000000000000000000"
    const val hexPrefix = "0x"
    const val urlPlaceholder = "https://www.placeholder.com/"
    const val xstTokenId = "0x0200080000000000000000000000000000000000000000000000000000000000"
    val xstPoolTokens = arrayListOf(
        "0x0200040000000000000000000000000000000000000000000000000000000000",
        "0x0200050000000000000000000000000000000000000000000000000000000000",
        "0x0200060000000000000000000000000000000000000000000000000000000000",
        "0x0200070000000000000000000000000000000000000000000000000000000000"
    )
}

fun ByteArray.toSoraAddress(): String = this.toAddress(RuntimeHolder.prefix)
fun String.assetIdFromKey() = this.takeLast(64).addHexPrefix()
fun Any.createAsset(id: String): TokenInfoDto? =
    (this as? List<*>)?.let {
        val s = (it[0] as? ByteArray)?.toString(Charsets.UTF_8)
        val n = (it[1] as? ByteArray)?.toString(Charsets.UTF_8)
        val p = (it[2] as? BigInteger)?.toInt()
        val m = it[3] as? Boolean
        if (s != null && n != null && p != null && m != null)
            TokenInfoDto(id, n, s, p, m) else null
    }

fun RuntimeSnapshot.accountPoolsKey(address: String): String =
    this.metadata.module(Pallete.POOL_XYK.palleteName)
        .storage(Storage.ACCOUNT_POOLS.storageName)
        .storageKey(this, address.toAccountId())

fun RuntimeSnapshot.reservesKey(tokenId: ByteArray): String =
    this.metadata.module(Pallete.POOL_XYK.palleteName)
        .storage(Storage.RESERVES.storageName)
        .storageKey(
            this,
            OptionsProvider.feeAssetId.fromHex(),
            tokenId
        )

enum class Pallete(val palleteName: String) {
    ASSETS("Assets"),
    IROHA_MIGRATION("IrohaMigration"),
    SYSTEM("System"),
    LIQUIDITY_PROXY("LiquidityProxy"),
    POOL_XYK("PoolXYK"),
    POOL_TBC("MulticollateralBondingCurvePool"),
    STAKING("Staking"),
}

enum class Storage(val storageName: String) {
    ASSET_INFOS("AssetInfos"),
    ACCOUNT("Account"),
    RESERVES("Reserves"),
    RESERVES_COLLATERAL("CollateralReserves"),
    LEDGER("Ledger"),
    ACTIVE_ERA("ActiveEra"),
    BONDED("Bonded"),
    UPGRADED_TO_DUAL_REF_COUNT("UpgradedToDualRefCount"),
    ACCOUNT_POOLS("AccountPools"),
    PROPERTIES("Properties"),
    TOTAL_ISSUANCES("TotalIssuances"),
    POOL_PROVIDERS("PoolProviders"),
}

enum class Method(val methodName: String) {
    TRANSFER("transfer"),
    MIGRATE("migrate"),
    SWAP("swap"),
}

enum class Events(val eventName: String) {
    EXTRINSIC_SUCCESS("ExtrinsicSuccess"),
    EXTRINSIC_FAILED("ExtrinsicFailed"),
}

enum class Constants(val constantName: String) {
    SS58Prefix("SS58Prefix")
}
