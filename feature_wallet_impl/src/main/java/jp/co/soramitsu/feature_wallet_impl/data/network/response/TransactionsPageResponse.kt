/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_wallet_impl.data.network.response

import androidx.annotation.Keep
import com.google.gson.JsonObject

@Keep
data class HistoryResponse(val data: HistoryResponseData)

@Keep
data class HistoryResponseData(val historyElements: HistoryResponseDataElements)

@Keep
data class HistoryResponseDataElements(val nodes: List<HistoryResponseItem>, val pageInfo: HistoryResponsePageInfo)

@Keep
data class HistoryResponsePageInfo(
    val endCursor: String,
    val hasNextPage: Boolean,
)

@Keep
data class HistoryResponseItem(
    val id: String,
    val blockHash: String,
    val module: String,
    val method: String,
    val timestamp: String,
    val networkFee: String,
    val execution: ExecutionResult,
    val data: JsonObject
)

@Keep
data class ExecutionResult(
    val success: Boolean,
    val error: Error?
)

@Keep
data class Error(
    val moduleErrorId: Int?,
    val moduleErrorIndex: Int?,
    val nonModuleErrorMessage: String?
)

@Keep
data class HistoryResponseItemTransfer(
    val to: String,
    val from: String,
    val amount: String,
    val assetId: String,
)

@Keep
data class HistoryResponseItemSwap(
    val selectedMarket: String,
    val liquidityProviderFee: String,
    val baseAssetId: String,
    val targetAssetId: String,
    val baseAssetAmount: String,
    val targetAssetAmount: String,
)
