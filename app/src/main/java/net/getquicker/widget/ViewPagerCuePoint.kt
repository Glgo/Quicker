package net.getquicker.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import kotlin.jvm.JvmOverloads
import android.widget.LinearLayout
import net.getquicker.bean.DataPageValues
import net.getquicker.R
import com.blankj.utilcode.util.SizeUtils
import java.lang.Exception

/**
 * viewpager的导航点
 */
class ViewPagerCuePoint @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val gLayout: LinearLayout
    private val cLayout: LinearLayout

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        gLayout = LinearLayout(context)
        cLayout = LinearLayout(context)
        addView(
            gLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        addView(
            cLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    /**
     * 更新全局页指示点
     */
    fun updateGlobalCuePoint() {
        while (true) {
            val tmp = gLayout.childCount - DataPageValues.globalDataPageCount
            if (tmp > 0) {
                gLayout.removeViewAt(gLayout.childCount - 1)
            } else if (tmp < 0) {
                addImageView(true)
            } else {
                break
            }
        }
        refreshView(true)
    }

    /**
     * 更新上下文页面指示点
     */
    fun updateContextCuePoint() {
        while (true) {
            val tmp = cLayout.childCount - DataPageValues.contextDataPageCount
            if (tmp > 0) {
                cLayout.removeViewAt(cLayout.childCount - 1)
            } else if (tmp < 0) {
                addImageView(false)
            } else {
                break
            }
        }
        refreshView(false)
    }

    /**
     * 更新指示点的ui
     */
    private fun refreshView(isGlobal: Boolean) {
        val view = if (isGlobal) gLayout else cLayout
        val count =
            if (isGlobal) DataPageValues.globalDataPageCount else DataPageValues.contextDataPageCount
        val index =
            if (isGlobal) DataPageValues.currentGlobalPageIndex else DataPageValues.currentContextPageIndex
        try {
            for (i in 0 until count) {
                if (i == index) {
                    view.getChildAt(i).setBackgroundResource(R.drawable.ic_point_light)
                } else {
                    view.getChildAt(i).setBackgroundResource(R.drawable.ic_point_dark)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 生成一个imageView并添加进布局
     */
    private fun addImageView(isGlobal: Boolean) {
        val image = ImageView(context)
        val layoutParams = LayoutParams(
            SizeUtils.dp2px(5f), SizeUtils.dp2px(5f)
        )
        layoutParams.setMargins(10, 10, 10, 10)
        image.layoutParams = layoutParams
        if (isGlobal) gLayout.addView(image) else cLayout.addView(image)
    }

}