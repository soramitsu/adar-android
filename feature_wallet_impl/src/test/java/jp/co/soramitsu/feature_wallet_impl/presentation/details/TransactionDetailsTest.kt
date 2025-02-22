/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.feature_wallet_impl.presentation.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jp.co.soramitsu.common.data.network.substrate.OptionsProvider
import jp.co.soramitsu.common.date.DateTimeFormatter
import jp.co.soramitsu.common.domain.Asset
import jp.co.soramitsu.common.domain.AssetBalance
import jp.co.soramitsu.common.domain.Token
import jp.co.soramitsu.common.resourses.ClipboardManager
import jp.co.soramitsu.common.resourses.ResourceManager
import jp.co.soramitsu.common.util.NumbersFormatter
import jp.co.soramitsu.common.util.TextFormatter
import jp.co.soramitsu.common.util.ext.truncateUserAddress
import jp.co.soramitsu.feature_ethereum_api.domain.interfaces.EthereumInteractor
import jp.co.soramitsu.feature_wallet_api.domain.interfaces.WalletInteractor
import jp.co.soramitsu.feature_wallet_api.domain.model.TransactionStatus
import jp.co.soramitsu.feature_wallet_api.domain.model.TransactionTransferType
import jp.co.soramitsu.feature_wallet_api.launcher.WalletRouter
import jp.co.soramitsu.feature_wallet_impl.R
import jp.co.soramitsu.test_shared.MainCoroutineRule
import jp.co.soramitsu.test_shared.anyNonNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.anyString
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TransactionDetailsTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var walletInteractor: WalletInteractor

    @Mock
    private lateinit var ethereumInteractor: EthereumInteractor

    @Mock
    private lateinit var router: WalletRouter

    @Mock
    private lateinit var resourceManager: ResourceManager

    @Mock
    private lateinit var numbersFormatter: NumbersFormatter

    @Mock
    private lateinit var textFormatter: TextFormatter

    @Mock
    private lateinit var dateTimeFormatter: DateTimeFormatter

    @Mock
    private lateinit var clipboardManager: ClipboardManager

    private val myAccountId = "myAccountId"
    private val peerId = "recipientId"
    private val soranetTransactionId = "soraTransactionId"
    private val date = 0L
    private val amount = BigDecimal("100")
    private val totalAmount = BigDecimal("100")
    private val transactionFee = BigDecimal.ZERO
    private val asset = Asset(
        Token(
            "xorId",
            "XOR",
            "XOR",
            16,
            false,
            0,
        ),
        false,
        0,
        AssetBalance(
            BigDecimal.TEN,
            BigDecimal.TEN,
            BigDecimal.TEN,
            BigDecimal.TEN,
            BigDecimal.TEN,
            BigDecimal.TEN,
            BigDecimal.TEN
        )
    )

    @Before
    fun setup() = runBlockingTest {
        given(dateTimeFormatter.formatDate(anyNonNull(), anyString())).willReturn("01 Jan")
        given(dateTimeFormatter.formatTimeWithSeconds(anyNonNull())).willReturn("03:00:00")
        given(walletInteractor.getAsset(OptionsProvider.feeAssetId)).willReturn(asset)
        //given(resourceManager.getString(R.string.status_success)).willReturn("Committed")
        //given(resourceManager.getString(R.string.status_pending)).willReturn("Pending")
        //given(resourceManager.getString(R.string.transaction_send_again)).willReturn("Send again")
        given(resourceManager.getString(R.string.transaction_details)).willReturn("Transaction details")
    }

    @Ignore("waiting for details")
    @Test
    fun `show PENDING incoming transaction details opened from list`() = runBlockingTest {
        val transactionType = TransactionTransferType.OUTGOING
        val status = TransactionStatus.PENDING

        given(resourceManager.getString(R.string.transaction_details)).willReturn("Transaction details")
        given(numbersFormatter.formatBigDecimal(amount)).willReturn("100")
        given(numbersFormatter.formatBigDecimal(totalAmount)).willReturn("100")
        given(numbersFormatter.formatBigDecimal(transactionFee)).willReturn("0")
        given(numbersFormatter.formatBigDecimal(BigDecimal("100"), 18)).willReturn("100")
        given(numbersFormatter.formatBigDecimal(BigDecimal("100"), 4)).willReturn("100")
        given(numbersFormatter.formatBigDecimal(BigDecimal.ZERO, 18)).willReturn("0")

        val transactionDetailsViewModel = TransactionDetailsViewModel(
            router,
            walletInteractor,
            resourceManager,
            numbersFormatter,
            dateTimeFormatter,
            myAccountId,
            "xor",
            "xor",
            18,
            peerId,
            soranetTransactionId,
            "sora_block_id_hash",
            true,
            date,
            amount,
            totalAmount,
            transactionFee,
            clipboardManager
        )

        transactionDetailsViewModel.fromLiveData.observeForever {
            assertEquals(myAccountId.truncateUserAddress(), it)
        }
        assertEquals(
            myAccountId.truncateUserAddress(),
            transactionDetailsViewModel.fromLiveData.value
        )

        transactionDetailsViewModel.toLiveData.observeForever {
            assertEquals(peerId.truncateUserAddress(), it)
        }
        assertEquals(peerId.truncateUserAddress(), transactionDetailsViewModel.toLiveData.value)

        transactionDetailsViewModel.btnTitleLiveData.observeForever {
            assertEquals("", it)
        }
        assertEquals("", transactionDetailsViewModel.btnTitleLiveData.value)

        transactionDetailsViewModel.titleLiveData.observeForever {
            assertEquals("Transaction details", it)
        }
        assertEquals("Transaction details", transactionDetailsViewModel.titleLiveData.value)

        transactionDetailsViewModel.statusLiveData.observeForever {
            assertEquals("Pending", it)
        }
        assertEquals("Pending", transactionDetailsViewModel.statusLiveData.value)

        transactionDetailsViewModel.statusImageLiveData.observeForever {
            assertEquals(R.drawable.ic_pending_grey_18, it)
        }
        assertEquals(
            R.drawable.ic_pending_grey_18,
            transactionDetailsViewModel.statusImageLiveData.value
        )

        transactionDetailsViewModel.amountLiveData.observeForever {
            assertEquals("- 100 xor" to "100 xor", it)
        }
        assertEquals("- 100 xor" to "100 xor", transactionDetailsViewModel.amountLiveData.value)

        transactionDetailsViewModel.transactionFeeLiveData.observeForever {
            assertEquals("0 XOR", it)
        }
        assertEquals("0 XOR", transactionDetailsViewModel.transactionFeeLiveData.value)

        transactionDetailsViewModel.transactionHashLiveData.observeForever {
            assertEquals("soraT...ionId", it)
        }
        assertEquals(
            "soraT...ionId",
            transactionDetailsViewModel.transactionHashLiveData.value
        )
    }

    @Ignore("waiting for details")
    @Test
    fun `show REJECTED outgoing transaction details`() = runBlockingTest {
        val transactionType = TransactionTransferType.OUTGOING
        val status = TransactionStatus.COMMITTED
        given(numbersFormatter.formatBigDecimal(totalAmount)).willReturn("100")
        given(numbersFormatter.formatBigDecimal(transactionFee)).willReturn("0")
        given(numbersFormatter.formatBigDecimal(BigDecimal("100"), 18)).willReturn("100")
        given(numbersFormatter.formatBigDecimal(BigDecimal("100"), 4)).willReturn("100")
        given(numbersFormatter.formatBigDecimal(BigDecimal.ZERO, 18)).willReturn("0")

        val transactionDetailsViewModel = TransactionDetailsViewModel(
            router,
            walletInteractor,
            resourceManager,
            numbersFormatter,
            dateTimeFormatter,
            myAccountId,
            "xor",
            "xor",
            18,
            peerId,
            soranetTransactionId,
            "sora_block_id_hash",
            true,
            date,
            amount,
            totalAmount,
            transactionFee,
            clipboardManager
        )

        transactionDetailsViewModel.fromLiveData.observeForever {
            assertEquals(myAccountId.truncateUserAddress(), it)
        }
        assertEquals(
            myAccountId.truncateUserAddress(),
            transactionDetailsViewModel.fromLiveData.value
        )

        transactionDetailsViewModel.toLiveData.observeForever {
            assertEquals(peerId.truncateUserAddress(), it)
        }
        assertEquals(peerId.truncateUserAddress(), transactionDetailsViewModel.toLiveData.value)

        transactionDetailsViewModel.btnTitleLiveData.observeForever {
            assertEquals("Send again", it)
        }
        assertEquals("Send again", transactionDetailsViewModel.btnTitleLiveData.value)

        transactionDetailsViewModel.titleLiveData.observeForever {
            assertEquals("Transaction details", it)
        }
        assertEquals("Transaction details", transactionDetailsViewModel.titleLiveData.value)

        transactionDetailsViewModel.statusLiveData.observeForever {
            assertEquals("Committed", it)
        }
        assertEquals("Committed", transactionDetailsViewModel.statusLiveData.value)

        transactionDetailsViewModel.statusImageLiveData.observeForever {
            assertEquals(R.drawable.ic_success_green_18, it)
        }
        assertEquals(
            R.drawable.ic_success_green_18,
            transactionDetailsViewModel.statusImageLiveData.value
        )

        transactionDetailsViewModel.amountLiveData.observeForever {
            assertEquals("- 100 xor" to "100 xor", it)
        }
        assertEquals("- 100 xor" to "100 xor", transactionDetailsViewModel.amountLiveData.value)

        transactionDetailsViewModel.transactionFeeLiveData.observeForever {
            assertEquals("0 XOR", it)
        }
        assertEquals("0 XOR", transactionDetailsViewModel.transactionFeeLiveData.value)

        transactionDetailsViewModel.transactionHashLiveData.observeForever {
            assertEquals("soraT...ionId", it)
        }
        assertEquals(
            "soraT...ionId",
            transactionDetailsViewModel.transactionHashLiveData.value
        )
    }

    @Test
    fun `click next button calls showTransferAmount if opened from list`() = runBlockingTest {
        val transactionType = TransactionTransferType.OUTGOING
        val status = TransactionStatus.COMMITTED

        given(resourceManager.getString(R.string.transaction_details)).willReturn("Transaction details")
        given(numbersFormatter.formatBigDecimal(amount)).willReturn("100")
        given(numbersFormatter.formatBigDecimal(totalAmount)).willReturn("100")
        given(numbersFormatter.formatBigDecimal(transactionFee)).willReturn("0")

        val transactionDetailsViewModel = TransactionDetailsViewModel(
            router,
            walletInteractor,
            resourceManager,
            numbersFormatter,
            dateTimeFormatter,
            myAccountId,
            "xor",
            "xor",
            18,
            peerId,
            soranetTransactionId,
            "sora_block_id_hash",
            true,
            date,
            amount,
            totalAmount,
            transactionFee,
            clipboardManager
        )

        transactionDetailsViewModel.btnNextClicked()

        verify(router).showValTransferAmount(peerId, "xor", BigDecimal.ZERO)
    }

    @Test
    fun `backpress calls returnToWalletFragment() from wallet`() = runBlockingTest {
        val transactionType = TransactionTransferType.INCOMING
        val status = TransactionStatus.COMMITTED

        given(resourceManager.getString(R.string.transaction_details)).willReturn("Transaction details")
        given(numbersFormatter.formatBigDecimal(amount)).willReturn("100")
        given(numbersFormatter.formatBigDecimal(totalAmount)).willReturn("100")
        given(numbersFormatter.formatBigDecimal(transactionFee)).willReturn("0")

        val transactionDetailsViewModel = TransactionDetailsViewModel(
            router,
            walletInteractor,
            resourceManager,
            numbersFormatter,
            dateTimeFormatter,
            myAccountId,
            "xor",
            "xor",
            18,
            peerId,
            soranetTransactionId,
            "sora_block_id_hash",
            true,
            date,
            amount,
            totalAmount,
            transactionFee,
            clipboardManager
        )

        transactionDetailsViewModel.btnBackClicked()

        verify(router).returnToWalletFragment()
    }
}