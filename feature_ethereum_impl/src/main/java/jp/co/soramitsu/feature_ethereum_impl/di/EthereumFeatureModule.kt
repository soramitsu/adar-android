/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_ethereum_impl.di

import dagger.Module
import dagger.Provides
import jp.co.soramitsu.common.data.EncryptedPreferences
import jp.co.soramitsu.common.data.SoraPreferences
import jp.co.soramitsu.common.domain.HealthChecker
import jp.co.soramitsu.common.domain.credentials.CredentialsRepository
import jp.co.soramitsu.feature_ethereum_api.domain.interfaces.EthereumDatasource
import jp.co.soramitsu.feature_ethereum_api.domain.interfaces.EthereumInteractor
import jp.co.soramitsu.feature_ethereum_api.domain.interfaces.EthereumRepository
import jp.co.soramitsu.feature_ethereum_impl.data.mappers.EthRegisterStateMapper
import jp.co.soramitsu.feature_ethereum_impl.data.mappers.EthereumCredentialsMapper
import jp.co.soramitsu.feature_ethereum_impl.data.repository.EthereumRepositoryImpl
import jp.co.soramitsu.feature_ethereum_impl.data.repository.datasource.PrefsEthereumDatasource
import jp.co.soramitsu.feature_ethereum_impl.domain.EthereumInteractorImpl
import jp.co.soramitsu.feature_ethereum_impl.domain.EthereumStatusObserver
import javax.inject.Singleton

@Module
class EthereumFeatureModule {

    @Provides
    @Singleton
    fun provideEthereumRepository(ethereumRepositoryImpl: EthereumRepositoryImpl): EthereumRepository =
        ethereumRepositoryImpl

    @Provides
    @Singleton
    fun provideEthereumCredentialsMapper(): EthereumCredentialsMapper = EthereumCredentialsMapper()

    @Provides
    @Singleton
    fun provideEthereumDatasource(
        encryptedPreferences: EncryptedPreferences,
        soraPreferences: SoraPreferences
    ): EthereumDatasource {
        return PrefsEthereumDatasource(encryptedPreferences, soraPreferences)
    }

    @Singleton
    @Provides
    fun provideEthereumInteractor(
        ethereumRepository: EthereumRepository,
        credentialsRepository: CredentialsRepository,
        health: HealthChecker
    ): EthereumInteractor {
        return EthereumInteractorImpl(ethereumRepository, credentialsRepository, health)
    }

    @Singleton
    @Provides
    fun provideEthereumStatusObserver(
        ethereumRepository: EthereumRepository,
        credentialsRepository: CredentialsRepository
    ): EthereumStatusObserver {
        return EthereumStatusObserver(ethereumRepository, credentialsRepository)
    }

    @Singleton
    @Provides
    fun provideEthRegisterStateMapper(): EthRegisterStateMapper = EthRegisterStateMapper()
}
