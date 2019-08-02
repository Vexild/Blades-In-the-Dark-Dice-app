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
        val SETTING_TABLE = "settings"
        val DICE_NUMBER = "dice_number"
        val DICE_RESULT = "dice_result"
        val HIGHEST_RESULT = "highest_result"
        val CRITICAL = "critical"
        val ACTION_NAME = "action_name"
        val POSITION = "position"
        val EFFECT = "effect"
        val DATE = "date"
        val MUTE = "mute"
        val OUTCOME = "outcome"
        val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS rolls (ID INTEGER PRIMARY KEY AUTOINCREMENT, dice_number INT, dice_result TEXT,  highest_result INT, critical BOOLEAN, action_name TEXT, position TEXT, effect TEXT, date TEXT, outcome TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS settings (ID INTEGER PRIMARY KEY AUTOINCREMENT, mute INT)")
        Log.i("DatabaseHelper", "DB Created")
        //Log.i("DatabaseHelper", getProfile().toString())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS "+ ROLLS_TABLE)
        onCreate(db)
    }

    fun reCreateRollsDb() {
        val db = this.writableDatabase
        db.execSQL("CREATE TABLE IF NOT EXISTS rolls (ID INTEGER PRIMARY KEY AUTOINCREMENT, dice_number INT, dice_result TEXT,  highest_result INT, critical BOOLEAN, action_name TEXT, position TEXT, effect TEXT, date TEXT, outcome TEXT)")
    }

    fun reCreateSettingsDb() {
        val db = this.writableDatabase
        db.execSQL("CREATE TABLE IF NOT EXISTS settings (ID INTEGER PRIMARY KEY AUTOINCREMENT, mute INT)")
        val initSettings = UpdateSettings(0)
        InitializeSettings(settings = initSettings)
    }

    fun setMute(settings: UpdateSettings) {
        val db = this.writableDatabase
        Log.i("Set Mute", "Settings: "+settings+". And settings.ismuted: "+settings.isMuted)
        var muteSetting = settings.isMuted

        Log.i("Set Mute", "Mutesetting = "+muteSetting)
        return db.execSQL("UPDATE $SETTING_TABLE SET $MUTE = $muteSetting")
    }

    fun InitializeSettings(settings: UpdateSettings){
        val db = this.writableDatabase
        var initSettings = ContentValues()
        initSettings.put(MUTE, settings.isMuted)
        db.insert("$SETTING_TABLE", null, initSettings)
        db.close()
    }

    fun getMute(): Cursor{
        val db =this.writableDatabase
        return db.rawQuery("SELECT $MUTE FROM $SETTING_TABLE", null)

    }
    fun insertRollToDb(roll: PlayerRoll){
        val db = this.writableDatabase

        var rollValues = ContentValues()
        rollValues.put(DICE_NUMBER, roll.dice_number)
        rollValues.put(DICE_RESULT, roll.dice_result)
        rollValues.put(HIGHEST_RESULT, roll.highest_result)
        rollValues.put(CRITICAL, roll.critical)
        rollValues.put(ACTION_NAME, roll.action_name)
        rollValues.put(POSITION, roll.position)
        rollValues.put(EFFECT, roll.effect)
        rollValues.put(DATE, roll.date)
        rollValues.put(OUTCOME, roll.outcome)
        Log.i("DBhelper", rollValues.toString())
        db.insert(ROLLS_TABLE,null, rollValues)
        db.close()
    }

    fun getAllRolls(): Cursor? {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $ROLLS_TABLE", null)
    }

    fun getAllSettings(): Cursor?{
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $SETTING_TABLE", null)
    }
    fun destroyTable(tablename: String){
        val db = this.writableDatabase
        return db.execSQL("DROP TABLE IF EXISTS $tablename")
    }

    fun deleteRolls() {
        val db = this.writableDatabase
        //return db.delete("$ROLLS_TABLE",null,null)
        //return db.execSQL("DROP TABLE IF EXISTS $ROLLS_TABLE")
        return db.execSQL("DELETE FROM $ROLLS_TABLE")
    }
    fun getSettings(): Cursor? {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $SETTING_TABLE", null)
    }
}


class PlayerRoll {
    var dice_number: Int? = null
    var dice_result: String? = null
    var highest_result: Int? = null
    var critical : Boolean? = null
    var action_name: String? = null
    var position: String? = null
    var effect: String? = null
    var date: String? = null
    var outcome: String? = null
    constructor(dice_number_given: Int?, dice_result: String?, highest_result: Int?, critical: Boolean, action_name: String?, position: String?, effect: String?, date: String, outcome: String) {

        this.dice_number = dice_number_given
        this.dice_result = dice_result
        this.highest_result = highest_result
        this.critical = critical
        this.action_name = action_name
        this.position = position
        this.effect = effect
        this.date = date
        this.outcome = outcome
    }
}
class UpdateSettings{
    var isMuted: Int = 0
    constructor(mute: Int){
        this.isMuted = mute
    }
}