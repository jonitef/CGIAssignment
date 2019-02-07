package com.example.joni.assignment

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.example.joni.assignment.fragments.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(R.id.fragment_container, MainFragment()).commit()
    }

    // create sorting options, get the last used sorting methods
    // from SharedPreferences and check the right settings
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val mainCheckedMenuItem = menu!!.findItem(PreferenceManager.getDefaultSharedPreferences(this).getInt("mainChoiceIsChecked", R.id.newest))
        val secondaryCheckedMenuItem = menu.findItem(PreferenceManager.getDefaultSharedPreferences(this).getInt("secondaryChoiceIsChecked", R.id.all))
        mainCheckedMenuItem.isChecked = true
        secondaryCheckedMenuItem.isChecked = true
        return true
    }
}
