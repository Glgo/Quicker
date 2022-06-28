package net.getquicker.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import net.getquicker.R
import net.getquicker.client.ClientManager.sendButtonClickMsg
import java.util.*

/**
 * Created by Void on 2020/3/11 16:57
 * 将展示动作按钮数据的页面抽象，
 * 全局和上下文page的单独操作在其实现类里面进行
 */
abstract class DataPageView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : GridLayout(context, attrs, defStyle), View.OnClickListener {
    //存放page的按钮对象，用于更新按钮的图片和文字等
    var actionBtnArray: MutableMap<Int, UiButtonItem> = HashMap<Int, UiButtonItem>()

    //当前页面的行，用于布局
    var currentPageRow = 0

    //当前页面的列，用于布局
    var currentPageCol = 0
    override fun onClick(v: View) {
        val btnIndex = v.tag as Int
        sendButtonClickMsg(btnIndex)
    }

    /**
     * 根据位置生成按钮索引
     */
    protected abstract fun getButtonIndex(row: Int, col: Int): Int
    protected fun setColAndRowCount(row: Int, col: Int) {
        rowCount = row
        columnCount = col
        currentPageRow = row
        currentPageCol = col
    }

    /**
     * 根据屏幕方向创建按钮
     */
    fun createActionButton() {
        if (actionBtnArray.isNotEmpty()) actionBtnArray.clear()
        for (rowIndex in 0 until currentPageRow) for (colIndex in 0 until currentPageCol) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_panel, null)
            val actionBtn = view.findViewById<ViewGroup>(R.id.ll_action)
            actionBtn.tag = getButtonIndex(rowIndex, colIndex)
            actionBtn.setOnClickListener(this)
            val gridLayoutParam = LayoutParams(
                spec(rowIndex, 1f),
                spec(colIndex, 1f)
            )
            gridLayoutParam.setMargins(1, 1, 1, 1)
            gridLayoutParam.height = 0
            gridLayoutParam.width = 0
            addView(view, gridLayoutParam)
            val item = UiButtonItem(
                actionBtn,
                view.findViewById(R.id.iv_action),
                view.findViewById(R.id.tv_action_name)
            )
            actionBtnArray[getButtonIndex(rowIndex, colIndex)] = item
        }
    }
}

// 按钮项各组件的引用
class UiButtonItem(
    val button: ViewGroup,
    val imageView: ImageView,
    val textView: TextView
)
