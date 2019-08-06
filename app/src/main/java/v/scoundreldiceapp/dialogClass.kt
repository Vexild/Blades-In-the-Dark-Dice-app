package v.scoundreldiceapp

import android.app.Dialog
import android.content.Context
import android.util.Log
import kotlinx.android.synthetic.main.settings_layout.*

class dialogClass(context: Context) : Dialog(context) {
    private var myDb = databaseHelper(context, null)


    fun setLayout(id: Int){
        Log.i("setterClass", "View set to +"+id.toString())
        setContentView(id)
    }

    // Mute settings
    fun onCreateSettings(){
        var result: String = getMute()
        Log.i("OnCreate", "Mute setting RESULT is: " + result)
        this.muteCheckBox.isChecked = result == "1"
    }

    // Getting the mute boolean from settings table
    fun getMute(): String {
        val muteSetting = myDb.getAllSettings()
        muteSetting!!.moveToFirst()
        return muteSetting.getString(1)
    }
}