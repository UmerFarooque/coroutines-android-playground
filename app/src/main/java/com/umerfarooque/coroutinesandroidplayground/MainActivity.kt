package com.umerfarooque.coroutinesandroidplayground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.umerfarooque.coroutinesandroidplayground.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        navController = navHostFragment?.findNavController()?.also {
            setupActionBarWithNavController(it)
        }
    }

    override fun onSupportNavigateUp() =
        navController?.navigateUp() ?: false || super.onSupportNavigateUp()
}
