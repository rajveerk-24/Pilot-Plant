package com.sarrafenterprises.app7_nav

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sarrafenterprises.app7_nav.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener {menuItem ->
            when(menuItem.itemId){
                R.id.flow -> {
                    replaceFragement(flowFragement())
                    true
                }
                R.id.level -> {
                    replaceFragement(levelFragement())
                    true
                }
                R.id.temperature -> {
                    replaceFragement(temperatureFragement())
                    true
                }
                R.id.controlValve -> {
                    replaceFragement(cvFragement())
                    true
                }
                else -> false
            }

        }
        replaceFragement(flowFragement())
    }

        private fun replaceFragement(fragement:Fragment){

            supportFragmentManager.beginTransaction().replace(R.id.frame, fragement).commit()
    }
}