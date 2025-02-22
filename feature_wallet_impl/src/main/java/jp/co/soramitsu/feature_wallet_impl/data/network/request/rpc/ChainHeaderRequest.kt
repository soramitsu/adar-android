/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_wallet_impl.data.network.request.rpc

import jp.co.soramitsu.fearless_utils.wsrpc.request.runtime.RuntimeRequest

class ChainHeaderRequest(hash: String) : RuntimeRequest("chain_getHeader", listOf(hash))
class ChainLastHeaderRequest() : RuntimeRequest("chain_getHeader", listOf())
