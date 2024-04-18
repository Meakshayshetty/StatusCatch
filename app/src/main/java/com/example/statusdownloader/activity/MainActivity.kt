package com.example.statusdownloader.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.statusdownloader.R
import com.example.statusdownloader.databinding.ActivityMainBinding
import com.example.statusdownloader.fraagment.DownloadFragment
import com.example.statusdownloader.fraagment.StorieFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val binding :ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var bottomNav :BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        val bottomNav = binding.bottomNavigation
        binding.bottomNavigation.itemIconTintList =null
        supportFragmentManager.beginTransaction()
            .replace(R.id.flMain, StorieFragment())
            .commit()
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.status ->{
                    Log.e("fragmentOpen","0ne")
                    loadFragment(StorieFragment())
                }
                R.id.downlaod -> {
                    Log.e("fragmentOpen","two")

                    loadFragment(DownloadFragment())
                }
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.flMain, fragment)
            .commit()
    }
}