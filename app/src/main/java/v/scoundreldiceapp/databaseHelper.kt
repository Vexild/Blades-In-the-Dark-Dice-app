package v.scoundreldiceapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class databaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?): SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        val DATABASE_NAME = "BladesDb.db"
        val ROLLS_TABLE = "rolls"
        val SETTING_TABLE = "setting"
        val DICE_NUMBER = "dice_number"
        val HIGHEST_RESULT = "highest_result"
        val CRITICAL = "critical"
        val ACTION_NAME = "action_name"
        val POSITION = "position"
        val EFFECT = "effect"
        val DATE = "date"
        val MUTE = "mute"
        val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS rolls (ID INTEGER PRIMARY KEY AUTOINCREMENT, dice_number INT,  highest_result INT, critical BOOLEAN, action_name TEXT, position TEXT, effect TEXT, date TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS settings (ID INTEGER PRIMARY KEY AUTOINCREMENT, mute BOOLEAN)")

        //Log.i("DatabaseHelper", "DB Created")
        //Log.i("DatabaseHelper", getProfile().toString())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS "+ ROLLS_TABLE)
        onCreate(db)
    }

    fun reCreateDb() {
        val db = this.writableDatabase
        db.execSQL("CREATE TABLE IF NOT EXISTS rolls (ID INTEGER PRIMARY KEY AUTOINCREMENT, dice_number INT,  highest_result INT, critical BOOLEAN, action_name TEXT, position TEXT, effect TEXT, date TEXT)")

    }


    fun updateSettings(settings: UpdateSettings) {
        val db = this.writableDatabase
        val mute = settings.isMuted
        Log.i("Update Settings", "Mute ="+mute.toString())
        return db.execSQL("UPDATE $SETTING_TABLE SET $MUTE = $mute")
    }
    fun insertRollToDb(roll: PlayerRoll){
        val db = this.writableDatabase

        var rollValues = ContentValues()
        rollValues.put(DICE_NUMBER, roll.dice_number)
        rollValues.put(HIGHEST_RESULT, roll.highest_result)
        rollValues.put(CRITICAL, roll.critical)
        rollValues.put(ACTION_NAME, roll.action_name)
        rollValues.put(POSITION, roll.position)
        rollValues.put(EFFECT, roll.effect)
        rollValues.put(DATE, roll.date)
        Log.i("DBhelper", rollValues.toString())
        db.insert(ROLLS_TABLE,null, rollValues)
        db.close()
    }

    fun getAllRolls(): Cursor? {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $ROLLS_TABLE", null)
    }
    fun destroyRollsTable(){
        val db = this.writableDatabase
        return db.execSQL("DROP TABLE IF EXISTS $ROLLS_TABLE")
    }

    fun deleteRolls() {
        val db = this.writableDatabase
        //return db.delete("$ROLLS_TABLE",null,null)
        //return db.execSQL("DROP TABLE IF EXISTS $ROLLS_TABLE")
        return db.execSQL("DELETE FROM $ROLLS_TABLE")
    }
}


class PlayerRoll {
    var dice_number: Int? = null
    var highest_result: Int? = null
    var critical : Boolean? = null
    var action_name: String? = null
    var position: String? = null
    var effect: String? = null
    var date: String? = null
    constructor(dice_number_given: Int?, highest_result: Int?, critical: Boolean, action_name: String?, position: String?, effect: String?, date: String) {
        this.dice_number = dice_number_given
        this.highest_result = highest_result
        this.critical = critical
        this.action_name = action_name
        this.position = position
        this.effect = effect
        this.date = date
    }
}
class UpdateSettings{
    var isMuted : Boolean? = null
    constructor(mute: Boolean){
        this.isMuted = mute
    }
}