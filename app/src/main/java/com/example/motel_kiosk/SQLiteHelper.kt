package com.example.motel_kiosk

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

class SQLiteHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "test.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "reservation_table"
        const val COL1_ID = "id"
        const val COL2_ROOM_NAME = "room_number"
        const val COL3_RESERVED = "reserved"
        const val FLOOR_COUNT = 5
        const val DOOR_COUNT = 5

        @Volatile
        private var instance: SQLiteHelper? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(context: Context) =
            instance ?: synchronized(SQLiteHelper::class.java) {
                instance ?: SQLiteHelper(context).also {
                    instance = it
                }
            }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COL1_ID INTEGER PRIMARY KEY, " +
                "$COL2_ROOM_NAME TEXT, " +
                "$COL3_RESERVED INTEGER" +
                ")"
        db?.execSQL(createQuery)
        for (floor in 1 until 6 ) {
            for (nb in 1 until 6) {
                val roomNb = "$floor" + "0" + "$nb"
                insertRoom(db, roomNb, roomNb + "호")
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    private fun insertRoom(db: SQLiteDatabase?, id: String, roomName: String) {
        if (db != null) {
            val contentValues = ContentValues().apply {
                put(COL1_ID, id)
                put(COL2_ROOM_NAME, roomName)
                put(COL3_RESERVED, false)
            }
            db.insert(TABLE_NAME, null, contentValues) // 값이 없으면 행을 삽입하지않음
        }
    }

    fun updateData(id: String, roomName: String, reserved: Boolean) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL1_ID, id)
            put(COL2_ROOM_NAME, roomName)
            put(COL3_RESERVED, reserved)
        }
        db.update(TABLE_NAME, contentValues, "$COL1_ID = ?", arrayOf(id))
    }

    @Suppress("unused")
    fun deleteData(id: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL1_ID = ?", arrayOf(id))
    }

    fun getRoomStates(): Array<Array<Boolean>> {
        val roomStates = Array(FLOOR_COUNT) { Array(DOOR_COUNT) { false } }

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        try {
            if (cursor.count != 0) {
                while (cursor.moveToNext()) {
                    val roomNb = cursor.getInt(0)
                    val reserve = cursor.getInt(2)
                    val floor = roomNb / 100 - 1
                    val door = roomNb % 10 - 1
                    roomStates[floor][door] = reserve != 0
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
        return roomStates
    }

    fun getRoomReservationInfo(): String {
        var result = "No data in DB"

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        try {
            if (cursor.count != 0) {
                val stringBuffer = StringBuffer()
                for (floor in 1 until 6) {
                    stringBuffer.append("[")
                    for (door in 1 until 6) {
                        cursor.moveToNext()
                        stringBuffer.append(cursor.getInt(2))
                        if (door < 5)
                            stringBuffer.append(", ")
                    }
                    stringBuffer.append("]\n")
                }
                result = stringBuffer.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
        return result
    }

    @Suppress("unused")
    fun getAllData(): String {
        var result = "No data in DB"

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        try {
            if (cursor.count != 0) {
                val stringBuffer = StringBuffer()
                while (cursor.moveToNext()) {
                    stringBuffer.append("ID :" + cursor.getInt(0) + "\n")
                    stringBuffer.append("ROOM_NB :" + cursor.getString(1) + "\n")
                    stringBuffer.append("RESERVED :" + cursor.getInt(2) + "\n")
                }
                result = stringBuffer.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
        return result
    }

}