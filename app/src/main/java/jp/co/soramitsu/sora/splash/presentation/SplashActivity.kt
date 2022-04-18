/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.sora.splash.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import jp.co.soramitsu.common.di.api.FeatureUtils
import jp.co.soramitsu.feature_account_api.domain.model.OnboardingState
import jp.co.soramitsu.feature_main_api.di.MainFeatureApi
import jp.co.soramitsu.feature_onboarding_api.di.OnboardingFeatureApi
import jp.co.soramitsu.sora.databinding.ActivitySplashBinding
import jp.co.soramitsu.sora.di.app_feature.AppFeatureComponent
import jp.co.soramitsu.sora.splash.domain.SplashRouter
import javax.inject.Inject
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity(), SplashRouter {

    @Inject
    lateinit var splashViewModel: SplashViewModel

    private lateinit var viewBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        setContentView(ActivitySplashBinding.inflate(layoutInflater).also { viewBinding = it }.root)

        lifecycleScope.launchWhenStarted {
            delay(2000)
            splashViewModel.nextScreen()
        }
    }

    private fun inject() {
        FeatureUtils.getFeature<AppFeatureComponent>(this, AppFeatureComponent::class.java)
            .splashComponentBuilder()
            .withActivity(this)
            .withRouter(this)
            .build()
            .inject(this)
    }

    override fun showOnBoardingScreen(onBoardingState: OnboardingState) {
        FeatureUtils.getFeature<OnboardingFeatureApi>(
            application,
            OnboardingFeatureApi::class.java
        )
            .provideOnboardingStarter()
            .start(this, onBoardingState)
    }

    override fun showOnBoardingScreenViaInviteLink() {
        FeatureUtils.getFeature<OnboardingFeatureApi>(
            application,
            OnboardingFeatureApi::class.java
        )
            .provideOnboardingStarter()
            .startWithInviteLink(this)
        finish()
    }

    override fun showMainScreen() {
        FeatureUtils.getFeature<MainFeatureApi>(application, MainFeatureApi::class.java)
            .provideMainStarter()
            .start(this)
    }

    override fun showMainScreenFromInviteLink() {
        FeatureUtils.getFeature<MainFeatureApi>(application, MainFeatureApi::class.java)
            .provideMainStarter()
            .startWithInvite(this)
        finish()
    }
}
