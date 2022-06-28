package net.getquicker.widget

import android.content.Context
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView

object DialogUtil {
    fun showTextDialog(context: Context, text: String?) {
        XPopup.Builder(context)
            .asConfirm(null, text, null, "确定", null, null, true).show()
    }

    fun showPushSettingDialog(context: Context) {
        XPopup.Builder(context).dismissOnTouchOutside(false).dismissOnBackPressed(true)
            .asCustom(PushSettingView(context)).show()
    }

    fun showSocketSettingDialog(context: Context,socketResult: SocketResult) {
        XPopup.Builder(context).dismissOnTouchOutside(false).dismissOnBackPressed(true)
            .asCustom(SocketSettingView(context,socketResult)).show()
    }

    fun showLoading(context: Context, canceled: Boolean = true): BasePopupView {
        return XPopup.Builder(context).dismissOnTouchOutside(canceled)
            .dismissOnBackPressed(canceled)
            .asLoading().show()
    }
}