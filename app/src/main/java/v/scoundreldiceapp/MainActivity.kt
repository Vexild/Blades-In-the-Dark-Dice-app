package v.scoundreldiceapp

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.database.Cursor
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.history_layout.*
import kotlinx.android.synthetic.main.settings_layout.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    var arrayOfResults: MutableList<Int> = mutableListOf<Int>()
    var currentRating: Int? = null
    var currentPosition: String? = null
    var currentEffect: String? = null
    var myDb = databaseHelper(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()

        // gett data from db
        var dbcontent: Cursor? = myDb.getAllRolls()
        dbcontent!!.moveToFirst()
        while(dbcontent.moveToNext()){
            Log.i("OnStart - DB Content", dbcontent.getString(1)+dbcontent.getString(2)+dbcontent.getString(3)+dbcontent.getString(4)+dbcontent.getString(5))

        }

    }

    fun startRoll(view: View) {

        resetValues()
        openRatingDialog()
        Log.i("StartRolll", "Selected rating is: " + currentRating)

        //log this roll to history (action, position, effect, result)

    }

    fun resetValues() {
        currentRating = 0
        currentPosition = ""
        currentEffect = ""
        arrayOfResults.clear()
        Log.i("Reset", "Values reseted")
    }

    fun openRatingDialog() {
        Log.i("GiveRating", "GiveRating")
        var result: Int = 1
        val b = android.app.AlertDialog.Builder(this)
        b.setTitle("Give action rating")
        val types = resources.getStringArray(R.array.die_results)
        b.setItems(types, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                result = p1 + 1
                currentRating = result
                Log.i("Onclick", "result is: " + result + " and current rating is: $currentRating ($p1)")
                if (p0 != null) {
                    p0.dismiss()
                }
                openPositionDialog()
            }
        })
        b.show()

    }

    fun openPositionDialog() {
        Log.i("GivePosition", "GivePosition")
        var result: String = "Risky"
        val b = android.app.AlertDialog.Builder(this)
        b.setTitle("Give Position")
        val types = resources.getStringArray(R.array.positions)
        b.setItems(types, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p2: Int) {
                when (p2) {
                    0 -> currentPosition = "Controlled"
                    1 -> currentPosition = "Risky"
                    2 -> currentPosition = "Desperate"
                    else -> "ERROR"
                }
                Log.i("Onclick", "Current Position is: $currentPosition($p2)")
                if (p0 != null) {
                    p0.dismiss()
                }
                openEffectDialog()
            }
        })
        b.show()
    }

    fun openEffectDialog() {
        Log.i("GiveEffect", "GiveEffect")

        val b = android.app.AlertDialog.Builder(this)
        b.setTitle("Give Effect")
        val types = resources.getStringArray(R.array.effects)
        b.setItems(types, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p3: Int) {
                when (p3) {
                    0 -> currentEffect = "Limited"
                    1 -> currentEffect = "Standard"
                    2 -> currentEffect = "Great"
                    else -> "ERROR"
                }
                Log.i("Onclick", "Current effect is: $currentEffect($p3)")
                if (p0 != null) {
                    p0.dismiss()
                }
                roll(currentRating, currentPosition, currentEffect)
            }
        })
        b.show()
    }

    @SuppressLint("SetTextI18n")
    fun roll(rating: Int?, position: String?, effect: String?) {
        Log.i("Rolling: gathered data", rating.toString() + " " + position + " " + effect)
        for (x in 1..currentRating!!) {
            var dieResult = (1..6).shuffled().first()
            Log.i("Rolling the dice", "Result: $dieResult")
            arrayOfResults.add(dieResult)

        }
        val mp = MediaPlayer.create(this, R.raw.dicesound)
        mp.start()
        // get highest roll
        var highestResult = arrayOfResults.max()
        var critical: Boolean = false
        var outcome = ""

        if (arrayOfResults.count() > 1) {
            var criticalChecker = 0
            for (x in arrayOfResults) {
                if (x == 6) {
                    Log.i("CritChecker", "6 found")
                    criticalChecker++
                }
            }
            if (criticalChecker > 1) {
                critical = true
                Log.i("CritChecker", "More than " + criticalChecker + " 6's! Critical!")

            }
        }
        if(critical == false) {
            when (highestResult) {
                in 1..3 -> outcome = "Failed"
                in 4..5 -> outcome = "Partial Success"
                6 -> outcome = "Full Success"
                else -> "ERROR"
            }
        }
        else{
            outcome = "Critical Success"
            val mp = MediaPlayer.create(this, R.raw.critical)
            mp.start()
        }
        printResult(arrayOfResults, highestResult,position,effect, outcome, critical)
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var storableData = PlayerRoll(rating,highestResult,critical, position, position, effect,timeStamp )
        Log.i("Saving after roll", storableData.toString())
        myDb.insertRollToDb(storableData)

    }
    @SuppressLint("SetTextI18n")
    fun printResult(result: MutableList<Int>, highestResult: Int?, position: String?, effect: String?, outcome: String?, critical: Boolean?){
        diceResultText.setText(""+ result.toString().replace("[]", ""))
        highestResultText.setText(""+highestResult)
        positionText.setText("" + position + " " + effect)
        if(critical == true) {
            outcomeText.setText(""+"Critical success")
        }
        else{
            outcomeText.setText(""+outcome)
        }
        initText1.text = "You've rolled:"
        initText2.text = "Highest result:"
    }

    fun soundButtonKnife(view: View) {
        val knifesound = MediaPlayer.create(this, R.raw.knifesound)
        knifesound.start()
    }

    fun soundButtonGunShot(view: View) {
        val gunsound: MediaPlayer = MediaPlayer.create(this, R.raw.gunshot)
        gunsound.start()
    }

    fun initToolbar() {
        setSupportActionBar(toolbar)
        initText1.text = ""
        initText2.text = ""
        // Now get the support action bar
        val actionBar = supportActionBar

        // Set action bar elevation
        actionBar!!.elevation = 4.0F

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_items, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        return when (item.itemId) {
            R.id.history -> {
                openHistoryDialog()
                return true
            }
            R.id.settings -> {
                openSettingsDialog()
                return true
            }
            R.id.info -> {
                openInfoDialog()
                return true
            }

            else -> return super.onOptionsItemSelected(item)

        }

    }

    fun openSettingsDialog(){

        Log.i("Settings", "Settings Dialog Opened")
        val settingDialog = dialogClass(this)
        val isMuted: Boolean = false
        settingDialog.setLayout(R.layout.settings_layout)
        settingDialog.saveSettings.setOnClickListener{


            if(settingDialog.muteCheckBox.isChecked) {
                // execute sql query -> update setting table mute ( true
                Log.i("Settings", "Settings saved"+settingDialog.muteCheckBox.isChecked.toString())
            }
            if(!settingDialog.muteCheckBox.isChecked){
                // execute sql query -> update setting table mute (
                Log.i("Settings", "Settings saved"+settingDialog.muteCheckBox.isChecked.toString())
            }
            settingDialog.cancel()
        }
        settingDialog.show()

    }
    fun openInfoDialog(){
        val infoDialog = dialogClass(this)
        infoDialog.setLayout(R.layout.info_layout)
        infoDialog.show()
    }
    fun openHistoryDialog(){
        val historyDialog = dialogClass(this)
        historyDialog.setLayout(R.layout.history_layout)
        //val testList = listOf<String>("Suomi","Ruotsi","Old World", "Westeros")

/*
        var roll = arrayOf(1,2,5,5)
        var highestroll = 5
        var action = "Hunt"
        var position = "Risky"
        var effect = "Standard"
        var outcome = "success"
        var date = "1.1.2019 12:00"



        val listFromDb: Cursor? = myDb.getAllRolls()
        var listOfRolls = arrayOf<String>()
        //var listOfRolls = JSONObject()

        listFromDb!!.moveToFirst()
        while(listFromDb.moveToNext()) {

            var row = arrayOf<String>()
            row += "[" + listFromDb.getString(1) + ", " + listFromDb.getString(2) + ", " + listFromDb.getString(3) + ", " + listFromDb.getString(
                4
            ) + ", " + listFromDb.getString(5) + ", " + listFromDb.getString(6) + ", " + listFromDb.getString(7) + "]"

            listOfRolls += row
            // Log.i("HistoryDialog", "found row: " + listFromDb)

        }
        //Log.i("HistoryDialog", "List of results: $listOfRolls")
        */



        val myListAdapter = MyListAdapter(this, generateData()) //roll, highestroll,action, position, effect, outcome, date)
        val history_roll_list = historyDialog.findViewById<ListView>(R.id.listViewLayout)
        history_roll_list.adapter = myListAdapter

        Log.i("HistoryDialog", "Adapter applied")

        historyDialog.historyTitle.text = "Previous Rolls"
        historyDialog.show()
    }

    fun generateData(): ArrayList<Roll> {
        var result = ArrayList<Roll>()

        for (i in 0..9) {
            var user: Roll = Roll(
                roll = "2,4,2,1",
                highestRoll = 4,
                action = "Hunt",
                position = "Risky",
                effect = "Great",
                outcome = "Partial Success",
                date = "1.1.2019 12:20"
            )
            result.add(user)
        }

        return result
    }
}
class Roll {
    var roll = ""
    var highestroll = 0
    var action = ""
    var position = ""
    var effect = ""
    var outcome = ""
    var date = ""

    constructor() {}

    constructor(roll: String, highestRoll: Int, action: String, position: String, effect: String, outcome: String, date: String) {
        this.roll = roll
        this.highestroll = highestRoll
        this.action = action
        this.position = position
        this.effect = effect
        this.outcome = outcome
        this.date = date
    }
}
