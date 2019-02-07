package com.example.joni.assignment

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class RVAdapter : RecyclerView.Adapter<RVAdapter.RVViewHolder>(){
    private var myDataset: List<ObservationRV>? = listOf()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RVAdapter.RVViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.rv_item, p0, false)
        return RVViewHolder(view)
    }

    override fun onBindViewHolder(p0: RVViewHolder, p1: Int) {
        if (myDataset != null) {
            p0.cv_species.text = myDataset!![p1].species
            p0.cv_rarity.text = myDataset!![p1].rarity
            p0.cv_date.text = myDataset!![p1].date
            p0.cv_time.text = myDataset!![p1].time
            p0.cv_note.text = myDataset!![p1].note

            if (myDataset!![p1].image != null && myDataset!![p1].image!!.isNotEmpty()){
                val imgBitmap = BitmapFactory.decodeFile(myDataset!![p1].image)
                p0.cv_img.setImageBitmap(imgBitmap)
            }
            else {
                p0.cv_img.setImageResource(R.drawable.noimage)
            }
        }
    }

    override fun getItemCount(): Int {
        return myDataset?.size ?: 0
    }

    fun update(list: List<ObservationRV>?){
        this.myDataset =  list
        notifyDataSetChanged()
    }

    inner class RVViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        internal var cv_species: TextView = itemView.findViewById<View>(R.id.cv_species) as TextView
        internal var cv_rarity: TextView = itemView.findViewById<View>(R.id.cv_rarity) as TextView
        internal var cv_date: TextView = itemView.findViewById<View>(R.id.cv_date) as TextView
        internal var cv_time: TextView = itemView.findViewById<View>(R.id.cv_time) as TextView
        internal var cv_note: TextView = itemView.findViewById<View>(R.id.cv_note) as TextView
        internal var cv_img: ImageView = itemView.findViewById<View>(R.id.cv_img) as ImageView
    }
}