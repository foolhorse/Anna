package me.machao.android.anna.sample

import android.app.Application
import me.machao.android.anna.library.Anna

/**
 * Date  2019/2/21
 * @author charliema
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initAnna()
    }

    override fun onTerminate() {
        super.onTerminate()
        Anna.newAppDestroyEvent(this)
    }

    private fun initAnna() {
        Anna.init(Anna.Builder(this).server("", ""))
    }

}