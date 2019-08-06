package v.scoundreldiceapp

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class MyListAdapter(private val context: Activity, private val data: ArrayList<Roll>) //private val highestRoll: Int, private val action: String, private val position : String, private val effect : String, private val outcome : String, private val date : String)
    : BaseAdapter() {

    private class ViewHolder(row: View?) {
        var history_date: TextView? = null
        var history_action: TextView? = null
        var history_positionEffect: TextView? = null
        var history_totalRoll: TextView? = null
        var history_highest: TextView? = null
        var history_outcome: TextView? = null

            init {
            this.history_date = row?.findViewById<TextView>(R.id.history_date)
            this.history_action = row?.findViewById<TextView>(R.id.history_action)
            this.history_positionEffect = row?.findViewById<TextView>(R.id.history_position_effect)
            this.history_totalRoll = row?.findViewById<TextView>(R.id.history_roll)
            this.history_highest = row?.findViewById<TextView>(R.id.history_highest)
            this.history_outcome = row?.findViewById<TextView>(R.id.history_outcome)

        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        Log.i("ListAdapter",data.toString())
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = context.layoutInflater
            view = inflater.inflate(R.layout.list_item_layout, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        var userData = data[position]
        viewHolder.history_date?.text = userData.date
        viewHolder.history_action?.text = userData.action
        viewHolder.history_positionEffect?.text = (userData.position+" "+userData.effect)
        viewHolder.history_totalRoll?.text = userData.roll_result
        Log.i("DEBUG APADTER","User Roll: "+ userData.roll.toString())
        viewHolder.history_highest?.text = userData.highestroll.toString()
        viewHolder.history_outcome?.text = userData.outcome

        return view as View
    }
    override fun getItem(i: Int): Roll {
        return data[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

}