package com.moviles.primer_examen.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moviles.primer_examen.interfaces.CourseDao
import com.moviles.primer_examen.interfaces.StudentDao

@Database(entities = [Course::class, Student::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun studentDao(): StudentDao
}