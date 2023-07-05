package com.example.project

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class ProfileFrag : Fragment(), View.OnClickListener {

    // getting the SharedViewModel
    private val mSharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory((requireActivity().application as App).repository)
    }

    // All of the elements that will need to be accessed later
    private var mEtFirstName: EditText? = null
    private var mEtLastName: EditText? = null
    private var mNpAge: NumberPicker? = null
    private var mNpHeight: NumberPicker? = null
    private var mNpWeight: NumberPicker? = null
    private var mSpActivityLevel: Spinner? = null
    private var mRgSex: RadioGroup? = null
    private var mRbMale: RadioButton? = null
    private var mRbFemale: RadioButton? = null
    private var mTvLocation: TextView? = null
    private var mIvProfilePic: ImageView? = null

    private var mProfilePicPath : String? = null

    // these will be used to get the phone's location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLatitude = 0.0
    private var mLongitude = 0.0

    // this will be used to make the observer not reload the user info when it shouldn't
    // because that could overwrite any unsaved changes that the user has made
    private var observerAlreadyRan: Boolean = false

    @SuppressLint("MissingPermission")
    override fun onCreateView( // this is where the view is created
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Getting all of the elements into the member variables
        mEtFirstName = view.findViewById(R.id.etFirstName)
        mEtLastName = view.findViewById(R.id.etLastName)
        mNpAge = view.findViewById(R.id.np_age)
        mNpHeight = view.findViewById(R.id.np_height)
        mNpWeight = view.findViewById(R.id.np_weight)
        mSpActivityLevel = view.findViewById(R.id.spActivityLevel)
        mRgSex = view.findViewById(R.id.radio_sex)
        mRbMale = view.findViewById(R.id.radio_male)
        mRbFemale = view.findViewById(R.id.radio_female)
        mTvLocation = view.findViewById(R.id.tvLocation)
        mIvProfilePic = view.findViewById(R.id.ivProfilePic)

        // attach the observer
        mSharedViewModel.userInfo.observe(viewLifecycleOwner, userObserver)

        // Populate the age NumberPicker
        mNpAge!!.minValue = 12
        mNpAge!!.maxValue = 99
        mNpAge!!.value = 25
        mNpAge!!.wrapSelectorWheel = false

        // Populate the height NumberPicker
        mNpHeight!!.minValue = 140
        mNpHeight!!.maxValue = 220
        mNpHeight!!.value = 160
        mNpHeight!!.wrapSelectorWheel = false

        // Populate the weight NumberPicker
        mNpWeight!!.minValue = 45
        mNpWeight!!.maxValue = 150
        mNpWeight!!.value = 75
        mNpWeight!!.wrapSelectorWheel = false

        // Populate the activity level spinner
        val activityLevels = arrayOf<String?>("Sedentary", "Mild", "Moderate", "Heavy", "Extreme")
        val arrayAdapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(view.context, R.layout.spinner_list_profile, activityLevels)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list_profile)
        mSpActivityLevel!!.adapter = arrayAdapter

        // Get the FusedLocationProviderClient for GPS
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

        // Adding functionality to the location button
        val btnLocation: Button = view.findViewById(R.id.btnLocation)
        btnLocation.setOnClickListener(this)

        // Adding functionality to the picture button
        val picButton : Button = view.findViewById(R.id.btnPic)
        picButton.setOnClickListener(this)

        // Adding functionality to the save button
        val saveButton: Button = view.findViewById(R.id.btnSave)
        saveButton.setOnClickListener(this)

        // make sure the ScrollView that holds everything is scrolled up to the top
        (activity as MainActivity).scrollToTop()

        return view
    }


    private val userObserver: Observer<User> =
        // Update the UI when any of the user info changes
        Observer { userInfo ->
            // we are using observerAlreadyRun to make sure that this does not run when there
            // are unsaved changes that we don't want to overwrite
            if (userInfo != null && !observerAlreadyRan) {
                mEtFirstName!!.setText(userInfo.firstName)
                mEtLastName!!.setText(userInfo.lastName)
                mNpAge!!.value = userInfo.age
                mNpHeight!!.value = userInfo.height
                mNpWeight!!.value = userInfo.weight
                mSpActivityLevel!!.setSelection(userInfo.activityLevel)
                mRbMale!!.isChecked = userInfo.isMale
                mRbFemale!!.isChecked = !userInfo.isMale
                mTvLocation!!.text = userInfo.location
                mProfilePicPath = userInfo.imagePath
                val bMap = BitmapFactory.decodeFile(mProfilePicPath)
                mIvProfilePic!!.setImageBitmap(bMap)
            }
            observerAlreadyRan = true
        }


    // handles any of the buttons being clicked
    @SuppressLint("MissingPermission")
    override fun onClick(view: View) {
        when(view.id) {
            R.id.btnLocation -> {
                // check location permission and get current location in string form (city, state, and country)
                when {
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                            getLocation()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                        Toast.makeText(activity, "Please Turn On Location Permissions", Toast.LENGTH_SHORT).show()
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    else -> {
                        requestPermissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }

            }

            R.id.btnPic -> {
                // start intent to take photo for profile
                val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                try{
                    cameraLauncher.launch(camIntent)
                }catch(ex: ActivityNotFoundException){
                    Toast.makeText(activity, "Error Opening Camera", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.btnSave -> {
                // save all user info currently in the profile form

                // check to make sure there are values for all fields (all fields not checked have default values)
                if (mTvLocation?.text.toString() != ""
                    && mEtFirstName?.text.toString() != ""
                    && mEtLastName?.text.toString() != ""
                    && mProfilePicPath != null) {

                    // set steps equal to NumSteps (or 0 if null)
                    // This prevents the step counter from being reset if new user is entered into table
                    val steps = mSharedViewModel.getNumSteps()

                    // making a user
                    val userInfo = User(
                        mEtFirstName!!.text.toString(),
                        mEtLastName!!.text.toString(),
                        mNpAge!!.value,
                        mNpHeight!!.value,
                        mNpWeight!!.value,
                        mSpActivityLevel!!.selectedItemPosition,
                        mRbMale!!.isChecked,
                        mTvLocation!!.text.toString(),
                        mProfilePicPath!!,
                        steps
                    )

                    // make SharedViewModel save the user info
                    mSharedViewModel.updateUser(userInfo)

                    // this sleep is here because MainFrag was being loaded before the user info was
                    // able to be sent to Room and then flow back to the Observer within MainFrag
                    // the check for existing user info in MainFrag was failing previously and sending
                    // the user back to ProfileFrag as a result
                    Thread.sleep(100)

                    // and switch to the main fragment
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.frag_container, MainFrag(), "Main Fragment")
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                // if the profile is not fully filled in
                else {
                    Toast.makeText(activity, "Profile Incomplete! Try again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // converts latitude and longitude into city, state, country
    // adapted from https://stackoverflow.com/questions/59095837/convert-from-latlang-to-address-using-geocoding-not-working-android-kotlin
    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(context)
        val list = geocoder.getFromLocation(lat, lng, 1)
        return list[0].locality + ", " + list[0].adminArea + ", " + list[0].countryName
    }

    // to be run when the add profile picture button is clicked
    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {

            val ivProfilePic : ImageView = requireView().findViewById(R.id.ivProfilePic)

            val extras = result.data!!.extras
            val mProfilePic = extras!!["data"] as Bitmap?

            //Open a file and write to it
            if (isExternalStorageWritable) {
                mProfilePicPath = saveImage(mProfilePic)
                ivProfilePic.setImageBitmap(mProfilePic)
            } else {
                Toast.makeText(activity, "External storage not writable.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // save the bitmap from the camera intent to the phone's storage and get the file path
    @SuppressLint("SimpleDateFormat")
    private fun saveImage(finalBitmap: Bitmap?): String {
        val root = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fname = "Thumbnail_$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
//            Toast.makeText(activity, "file saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    // get the device's current location and translate it to a string of city, state, and country
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        mTvLocation!!.text = "Loading..."

        val priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY //set priority to high accuracy
        val cancellationTokenSource = CancellationTokenSource() // cancellationtokensource is used to cancel the task

        mFusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
            .addOnSuccessListener { location: Location? ->
                // getting the last known or current location
                mLatitude = location!!.latitude
                mLongitude = location.longitude

//                Toast.makeText(activity, "Latitude:$mLatitude\nLongitude:$mLongitude", Toast.LENGTH_SHORT).show()

                val address = getAddress(mLatitude, mLongitude) // getting the address from the location

                mTvLocation!!.text = address // setting the address to the textview
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Failed on getting current location", Toast.LENGTH_SHORT).show()
            }
    }

    //check if the app has permission to access the location
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {isGranted: Boolean ->
        if (isGranted) {
            getLocation()
        }
        else {
            Toast.makeText(activity, "Please Turn On Location Permissions", Toast.LENGTH_SHORT).show()
        }
    }

}