/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_wallet_impl.presentation.polkaswap.swap

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import jp.co.soramitsu.common.base.BaseFragment
import jp.co.soramitsu.common.di.api.FeatureUtils
import jp.co.soramitsu.common.domain.Token
import jp.co.soramitsu.common.presentation.AssetBalanceData
import jp.co.soramitsu.common.presentation.AssetBalanceStyle
import jp.co.soramitsu.common.presentation.DebounceClickHandler
import jp.co.soramitsu.common.presentation.view.assetselectbottomsheet.AssetSelectBottomSheet
import jp.co.soramitsu.common.presentation.view.slippagebottomsheet.SlippageBottomSheet
import jp.co.soramitsu.common.util.KeyboardHelper
import jp.co.soramitsu.common.util.ext.asFlowCurrency
import jp.co.soramitsu.common.util.ext.decimalPartSized
import jp.co.soramitsu.common.util.ext.enableIf
import jp.co.soramitsu.common.util.ext.gone
import jp.co.soramitsu.common.util.ext.hideSoftKeyboard
import jp.co.soramitsu.common.util.ext.requireParcelable
import jp.co.soramitsu.common.util.ext.runDelayed
import jp.co.soramitsu.common.util.ext.setBalance
import jp.co.soramitsu.common.util.ext.setDebouncedClickListener
import jp.co.soramitsu.common.util.ext.show
import jp.co.soramitsu.common.util.ext.showOrGone
import jp.co.soramitsu.feature_wallet_api.di.WalletFeatureApi
import jp.co.soramitsu.feature_wallet_api.domain.interfaces.BottomBarController
import jp.co.soramitsu.feature_wallet_impl.R
import jp.co.soramitsu.feature_wallet_impl.databinding.FragmentSwapBinding
import jp.co.soramitsu.feature_wallet_impl.di.WalletFeatureComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class SwapFragment : BaseFragment<SwapViewModel>(R.layout.fragment_swap) {

    companion object {
        const val ID = 0
        val TITLE_RESOURCE = R.string.polkaswap_swap_title

        const val ARG_INPUT_TOKEN = "arg_input_token"
        const val ARG_INPUT_AMOUNT = "arg_input_amount"
        const val ARG_OUTPUT_TOKEN = "arg_output_token"
        const val ARG_ID = "arg_id"

        fun createSwapData(
            inputToken: Token,
            outputToken: Token,
            inputAmount: BigDecimal,
        ): Bundle = bundleOf(
            ARG_ID to ID,
            ARG_INPUT_TOKEN to inputToken,
            ARG_INPUT_AMOUNT to inputAmount,
            ARG_OUTPUT_TOKEN to outputToken,
        )
    }

    @Inject
    lateinit var debounceClickHandler: DebounceClickHandler
    private val binding by viewBinding(FragmentSwapBinding::bind)

    private lateinit var keyboardHelper: KeyboardHelper
    private var swapButtonText = ""

//    Disclaimer logic, will be added later
//    private var disclaimerVisibility: Boolean = false

    private val assetBalanceStyle = AssetBalanceStyle(
        R.style.TextAppearance_Soramitsu_Neu_Regular_14,
        R.style.TextAppearance_Soramitsu_Neu_Regular_11
    )

    private val swapDetailsStyle = AssetBalanceStyle(
        intStyle = R.style.TextAppearance_Soramitsu_Neu_Regular_14,
        decStyle = R.style.TextAppearance_Soramitsu_Neu_Regular_11,
        color = R.attr.disabledColor,
    )

    override fun onDestroy() {
        if (activity?.isChangingConfigurations == false)
            viewModelStore.clear()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fromCard.setClickListener {
            viewModel.fromCardClicked()
        }

        binding.toCard.setClickListener {
            viewModel.toCardClicked()
        }

        binding.reverseButton.setDebouncedClickListener(debounceClickHandler) {
            viewModel.reverseButtonClicked()
        }

        binding.slippageOptions.setDebouncedClickListener(debounceClickHandler) {
            binding.fromInput.clearFocus()
            binding.toInput.clearFocus()
            viewModel.slippageToleranceClicked()
        }

        binding.detailsIcon.setDebouncedClickListener(debounceClickHandler) {
            viewModel.detailsClicked()
        }

        binding.receiveSoldWrapper.setDebouncedClickListener(debounceClickHandler) {
            viewModel.onMinMaxClicked()
        }

        binding.liqudityProviderWrapper.setDebouncedClickListener(debounceClickHandler) {
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.polkaswap_liqudity_fee)
                .setMessage(R.string.polkaswap_liqudity_fee_info)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .show()
        }

        binding.networkFeeWrapper.setDebouncedClickListener(debounceClickHandler) {
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.polkaswap_network_fee)
                .setMessage(R.string.polkaswap_network_fee_info)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .show()
        }
