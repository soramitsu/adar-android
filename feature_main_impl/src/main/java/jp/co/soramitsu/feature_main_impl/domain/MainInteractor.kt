/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_main_impl.domain

import jp.co.soramitsu.common.domain.ResponseCode
import jp.co.soramitsu.common.domain.SoraException
import jp.co.soramitsu.common.domain.credentials.CredentialsRepository
import jp.co.soramitsu.common.resourses.Language
import jp.co.soramitsu.feature_account_api.domain.interfaces.UserRepository
import javax.inject.Inject

class MainInteractor @Inject constructor(
    private val userRepository: UserRepository,
    private val credentialsRepository: CredentialsRepository,
) {

    suspend fun getMnemonic(): String {
        return credentialsRepository.retrieveMnemonic().ifEmpty {
            throw SoraException.businessError(ResponseCode.GENERAL_ERROR)
        }
    }

    suspend fun voteForReferendum(referendumId: String, votes: Long) {
    }

    suspend fun voteAgainstReferendum(referendumId: String, votes: Long) {
    }

    suspend fun syncVotes() {
    }

    suspend fun getAppVersion(): String {
        return userRepository.getAppVersion()
    }

    suspend fun getInviteCode(): String {
        return userRepository.getParentInviteCode()
    }

    suspend fun getAvailableLanguagesWithSelected(): Pair<List<Language>, String> {
        return userRepository.getAvailableLanguages()
    }

    suspend fun changeLanguage(language: String): String {
        return userRepository.changeLanguage(language)
    }

    suspend fun setBiometryEnabled(isEnabled: Boolean) {
        return userRepository.setBiometryEnabled(isEnabled)
    }

    suspend fun isBiometryEnabled(): Boolean {
        return userRepository.isBiometryEnabled()
    }

    suspend fun isBiometryAvailable(): Boolean {
        return userRepository.isBiometryAvailable()
    }

    suspend fun saveAccountName(accountName: String) {
        return userRepository.saveAccountName(accountName)
    }

    suspend fun getAccountName(): String {
        return userRepository.getAccountName()
    }
}
