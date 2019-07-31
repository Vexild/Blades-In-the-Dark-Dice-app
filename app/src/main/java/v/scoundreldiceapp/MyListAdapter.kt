package v.scoundreldiceapp

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class MyListAdapter(private val context: Activity, private val data: ArrayList<Roll>) //private val highestRoll: Int, private val action: String, private val position : String, private val effect : String, private val outcome : String, private val date : String)
    : BaseAdapter() { //<String>(context, R.layout.list_item_layout) {

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
            this.history_positionEffect = row?.findViewById<TextView>(R.id.history_position)
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

        var userDto = data[position]
        viewHolder.history_date?.text = userDto.date
        viewHolder.history_action?.text = userDto.action
        viewHolder.history_positionEffect?.text = (userDto.position+" "+userDto.effect)
        viewHolder.history_totalRoll?.text = userDto.roll
        Log.i("DEBUG APADTER","User Roll: "+ userDto.roll.toString())
        viewHolder.history_highest?.text = userDto.highestroll.toString()
        viewHolder.history_outcome?.text = userDto.outcome

        return view as View
/*
val inflater = context.layoutInflater
val rowView = inflater.inflate(R.layout.list_item_layout, null, true)

Log.i("MyListAdapter", "$roll $highestRoll $action $position $effect $outcome $date")


val history_date = rowView.findViewById(R.id.history_date) as TextView
val history_action = rowView.findViewById(R.id.history_action) as TextView
val history_position = rowView.findViewById(R.id.history_position) as TextView
val history_effect = rowView.findViewById(R.id.history_effect) as TextView
val history_totalRoll = rowView.findViewById(R.id.history_roll) as TextView
val history_highest = rowView.findViewById(R.id.history_highest) as TextView
val history_outcome = rowView.findViewById(R.id.history_outcome) as TextView

history_totalRoll.text = roll.toString()
history_highest.text = highestRoll.toString()
history_action.text = action
history_position.text = position.toString()
history_effect.text = effect
history_outcome.text = "PLACE HOLDA"
history_date.text = date


return rowView
*/
//return super.getView(position, convertView, parent)
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