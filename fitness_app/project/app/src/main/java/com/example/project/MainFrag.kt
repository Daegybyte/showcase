package com.example.project

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.text.DecimalFormat
import kotlin.math.roundToInt


@SuppressLint("MissingPermission")
class MainFrag : Fragment(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    // getting the SharedViewModel
    private val mSharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory((requireActivity().application as App).repository)
    }

    // all of the elements that will need to be accessed later
    private var mTvUsername: TextView? = null
    private var mIvThumbnail: ImageView? = null
    private var mTvBMR: TextView? = null
    private var mTvActivityLevel: TextView? = null
    private var mTvSteps: TextView? = null
    private var mTvStepsLabel : TextView? = null
    private var mTvArrowLeft: TextView? = null
    private var mTvArrowRight: TextView? = null
    private var mBoxWeather: RelativeLayout? = null
    private var mTvWeather: TextView? = null

    // These will be used to get the phone's location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLatitude = 0.0
    private var mLongitude = 0.0

    // average of the temperatures from all weather requests by the user
    private var mHistoricalAve: String? = null

    private var mUserId: Int? = null

    // String Array for the activity levels
    private var mActivityLevels = arrayOf<String?>(
        "Change Activity Level",
        "Sedentary",
        "Mild",
        "Moderate",
        "Heavy",
        "Extreme"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        Log.d("MainFrag", "onCreateView")

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)
//        Log.d("MainFrag", "onCreateView: view inflated successfully")

        // get elements
        mTvUsername = view.findViewById(R.id.tvUsername)
        mIvThumbnail = view.findViewById(R.id.ivThumbnail)
        mTvBMR = view.findViewById(R.id.tvBMR)
        mTvActivityLevel = view.findViewById(R.id.tvActivityLevel)

        // get views for step counter
        mTvSteps = view.findViewById(R.id.tvSteps)
        mTvStepsLabel = view.findViewById(R.id.tvStepsLabel)
        mTvArrowLeft = view.findViewById(R.id.tvArrowLeft)
        mTvArrowRight = view.findViewById(R.id.tvArrowRight)

        // attach observers
        mSharedViewModel.userInfo.observe(viewLifecycleOwner, userObserver)
        mSharedViewModel.weatherData.observe(viewLifecycleOwner, liveWeatherObserver)
        mSharedViewModel.aveTemp.observe(viewLifecycleOwner, flowObserver)


        // Fill the dropdown for activity level
        val spinner: Spinner = view.findViewById(R.id.spActivityLevel)
        Log.d("MainFrag", "onCreateView: spinner found successfully")

        val arrayAdapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(view.context, R.layout.spinner_list_main, mActivityLevels)
        Log.d("MainFrag", "onCreateView: arrayAdapter created successfully")
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list_main)
        spinner.adapter = arrayAdapter
        Log.d("MainFrag", "onCreateView: spinner populated successfully")

        spinner.onItemSelectedListener = this
        Log.d("MainFrag", "onCreateView: onItemSelectedListener added successfully")

        // Add functionality to the edit profile button
        val btnEditProfile: Button = view.findViewById(R.id.btnEditProfile)
        btnEditProfile.setOnClickListener(this)

        // Add functionality to the step counter reset button
        val btnReset: Button = view.findViewById(R.id.btnResetSteps)
        btnReset.setOnClickListener(this)

        // Put the correct text in Step Counter TextViews based on the Bool in the repository
        if (mSharedViewModel.getCounterOn()) counterOn() else counterOff()

        // Get the FusedLocationProviderClient for GPS
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)
        Log.d("MainFrag", "onCreateView: got the FusedLocationProviderClient")

        // Add functionality to the Hikes button
        val btnHikes: Button = view.findViewById(R.id.btnHikes)
        btnHikes.setOnClickListener(this)

        // these will both be needed to make more details appear on click
        mBoxWeather = view.findViewById(R.id.boxWeather)
        mTvWeather = view.findViewById(R.id.tvWeather)

        // Add functionality to the Weather button
        val btnWeather: Button = view.findViewById(R.id.btnWeather)
        btnWeather.setOnClickListener(this)

        // Add functionality to the Details Weather button
        val btnMoreWeather: Button = view.findViewById(R.id.btnMoreWeather)
        btnMoreWeather.setOnClickListener(this)

        // make sure the ScrollView that holds everything is scrolled up to the top
        (activity as MainActivity).scrollToTop()

        return view
    }

    // Update the UI when any of the user info changes
    private val userObserver: Observer<User> =
        Observer { userInfo ->
            if (userInfo != null) {
                mUserId = userInfo.id
                Log.i("USER ID", mUserId.toString())

                mTvUsername!!.text = "${userInfo.firstName} ${userInfo.lastName}"

                val bMap = BitmapFactory.decodeFile(userInfo.imagePath)
                mIvThumbnail!!.setImageBitmap(bMap)

                val bmr = BMR()
                val baseBMR = bmr.calculateBMR(
                    userInfo.weight,
                    userInfo.height,
                    userInfo.age,
                    userInfo.isMale
                )
                val adjustedBMR = bmr.calculateAdjustedBMR(baseBMR, userInfo.activityLevel)
                mTvBMR!!.text = "$adjustedBMR kcal/day"

                val activityLevels = arrayOf<String?>("Sedentary", "Mild", "Moderate", "Heavy", "Extreme")
                mTvActivityLevel!!.text = activityLevels[userInfo.activityLevel]

                // update the spinner
                for (i in 1 until mActivityLevels.size){
                    mActivityLevels[i] = activityLevels[i-1] + " (" + bmr.calculateAdjustedBMR(baseBMR, i-1) + " kcal/day)"
                }

                // update steps member variable and steps TextView
                mTvSteps!!.text = userInfo.steps.toString()
                Log.i("STEPS", userInfo.steps.toString())
            }

            // if there is no userInfo yet then immediately go to the ProfileFrag
            else {
                Log.d("MainFrag", "no existing user was found")
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.frag_container, ProfileFrag(), "Profile Fragment")
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

    // update the displayed weather info if it changes
    private val liveWeatherObserver: Observer<JsonWeather> =
        Observer { weatherData ->
            weatherData?.let{

                val df = DecimalFormat("#.##")
                val tempTemp = df.format(it.main.temp - 273.15)
                val temp = (it.main.temp - 273.15).roundToInt()

                var outStr = "Current Weather for\n"
                outStr += it.name + ", " + it.sys.country + "\n"
                outStr += "Temp: $temp C\n"
                outStr += "Feels Like: " + (it.main.feels_like - 273.15).roundToInt() + " C\n"
                outStr += "Weather: " + it.weather[0].main
                outStr += "\n\nHistorical Avg\n(from Room):\n"

                outStr += if (mHistoricalAve != null){
                    "$mHistoricalAve C"
                } else {
                    "$tempTemp C"
                }

                // add weather data to textview
                mTvWeather!!.text = outStr
            }
        }

    // updates the average temperature of all weather requests when it changes
    private val flowObserver: Observer<Double> =
        Observer { aveTemp ->

            aveTemp?.let{
                val df = DecimalFormat("#.##")
                mHistoricalAve = df.format(aveTemp - 273.15).toString()
            }
        }

    // handles the "change activity level" spinner being changed
    override fun onItemSelected(parent: AdapterView<*>?, other: View?, pos: Int, id: Long) {
        Log.d("SpinnerListener", "Selected Something From the Spinner")
        val spString = parent!!.getItemAtPosition(pos).toString()
        Log.d("SpinnerListener", spString)

        // if anything other than "Change Activity Level" is selected then update the user's info and display
        if (spString != "Change Activity Level"){
            Log.d("SpinnerListener", "SUCCESS!")

            // get TextViews
            val tvBmr : TextView = view?.findViewById(R.id.tvBMR) as TextView
            val tvActLvl : TextView = view?.findViewById(R.id.tvActivityLevel) as TextView

            // split the string and put into Textviews
            val strArr = spString.split(" ")

            tvActLvl.text = strArr[0]

            val strBmr = strArr[1].removePrefix("(") + " " + strArr[2].dropLast(1)
            tvBmr.text = strBmr

            // get all the current user info
            val user: User? = mSharedViewModel.userInfo.value
            // update only the activity level
            user!!.activityLevel = pos-1
            // save the change to the activity level
            mSharedViewModel.updateUser(user)

            // reset spinner display to "Change Activity Level"
            parent.setSelection(0)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing (this method is required by AdapterView.OnItemSelectedListener)
    }

    // handles any of the buttons being clicked
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnEditProfile -> {
                // changes the ProfileFrag to be displayed
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.frag_container, ProfileFrag(), "Profile Fragment")
                transaction.addToBackStack(null)
                transaction.commit()
                Log.d("MainFrag", "onCreateView: edit profile button clicked")
            }
            R.id.btnResetSteps -> {
                // reset steps variable in repository to 0
                mSharedViewModel.setNumSteps(0)

                // reset user steps to 0
                val user: User? = mSharedViewModel.userInfo.value
                user!!.steps = 0
                mSharedViewModel.updateUser(user)

                // reset the step counter TextView
                mTvSteps!!.text = "0"
            }
            R.id.btnHikes -> {
                // check location permissions and get hikes when allowed
                when {
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                        getHikes()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                        Toast.makeText(activity, "Please Turn On Location Permissions", Toast.LENGTH_SHORT).show()
                        hikesRequestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    else -> {
                        hikesRequestPermissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            }
            R.id.btnWeather -> {
                // check location permissions and get the weather info when allowed
                when {
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                        getWeather()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                        Toast.makeText(activity, "Please Turn On Location Permissions", Toast.LENGTH_SHORT).show()

                        weatherRequestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    else -> {
                        weatherRequestPermissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            }
            R.id.btnMoreWeather -> {
                // open the detailed weather forecast
                openWeatherIntent()
            }
        }
    }


    // takes the user to Google Maps and searches for hikes near their location
    @SuppressLint("MissingPermission")
    private fun getHikes() {
        val priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
        val cancellationTokenSource = CancellationTokenSource()

        mFusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
            .addOnSuccessListener { location: Location? ->
                // getting the last known or current location
                mLatitude = location!!.latitude
                mLongitude = location.longitude

                val searchUri =
                    Uri.parse("geo:$mLatitude, $mLongitude?q=" + Uri.encode("hiking trails"))
                Log.d("MainFrag", "onViewCreated: searchUri created successfully")

                //Create the implicit intent
                val mapIntent = Intent(Intent.ACTION_VIEW, searchUri)
                Log.d("MainFrag", "onViewCreated: mapIntent created successfully")

                //If there's an activity associated with this intent, launch it
                try {
                    startActivity(mapIntent)
                    Log.d("MainFrag", "onViewCreated: startActivity(mapIntent) called successfully")
                } catch (ex: ActivityNotFoundException) {
                    Log.d("MainFrag", "onViewCreated: startActivity(mapIntent) failed")
                    Toast.makeText(
                        activity,
                        "There's no app that can handle this action.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //handle errors here
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Failed on getting current location", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    // uses the OpenWeatherMap API to get the weather at the current location
    private fun getWeather(){
        // make weather information visible
        mBoxWeather!!.visibility = View.VISIBLE
        mTvWeather!!.text = "Loading..."

        // get the current location

        val priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
        val cancellationTokenSource = CancellationTokenSource()

        mFusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
        .addOnSuccessListener { location: Location ->
            // get the weather for the current location
            mSharedViewModel.getWeather(location)

        }
        .addOnFailureListener {
            Toast.makeText(activity, "Failed on getting current location", Toast.LENGTH_SHORT).show()
        }
    }

    // takes the user to an online extended weather forecast for their location
    private fun openWeatherIntent() {
        val cityId = mSharedViewModel.getCityId()
        val url = "https://openweathermap.org/city/$cityId"
        val weatherIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        //If there's an activity associated with this intent, launch it
        try {
            startActivity(weatherIntent)
            Log.d("Main_ButtonFrag", "onViewCreated: startActivity(weatherIntent) called successfully")
        } catch (ex: ActivityNotFoundException) {
            Log.e("Main_ButtonFrag", "onViewCreated: startActivity(mapIntent) failed")
            //If there's no activity associated with this intent, display an error message
            Toast.makeText(activity, "No activity found to handle this intent", Toast.LENGTH_SHORT).show()
        }
    }


    private val hikesRequestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {isGranted: Boolean ->
        if (isGranted) {
            getHikes()
        }
        else {
            Toast.makeText(activity, "Please Turn On Location Permissions", Toast.LENGTH_SHORT).show()
        }
    }

    private val weatherRequestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {isGranted: Boolean ->
        if (isGranted) {
            getWeather()
        }
        else {
            Toast.makeText(activity, "Please Turn On Location Permissions", Toast.LENGTH_SHORT).show()
        }
    }

    // making UI changes when the step counter is turned on
    fun counterOn() {
        mTvStepsLabel!!.text = "STEP COUNTER: ON (swipe off)"
        mTvArrowRight!!.visibility = View.GONE
        mTvArrowLeft!!.visibility = View.VISIBLE
    }

    // making UI changes when the step counter is turned off
    fun counterOff(){
        mTvStepsLabel!!.text = "STEP COUNTER: off (swipe ON)"
        mTvArrowRight!!.visibility = View.VISIBLE
        mTvArrowLeft!!.visibility = View.GONE
    }

    // making UI changes when the step count changes
    fun updateStepCounter(steps: Int){
        mTvSteps!!.text = steps.toString()
    }

}