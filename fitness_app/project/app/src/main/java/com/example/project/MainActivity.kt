package com.example.project

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import java.lang.System.currentTimeMillis
import kotlin.math.roundToInt


class MainActivity : FragmentActivity() {

    // get the ViewModel
    private val mSharedViewModel: SharedViewModel by viewModels {
        SharedViewModelFactory((this.application as App).repository)
    }

    // variables needed for the step counter functionality
    private lateinit var mSensorManager: SensorManager
    private var mStepCounter: Sensor? = null

    // variables for the rotation gesture
    private var mGyroscope: Sensor? = null
    private val mThreshold = 5
    private var lastRotate = currentTimeMillis()
    private var cooldown = 1000 //wait at least 1 second between rotations

    // for audio alerts with step counter
    var mMediaPlayer : MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get the gyroscope sensor
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // set the custom swipe listener
        val view: View = findViewById(R.id.entire_view)
        view.setOnTouchListener(object: OnSwipeTouchListener(this) {
            override fun onSwipeRight() {
                super.onSwipeRight()
                // this will be listening on MainActivity so only start the step counter if swipe
                // occurred while MainFrag was displayed
                if(getCurrentFragment() is MainFrag) {
                    // if not on, turn it on
                    if(!mSharedViewModel.getCounterOn()) {
                        when {
                            ActivityCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED -> {
                                startCounter()
                            }
                            shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION) -> {
                                Toast.makeText(this@MainActivity, "Please Turn On Activity Recognition", Toast.LENGTH_SHORT).show()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    stepRequestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                                }
                            }
                            else -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    stepRequestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                                }
                            }
                        }
                    }
                }
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                // similarly, only turn off step counter if the swipe occurred with MainFrag displayed
                if(getCurrentFragment() is MainFrag) {
                    if (mSharedViewModel.getCounterOn()) {
                        stopCounter()
                    }
                }
            }
        })

        // do the Amplify initialization and sign in
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)
            Log.i("MyAmplifyApp", "Initialized Amplify")
            Amplify.Auth.signInWithWebUI(
                this,
                { result: AuthSignInResult ->
                    Log.i("AuthQuickStart", result.toString())


                    /**
                     * NOTE: The following line will successfully download room database files that
                     * the logged in user previously made through using the app but once
                     * downloaded, the app does not automatically update everything with the info
                     * from those files. We tried many ways to make that happen by getting new daos
                     * from the new database, refreshing the repository's connection to the new
                     * database, and reconnecting our LiveData and Flows. Nothing succeeded and so
                     * we undid many of those changes but left this here for reference and as a
                     * potential starting point if we were to revisit it.
                     */
//                    AWSHelper.loadFromBackup(application)
                },
                { error: AuthException -> Log.e("AuthQuickStart", error.toString()) }
            )
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }

    }

    // turning the step counter on
    private fun startCounter() {
        MediaPlayer.create(this@MainActivity, Settings.System.DEFAULT_NOTIFICATION_URI).start()
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        mSharedViewModel.setCounterOn(true)
        registerStepListener()
        val mainFrag = getCurrentFragment() as MainFrag
        mainFrag.counterOn()
    }

    // turning the step counter off
    private fun stopCounter() {
        mSensorManager.unregisterListener(stepListener)
        mSharedViewModel.setCounterOn(false)
        mMediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.wilhelm)
        mMediaPlayer!!.isLooping = false
        mMediaPlayer!!.start()
        val mainFrag = getCurrentFragment() as MainFrag
        mainFrag.counterOff()
    }

    private val stepRequestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {isGranted: Boolean ->
        if (isGranted) {
            startCounter()
        }
        else {
            Toast.makeText(this, "Please Turn On Activity Recognition", Toast.LENGTH_SHORT).show()
        }
    }

    // listener for the step counter
    private val stepListener: SensorEventListener = object : SensorEventListener {

        override fun onSensorChanged(sensorEvent: SensorEvent) {

            val curNumSteps = mSharedViewModel.getNumSteps()

            // get all the current user info
            val user: User? = mSharedViewModel.userInfo.value

            // increment the step count
            val steps = sensorEvent.values[0].roundToInt()
            val newSteps = curNumSteps + steps
            user!!.steps = newSteps

            // save the change to the step numbers
            mSharedViewModel.updateUser(user)
            mSharedViewModel.setNumSteps(newSteps)

            if(getCurrentFragment() is MainFrag) {
                val mainFrag = getCurrentFragment() as MainFrag
                mainFrag.updateStepCounter(newSteps)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {
            // this is required to be here for SensorEventListener but don't need to do anything
        }
    }


    private val rotateListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {

            // get the rotation rate around the vertical axis (this will depend on the orientation)
            // when in portrait mode this will be the y axis (sensorEvent.values[1])
            // when in landscape mode this will be the x axis (sensorEvent.values[0])
            val rot =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) sensorEvent.values[1].toDouble() else sensorEvent.values[0].toDouble()

            // compute the squared magnitude of the rotation
            val sq_magnitude = Math.pow(rot, 2.0)

            // check if cooldown has passed and squared magnitude is greater than some threshold
            var now = currentTimeMillis()
            if ((now >= (lastRotate + cooldown)) && (sq_magnitude > mThreshold)) {
                if (!mSharedViewModel.getCounterOn()) startCounter() else stopCounter()
                lastRotate = now
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {
            // this is required to be here for SensorEventListener but don't need to do anything
        }
    }

    // this will return the currently displayed fragment
    // found this here: https://stackoverflow.com/questions/9294603/how-do-i-get-the-currently-displayed-fragment
    private fun getCurrentFragment(): Fragment {
        return supportFragmentManager.fragments.last()
    }

    override fun onResume() {
        super.onResume()
        if (mSharedViewModel.getCounterOn()){
            registerStepListener()
        }

        mSensorManager.registerListener(
            rotateListener,
            mGyroscope,
            SensorManager.SENSOR_DELAY_NORMAL
        )

    }

    private fun registerStepListener(){
        mSensorManager.registerListener(
            stepListener,
            mStepCounter,
            SensorManager.SENSOR_DELAY_NORMAL,
            0
        )
    }

    override fun onPause() {
        super.onPause()
        if (mSharedViewModel.getCounterOn()){
            mSensorManager.unregisterListener(stepListener)
        }

        mSensorManager.unregisterListener(rotateListener)

        // onPause could potentially be called before the Cognito login is complete
        // this will make sure that backupRoom is not called unless the login is complete
        if (Amplify.Auth.currentUser != null) {
            AWSHelper.backupRoom(application)
        }
    }

    override fun onStop() {
        super.onStop()
        if (mMediaPlayer != null){
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    // for making sure the ScrollView is scrolled all the way up when the fragment switches
    fun scrollToTop() {
        val scrollView: ScrollView = findViewById(R.id.entire_view)
        scrollView.scrollTo(0,0)
    }
}