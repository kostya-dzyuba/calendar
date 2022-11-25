package ru.kostya_dzyuba.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kostya_dzyuba.calendar.model.Task
import java.time.LocalDate

class MainViewModel : ViewModel() {
    val dayTasks = MutableLiveData<MutableList<Task>>()
    val tasks = MutableLiveData<MutableList<Task>>()
    var selected = MutableLiveData(LocalDate.now())
    lateinit var service: CalendarService
    private val scope = CoroutineScope(Dispatchers.Main)

    fun loadTasks() {
        scope.launch {
            tasks.value = service.getTasks() as MutableList<Task>
        }
    }

    fun check(id: Long) {
        val index = tasks.value!!.indexOfFirst { it.id == id }
        val task = tasks.value!![index].copy(completed = !tasks.value!![index].completed)
        scope.launch {
            service.updateTask(id, task)
            tasks.value!![index] = task
            tasks.notifyObservers()
        }
    }

    fun delete(id: Long) {
        scope.launch {
            service.deleteTask(id)
            tasks.value!!.removeIf { it.id == id }
            tasks.notifyObservers()
        }
    }

    fun add(name: String) {
        scope.launch {
            var task = Task(name = name, date = selected.value!!)
            task = task.copy(id = service.addTask(task))
            tasks.value!!.add(task)
            tasks.notifyObservers()
        }
    }

    fun filter() {
        dayTasks.value = tasks.value!!.filter {
            it.date.toEpochDay() == selected.value!!.toEpochDay()
        } as MutableList<Task>
    }

    private fun <T> MutableLiveData<T>.notifyObservers() {
        value = value
    }
}