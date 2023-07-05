package com.example.project

import org.junit.Test
import org.junit.Assert.*

internal class BMRTest {

    private val delta = 1.0
    private var person1 = Triple(25, 183, 80)
    private val bmr = BMR()

    @Test
    fun calculateBMRMen() {
        val result = bmr.calculateBMR(person1.first, person1.second, person1.third, true)
        assertEquals(1823.0, result, delta)
    }

    @Test
    fun calculateBMRWomen() {
        val result = bmr.calculateBMR(person1.first, person1.second, person1.third, false)
        assertEquals(1657.0, result, delta)
    }

    @Test
    fun calculateCaloriesSedentary() {
        //male
        var result = bmr.calculateCaloriesSedentary(1913.0)
        assertEquals(2296.0, result, delta)
        //female
        result = bmr.calculateCaloriesSedentary(1652.0)
        assertEquals(1982.0, result, delta)
    }

    @Test
    fun calculateCaloriesMildActivity() {
        //male
        var result = bmr.calculateCaloriesMildActivity(1913.0)
        assertEquals(2630.0, result, delta)
        //female
        result = bmr.calculateCaloriesMildActivity(1652.0)
        assertEquals(2270.0, result, delta)
    }

    @Test
    fun calculateCaloriesModerateActivity() {
        //male
        var result = bmr.calculateCaloriesModerateActivity(1913.0)
        assertEquals(2963.0, result, delta)
        //female
        result = bmr.calculateCaloriesModerateActivity(1652.0)
        assertEquals(2559.0, result, delta)
    }

    @Test
    fun calculateCaloriesHeavyActivity() {
        //male
        var result = bmr.calculateCaloriesHeavyActivity(1913.0)
        assertEquals(3252.0, result, delta)
        //female
        result = bmr.calculateCaloriesHeavyActivity(1652.0)
        assertEquals(2808.0, result, delta)
    }

    @Test
    fun calculateCaloriesExtremeActivity() {
        //male
        var result = bmr.calculateCaloriesExtremeActivity(1913.0)
        assertEquals(3635.0, result, delta)
        //female
        result = bmr.calculateCaloriesExtremeActivity(1652.0)
        assertEquals(3139.0, result, delta)
    }
}