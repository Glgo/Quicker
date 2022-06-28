package net.getquicker.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SizeUtils
import net.getquicker.R
import kotlin.math.sqrt

class ArrowView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val lightPaint = Paint()
    private val darkPaint = Paint()
    private val linePaint = Paint()
    private val textPaint = Paint()
    private val textBounds = Rect()
    private val normalColor = ColorUtils.getColor(R.color.key_color)
    private val darkColor = ColorUtils.getColor(R.color.key_color_dark)
    private var arrowClickListener: ((Arrow?) -> Unit)? = null

    //点击的区域。0：无点击，1：左，2：上，3右，4：下
    private var pressIndex: Arrow? = null

    enum class Arrow {
        LEFT, UP, RIGHT, DOWN
    }

    init {
        lightPaint.color = normalColor
        lightPaint.style = Paint.Style.FILL

        darkPaint.color = darkColor
        darkPaint.style = Paint.Style.FILL

        linePaint.color = ColorUtils.getColor(R.color.white)
        linePaint.style = Paint.Style.FILL
        linePaint.strokeWidth = 3f

        textPaint.color = ColorUtils.getColor(R.color.white)
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = SizeUtils.dp2px(12f).toFloat()
        textPaint.getTextBounds("↑", 0, 1, textBounds)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //画扇形
        //左
        canvas.drawArc(
            0f, 0f, width.toFloat(), height.toFloat(), 135f,
            90f, true, if (pressIndex == Arrow.LEFT) darkPaint else lightPaint
        )
        //上
        canvas.drawArc(
            0f, 0f, width.toFloat(), height.toFloat(), -135f,
            90f, true, if (pressIndex == Arrow.UP) darkPaint else lightPaint
        )
        //右
        canvas.drawArc(
            0f, 0f, width.toFloat(), height.toFloat(), -45f,
            90f, true, if (pressIndex == Arrow.RIGHT) darkPaint else lightPaint
        )
        //下
        canvas.drawArc(
            0f, 0f, width.toFloat(), height.toFloat(), 45f,
            90f, true, if (pressIndex == Arrow.DOWN) darkPaint else lightPaint
        )

        //画X直线
        val temp = sqrt((2 * width * width).toDouble()) / 2 - width / 2
        val left = sqrt(temp * temp / 2).toFloat()
        canvas.drawLine(left, left, width.toFloat()-left, height.toFloat()-left, linePaint)
        canvas.drawLine(width.toFloat()-left, left, left, height.toFloat()-left, linePaint)

        //写文字
        canvas.drawText("↑", width / 2f, height / 4f + textBounds.height() / 2f, textPaint)
        canvas.drawText("↓", width / 2f, height * 3 / 4f + textBounds.height() / 2f, textPaint)
        canvas.drawText("←", width / 4f, height / 2f + textBounds.height() / 2f, textPaint)
        canvas.drawText("→", width * 3 / 4f, height / 2f + textBounds.height() / 2f, textPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                pressIndex = if (x < width / 2) {
                    when {
                        y <= x -> Arrow.UP //上
                        y >= height - x -> Arrow.DOWN//下
                        else -> Arrow.LEFT//左
                    }
                } else {
                    when {
                        y <= width - x -> Arrow.UP//上
                        y >= height - (width - x) -> Arrow.DOWN//下
                        else -> Arrow.RIGHT//右
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                arrowClickListener?.invoke(pressIndex)
                pressIndex = null
            }
            else -> {
                pressIndex = null
            }
        }
        invalidate()
        return true
    }

    fun setOnArrowClickListener(listener: (Arrow?) -> Unit) {
        arrowClickListener = listener
    }
}