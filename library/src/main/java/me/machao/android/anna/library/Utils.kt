package me.machao.android.anna.library

import android.util.Log
import com.google.gson.Gson

/**
 * Date  2019/2/22
 * @author charliema
 */
fun Any.log(msg: String) {
    Log.d(this.javaClass.simpleName, msg)
}

val GSON: Gson by lazy {
    Gson()
}