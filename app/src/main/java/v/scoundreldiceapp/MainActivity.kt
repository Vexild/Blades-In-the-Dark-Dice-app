package v.scoundreldiceapp

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    var arrayOfResults: MutableList<Int> = mutableListOf<Int>()
    var currentRating : Int? = null
    var currentPosition: String? = null
    var currentEffect: String? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    fun startRoll(view: View) {

        resetValues()
        openRatingDialog()
        Log.i("StartRolll", "Selected rating is: "+ currentRating)



        // for each dice -> roll


        // add roll to list

        // print result
        // if 2 or more dice, check for crit
        // select highset

        // play sound and animation
        //log this roll to history (action, position, effect, result)

    }

    fun resetValues(){
        currentRating = 0
        currentPosition = ""
        currentEffect = ""
        arrayOfResults.clear()
        Log.i("Reset","Values reseted")
    }
    fun openRatingDialog() {
        Log.i("GiveRating","GiveRating")
        var result : Int = 1
        val b = android.app.AlertDialog.Builder(this)
        b.setTitle("Give action rating")
        val types = resources.getStringArray(R.array.die_results)
        b.setItems(types, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                result = p1+1
                currentRating = result
                Log.i("Onclick", "result is: "+result+" and current rating is: $currentRating ($p1)")
                if (p0 != null) {
                    p0.dismiss()
                }
                openPositionDialog()
            }
        })
        b.show()

    }
    fun openPositionDialog(){
        Log.i("GivePosition","GivePosition")
        var result : String = "Risky"
        val b = android.app.AlertDialog.Builder(this)
        b.setTitle("Give Position")
        val types = resources.getStringArray(R.array.positions)
        b.setItems(types, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p2: Int) {
                when(p2){
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
    fun openEffectDialog(){
        Log.i("GiveEffect","GiveEffect")

        val b = android.app.AlertDialog.Builder(this)
        b.setTitle("Give Effect")
        val types = resources.getStringArray(R.array.effects)
        b.setItems(types, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p3: Int) {
                when(p3){
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
    fun roll(rating: Int?, position: String?, effect: String?){
        Log.i("Rolling: gathered data",rating.toString()+" "+position+" "+effect)
        for (x in 1..currentRating!!){
            var dieResult =  (1..6).shuffled().first()
            // prints new sequence every time
            //Toast.makeText(this, "Random Number : $dieResult", Toast.LENGTH_SHORT).show()
            Log.i("Rolling the dice", "Result: $dieResult")
            arrayOfResults.add(dieResult)

        }
        val  mp = MediaPlayer.create (this, R.raw.dicesound)
        mp.start()
        // get highest roll
        var highestResult = arrayOfResults.max()
        debugText.setText("number of dice: $rating \nResults: "+arrayOfResults+" \nGiven position: $position. \nGiven effect: $effect \nHighest result is : $highestResult ")

        if(arrayOfResults.count() > 1){
            var criticalChecker = 0
            for(x in arrayOfResults){
                if(x == 6){
                    Log.i("CritChecker","6 found")
                    criticalChecker++
                }
            }
            if(criticalChecker >1){
                debugText.setText("Number of dice: $rating. \nResults: "+arrayOfResults+" \nGiven position: $position \nGiven effect: $effect\n" +
                        "Highest result is : $highestResult \nMore than $criticalChecker x 6's! Critical!")
                Log.i("CritChecker","More than "+criticalChecker+" 6's! Critical!")
                val  mp = MediaPlayer.create (this, R.raw.critical)
                mp.start()
            }

        }
        else{
            debugText.setText("Number of dice: $rating \nResults: "+arrayOfResults+" \nGiven position: $position \nGiven effect: $effect\n" +
                    "Highest result is : $highestResult \nHighest: $highestResult ")
            Log.i("HighestResult", "Highest: "+ highestResult)
        }


    }

    fun soundButtonKnife(view: View){
        val knifesound = MediaPlayer.create(this, R.raw.knifesound)
        knifesound.start()
    }

    fun soundButtonGunShot(view: View){
        val gunsound: MediaPlayer = MediaPlayer.create(this, R.raw.gunshot)
         gunsound.start()
    }




}
