package v.scoundreldiceapp

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

    // List for dice roll result
    var arrayOfResults: MutableList<Int> = mutableListOf<Int>()

    // Given action, action rating, position and effed. Determined among players and DM
    var currentAction: String? = null
    var currentRating: Int? = null
    var currentPosition: String? = null
    var currentEffect: String? = null

    // Object for local database (SQLite)
    private var myDb = databaseHelper(this, null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()

    }

    // The BIG roll button
    fun startRoll(view: View) {
        // First we reset and initialize all walues used in roll sequence
        resetValues()

        // Then we open the first dialog for choosing an action.
        openActionDialog()
        Log.i("StartRolll", "Selected rating is: " + currentRating)
    }


    fun resetValues() {
        currentRating = 0
        currentPosition = ""
        currentEffect = ""
        arrayOfResults.clear()
        Log.i("Reset", "Values reset")
    }

    fun openActionDialog() {
        Log.i("GiveAction", "GiveAction")

        // Let's build a dialog and give it a name. Next upcoming dialogs are built pretty much the same way as this so
        // I'öö save the comments from them
        val ActionDialog = android.app.AlertDialog.Builder(this)
        ActionDialog.setTitle("Which action do you use?")

        // we get predetermined values from strings.xml and set them inside the dialog
        val actions = resources.getStringArray(R.array.actions)
        ActionDialog.setItems(actions, object : DialogInterface.OnClickListener {

            // And set an OnClickListener on items. User selects an item and selected value is saved. Then we move on to
            // next dialog
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    0 -> currentAction = actions[0]
                    1 -> currentAction = actions[1]
                    2 -> currentAction = actions[2]
                    3 -> currentAction = actions[3]
                    4 -> currentAction = actions[4]
                    5 -> currentAction = actions[5]
                    6 -> currentAction = actions[6]
                    7 -> currentAction = actions[7]
                    8 -> currentAction = actions[8]
                    9 -> currentAction = actions[9]
                    10 -> currentAction =actions[10]
                    11 -> currentAction =actions[11]
                    else -> "ERROR"
                }
                Log.i("Onclick", "Current Position is: $currentPosition($p1)")
                if (p0 != null) {
                    p0.dismiss()
                }
                openRatingDialog()
            }
        })
        ActionDialog.show()
    }

    fun openRatingDialog() {
        Log.i("GiveRating", "GiveRating")
        val RatingDialog = android.app.AlertDialog.Builder(this)
        RatingDialog.setTitle("Give action rating")
        val types = resources.getStringArray(R.array.die_results)
        RatingDialog.setItems(types, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                currentRating = p1 + 1
                Log.i("Onclick", "current rating is: $currentRating ($p1)")
                if (p0 != null) {
                    p0.dismiss()
                }
                openPositionDialog()
            }
        })
        RatingDialog.show()
    }

    fun openPositionDialog() {
        Log.i("GivePosition", "GivePosition")
        val PositionDialog = android.app.AlertDialog.Builder(this)
        PositionDialog.setTitle("Give Position")
        val positions = resources.getStringArray(R.array.positions)
        PositionDialog.setItems(positions, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p2: Int) {
                when (p2) {
                    0 -> currentPosition = positions[0]
                    1 -> currentPosition = positions[1]
                    2 -> currentPosition = positions[2]
                    else -> "ERROR"
                }
                Log.i("Onclick", "Current Position is: $currentPosition($p2)")
                if (p0 != null) {
                    p0.dismiss()
                }
                openEffectDialog()
            }
        })
        PositionDialog.show()
    }

    fun openEffectDialog() {
        Log.i("GiveEffect", "GiveEffect")

        val EffectDialog = android.app.AlertDialog.Builder(this)
        EffectDialog.setTitle("Give Effect")
        val effects = resources.getStringArray(R.array.effects)
        EffectDialog.setItems(effects, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p3: Int) {
                when (p3) {
                    0 -> currentEffect = effects[0]
                    1 -> currentEffect = effects[1]
                    2 -> currentEffect = effects[2]
                    else -> "ERROR"
                }
                Log.i("Onclick", "Current effect is: $currentEffect($p3)")
                if (p0 != null) {
                    p0.dismiss()
                }
                roll(currentAction, currentRating, currentPosition, currentEffect)
            }
        })
        EffectDialog.show()
    }

    //@SuppressLint("SetTextI18n")

    // In roll function we take the saved user inputs and use simple randomize to get results.
    fun roll(action: String?, rating: Int?, position: String?, effect: String?) {
        Log.i("Rolling: gathered data", rating.toString() + " " + position + " " + effect)

        // For each dice we get a random value between 1 and 6. Single result is added to arrayOfResults
        for (x in 1..currentRating!!) {
            var dieResult = (1..6).shuffled().first()
            Log.i("Rolling the dice", "Result: $dieResult")
            arrayOfResults.add(dieResult)
        }

        // Get highest roll among all
        var highestResult = arrayOfResults.max()

        // We set critical boolean to false as default. We have criteria for critical rolls so false is good for now
        var critical: Boolean = false

        // Initialize outcome variable. Maybe this could be done even before OnCreate buuuut I think this doesn't hurt anyone
        var outcome = ""

        // We check for crits. So, we first check has the user rolled more than 1 die because critical can only be achieved
        // with 2 or more 6's. In case of more than 1 die we count all 6's and in case of two of them we set the ciritcal
        // boolean = true.
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

        // If roll is not critical - as an default is should not be -  we simply check the result and return corresponding outcome
        if (critical == false) {
            when (highestResult) {
                in 1..3 -> outcome = "Failed"
                in 4..5 -> outcome = "Partial Success"
                6 -> outcome = "Full Success"
                else -> "ERROR"
            }
            // Play the nice rolling sound
            playSound("rollsound")
        } else {
            // But of course in case of crit we have a nice critical sound played
            outcome = "Critical Success"
            playSound("rollsound")
            playSound("critical")
        }

        // this cunftion is simply for debuging and therefore has no functional role
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

    //@SuppressLint("SetTextI18n")
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

    // Functions for two sound buttons. These buttons are there to give some RPG feeling.
    fun soundButtonKnife(view: View) {
        playSound("knifesound")
    }
    fun soundButtonGunShot(view: View) {
        playSound("gunsound")
    }

    // General sound palying function
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

    // Toolbar initialization
    fun initToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
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

    // Setting dialog for sound mute and roll history clear. There's also a fun button if user wants to have fun :)
    fun openSettingsDialog() {
        Log.i("Settings", "Settings Dialog Opened")
        val settingDialog = dialogClass(this)

        // we set our layout xml
        settingDialog.setLayout(R.layout.settings_layout)

        // This is a doppelganger "onCreate" function, not an actual onCreate
        settingDialog.onCreateSettings()

        // And set onClickListeners on all buttons and determine their functionality
        settingDialog.saveSettings.setOnClickListener {

            if (settingDialog.muteCheckBox.isChecked) {
                // execute sql query -> update setting table mute = true
                myDb.setMute(UpdateSettings(1))
                Log.i("Settings", "Settings saved " + settingDialog.muteCheckBox.isChecked.toString())
            }
            if (!settingDialog.muteCheckBox.isChecked) {
                // execute sql query -> update setting table mute = false
                myDb.setMute(UpdateSettings(0))
                Log.i("Settings", "Settings saved " + settingDialog.muteCheckBox.isChecked.toString())
            }
            // After saving we close the dialog
            settingDialog.cancel()

            //val afterCheck = myDb.getAllSettings()
            //afterCheck!!.moveToFirst()
            //Log.i("after Check fom Dialog", afterCheck.getString(0) + " " + afterCheck.getString(1))
        }

        // Clear roll history. I saved commented lines for table destroying and recreating them because they might
        // cause some troubles
        settingDialog.dbDelete.setOnClickListener {
            //myDb.destroyTable("settings")
            //myDb.reCreateSettingsDb()
            myDb.deleteRolls()
            //myDb.destroyTable("rolls")
            //myDb.reCreateRollsDb()
            Toast.makeText(this, "Rolls history cleared", Toast.LENGTH_SHORT).show()
        }

        // I had an adctual recreate database button but now on it's just a fun button :) it makes fun
        settingDialog.reCreateDb.setOnClickListener {
            Toast.makeText(this, ":-)", Toast.LENGTH_LONG).show()
        }
        settingDialog.show()
    }

    // Infodialog for plain info
    fun openInfoDialog() {
        val infoDialog = dialogClass(this)
        infoDialog.setLayout(R.layout.info_layout)
        infoDialog.show()
    }

    // Dialog for previous rolls. Rolls are loaded during this dialog opens.
    fun openHistoryDialog() {
        val historyDialog = dialogClass(this)
        // Let's set our history layout
        historyDialog.setLayout(R.layout.history_layout)

        // And create a custom adapter for entries. Adapter gets its data from getDataFromDb() function
        val myListAdapter = MyListAdapter(this, getDataFromBd())

        // Create a listview inside dialog and set custom adapter
        val history_roll_list = historyDialog.run { findViewById<ListView>(R.id.listViewLayout) }
        history_roll_list.adapter = myListAdapter
        Log.i("HistoryDialog", "Adapter applied")

        historyDialog.historyTitle.text = "Previous Rolls"
        historyDialog.show()
    }

    // Get rolls from database and return it as a ArrayList
    private fun getDataFromBd(): ArrayList<Roll> {
        // Basic db cursorin
        val listFromDb: Cursor? = myDb.getAllRolls()
        var listOfRolls = ArrayList<Roll>()
        listFromDb!!.moveToFirst()

        // Get the size of the table and get data only if the table is not empty
        var dbSize = listFromDb.count
        if (dbSize > 0) {
            for (x in 1..dbSize) {

                // Create row object by using Roll constructor
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
        // Important to reverse the list.
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
