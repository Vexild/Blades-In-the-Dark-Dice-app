package v.scoundreldiceapp

import android.app.Dialog
import android.content.Context
import android.util.Log

class dialogClass(context: Context) : Dialog(context) {


    fun setLayout(id: Int){
        Log.i("setterClass", "View set to +"+id.toString())
        setContentView(id)
    }
}