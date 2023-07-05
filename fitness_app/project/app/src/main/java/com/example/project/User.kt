package com.example.project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    // they are not explicitly set, the val name is assigned to the column name
    val firstName: String,
    val lastName: String,
    val age: Int,
    val height: Int,
    val weight: Int,
    var activityLevel: Int,
    val isMale: Boolean,
    val location: String,
    val imagePath: String,
    var steps: Int          // initialized to 0 when user is created
){

    // auto generate primary key for each user
    @PrimaryKey (autoGenerate = true) var id: Int = 0
}