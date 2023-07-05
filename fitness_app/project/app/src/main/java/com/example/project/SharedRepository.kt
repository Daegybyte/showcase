package com.example.project

import android.location.Location
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SharedRepository (private val userDao: UserDao, private val dbWeatherDao: DBWeatherDao) {
    // user flow
    val userInfo: Flow<User> = userDao.getUser()
    // weather flow
    val aveTemp: Flow<Double> = dbWeatherDao.getAveTemp()
    // weather data
    val liveWeather = MutableLiveData<JsonWeather>()

    // for formatted weather data returned by weather API
    var mJsonWeatherData: JsonWeather? = null

    // for step counter
    var rNumSteps: Int = 0
    var rCounterOn = false

    @WorkerThread
    suspend fun updateUser(user: User) {
        userDao.insert(user)    // <<--- inserts a new row for the user on every update
    }

    private var mJsonString: String? = null

    // gets the current weather data from the OpenWeatherAPI
    fun getWeather(location: Location) {

        mScope.launch(Dispatchers.IO) {
            getJsonWeatherString(location)

            mJsonString?.let {
//                Log.d("JSON", mJsonString.toString())
                val wd = (Gson()).fromJson(it, JsonWeather::class.java)

                mJsonWeatherData = wd

//                Log.d("JsonWeather", wd.toString())

                liveWeather.postValue(wd)

                val dbWeatherData = DBWeather(
                    wd.id,
                    wd.name,
                    wd.coord.lat,
                    wd.coord.lon,
                    wd.weather[0].main,
                    wd.weather[0].description,
                    wd.weather[0].icon,
                    wd.main.temp,
                    wd.sys.country
                )

//                Log.d("dbWeather", dbWeatherData.toString())


                insertWeather(dbWeatherData)
            }
        }
    }

    @WorkerThread
    suspend fun insertWeather(dbWeather: DBWeather){
        dbWeatherDao.insert(dbWeather)
    }

    // handles communication with OpenWeatherMap API
    @WorkerThread
    suspend fun getJsonWeatherString(location: Location){
        val mLatitude = location.latitude
        val mLongitude = location.longitude

        val weatherDataURL = NetworkUtils.buildURLFromString(mLatitude, mLongitude)

        weatherDataURL?.let{
            val jsonWeatherData = NetworkUtils.getDataFromURL(it)

            jsonWeatherData?.let{
                mJsonString = jsonWeatherData
            }
        }
    }

    fun getCityId() : Int {
        return mJsonWeatherData!!.id
    }

    // for creating/getting repository as a singleton
    companion object {
        private var mInstance: SharedRepository? = null
        private lateinit var mScope: CoroutineScope
        @Synchronized
        fun getInstance(userDao: UserDao,
                        dbWeatherDao: DBWeatherDao,
                        scope: CoroutineScope
        ): SharedRepository {
            mScope = scope
            return mInstance?: synchronized(this){
                val instance = SharedRepository(userDao, dbWeatherDao)
                mInstance = instance
                instance
            }
        }
    }
}