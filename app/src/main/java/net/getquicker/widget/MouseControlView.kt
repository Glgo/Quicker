package net.getquicker.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import net.getquicker.R
import kotlin.math.abs

/**
 *  author : Clay
 *  date : 2022/01/04 08:53:33
 *  description : 触控板
 */

typealias MouseChangeListener = (type: Int, x: Float?, y: Float?) -> Unit

class MouseControlView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    companion object {
        val LEFTCLICK = 0//点击左键
        val RIGHTCLICK = 1//点击右键
        val DOUBLECLICK = 2//双击左键
        val MOUSEMOVE = 3//移动鼠标
        val WHEEL = 4//移动滚轮
        val LEFTDOWN = 5//按下鼠标左键
        val LEFTUP = 6//抬起鼠标左键
    }

    init {
        //屏幕常亮
        keepScreenOn = true
    }

    //手指落下的位置
    private var downX = 0f
    private var downY = 0f

    //移动的上次位置
    private var moveX = 0f
    private var moveY = 0f

    //按下屏幕时间
    private var downTime = 0L

    //抬起屏幕时间
    private var upTime = 0L
    private val currentTime: Long
        get() = System.currentTimeMillis()
    private var mouseChangeListener: MouseChangeListener? = null
    private val paint: Paint = Paint()
    private var monthDown = false

    // 计算移动距离，速度慢的时候短一些，快的时候加快一些
    private val lowSpeedDist = 5

    //当前触摸位置
    private val touchLocation: PointF = PointF(-1f, -1f)

    //是否是多指操作
    private var isMultiPoint = false

    init {
        paint.color = ContextCompat.getColor(context, R.color.light_blue)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 12f
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        touchLocation.x = x
        touchLocation.y = y
        invalidate()
        when (event.action and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = x
                downY = y
                moveX = x
                moveY = y
                if (upTime - downTime < 200 && currentTime - upTime < 200) {//双击
                    mouseChangeListener?.invoke(LEFTDOWN, null, null)
                    monthDown = true
                }
                downTime = currentTime
            }
            MotionEvent.ACTION_POINTER_DOWN -> {//多点触控
                isMultiPoint = true
                val index = event.actionIndex
                LogUtils.e("index_down+++++:$index")
            }
            MotionEvent.ACTION_POINTER_UP -> {
//                LogUtils.e("count-----${event.pointerCount}")
                val index = event.actionIndex
                LogUtils.e("index_up+++++:$index")
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isMultiPoint) {
                    mouseChangeListener?.invoke(
                        MOUSEMOVE, computeDelta(x - moveX), computeDelta(y - moveY)
                    )
                } else if (event.pointerCount == 2) {//双指滑动
                    mouseChangeListener?.invoke(
                        WHEEL, computeDelta(x - moveX), computeDelta(y - moveY)
                    )
                }
                moveX = x
                moveY = y
            }
            MotionEvent.ACTION_UP -> {
                if (monthDown) {
                    mouseChangeListener?.invoke(LEFTUP, null, null)
                    monthDown = false
                } else if (!monthDown && currentTime - downTime < 200) {
                    //点击事件
                    mouseChangeListener?.invoke(LEFTCLICK, null, null)
                }
                isMultiPoint = false
                upTime = currentTime
                touchLocation.x = -1f
                touchLocation.y = -1f
                invalidate()
            }
            else -> {
                touchLocation.x = -1f
                touchLocation.y = -1f
                invalidate()
            }
        }
        return true
    }

    // 计算要移动的距离：根据速度加速。（触屏采样频率基本是固定的）
    private fun computeDelta(delta: Float): Float {
        val absD = abs(delta)
        return when {
            absD < lowSpeedDist -> delta
            absD < 2 * lowSpeedDist -> delta * 1.5f
            else -> delta * 2.5f
        }
    }

    fun updateMouseListener(mouseChangeListener: MouseChangeListener) {
        this.mouseChangeListener = mouseChangeListener
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (touchLocation.x == -1f && touchLocation.y == -1f) return
        canvas.drawCircle(touchLocation.x, touchLocation.y, 100f, paint)
    }
}