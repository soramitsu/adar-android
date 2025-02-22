/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.common.data.network.substrate

import jp.co.soramitsu.fearless_utils.wsrpc.logging.Logger

class WsLogger : Logger {
    override fun log(message: String?) = com.orhanobut.logger.Logger.d("socket log = $message")

    override fun log(throwable: Throwable?) =
        com.orhanobut.logger.Logger.e(throwable, "socket log error")
}
