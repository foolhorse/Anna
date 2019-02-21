package me.machao.android.anna.library

/**
 * Date  2019/2/21
 * @author charliema
 */
enum class Strategy {

    /**
     * 关掉
     */
    OFF,

    /**
     * 只本地记录
     */
    DEBUG,

    /**
     * 实时上传服务端
     */
    REALTIME,

    /**
     * 到达一定阈值后 threshold 上传服务端
     */
    THRESHOLD


}