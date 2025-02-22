/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_wallet_impl.presentation.receive

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import by.kirich1409.viewbindingdelegate.viewBinding
import jp.co.soramitsu.common.base.BaseFragment
import jp.co.soramitsu.common.di.api.FeatureUtils
import jp.co.soramitsu.common.presentation.DebounceClickHandler
import jp.co.soramitsu.common.util.ext.attrColor
import jp.co.soramitsu.common.util.ext.gone
import jp.co.soramitsu.common.util.ext.setDebouncedClickListener
import jp.co.soramitsu.common.util.ext.show
import jp.co.soramitsu.feature_wallet_api.di.WalletFeatureApi
import jp.co.soramitsu.feature_wallet_api.domain.interfaces.BottomBarController
import jp.co.soramitsu.feature_wallet_api.domain.model.ReceiveAssetModel
import jp.co.soramitsu.feature_wallet_impl.R
import jp.co.soramitsu.feature_wallet_impl.databinding.FragmentReceiveBinding
import jp.co.soramitsu.feature_wallet_impl.di.WalletFeatureComponent
import javax.inject.Inject

class ReceiveFragment : BaseFragment<ReceiveViewModel>(R.layout.fragment_receive) {

    companion object {
        private const val ARG_ASSET = "arg_asset"
        fun createBundle(asset: ReceiveAssetModel) = Bundle().apply {
            putSerializable(ARG_ASSET, asset)
        }
    }

    @Inject
    lateinit var debounceClickHandler: DebounceClickHandler

    private val model: ReceiveAssetModel by lazy { requireArguments().getSerializable(ARG_ASSET) as ReceiveAssetModel }
    private val viewBinding by viewBinding(FragmentReceiveBinding::bind)

    override fun inject() {
        FeatureUtils.getFeature<WalletFeatureComponent>(
            requireContext(),
            WalletFeatureApi::class.java
        )
            .receiveAmountComponentBuilder()
            .withFragment(this)
            .withReceiveModel(model)
            .withQrBackgroundColor(context.attrColor(R.attr.flatAboveBackground))
            .build()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BottomBarController).hideBottomBar()
        with(viewBinding.tbReceive) {
            setTitle(composeTitle())
            setHomeButtonListener {
                viewModel.backButtonPressed()
            }
        }
        viewBinding.ibReceiveShare.setDebouncedClickListener(debounceClickHandler) {
            viewModel.shareQr()
        }
        viewBinding.ivCopyAddress.setOnClickListener {
            viewModel.copyAddress()
        }
        viewBinding.ivQr.setOnClickListener {
            viewModel.copyAddress()
        }
        initListeners()
    }

    private fun initListeners() {
        viewModel.qrBitmapLiveData.observe {
            viewBinding.ivQr.setImageBitmap(it)
        }

        viewModel.shareQrCodeLiveData.observe {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, it.first)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.common_share))
                putExtra(Intent.EXTRA_TEXT, it.second)
            }
            startActivity(Intent.createChooser(intent, getString(R.string.common_share)))
        }

        viewModel.userNameAddress.observe {
            if (it.second.isEmpty()) {
                viewBinding.tvUserName.text = it.first
                viewBinding.tvUserAddress.gone()
            } else {
                viewBinding.tvUserName.text = it.second
                viewBinding.tvUserAddress.show()
                viewBinding.tvUserAddress.text = it.first
            }
        }

        viewModel.userAvatar.observe {
            viewBinding.ivUserAvatar.setImageDrawable(it)
        }

        viewModel.copiedAddressEvent.observe {
            Toast.makeText(requireContext(), R.string.common_copied, Toast.LENGTH_SHORT).show()
        }
    }

    private fun composeTitle() = listOf(
        getString(R.string.common_receive),
        model.tokenName
    ).filter { it.isNotEmpty() }.joinToString(separator = " ")
}
