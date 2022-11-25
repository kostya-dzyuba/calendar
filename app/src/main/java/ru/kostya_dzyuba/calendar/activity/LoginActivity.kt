package ru.kostya_dzyuba.calendar.activity

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.InputType
import android.util.TypedValue
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kostya_dzyuba.calendar.CalendarService
import ru.kostya_dzyuba.calendar.databinding.ActivityLoginBinding
import ru.kostya_dzyuba.calendar.model.User

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (intent.getBooleanExtra("reset", false)) {
            preferences.edit { remove("token") }
        } else {
            val token = preferences.getString("token", null)
            if (token != null) {
                login(token)
                return
            }
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val service = CalendarService.createInstance(null)
        binding.login.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val response = service.login(
                    User(
                        binding.name.text.toString(),
                        binding.password.text.toString()
                    )
                )
                if (response.isSuccessful) {
                    val token = response.body()!!.string()
                    preferences.edit { putString("token", token) }
                    login(token)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Неверные данные",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.signup.setOnClickListener {
            if (binding.name.length() < 4) {
                Toast.makeText(
                    this,
                    "Логин должен быть не короче 4 символов",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.password.length() < 8) {
                Toast.makeText(
                    this,
                    "Пароль должен быть не короче 8 символов",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val editText = EditText(this)
                editText.hint = "Повторите пароль"
                editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                val layout = FrameLayout(this)
                layout.addView(editText)
                val padding =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        20f,
                        resources.displayMetrics
                    ).toInt()
                layout.setPadding(padding, 0, padding, 0)
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Введите пароль еще раз")
                    .setView(layout)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        if (binding.password.text.toString() != editText.text.toString()) {
                            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                val response = service.signUp(
                                    User(
                                        binding.name.text.toString(),
                                        binding.password.text.toString()
                                    )
                                )
                                if (response.isSuccessful) {
                                    val token = response.body()!!.string()
                                    preferences.edit { putString("token", token) }
                                    login(token)
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Логин уже занят",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
                dialog.show()
                editText.requestFocus()
                dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
    }

    private fun login(token: String) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("token", token)
        startActivity(intent)
        finish()
    }
}