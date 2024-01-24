package ru.androidschool.migrations

import androidx.room.ColumnInfo
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
    @ColumnInfo(name = "deadline", defaultValue = DEFAULT_DATE)
    val deadline: Date?
)
const val DEFAULT_DATE = "1706108052"