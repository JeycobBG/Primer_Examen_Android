package com.moviles.primer_examen.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.moviles.primer_examen.interfaces.CourseDao
import com.moviles.primer_examen.interfaces.StudentDao

@Database(
    entities = [Course::class, Student::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "primer_examen_db"
                )
                    .fallbackToDestructiveMigration() // Esto elimina la base de datos y la recrea si la versión cambia
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
