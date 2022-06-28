package net.getquicker.utils

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import net.getquicker.mouse.MouseControlFragment
import net.getquicker.panel.LoginFragment
import net.getquicker.panel.PanelFragment
import net.getquicker.push.PushFragment

object PageNavUtil {
    const val panelLogin = "panel_login"
    const val panel = "panel"
    const val push = "push"
    const val mouse = "mouse"
    private val fragmentMap = mapOf(
        panelLogin to LoginFragment(), panel to PanelFragment(),
        push to PushFragment(), mouse to MouseControlFragment()
    )

    @IdRes
    var containerId: Int = 0
    fun FragmentManager.gotoFragment(name: String) {
        val fragment = fragmentMap[name] ?: return
        commit {
            if (fragments.contains(fragment)) {//已添加过则展示
                for (tempFragment in fragments) {
                    if (tempFragment == fragment) {
                        show(tempFragment)
                    } else {
                        hide(tempFragment)
                    }
                }
            } else {//添加
                fragments.forEach { hide(it) }
                add(containerId, fragment, name)
            }
        }
    }
}