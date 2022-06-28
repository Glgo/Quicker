package net.getquicker.utils

import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.util.*

/**
 *  author : Clay
 *  date : 2021/12/28 09:59:26
 *  description : 二维码识别
 */
object ZxingManager {
    fun decode(data: ByteArray, width: Int, height: Int, result: (String) -> Unit) {
        val source =
            PlanarYUVLuminanceSource(data, width, height, 10, 10, width - 10, height - 10, false)
        val enumSet = EnumSet.of(BarcodeFormat.QR_CODE)//二维码格式
        val enumMap = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java)
        enumMap[DecodeHintType.POSSIBLE_FORMATS] = enumSet
        val multiFormatReader = MultiFormatReader()
        multiFormatReader.setHints(enumMap)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val rawResult = multiFormatReader.decodeWithState(bitmap)
            result(rawResult.text)
        } catch (e: Exception) {
            //识别失败也会错误回调
            e.printStackTrace()
        } finally {
            multiFormatReader.reset()
        }
    }
}