package com.example.android_project_2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    private var navigationView: NavigationView? = null
    private var drawerLayout: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mToolbar = findViewById<View>(R.id.app_bar) as Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Location App"
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setHomeAsUpIndicator(R.drawable.menu)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)

        val view = navigationView!!.inflateHeaderView(R.layout.nav_header)

        navigationView!!.setNavigationItemSelectedListener { item ->
            UserMenuSelector(item)
            drawerLayout!!.closeDrawer(GravityCompat.END)
            true
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MapsFragment())
                .commit()
        }

        val hamburgerIcon = findViewById<ImageView>(R.id.toogle)
        hamburgerIcon.setOnClickListener {
            if (drawerLayout!!.isDrawerOpen(GravityCompat.END)) {
                drawerLayout!!.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout!!.openDrawer(GravityCompat.END)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                drawerLayout!!.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout!!.openDrawer(GravityCompat.START)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("ResourceType")
    private fun UserMenuSelector(item: MenuItem) {
        when (item.itemId) {
            R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Home", Toast.LENGTH_LONG).show()
            }

//            R.id.maps -> {
//                loadFragment(MapsFragment())
//                Toast.makeText(this, "Maps", Toast.LENGTH_LONG).show()
//            }

            R.id.places -> {
                loadFragment(PlacesFragment())
//                startActivity(Intent(this, LoginActivity::class.java))
                Toast.makeText(this, "Places", Toast.LENGTH_SHORT).show()
            }

            R.id.email -> {
                loadFragment(EmailFragment())
                Toast.makeText(this, "Email", Toast.LENGTH_SHORT).show()
            }

            R.id.about -> {
                loadFragment(AboutFragment())
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
