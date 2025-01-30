package com.sukata.bjj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sukata.bjj.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sukata.bjj.ui.alumnos.AlumnosFragment
import com.sukata.bjj.ui.cronogramas.CronogramaFragment
import com.sukata.bjj.ui.fragments.PlanesFragment
import com.sukata.bjj.ui.tecnicas.TecnicasFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val tecnicasFragment = TecnicasFragment()
    private val alumnosFragment = AlumnosFragment()
    private val cronogramasFragment = CronogramaFragment()
    private val planesFragment = PlanesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_tecnicas -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, tecnicasFragment)
                        .commit()
                    true
                }
                R.id.nav_alumnos -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, alumnosFragment)
                        .commit()
                    true
                }
                R.id.nav_cronogramas -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, cronogramasFragment)
                        .commit()
                    true
                }
                R.id.nav_planes -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, planesFragment)
                        .commit()
                    true
                }
                else -> false
            }
        }

        // Establecer el fragmento inicial
        if (savedInstanceState == null){
            binding.bottomNavigation.selectedItemId = R.id.nav_cronogramas
        }
    }
}
