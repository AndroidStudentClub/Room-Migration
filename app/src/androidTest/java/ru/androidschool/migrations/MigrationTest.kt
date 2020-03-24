package ru.androidschool.migrations

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
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
        ToDoDatabase::class.java.canonicalName
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
            assertThat(it.count, equalTo(1))
            it.moveToFirst()
            assertThat(it.getInt(0), equalTo(1))
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

        // Проверяме что все отработало корректно
        // Поле Date теперь присутсвтует в таблице и должно быть пусто
        // Номер столбца соответсвует полю сущности в том порядке, в котором описана TaskEntity
        db.query("SELECT id, title, shortDescription, fullDescription, deadline FROM tasks")
            .use {
                assertThat(it.count, equalTo(1))
                it.moveToFirst()
                assertThat(it.getInt(0), equalTo(TEST_ID))
                assertThat(it.getString(1), equalTo(TEST_TITLE))
                assertThat(it.getString(2), equalTo(TEST_SHORT_TEXT))
                assertThat(it.getString(3), equalTo(TEST_FULL_TEXT))
                assertThat(it.getString(4), absent())
            }
    }
}
