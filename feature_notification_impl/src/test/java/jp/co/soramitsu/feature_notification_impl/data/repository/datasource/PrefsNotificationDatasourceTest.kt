/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_notification_impl.data.repository.datasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.co.soramitsu.common.data.EncryptedPreferences
import jp.co.soramitsu.common.data.SoraPreferences
import jp.co.soramitsu.common.util.Const.Companion.DEVICE_TOKEN
import jp.co.soramitsu.common.util.Const.Companion.IS_PUSH_UPDATE_NEEDED
import jp.co.soramitsu.test_shared.MainCoroutineRule
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@Ignore
@RunWith(MockitoJUnitRunner::class)
class PrefsNotificationDatasourceTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var soraPreferences: SoraPreferences

    @Mock
    private lateinit var encryptedPreferences: EncryptedPreferences

    private lateinit var prefsNotificationDatasource: PrefsNotificationDatasource

    @Before
    fun setUp() {
        prefsNotificationDatasource = PrefsNotificationDatasource(soraPreferences, encryptedPreferences)
    }

    @Test
    fun `save pushToken calls prefsutil putEncryptedString for DEVICE_TOKEN`() = runBlockingTest {
        val notificationToken = "1234"

        prefsNotificationDatasource.savePushToken(notificationToken)

        verify(encryptedPreferences).putEncryptedString(DEVICE_TOKEN, notificationToken)
    }

    @Test
    fun `save IsPushTokenUpdateNeeded calls prefsutil putBoolean for IS_PUSH_UPDATE_NEEDED`() = runBlockingTest {
        val isUpdateNeeded = true

        prefsNotificationDatasource.saveIsPushTokenUpdateNeeded(isUpdateNeeded)

        verify(soraPreferences).putBoolean(IS_PUSH_UPDATE_NEEDED, isUpdateNeeded)
    }
}
