/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_wallet_impl.presentation.claim

import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import jp.co.soramitsu.common.base.BaseFragment
import jp.co.soramitsu.common.data.network.substrate.ConnectionManager
import jp.co.soramitsu.common.di.api.FeatureUtils
import jp.co.soramitsu.common.presentation.DebounceClickHandler
import jp.co.soramitsu.common.presentation.view.SoraProgressDialog
import jp.co.soramitsu.common.util.ext.createSendEmailIntent
import jp.co.soramitsu.common.util.ext.highlightWords
import jp.co.soramitsu.common.util.ext.onBackPressed
import jp.co.soramitsu.common.util.ext.setDebouncedClickListener
import jp.co.soramitsu.common.util.ext.showOrHide
import jp.co.soramitsu.feature_wallet_api.di.WalletFeatureApi
import jp.co.soramitsu.feature_wallet_api.domain.interfaces.BottomBarController
import jp.co.soramitsu.feature_wallet_impl.R
import jp.co.soramitsu.feature_wallet_impl.databinding.FragmentClaimBinding
import jp.co.soramitsu.feature_wallet_impl.di.WalletFeatureComponent
import javax.inject.Inject

class ClaimFragment : BaseFragment<ClaimViewModel>(R.layout.fragment_claim) {

    @Inject
    lateinit var debounceClickHandler: DebounceClickHandler

    @Inject
    lateinit var cma: ConnectionManager

    private lateinit var progressDialog: SoraProgressDialog
    private val viewBinding by viewBinding(FragmentClaimBinding::bind)

    override fun inject() {
        FeatureUtils.getFeature<WalletFeatureComponent>(
            requireContext(),
            WalletFeatureApi::class.java
        )
            .claimComponentBuilder()
            .withFragment(this)
            .build()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BottomBarController).hideBottomBar()

        onBackPressed { requireActivity().finish() }

        progressDialog = SoraProgressDialog(requireActivity())

        configureContactUsView()
        configureTitleView()

        viewBinding.nextBtn.setDebouncedClickListener(debounceClickHandler) {
            viewModel.nextButtonClicked(requireContext())
        }
        viewModel.openSendEmailEvent.observe {
            requireActivity().createSendEmailIntent(
                it,
                getString(R.string.common_select_email_app_title)
            )
        }
        viewModel.buttonPendingStatusLiveData.observe {
            viewBinding.claimSubtitle1.showOrHide(it)
            viewBinding.nextBtn.showOrHide(!it)
            viewBinding.loadingLayout.showOrHide(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkMigrationIsAlreadyFinished()
    }

    private fun configureTitleView() {
        val contactUsContent = getString(R.string.claim_welcome_sora2_v1).highlightWords(
            listOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.brand_soramitsu_red
                )
            ),
            listOf { }
        )
        viewBinding.claimTitle.setText(contactUsContent, TextView.BufferType.SPANNABLE)
        viewBinding.claimTitle.movementMethod = LinkMovementMethod.getInstance()
        viewBinding.claimTitle.highlightColor = Color.TRANSPARENT
    }

    private fun configureContactUsView() {
        val contactUsContent = getString(R.string.claim_contact_us).highlightWords(
            listOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey_400
                )
            ),
            listOf { viewModel.contactsUsClicked() },
            true
        )
        viewBinding.claimContactUs.setText(contactUsContent, TextView.BufferType.SPANNABLE)
        viewBinding.claimContactUs.movementMethod = LinkMovementMethod.getInstance()
        viewBinding.claimContactUs.highlightColor = Color.TRANSPARENT
    }
}
