/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_wallet_impl.presentation.polkaswap

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import jp.co.soramitsu.feature_wallet_impl.presentation.polkaswap.pool.PoolFragment
import jp.co.soramitsu.feature_wallet_impl.presentation.polkaswap.swap.SwapFragment

class ViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val swapFragment: Fragment by lazy { SwapFragment() }
    private val poolFragment: Fragment by lazy { PoolFragment() }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            SwapFragment.ID -> swapFragment
            PoolFragment.ID -> poolFragment
            else -> throw IllegalArgumentException("Incorrect Polkaswap $position fragment")
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    fun getTitle(position: Int): Int {
        return when (position) {
            SwapFragment.ID -> SwapFragment.TITLE_RESOURCE
            PoolFragment.ID -> PoolFragment.TITLE_RESOURCE
            else -> SwapFragment.TITLE_RESOURCE
        }
    }

    fun getFragmentAtPosition(position: Int): Fragment {
        return when (position) {
            SwapFragment.ID -> swapFragment
            PoolFragment.ID -> poolFragment
            else -> throw IllegalArgumentException("Incorrect Polkaswap $position fragment")
        }
    }
}
