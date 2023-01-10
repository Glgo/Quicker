package net.getquicker.hook.edittext;

import android.graphics.Rect;
import android.view.View;
import android.widget.EditText;

import net.getquicker.hook.BaseHook;
import net.getquicker.hook.helper.MethodHookWrapper;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EditTextHook extends BaseHook {
    public static EditText editText;

    @Override
    public void onLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod(View.class, "onFocusChanged", boolean.class, int.class, Rect.class, new MethodHookWrapper() {
            @Override
            protected void after(MethodHookParam param) throws Throwable {
                Object thisObject = param.thisObject;
                if (thisObject instanceof EditText) {
                    Object[] args = param.args;
                    boolean gainFocus = (boolean) args[0];
                    if (gainFocus) {
                        editText = (EditText) thisObject;
                    } else {
                        editText = null;
                    }
                }
            }
        });
    }
}