//        Disclaimer logic, will be added later
//        binding.infoButtonWrapper.setDebouncedClickListener(debounceClickHandler) {
//            viewModel.infoClicked()
//        }

        binding.doneButton.setOnClickListener {
            hideSoftKeyboard()
        }

        binding.percent100.setOnClickListener {
            activity?.window?.currentFocus?.let {
                if (it.id == R.id.fromInput) {
                    viewModel.fromInputPercentClicked(100)
                }
            }
        }

        binding.percent75.setOnClickListener {
            activity?.window?.currentFocus?.let {
                if (it.id == R.id.fromInput) {
                    viewModel.fromInputPercentClicked(75)
                }
            }
        }

        binding.percent50.setOnClickListener {
            activity?.window?.currentFocus?.let {
                if (it.id == R.id.fromInput) {
                    viewModel.fromInputPercentClicked(50)
                }
            }
        }

        binding.percent25.setOnClickListener {
            activity?.window?.currentFocus?.let {
                if (it.id == R.id.fromInput) {
                    viewModel.fromInputPercentClicked(25)
                }
            }
        }

        with(binding.nextBtn) {
            setDebouncedClickListener(debounceClickHandler) {
                viewModel.swapClicked()
            }
            viewLifecycleOwner.bindProgressButton(this)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            binding.fromInput.asFlowCurrency().debounce(800).filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collectLatest {
                    viewModel.fromAmountChanged(
                        binding.fromInput.getBigDecimal() ?: BigDecimal.ZERO
                    )
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            binding.toInput.asFlowCurrency().debounce(800).filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collectLatest {
                    viewModel.toAmountChanged(
                        binding.toInput.getBigDecimal()
                            ?: BigDecimal.ZERO
                    )
                }
        }

        binding.fromInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.fromAmountFocused()
                if (!binding.amountPercentage.isVisible) {
                    binding.amountPercentage.show()
                }
            }
        }

        binding.toInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.toAmountFocused()
                binding.amountPercentage.gone()
            }
        }

        initListeners()
    }

    override fun inject() {
        FeatureUtils.getFeature<WalletFeatureComponent>(
            requireContext(),
            WalletFeatureApi::class.java
        )
            .polkaswapComponentBuilder()
            .withFragment(this)
            .build()
            .inject(this)
    }

    private fun initListeners() {
//        Disclaimer logic, will be added later
//        viewModel.disclaimerVisibilityLiveData.observe {
//            disclaimerVisibility = it
//            binding.infoButtonWrapper.showOrGone(disclaimerVisibility)
//        }

        viewModel.detailsPriceValue.observe { pair ->
            pair.first?.let { first ->
                pair.second?.let { second ->
                    binding.detailsPriceValue1.text = first.decimalPartSized()
                    binding.detailsPriceValue2.text = second.decimalPartSized()
                }
            }
        }

        viewModel.minmaxLiveData.observe {
            binding.receivedSoldValue.setBalance(
                AssetBalanceData(
                    amount = it.first?.first.orEmpty(),
                    ticker = it.first?.second.orEmpty(),
                    style = swapDetailsStyle
                )
            )
            binding.receivedSoldTitle.text = it.second
        }
        viewModel.minmaxClickLiveData.observe {
            AlertDialog.Builder(requireActivity())
                .setTitle(it.first)
                .setMessage(it.second)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .show()
        }

        viewModel.liquidityFeeLiveData.observe {
            binding.liqudityProviderValue.setBalance(
                AssetBalanceData(
                    amount = it?.first.orEmpty(),
                    ticker = it?.second.orEmpty(),
                    style = swapDetailsStyle
                )
            )
        }

        viewModel.networkFeeLiveData.observe {
            binding.networkFeeValue.setBalance(
                AssetBalanceData(
                    amount = it?.first.orEmpty(),
                    ticker = it?.second.orEmpty(),
                    style = swapDetailsStyle
                )
            )
        }

        viewModel.fromAmountLiveData.observe {
            binding.fromInput.listenerEnabled = false
            binding.fromInput.setValue(it)
            binding.fromInput.listenerEnabled = true
        }

        viewModel.toAmountLiveData.observe {
            binding.toInput.listenerEnabled = false
            binding.toInput.setValue(it)
            binding.toInput.listenerEnabled = true
        }

        viewModel.swapButtonTitleLiveData.observe {
            binding.nextBtn.text = it
        }

        viewModel.showFromAssetSelectBottomSheet.observe { list ->
            AssetSelectBottomSheet(
                this, list,
                {
                    binding.fromCard.resetChevron()
                },
                {
                    viewModel.fromAssetSelected(it)
                }
            ).show()
        }

        viewModel.showToAssetSelectBottomSheet.observe { list ->
            AssetSelectBottomSheet(
                this, list,
                {
                    binding.toCard.resetChevron()
                },
                {
                    viewModel.toAssetSelected(it)
                }
            ).show()
        }

        viewModel.fromBalanceLiveData.observe {
            binding.fromBalanceValue.setBalance(
                AssetBalanceData(
                    amount = it.first,
                    ticker = it.second,
                    style = assetBalanceStyle
                )
            )
        }
        viewModel.toBalanceLiveData.observe {
            binding.toBalanceValue.setBalance(
                AssetBalanceData(
                    amount = it.first,
                    ticker = it.second,
                    style = assetBalanceStyle
                )
            )
        }

        viewModel.fromAssetLiveData.observe {
            binding.fromCard.setAsset(it.token)
            binding.fromInput.decimalPartLength = it.token.precision
            binding.fromInput.isEnabled = true

            binding.detailPriceTitle1.text = "%s/%s".format(
                viewModel.fromAssetLiveData.value?.token?.symbol,
                viewModel.toAssetLiveData.value?.token?.symbol
            )

            binding.detailPriceTitle2.text = "%s/%s".format(
                viewModel.toAssetLiveData.value?.token?.symbol,
                viewModel.fromAssetLiveData.value?.token?.symbol
            )
        }

        viewModel.toAssetLiveData.observe {
            binding.toCard.setAsset(it.token)
            binding.toInput.decimalPartLength = it.token.precision
            binding.toInput.isEnabled = true

            binding.detailPriceTitle1.text = "%s/%s".format(
                viewModel.fromAssetLiveData.value?.token?.symbol,
                viewModel.toAssetLiveData.value?.token?.symbol
            )

            binding.detailPriceTitle2.text = "%s/%s".format(
                viewModel.toAssetLiveData.value?.token?.symbol,
                viewModel.fromAssetLiveData.value?.token?.symbol
            )
        }

        viewModel.swapButtonEnabledLiveData.observe {
            binding.nextBtn.enableIf(it)
        }

        viewModel.showSlippageToleranceBottomSheet.observe { value ->
            SlippageBottomSheet(requireContext(), value) { viewModel.slippageChanged(it) }.show()
        }

        viewModel.slippageToleranceLiveData.observe {
            binding.slippageValue.text = "$it%"
        }

        viewModel.detailsEnabledLiveData.observe {
            binding.detailsIcon.isEnabled = it
        }

        viewModel.detailsShowLiveData.observe {
            binding.detailsGroup.showOrGone(it)

            if (it) {
                binding.detailsIcon.setImageResource(R.drawable.ic_neu_chevron_up)
            } else {
                binding.detailsIcon.setImageResource(R.drawable.ic_neu_chevron_down)
            }
        }

        viewModel.preloaderEventLiveData.observe {
            if (it) {
                swapButtonText = binding.nextBtn.text.toString()
                binding.nextBtn.showProgress {
                    progressColorRes = R.color.grey_400
                }
            } else {
                binding.nextBtn.hideProgress(swapButtonText)
            }
        }

        viewModel.dataInitiatedEvent.observeForever {
            setInitialDataIfExists()
        }
    }

    private fun setInitialDataIfExists() {
        arguments?.let {
            val inputToken = requireParcelable<Token>(ARG_INPUT_TOKEN)
            val outputToken = requireParcelable<Token>(ARG_OUTPUT_TOKEN)
            val inputAmount = requireArguments().getSerializable(ARG_INPUT_AMOUNT) as BigDecimal

            viewModel.setSwapData(inputToken, outputToken, inputAmount)
        }
    }

    override fun onResume() {
        super.onResume()
        keyboardHelper = KeyboardHelper(
            requireView(),
            object : KeyboardHelper.KeyboardListener {
                override fun onKeyboardShow() {
                    (activity as BottomBarController).hideBottomBar()
                    activity?.window?.currentFocus?.let {
                        if (it.id == R.id.fromInput) {
                            if (binding.toInput.isEnabled) {
                                binding.amountPercentage.show()
                            }
                        } else {
                            binding.amountPercentage.gone()
                        }
                    }
//                    Disclaimer logic, will be added later
//                    if (disclaimerVisibility) {
//                        binding.infoButtonWrapper.gone()
//                    }
                }

                override fun onKeyboardHide() {
                    runDelayed(100) {
                        (activity as BottomBarController).showBottomBar()
                        binding.amountPercentage.gone()

//                        Disclaimer logic, will be added later
//                        if (disclaimerVisibility) {
//                            binding.infoButtonWrapper.show()
//                        }
                    }
                }
            }
        )
    }

    override fun onPause() {
        keyboardHelper.release()
        super.onPause()
    }
}
