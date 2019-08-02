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



    fun onCreateSettings(){

        var result: String = getMute()
        Log.i("OnCreate", "Mute setting RESULT is: " + result)
        if(result == "1") {
            this.muteCheckBox.isChecked = true
        }
        else{
            this.muteCheckBox.isChecked = false
        }
    }

    fun getMute(): String {
        val muteSetting = myDb.getAllSettings()
        muteSetting!!.moveToFirst()
        //Log.i("OnCreate", "How many columns Settings has " + muteSetting.columnCount+" mute is"+muteSetting.getString(1))

        return muteSetting.getString(1)
    }



}