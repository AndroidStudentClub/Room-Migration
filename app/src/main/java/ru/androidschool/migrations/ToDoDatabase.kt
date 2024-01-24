package ru.androidschool.migrations

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TaskEntity::class], version = 2)
@TypeConverters(DateConverter::class)
abstract class ToDoDatabase : RoomDatabase() {
    companion object {

        fun newTestDatabase(context: Context) = Room.inMemoryDatabaseBuilder(
            context,
            ToDoDatabase::class.java,
        ).addMigrations(MIGRATION_1_2)
            .build()
    }

    abstract fun tasks(): TodoDao
}

@VisibleForTesting
internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE tasks ADD COLUMN deadline INT")
    }
}
