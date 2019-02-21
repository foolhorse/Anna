package me.machao.android.anna.library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Date  2019/2/22
 * @author charliema
 */
class AnnaDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {

    companion object {

        const val TABLE_NAME ="Event"


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
            TABLE_NAME ,
            true,
            "id" to INTEGER + PRIMARY_KEY + UNIQUE,
            "name" to TEXT,
            "photo" to BLOB
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("User", true)
    }
}

// Access property for Context
val Context.database: AnnaDatabaseOpenHelper
    get() = AnnaDatabaseOpenHelper.getInstance(this.applicationContext)