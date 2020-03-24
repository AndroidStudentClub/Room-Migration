package ru.androidschool.migrations

import androidx.room.*

@Dao
interface TodoDao {
    @Query("SELECT * FROM tasks")
    fun loadAll(): List<TaskEntity>

    @Insert
    fun insert(task: TaskEntity)

    @Update
    fun update(task: TaskEntity)

    @Delete
    fun delete(vararg tasks: TaskEntity)
}