package net.getquicker.panel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.util.Base64
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.getquicker.messages.recv.UpdateButtonsMessage
import net.getquicker.utils.Constants
import net.getquicker.utils.svg.GlideApp
import net.getquicker.utils.svg.SvgSoftwareLayerSetter
import net.getquicker.widget.DataPageContextView
import net.getquicker.widget.DataPageGlobalView
import net.getquicker.widget.DataPageView
import net.getquicker.widget.UiButtonItem
import java.util.*

class PanelPageAdapter(val context: Context, private val isGlobal: Boolean) :
    RecyclerView.Adapter<PanelViewHolder>() {
    /**
     * data, Only allowed to get.
     * 数据, 只允许 get。
     */
    private val data: SparseArray<List<UpdateButtonsMessage.ButtonItem>> = SparseArray()

    private var pagerCount: Int = 0
    private val requestBuilder = GlideApp.with(context).`as`(PictureDrawable::class.java)
        .listener(SvgSoftwareLayerSetter())
    private val rootLayoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanelViewHolder {
        return if (isGlobal) PanelViewHolder(DataPageGlobalView(parent.context).apply {
            layoutParams = rootLayoutParams
        })
        else PanelViewHolder(DataPageContextView(parent.context).apply {
            layoutParams = rootLayoutParams
        })
    }

    override fun onBindViewHolder(holder: PanelViewHolder, position: Int) {
        val dataPageView = holder.itemView as DataPageView
        val buttonDataList = data[position]
        if (buttonDataList == null) {//数据为空则显示空白面板，防止view重用后显示数据错误问题
            dataPageView.actionBtnArray.values.forEach {
                updateButton(it, UpdateButtonsMessage.ButtonItem())
            }
        } else {
            for (buttonItem in buttonDataList) {
                val buttonIndex = buttonItem.Index
                val uiButtonItem = try {
                    dataPageView.actionBtnArray[buttonIndex]
                } catch (e: Exception) {
                    null
                } ?: return
                updateButton(uiButtonItem, buttonItem)
            }
        }

    }

    /**
     * @param count 总页数
     * @param position 当前显示的页
     * @param newData 当前页的数据
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setNewData(count: Int, position: Int, newData: List<UpdateButtonsMessage.ButtonItem>?) {
        pagerCount = count
        data.put(position, newData)
        notifyDataSetChanged()
    }

    /**
     * 更新按钮状态
     * @param item
     */
    private fun updateButton(button: UiButtonItem, item: UpdateButtonsMessage.ButtonItem) {
        button.button.isEnabled = item.IsEnabled

        //无论是否禁用，都加载文字和图片
        if (!item.Label.isNullOrEmpty()) {
            item.Label = item.Label!!.replace("\\n", "\n")
            button.textView.text = item.Label
            button.textView.visibility = View.VISIBLE
        } else {
            button.textView.text = ""
            button.textView.visibility = View.INVISIBLE
        }
        if (!item.IconFileContent.isNullOrEmpty()) {
            val imgContent: ByteArray = Base64.decode(item.IconFileContent, Base64.DEFAULT)
            Glide.with(context).asBitmap().load(imgContent).into(button.imageView)
            button.imageView.visibility = View.VISIBLE
        } else if (!item.IconFileName.isNullOrEmpty()) {
            when {
                item.IconFileName!!.startsWith("fa:") -> {
                    val split: List<String> = item.IconFileName!!.substring(3).split("_", ":")
                    val style = split[0].lowercase(Locale.getDefault())
                    val name = split[1].replace("([a-z])([A-Z]+)".toRegex(), "$1-$2")
                        .lowercase(Locale.getDefault())
                    // TODO: 2021/12/22 如果有颜色，则添加颜色
//                    if (split.size > 2) {
//                        val color = split[2]
//                    }
                    val svgUrl =
                        Constants.svgServer.replace("{style}", style).replace("{name}", name)
                    requestBuilder
                        .load(svgUrl)
                        .into(button.imageView)
                }
                item.IconFileName!!.endsWith(".svg") -> {
                    requestBuilder.load(item.IconFileName).into(button.imageView)
                }
                else -> {
                    Glide.with(context).load(item.IconFileName).into(button.imageView)
                }
            }
            button.imageView.visibility = View.VISIBLE
        } else {
            button.imageView.setImageBitmap(null)
            button.imageView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = pagerCount
}

class PanelViewHolder(view: DataPageView) : RecyclerView.ViewHolder(view)