package jp.co.soramitsu.feature_wallet_impl.data.mappers

import jp.co.soramitsu.feature_wallet_api.domain.model.QrData
import jp.co.soramitsu.feature_wallet_impl.data.qr.QrDataRecord

fun mapQrDataRecordToQrData(qrDataRecord: QrDataRecord): QrData {
    return with(qrDataRecord) {
        QrData(accountId, amount, assetId)
    }
}