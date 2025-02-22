/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_wallet_impl.presentation.wallet.model

data class AssetModel(
    val id: String,
    val assetFirstName: String,
    val assetLastName: String,
    val assetIconResource: Int,
    val balance: String?,
    val roundingPrecision: Int,
    val position: Int,
    val hidingAllowed: Boolean,
    val displayed: Boolean,
)
