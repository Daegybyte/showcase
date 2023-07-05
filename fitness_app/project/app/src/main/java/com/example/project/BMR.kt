package com.example.project

import kotlin.math.roundToInt

/**
 * This is a class for calculating the Basal Metabolic rate of a person using The Harris–Benedict equations revised by Mifflin and St Jeor in 1990.
 * @param weight the weight of the person in kilograms
 * @param height the height of the person in centimeters
 * @param age the age of the person in years
 * @sample calculateBMR()
// * @sample calculateBMRMen() - calculates the BMR for men
// * @sample calculateBMRWomen() - calculates the BMR for women
 * @sample calculateCaloriesSedentary() - calculates the calories needed for a sedentary person
 * @sample calculateCaloriesMildActivity() - calculates the calories needed for a lightly active person
 * @sample calculateCaloriesModerateActivity() - calculates the calories needed for a moderately active person
 * @sample calculateCaloriesHeavyActivity() - calculates the calories needed for a very active person
 * @sample calculateCaloriesExtremeActivity() - calculates the calories needed for an extra active person
 */
class BMR {

    private val sedentary = 1.2
    private val mildActivity = 1.375
    private val moderateActivity = 1.55
    private val heavyActivity = 1.7
    private val extremeActivity = 1.9

    /**
     * Calculates the base BMR (not including activity level)
     * returns Double
     * @param age: Int
     * @param height: Int
     * @param weight: Int
     * @return Double
     */
    fun calculateBMR(age: Int, height: Int, weight: Int, isMale: Boolean): Double {
        var ret = (10 * weight) + (6.25 * height) - (5 * age)
        if (isMale){
            ret += 5
        }
        else {
            ret -= 161
        }
        return ret
    }

    fun calculateAdjustedBMR(baseBMR: Double, actLvl: Int) : String {
        when (actLvl){
            0 -> return (baseBMR * sedentary).roundToInt().toString()
            1 -> return (baseBMR * mildActivity).roundToInt().toString()
            2 -> return (baseBMR * moderateActivity).roundToInt().toString()
            3 -> return (baseBMR * heavyActivity).roundToInt().toString()
            4 -> return (baseBMR * extremeActivity).roundToInt().toString()
            else -> return "0"
        }
    }

    /**
     * Calculates the calorie needs of a sedentary person
     * @param bmr: Double
     * @return Double
     */
    fun calculateCaloriesSedentary(bmr: Double): Double {
        return bmr * sedentary
    }

    /**
     * Calculates the calorie needs of a person with mild activity
     * @param bmr: Double
     * @return Double
     */
    fun calculateCaloriesMildActivity(bmr: Double): Double {
        return (bmr * mildActivity) - 1.375
    }

    /**
     * Calculates the calorie needs of a person with moderate activity
     * @param bmr: Double
     * @return Double
     */
    fun calculateCaloriesModerateActivity(bmr: Double): Double {
        return (bmr * moderateActivity) - 1.55
    }

    /**
     * Calculates the calorie needs of a person with heavy activity
     * @param bmr: Double
     * @return Double
     */
    fun calculateCaloriesHeavyActivity(bmr: Double): Double {
        return (bmr * heavyActivity)
    }

    /**
     * Calculates the calorie needs of a person with extreme activity
     * @param bmr: Double
     * @return Double
     */
    fun calculateCaloriesExtremeActivity(bmr: Double): Double {
        return (bmr * extremeActivity)
    }
}