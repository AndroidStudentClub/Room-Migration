package ru.androidschool.migrations

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.espresso.core.internal.deps.guava.base.Optional.absent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val DB_NAME = "ToDoDatabase.db"
private const val TEST_ID = 1337
private const val TEST_TITLE = "Test Title"
private const val TEST_SHORT_TEXT = "Test short_text"
private const val TEST_FULL_TEXT = "Test full text"

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ToDoDatabase::class.java
    )

    @Test
    fun test1To2() {
        // Создаём БД с версией 1
        val initialDb = migrationTestHelper.createDatabase(DB_NAME, 1)

        // Вставляем задачу
        initialDb.execSQL(
            "INSERT INTO tasks (id, title,  shortDescription,  fullDescription) VALUES (?, ?, ?, ?)",
            arrayOf(TEST_ID, TEST_TITLE, TEST_SHORT_TEXT, TEST_FULL_TEXT)
        )

        // Проверяем что созданная задача сохранилась
        initialDb.query("SELECT COUNT(*) FROM tasks").use {
            assertEquals(it.count, 1)
            it.moveToFirst()
            assertEquals(it.getInt(0), 1)
        }

        // Закрываем БД
        initialDb.close()

        // Запускаем миграцию схемы БД с версии с на 2
        val db = migrationTestHelper.runMigrationsAndValidate(
            DB_NAME,
            2,
            true,
            MIGRATION_1_2
        )

        // Проверяем, что все отработало корректно
        // Поле Date теперь присутствует в таблице и должно быть пусто
        // Номер столбца соответсвует полю сущности в том порядке, в котором описана TaskEntity
        db.query("SELECT id, title, shortDescription, fullDescription, deadline FROM tasks")
            .use {
                assertEquals(it.count, 1)
                it.moveToFirst()
                assertEquals(it.getInt(0), TEST_ID)
                assertEquals(it.getString(1), TEST_TITLE)
                assertEquals(it.getString(2), TEST_SHORT_TEXT)
                assertEquals(it.getString(3), TEST_FULL_TEXT)
                assertEquals(it.getString(4), null)
            }
    }
}
