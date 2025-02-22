/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_notification_impl.data.repository.datasource

import jp.co.soramitsu.common.data.EncryptedPreferences
import jp.co.soramitsu.common.data.SoraPreferences
import jp.co.soramitsu.feature_notification_api.domain.interfaces.NotificationDatasource
import javax.inject.Inject

class PrefsNotificationDatasource @Inject constructor(
    private val soraPreferences: SoraPreferences,
    private val encryptedPreferences: EncryptedPreferences
) : NotificationDatasource {

    companion object {
        private const val PREFS_PUSH_UPDATE_NEEDED = "is_push_update_needed"
        private const val PREFS_PUSH_TOKEN = "device_token"
    }

    override fun saveIsPushTokenUpdateNeeded(updateNeeded: Boolean) {
        // soraPreferences.putBoolean(PREFS_PUSH_UPDATE_NEEDED, updateNeeded)
    }

    override fun savePushToken(notificationToken: String) {
        // encryptedPreferences.putEncryptedString(PREFS_PUSH_TOKEN, notificationToken)
    }
}
