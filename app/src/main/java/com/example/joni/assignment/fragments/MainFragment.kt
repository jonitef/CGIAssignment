package com.example.joni.assignment.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.joni.assignment.ObservationRV
import com.example.joni.assignment.R
import com.example.joni.assignment.RVAdapter
import com.example.joni.assignment.db.Observation
import com.example.joni.assignment.db.ObservationViewModel
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment: Fragment() {

    private var sp: SharedPreferences? = null
    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    private var ovm: ObservationViewModel? = null

    // need to setHasOptions to true, so that the menu can be used in fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sp = PreferenceManager.getDefaultSharedPreferences(context)

        val viewManager = LinearLayoutManager(context)

        val viewAdapter = RVAdapter()

        activity!!.findViewById<RecyclerView>(R.id.rv).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        // set listener for SharedPreferences, if user change sorting settings
        // remove old observer and add new with a right parameters
        listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, _ ->
            if (ovm != null && ovm!!.getObservations(1, "*").hasObservers()){
                ovm!!.getObservations(prefs.getInt("sorting", 1), prefs.getString("rarity", "*")!!).removeObservers(this)
            }
            startObservation(viewAdapter, prefs.getInt("sorting", 1), sp!!.getString("rarity", "*")!!)
        }

        sp!!.registerOnSharedPreferenceChangeListener(listener)

        startObservation(viewAdapter, sp!!.getInt("sorting", 1), sp!!.getString("rarity", "*")!!)

        addObservationBtn.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.fragment_container, AddObservationFragment()).addToBackStack(null).commit()
        }
    }

    // Handle sorting settings
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId){
            R.id.newest -> {
                item.isChecked = true
                sorting(1)
                mainItem(item.itemId)
                return true
            }
            R.id.species -> {
                item.isChecked = true
                sorting(2)
                mainItem(item.itemId)
                return true
            }
            R.id.all -> {
                item.isChecked = true
                rarity("*")
                secondaryitem(item.itemId)
                return true
            }
            R.id.common -> {
                item.isChecked = true
                rarity("Common")
                secondaryitem(item.itemId)
                return true
            }
            R.id.rare -> {
                item.isChecked = true
                rarity("Rare")
                secondaryitem(item.itemId)
                return true
            }
            R.id.extRare -> {
                item.isChecked = true
                rarity("Extremely rare")
                secondaryitem(item.itemId)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }


    // save sorting methods to SharedSpreferences
    private fun mainItem(mainItem: Int){
        sp!!.edit().putInt("mainChoiceIsChecked", mainItem).apply()
    }
    private fun secondaryitem(secondaryItem: Int) {
        sp!!.edit().putInt("secondaryChoiceIsChecked", secondaryItem).apply()
    }
    private fun sorting(sorting: Int){
        sp!!.edit().putInt("sorting", sorting).apply()
    }
    private fun rarity(rarity: String){
        sp!!.edit().putString("rarity", rarity).apply()
    }

    // start observing observations data from the database
    private fun startObservation(viewAdapter: RVAdapter, mainPrefs: Int, secondPrefs: String){
        ovm = ViewModelProviders.of(this).get(ObservationViewModel::class.java)
        ovm!!.getObservations(mainPrefs, secondPrefs).observe(this, Observer { it ->
            val list: List<Observation> = it!!
            val array: ArrayList<ObservationRV>? = ArrayList()

            list.forEach {
                val observationRV = ObservationRV()
                observationRV.id = it.observationid
                observationRV.species = it.species
                observationRV.rarity = it.rarity
                observationRV.date = it.date
                observationRV.time = it.time
                observationRV.note = it.note
                observationRV.image = it.image
                array?.add(observationRV)
            }

            viewAdapter.update(array)
        })
    }
}