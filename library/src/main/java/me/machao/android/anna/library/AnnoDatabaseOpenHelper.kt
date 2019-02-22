package me.machao.android.anna.library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Date  2019/2/22
 * @author charliema
 */
class AnnaDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(
    ctx,
    DB_NAME,
    null,
    1
) {

    companion object {

        const val DB_NAME = "Anna"

        const val TABLE_NAME = "Event"

        const val COLUMNS_ID = "id"
        const val COLUMNS_DATA = "data"
        const val COLUMNS_TIMESTAMP = "timestamp"


        private var instance: AnnaDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): AnnaDatabaseOpenHelper {
            if (instance == null) {
                instance = AnnaDatabaseOpenHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(
            TABLE_NAME,
            true,
            COLUMNS_ID to INTEGER + PRIMARY_KEY + UNIQUE,
            COLUMNS_DATA to TEXT,
            COLUMNS_TIMESTAMP to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable(TABLE_NAME, true)
    }
}

// Access property for Context
val Context.database: AnnaDatabaseOpenHelper
    get() = AnnaDatabaseOpenHelper.getInstance(this.applicationContext)