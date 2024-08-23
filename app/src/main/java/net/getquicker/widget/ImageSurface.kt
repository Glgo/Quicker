package net.getquicker.widget

import android.R
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.RectF
import android.util.AttributeSet
import android.view.SurfaceView
import okio.ByteString
import kotlin.math.min


/**
 *  author : GL
 *  date : 2024-05-10 13:32
 *  description :
 */
class ImageSurface : SurfaceView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

//    private lateinit var holder: SurfaceHolder
//
//    init {
//        holder.addCallback(object : SurfaceHolder.Callback {
//
//            override fun surfaceCreated(holder: SurfaceHolder) {
//            }
//
//            override fun surfaceChanged(
//                holder: SurfaceHolder, format: Int, width: Int, height: Int
//            ) {
//                this@ImageSurface.holder = holder
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//            }
//        })
//    }

    fun update(bytes: ByteString) {
        holder.lockCanvas()?.let { canvas ->
            val bitmap = BitmapFactory.decodeByteArray(bytes.toByteArray(), 0, bytes.size)
            canvas.translate(width / 2f - bitmap.width / 2f, height / 2f - bitmap.height / 2f)
            //旋转缩放和移动的位置，都是基于bitmap的中心点
            //bitmap旋转90度
            canvas.rotate(90f, bitmap.width / 2f, bitmap.height / 2f)
            //计算缩放比R
            val scale = min(width.toFloat() / bitmap.height, height.toFloat() / bitmap.width)
            canvas.scale(scale, scale, bitmap.width / 2f, bitmap.height / 2f)
            //位移，从bitmap中心点，移动到SurfaceView中心
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            holder.unlockCanvasAndPost(canvas)
        }
    }
}