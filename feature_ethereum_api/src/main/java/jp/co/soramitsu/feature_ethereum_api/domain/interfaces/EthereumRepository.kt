/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_ethereum_api.domain.interfaces

import jp.co.soramitsu.feature_ethereum_api.domain.model.EthRegisterState
import jp.co.soramitsu.feature_ethereum_api.domain.model.EthereumCredentials
import jp.co.soramitsu.feature_ethereum_api.domain.model.Gas
import java.math.BigDecimal
import java.math.BigInteger
import java.security.KeyPair

interface EthereumRepository {

    fun getValTokenAddress(ethCredentials: EthereumCredentials): String

    fun getSerializedProof(ethCredentials: EthereumCredentials): String

    suspend fun getEthCredentials(mnemonic: String): EthereumCredentials

    fun startWithdraw(
        amount: BigDecimal,
        srcAccountId: String,
        ethAddress: String,
        transactionFee: String,
        keyPair: KeyPair
    )

    fun startCombinedValErcTransfer(
        partialAmount: BigDecimal,
        amount: BigDecimal,
        srcAccountId: String,
        withdrawEthAddress: String,
        transferEthAddress: String,
        transactionFee: String,
        ethCredentials: EthereumCredentials,
        keyPair: KeyPair
    )

    fun startCombinedValTransfer(
        partialAmount: BigDecimal,
        amount: BigDecimal,
        transferAccountId: String,
        transferName: String,
        transactionFee: BigDecimal,
        description: String,
        ethCredentials: EthereumCredentials,
        keyPair: KeyPair,
        tokenAddress: String
    )

    fun getEthWalletAddress(ethCredentials: EthereumCredentials): String

    fun calculateValErc20TransferFee(): BigDecimal

    fun calculateValErc20WithdrawFee(): BigDecimal

    fun setGasPrice(gasPriceInGwei: BigInteger): BigDecimal

    fun setGasLimit(gasLimit: BigInteger): BigDecimal

    fun transferValErc20(
        to: String,
        amount: BigDecimal,
        ethCredentials: EthereumCredentials,
        tokenAddress: String
    )

    suspend fun getEthRegistrationState(): EthRegisterState

    suspend fun registrationStarted(operationId: String)

    suspend fun registrationCompleted(operationId: String)

    suspend fun registrationFailed(operationId: String)

    fun getGasEstimations(gasLimit: BigInteger, ethCredentials: EthereumCredentials): Gas

    fun getBlockChainExplorerUrl(transactionHash: String): String

    fun updateValErc20AndEthBalance(
        ethCredentials: EthereumCredentials,
        ethWalletAddress: String,
        tokenAddress: String
    )

    fun confirmWithdraw(
        ethCredentials: EthereumCredentials,
        amount: BigDecimal,
        txHash: String,
        accountId: String,
        gasPrice: BigInteger,
        gasLimit: BigInteger,
        tokenAddress: String
    ): String

    fun getActualEthRegisterState(): EthRegisterState.State

    fun isBridgeEnabled(ethCredentials: EthereumCredentials): Boolean

    fun calculateValErc20CombinedFee(): BigDecimal
}
