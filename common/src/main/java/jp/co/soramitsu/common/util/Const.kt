/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.common.util

class Const private constructor() {

    init {
        throw IllegalStateException("Utility class")
    }

    companion object {
        const val WEBSITE = "https://sora.org/"
        const val PARLIAMENT_LEARN_MORE_LINK = "https://medium.com/sora-xor/the-sora-parliament-af8184dae384"
        const val SOURCE_LINK = "https://github.com/sora-xor/Sora-Android"
        const val STAKING_LEARN_MORE_LINK = "https://medium.com/sora-xor/sora-validator-reward-token-val-c96a8afb8541"
        const val POLKASWAP_WEBSITE = "https://polkaswap.io"
        const val POLKASWAP_FAQ = "https://wiki.sora.org/polkaswap/polkaswap-faq"
        const val POLKASWAP_PRIVACY_POLICY = "https://wiki.sora.org/polkaswap/polkaswap-privacy-policy"
        const val POLKASWAP_MEMORANDUM = "https://wiki.sora.org/polkaswap/polkaswap-tos"
        const val TELEGRAM_LINK = "https://t.me/sora_xor"
        const val SORA_TERMS_PAGE = "https://sora.org/terms"
        const val SORA_PRIVACY_PAGE = "https://sora.org/privacy"
        const val XOR_ASSET_ID = "xor#sora"
        const val VAL_ASSET_ID = "val#sora"

        const val PIN_CODE_ACTION = "pin_code_action"
        const val IS_PUSH_UPDATE_NEEDED = "is_push_update_needed"
        const val DEVICE_TOKEN = "device_token"

        const val INVITED_USERS = "prefs_invited_users"

        const val NAME_MAX_LENGTH = 25

        const val NO_ICON_RESOURCE = 0

        val PROJECT_DID = arrayOf("did:sora:passport")

        val NOTARY_ADDRESS = "notary@notary"
    }
}
