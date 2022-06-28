package net.getquicker

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.BarUtils
import dagger.hilt.android.AndroidEntryPoint
import net.getquicker.base.BaseActivity
import net.getquicker.client.ClientManager
import net.getquicker.client.ClientService
import net.getquicker.databinding.ActivityMainBinding
import net.getquicker.utils.PageNavUtil
import net.getquicker.utils.PageNavUtil.gotoFragment
import net.getquicker.utils.PageNavUtil.panelLogin

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun ActivityMainBinding.initBinding(savedInstanceState: Bundle?) {
        BarUtils.setStatusBarLightMode(window,false)
        startService(Intent(this@MainActivity, ClientService::class.java))
        PageNavUtil.containerId = R.id.container
        supportFragmentManager.gotoFragment(panelLogin)
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_panel -> {
                    val pageName =
                        if (ClientManager.isConnected()) PageNavUtil.panel else panelLogin
                    supportFragmentManager.gotoFragment(pageName)
                }
                R.id.action_mouse -> supportFragmentManager.gotoFragment(PageNavUtil.mouse)
                R.id.action_push -> supportFragmentManager.gotoFragment(PageNavUtil.push)
            }
            true
        }
    }
}