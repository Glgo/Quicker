package net.getquicker.widget

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log

/**
 * Created by Void on 2020/3/9 16:37
 * 上下文数据页面
 * 继承GridLayout是为了能够储存，该页面一些数据。
 * 如当前页面索引，全局页面总数量等
 */
class DataPageGlobalView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DataPageView(context, attrs, defStyleAttr) {
    override fun getButtonIndex(row: Int, col: Int): Int {
        return row * 1000 + col
    }

    init {
        setColAndRowCount(3, 4)
        createActionButton()
    }
}