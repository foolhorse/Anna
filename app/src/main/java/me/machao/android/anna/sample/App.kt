package me.machao.android.anna.sample

import android.app.Application
import me.machao.android.anna.library.Anna
import me.machao.android.anna.library.Strategy

/**
 * Date  2019/2/21
 * @author charliema
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initAnna()
        Anna.getInstance().newAppCreateEvent(this )
    }

    private fun initAnna() {
        Anna.init(Anna.Builder(this).server("http://foo.bar", "").strategy(Strategy.RELEASE).uploadThreshold(10))
    }

}