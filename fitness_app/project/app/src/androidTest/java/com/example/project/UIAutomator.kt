import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.*
import junit.framework.Assert.assertTrue
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep


private const val PACKAGE = "com.example.project"
private const val LAUNCH_TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 30)
class ChangeTextBehaviorTest2 {

    private lateinit var device: UiDevice

    private fun click_btnEdit() {
        val btnEditProfile =
            device.findObject(UiSelector().resourceId("$PACKAGE:id/btnEditProfile"))
        btnEditProfile.click()
    }

    private fun check_btnEdit(): Boolean {
        val btnEditProfile =
            device.findObject(UiSelector().resourceId("$PACKAGE:id/btnEditProfile"))
        return btnEditProfile.exists()
    }

    private fun scrollDown() {
//        scroll from the bottom right corner to the top right corner
        device.swipe(1000, 2000, 1000, 0, 10)
    }

    @Before
    fun startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Start from the home screen
        device.pressHome()

        // Wait for launcher
        val launcherPackage: String = device.launcherPackageName
        assertThat(launcherPackage, notNullValue())
        device.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            LAUNCH_TIMEOUT
        )

        // Launch the app
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(
            PACKAGE
        ).apply {
            // Clear out any previous instances
            this!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(
            Until.hasObject(By.pkg(PACKAGE).depth(0)),
            LAUNCH_TIMEOUT
        )

    }

    @Test
    fun test_btnHike() {
        val btn = device.findObject(UiSelector().textContains("Hike"))
        if (btn.exists()) {
            btn.click()
            device.wait(
                Until.hasObject(By.pkg(PACKAGE).depth(0)),
                LAUNCH_TIMEOUT
            )
        }
        device.pressBack()
    }

    @Test
    fun test_btnWeather() {
        val btn = device.findObject(UiSelector().textContains("Weather"))
        if (btn.exists()) {
            btn.click()
            device.wait(
                Until.hasObject(By.pkg(PACKAGE).depth(0)),
                LAUNCH_TIMEOUT
            )
        }
        device.pressBack()
    }

    @Test
    fun test_etFirstName() {
        if (check_btnEdit()) {
            click_btnEdit()
        }

        val name = "First"

        val etFirstName = device.findObject(UiSelector().resourceId("$PACKAGE:id/etFirstName"))
        etFirstName.click()
        etFirstName.setText(name)
        device.pressBack()
        //scroll to the bottom of the screen
        scrollDown()
        scrollDown()
//        device.swipe(100, 2000, 100, 0, 10)
        val btnSave = device.findObject(UiSelector().resourceId("$PACKAGE:id/btnSave"))
        btnSave.click()
    }


    @Test
    fun test_etLastName() {
        if (check_btnEdit()) {
            click_btnEdit()
        }

        val name = "Last"

        val etLastName = device.findObject(UiSelector().resourceId("$PACKAGE:id/etLastName"))
        etLastName.click()
        etLastName.setText(name)
        device.pressBack()
        //scroll to the bottom of the screen
        scrollDown()
        scrollDown()

//        swipe from the bottom left corner to the top right corner
//        device.swipe(100, 2000, 100, 0, 10)
        val btnSave = device.findObject(UiSelector().resourceId("$PACKAGE:id/btnSave"))
        btnSave.click()
    }

    @Test
    fun test_btnSave() {
        val btnEditProfile =
            device.findObject(UiSelector().resourceId("$PACKAGE:id/btnEditProfile"))
        btnEditProfile.click()

//        get device height and width
//        val height = device.displayHeight

        scrollDown()

        val btnSave = device.findObject(UiSelector().resourceId("$PACKAGE:id/btnSave"))
        scrollDown()
        btnSave.click()
    }

    @Test
    fun test_btnEditProfile() {
        val btnEdit: UiObject = device.findObject(
            UiSelector().text("btnEditProfile").className("android.widget.Button")
        )
        if (btnEdit.exists() && btnEdit.isEnabled) {
            btnEdit.click()
            device.wait(
                Until.hasObject(By.pkg(PACKAGE).depth(0)),
                LAUNCH_TIMEOUT
            )
        }
    }

    @Test
    fun test_spActivityLevel() {
        val spActivityLevel: UiObject = device.findObject(
            UiSelector().text("spActivityLevel").className("android.widget.Spinner")
        )
        if (spActivityLevel.exists() && spActivityLevel.isEnabled) {
            spActivityLevel.click()
            device.wait(
                Until.hasObject(By.pkg(PACKAGE).depth(0)),
                LAUNCH_TIMEOUT
            )
        }
    }

    @Test
    fun test_spActivityLevel_sedentary() {
        val x = "Sedentary"
        val spinner = device.findObject(UiSelector().className("android.widget.Spinner"))
        //from the spinner, select the Sedentary option
        spinner.click()
        device.findObject(UiSelector().textContains(x)).click()

        //check that the text field is set to Sedentary
        val spActivityLevel = device.findObject(
            UiSelector().resourceId("$PACKAGE:id/tvActivityLevel")
                .className("android.widget.TextView")
        )
        assertTrue(spActivityLevel.text.contains(x))
    }

    @Test
    fun test_spActivityLevel_mild() {
        val x = "Mild"
        val spinner = device.findObject(UiSelector().className("android.widget.Spinner"))
        //from the spinner, select the Mild option
        spinner.click()
        device.findObject(UiSelector().textContains(x)).click()

        //check that the text field is set to Mild
        val spActivityLevel = device.findObject(
            UiSelector().resourceId("$PACKAGE:id/tvActivityLevel")
                .className("android.widget.TextView")
        )
        assertTrue(spActivityLevel.text.contains(x))
    }

    @Test
    fun test_spActivityLevel_moderate() {
        val x = "Moderate"
        val spinner = device.findObject(UiSelector().className("android.widget.Spinner"))
        //from the spinner, select the Moderate option
        spinner.click()
        device.findObject(UiSelector().textContains(x)).click()

        //check that the text field is set to Moderate
        val spActivityLevel = device.findObject(
            UiSelector().resourceId("$PACKAGE:id/tvActivityLevel")
                .className("android.widget.TextView")
        )
        assertTrue(spActivityLevel.text.contains(x))
    }

    @Test
    fun test_spActivityLevel_heavy() {
        val x = "Heavy"
        val spinner = device.findObject(UiSelector().className("android.widget.Spinner"))
        //from the spinner, select the Active option
        spinner.click()
        device.findObject(UiSelector().textContains(x)).click()

        //check that the text field is set to Active
        val spActivityLevel = device.findObject(
            UiSelector().resourceId("$PACKAGE:id/tvActivityLevel")
                .className("android.widget.TextView")
        )
        assertTrue(spActivityLevel.text.contains(x))
    }

    @Test
    fun test_spActivityLevel_extreme() {
        val x = "Extreme"
        val spinner = device.findObject(UiSelector().className("android.widget.Spinner"))
        //from the spinner, select the Extreme option
        spinner.click()
        device.findObject(UiSelector().textContains(x)).click()

        //check that the text field is set to Extreme
        val spActivityLevel = device.findObject(
            UiSelector().resourceId("$PACKAGE:id/tvActivityLevel")
                .className("android.widget.TextView")
        )
        assertTrue(spActivityLevel.text.contains(x))
    }

    @Test
    fun test_radio() {

        if (check_btnEdit()) {
            click_btnEdit()
        }
        val radioM = device.findObject(UiSelector().resourceId("$PACKAGE:id/radio_male"))
        val radioF = device.findObject(UiSelector().resourceId("$PACKAGE:id/radio_female"))

        for (i in 1..10) {
            radioF.click()
            radioM.click()
        }

        scrollDown()
        val btnSave = device.findObject(UiSelector().resourceId("$PACKAGE:id/btnSave"))
        btnSave.click()
    }

    @Test
    fun test_profile_activity_level_extreme() {
        click_btnEdit()
        val x = "Extreme"
        val spinner = device.findObject(UiSelector().className("android.widget.Spinner"))
        //from the spinner, select the Extreme option
        spinner.click()
        device.findObject(UiSelector().textContains(x)).click()

        scrollDown()
        val btnSave = device.findObject(UiSelector().resourceId("$PACKAGE:id/btnSave"))
        btnSave.click()

        //check that the text field is set to Extreme
        val spActivityLevel = device.findObject(
            UiSelector().resourceId("$PACKAGE:id/tvActivityLevel")
                .className("android.widget.TextView")
        )
        assertTrue(spActivityLevel.text.contains(x))
    }

    @Test
    fun test_profile_activity_level_moderate() {
        click_btnEdit()
        val x = "Moderate"
        val spinner = device.findObject(UiSelector().className("android.widget.Spinner"))
        //from the spinner, select the Moderate option
        spinner.click()
        device.findObject(UiSelector().textContains(x)).click()

        scrollDown()
        val btnSave = device.findObject(UiSelector().resourceId("$PACKAGE:id/btnSave"))
        btnSave.click()

        //check that the text field is set to Moderate
        val spActivityLevel = device.findObject(
            UiSelector().resourceId("$PACKAGE:id/tvActivityLevel")
                .className("android.widget.TextView")
        )
        assertTrue(spActivityLevel.text.contains(x))
    }
    @Test
    fun test_profile_activity_level_sedentary() {
        click_btnEdit()
        val x = "Sedentary"
        val spinner = device.findObject(UiSelector().className("android.widget.Spinner"))
        //from the spinner, select the Mild option
        spinner.click()
        device.findObject(UiSelector().textContains(x)).click()

        scrollDown()
        val btnSave = device.findObject(UiSelector().resourceId("$PACKAGE:id/btnSave"))
        btnSave.click()

        //check that the text field is set to Mild
        val spActivityLevel = device.findObject(
            UiSelector().resourceId("$PACKAGE:id/tvActivityLevel")
                .className("android.widget.TextView")
        )
        assertTrue(spActivityLevel.text.contains(x))
    }
    @Test
    fun test_profile_activity_level_mild() {
        click_btnEdit()
        val x = "Mild"
        val spinner = device.findObject(UiSelector().className("android.widget.Spinner"))
        //from the spinner, select the Light option
        spinner.click()
        device.findObject(UiSelector().textContains(x)).click()

        scrollDown()
        val btnSave = device.findObject(UiSelector().resourceId("$PACKAGE:id/btnSave"))
        btnSave.click()

        //check that the text field is set to Light
        val spActivityLevel = device.findObject(
            UiSelector().resourceId("$PACKAGE:id/tvActivityLevel")
                .className("android.widget.TextView")
        )
        assertTrue(spActivityLevel.text.contains(x))
    }

    @Test
    //test the number picker
    fun test_np_age() {
        click_btnEdit()
        //select the number picker class
        val npAge = device.findObject(UiSelector().className("android.widget.NumberPicker"))
        //scroll down to the number picker
        npAge.swipeDown(10)
        scrollDown()
        val btnSave = device.findObject(UiSelector().resourceId("$PACKAGE:id/btnSave"))
        btnSave.click()
        
    }

    @Test
    fun test_step_counter() {
        val stepCounter = device.findObject(UiSelector().resourceId("$PACKAGE:id/tvSteps"))
        assertTrue(stepCounter.text.contains("0"))
    }

    @Test
    fun test_swipe() {
        val btnReset = device.findObject(UiSelector().resourceId("$PACKAGE:id/btnResetSteps"))
        val stepCounter = device.findObject(UiSelector().resourceId("$PACKAGE:id/tvSteps"))

        device.swipe(500, 500, 1000, 500, 10)

        sleep(3000)


//        swipe the other way
        device.swipe(700, 500, 500, 500, 10)

        sleep(1000)

    }


}