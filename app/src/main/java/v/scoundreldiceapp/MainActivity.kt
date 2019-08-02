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
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.history_layout.*
import kotlinx.android.synthetic.main.settings_layout.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    var arrayOfResults: MutableList<Int> = mutableListOf<Int>()
    var currentAction: String? = null
    var currentRating: Int? = null
    var currentPosition: String? = null
    var currentEffect: String? = null
    private var myDb = databaseHelper(this, null)
    //var localMuteSetting: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()

        //myDb.destroyTable("rolls")
        //myDb.reCreateRollsDb()

    }

    fun startRoll(view: View) {

        resetValues()
        openActionDialog()
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

    fun openActionDialog() {
        Log.i("GiveAction", "GiveAction")
        var result: String = ""
        val b = android.app.AlertDialog.Builder(this)
        b.setTitle("Which action do you use?")
        val types = resources.getStringArray(R.array.actions)
        b.setItems(types, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    0 -> currentAction = types[0]
                    1 -> currentAction = types[1]
                    2 -> currentAction = types[2]
                    3 -> currentAction = types[3]
                    4 -> currentAction = types[4]
                    5 -> currentAction = types[5]
                    6 -> currentAction = types[6]
                    7 -> currentAction = types[7]
                    8 -> currentAction = types[8]
                    9 -> currentAction = types[9]
                    10 -> currentAction =types[10]
                    11 -> currentAction =types[11]
                    else -> "ERROR"
                }
                Log.i("Onclick", "Current Position is: $currentPosition($p1)")
                if (p0 != null) {
                    p0.dismiss()
                }
                openRatingDialog()
            }
        })
        b.show()

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
                roll(currentAction, currentRating, currentPosition, currentEffect)
            }
        })
        b.show()
    }

    @SuppressLint("SetTextI18n")
    fun roll(action: String?, rating: Int?, position: String?, effect: String?) {
        Log.i("Rolling: gathered data", rating.toString() + " " + position + " " + effect)
        for (x in 1..currentRating!!) {
            var dieResult = (1..6).shuffled().first()
            Log.i("Rolling the dice", "Result: $dieResult")
            arrayOfResults.add(dieResult)

        }
        playSound("rollsound")
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
        if (critical == false) {
            when (highestResult) {
                in 1..3 -> outcome = "Failed"
                in 4..5 -> outcome = "Partial Success"
                6 -> outcome = "Full Success"
                else -> "ERROR"
            }
        } else {
            outcome = "Critical Success"
            playSound("rollsound")
            playSound("critical")
        }
        printResult(action, arrayOfResults, highestResult, position, effect, outcome, critical)
        var timeStamp: String = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date())
        var storableData = PlayerRoll(
            rating,
            arrayOfResults.toString(),
            highestResult,
            critical,
            action,
            position,
            effect,
            timeStamp,
            outcome
        )
        Log.i("Saving after roll", action+" "+arrayOfResults.toString()+" "+ highestResult+" "+position+" "+effect+" "+outcome+" "+critical)
        myDb.insertRollToDb(storableData)

    }

    @SuppressLint("SetTextI18n")
    fun printResult(
        action: String?,
        result: MutableList<Int>,
        highestResult: Int?,
        position: String?,
        effect: String?,
        outcome: String?,
        critical: Boolean?
    ) {
        diceResultText.text = "" + result.toString().replace("[]", "")
        highestResultText.text = "" + highestResult
        positionText.text = "" + position + " " + effect
        if (critical == true) {
            outcomeText.text = "" + "Critical success"
        } else {
            outcomeText.text = "" + outcome
        }
        initText1.text = "You've rolled:"
        initText2.text = "Highest result:"
    }

    fun soundButtonKnife(view: View) {
        playSound("knifesound")
    }

    fun soundButtonGunShot(view: View) {
        playSound("gunsound")
    }

    fun playSound(track: String) {
        val ismuted = dialogClass(this)
        if (ismuted.getMute() == "0") {
            val gunsound: MediaPlayer = MediaPlayer.create(this, R.raw.gunshot)
            val knifesound = MediaPlayer.create(this, R.raw.knifesound)
            val critical = MediaPlayer.create(this, R.raw.critical)
            val rollsound = MediaPlayer.create(this, R.raw.dicesound)

            when (track) {
                "gunsound" -> gunsound.start()
                "knifesound" -> knifesound.start()
                "critical" -> critical.start()
                "rollsound" -> rollsound.start()

            }
        }
    }

    fun initToolbar() {
        setSupportActionBar(toolbar)
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

    fun openSettingsDialog() {

        Log.i("Settings", "Settings Dialog Opened")
        val settingDialog = dialogClass(this)
        settingDialog.setLayout(R.layout.settings_layout)
        settingDialog.onCreateSettings()
        settingDialog.saveSettings.setOnClickListener {


            if (settingDialog.muteCheckBox.isChecked) {
                // execute sql query -> update setting table mute ( true
                myDb.setMute(UpdateSettings(1))
                Log.i("Settings", "Settings saved " + settingDialog.muteCheckBox.isChecked.toString())
            }
            if (!settingDialog.muteCheckBox.isChecked) {
                // execute sql query -> update setting table mute (
                myDb.setMute(UpdateSettings(0))
                Log.i("Settings", "Settings saved " + settingDialog.muteCheckBox.isChecked.toString())
            }

            settingDialog.cancel()

            val afterCheck = myDb.getAllSettings()
            afterCheck!!.moveToFirst()
            Log.i("after Check fom Dialog", afterCheck.getString(0) + " " + afterCheck.getString(1))
        }

        settingDialog.dbDelete.setOnClickListener {
            //myDb.destroyTable("settings")
            //myDb.reCreateSettingsDb()
            myDb.deleteRolls()
            Toast.makeText(this, "Rolls history cleared", Toast.LENGTH_SHORT).show()
        }
        settingDialog.reCreateDb.setOnClickListener {
            //myDb.reCreateDb()
            Toast.makeText(this, ":-)", Toast.LENGTH_LONG).show()
        }

        settingDialog.show()

    }

    fun openInfoDialog() {
        val infoDialog = dialogClass(this)
        infoDialog.setLayout(R.layout.info_layout)
        infoDialog.show()
    }

    fun openHistoryDialog() {
        val historyDialog = dialogClass(this)
        historyDialog.setLayout(R.layout.history_layout)
        //val testList = listOf<String>("Suomi","Ruotsi","Old World", "Westeros")

        val myListAdapter =
            MyListAdapter(this, getDataFromBd()) //roll, highestroll,action, position, effect, outcome, date)
        val history_roll_list = historyDialog.findViewById<ListView>(R.id.listViewLayout)
        history_roll_list.adapter = myListAdapter

        Log.i("HistoryDialog", "Adapter applied")

        historyDialog.historyTitle.text = "Previous Rolls"
        historyDialog.show()
    }

    private fun getDataFromBd(): ArrayList<Roll> {
        val listFromDb: Cursor? = myDb.getAllRolls()
        var listOfRolls = ArrayList<Roll>()
        listFromDb!!.moveToFirst()
        var dbSize = listFromDb.count
        if (dbSize > 0) {
            for (x in 1..dbSize) { // (listFromDb.moveToNext()) {

                var row: Roll = Roll(
                    roll = listFromDb.getString(1),
                    roll_result = listFromDb.getString(2),
                    highestRoll = listFromDb.getString(3),
                    action = listFromDb.getString(5),
                    position = listFromDb.getString(6),
                    effect = listFromDb.getString(7),
                    outcome = when (listFromDb.getString(4)) {
                        "0" -> when (listFromDb.getString(3).toString()) {
                            "1" -> "Failure"
                            "2" -> "Failure"
                            "3" -> "Failure"
                            "4" -> "Partial Success"
                            "5" -> "Partial Success"
                            "6" -> "Full Success"
                            else -> "Error"
                        }
                        "1" -> {
                            "Critical"
                        }
                        else -> "Error"
                    },
                    date = listFromDb.getString(8)
                )
                listOfRolls.add(row)
                listFromDb.moveToNext()
            }
        }
        Log.i("DataList", listOfRolls.toString())
        listOfRolls.reverse()

        Log.i("DataList Reversed", listOfRolls.toString())
        return listOfRolls
    }
}

class Roll {
    var roll = ""
    var roll_result = ""
    var highestroll = ""
    var action = ""
    var position = ""
    var effect = ""
    var outcome = ""
    var date = ""


    constructor(roll: String, roll_result: String, highestRoll: String, action: String, position: String, effect: String, outcome: String, date: String) {
        this.roll = roll
        this.roll_result = roll_result
        this.highestroll = highestRoll
        this.action = action
        this.position = position
        this.effect = effect
        this.outcome = outcome
        this.date = date
    }
}
