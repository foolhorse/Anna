package me.machao.android.anna.library.data

/**
 * Date  2019/2/21
 * @author charliema
 */
enum class EventType {

    /**
     *
     */
    APP_CREATE,

    /**
     *
     */
    APP_DESTROY,

    /**
     *
     */
    APP_START,

    /**
     *
     */
    APP_STOP,

    /**
     * Page showed, (Activity or Fragment)
     */
    PAGE_START,

    PAGE_STOP,

    /**
     * User-Defined
     */
    CUSTOM


}