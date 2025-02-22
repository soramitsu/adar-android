/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.common.data.network.dto

import java.math.BigInteger

data class XorBalanceDto(
    val free: BigInteger,
    val reserved: BigInteger,
    val miscFrozen: BigInteger,
    val feeFrozen: BigInteger,
    val bonded: BigInteger,
    val redeemable: BigInteger,
    val unbonding: BigInteger
)
