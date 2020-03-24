package ru.androidschool.migrations

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val shortDescription: String,
    val fullDescription: String,
    val deadline: Date?
)