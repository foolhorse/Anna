package me.machao.android.anna.sample

import android.app.Application
import me.machao.android.anna.library.Anna

/**
 * Date  2019/2/21
 * @author charliema
 */
class App :Application(){

    override fun onCreate() {
        super.onCreate()
        initAnna()
    }

    private fun initAnna() {

        Anna.Builder(this).server("","").build()
        Anna.with(this)

    }

}