/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.common.domain

import kotlinx.coroutines.flow.Flow

interface AppStateProvider {
    val isForeground: Boolean
    val isBackground: Boolean
    fun observeState(): Flow<AppEvent>

    enum class AppEvent {
        ON_CREATE, ON_RESUME, ON_PAUSE, ON_DESTROY
    }
}
