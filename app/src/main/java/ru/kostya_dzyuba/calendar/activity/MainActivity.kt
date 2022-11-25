package ru.kostya_dzyuba.calendar.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import ru.kostya_dzyuba.calendar.CalendarService
import ru.kostya_dzyuba.calendar.MainViewModel
import ru.kostya_dzyuba.calendar.R
import ru.kostya_dzyuba.calendar.databinding.ActivityMainBinding
import ru.kostya_dzyuba.calendar.fragment.ListFragment
import ru.kostya_dzyuba.calendar.fragment.MainFragment

class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val token = intent.getStringExtra("token")
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.service = CalendarService.createInstance(token)
        setListeners()
        viewModel.tasks.observe(this) {
            binding.progress.visibility = View.GONE
            binding.refresh.isRefreshing = false
        }
        viewModel.loadTasks()
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        val fragment = when (tab.position) {
            0 -> MainFragment()
            1 -> ListFragment()
            else -> throw NotImplementedError("tab.position = ${tab.position}")
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {}
    override fun onTabReselected(tab: TabLayout.Tab) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        AlertDialog.Builder(this)
            .setTitle("Выход")
            .setMessage("Вы действительно хотите выйти из аккаунта?")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                startActivity(Intent(this, LoginActivity::class.java).putExtra("reset", true))
                finish()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
        return true
    }

    private fun setListeners() {
        binding.tabs.addOnTabSelectedListener(this)
        binding.refresh.setOnRefreshListener(viewModel::loadTasks)
    }
}