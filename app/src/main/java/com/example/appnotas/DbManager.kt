package com.example.appnotas

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class DbManager {

    var dbNom = "MisNotas"
    var dbTabla = "Notas"
    var colID = "ID"
    var colTitulo = "Titulo"
    var colDes = "Descripcion"
    var dbVersion = 1

    val sqlCreateTable = "CREATE TABLE IF NOT EXISTS "+dbTabla+" ("+colID+" INTEGER PRIMARY KEY,"+ colTitulo +" TEXT,"+colDes+"TEXT);"

    var sqlDB:SQLiteDatabase?=null

    constructor(context: Context){
        var db = DatabaseHelperNotes(context)
        sqlDB = db.writableDatabase
    }

    inner class DatabaseHelperNotes:SQLiteOpenHelper {
        var context: Context? = null
        constructor(context: Context) : super(context, dbNom, null, dbVersion) {
            this.context = context
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(sqlCreateTable)
            Toast.makeText(this.context, "database created...", Toast.LENGTH_SHORT).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop table if Exists" + dbTabla)
        }


    }

    fun insert(values: ContentValues):Long{
        val ID = sqlDB!!.insert(dbTabla, "",values)
        return ID
    }
    fun Query(projection:Array<String>, selection:String, selectionArgs:Array<String>, sorOrder:String): Cursor{
        val qb = SQLiteQueryBuilder();
        qb.tables = dbTabla
        val cursor = qb.query(sqlDB, projection, selection, selectionArgs, null, null, sorOrder)
        return cursor
    }

    fun delete(selection: String, selectionArgs: Array<String>):Int{
        val count = sqlDB!!.delete(dbTabla, selection, selectionArgs)
        return count
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>):Int{
        val count = sqlDB!!.update(dbTabla, values, selection, selectionArgs)
        return count
    }

}