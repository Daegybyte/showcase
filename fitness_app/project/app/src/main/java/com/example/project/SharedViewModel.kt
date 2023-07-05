package com.example.project

import android.location.Location
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class SharedViewModel(private val repository: SharedRepository) : ViewModel() {
    // user livedata
    val userInfo: LiveData<User> = repository.userInfo.asLiveData()
    // weather livedata
    val aveTemp: LiveData<Double> = repository.aveTemp.asLiveData()

    private val liveWeather: LiveData<JsonWeather> = repository.liveWeather

    fun updateUser(user: User) = viewModelScope.launch {
        repository.updateUser(user)
    }

    fun getWeather(location: Location) {
        repository.getWeather(location)
    }

    fun getCityId(): Int {
        return repository.getCityId()
    }

    val weatherData: LiveData<JsonWeather>
        get() = liveWeather

    fun getCounterOn() : Boolean {
        return repository.rCounterOn
    }
    fun setCounterOn(counterOn : Boolean){
        repository.rCounterOn = counterOn
    }

    fun getNumSteps() : Int {
        return repository.rNumSteps
    }
    fun setNumSteps(numSteps: Int){
        repository.rNumSteps = numSteps
    }

}

// for creating an instance of our ViewModel using Repository as parameter
class SharedViewModelFactory(private val repository: SharedRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) { // check if the model class is the same
            @Suppress("UNCKECKED_CAST")
            return SharedViewModel(repository) as T // return the view model
        } else {
            throw IllegalArgumentException("Unknown ViewModel class") // throw an exception
        }
    }
}