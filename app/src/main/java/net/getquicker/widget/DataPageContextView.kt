package net.getquicker.widget

import android.content.Context
import android.util.AttributeSet
import kotlin.jvm.JvmOverloads
import net.getquicker.widget.DataPageView

/**
 * Created by Void on 2020/3/9 16:37
 * 上下文数据页面
 * 用于管理上下文page  View的显示和数据更新等
 * 如当前页面索引，上下文页面总数量等
 */
class DataPageContextView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DataPageView(context, attrs, defStyleAttr) {
    override fun getButtonIndex(row: Int, col: Int): Int {
        return 1000000 + row * 1000 + col
    }

    init {
        setColAndRowCount(4, 4)
        createActionButton()
    }
}