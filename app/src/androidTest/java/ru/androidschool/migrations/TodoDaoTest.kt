package ru.androidschool.migrations

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals

import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class TodoDaoTest {

    private val db = Room.inMemoryDatabaseBuilder(
        InstrumentationRegistry.getInstrumentation().targetContext,
        ToDoDatabase::class.java
    ).build()

    private val underTest = db.tasks()

    @Test
    fun insertAndDelete() {
        // Проверяем что вначале таблица пуста
        assertEquals(underTest.loadAll().size, 0)

        // Поставим дедлайн сегодня
        val date = Calendar.getInstance().time

        // Создаём задачу
        val entity = TaskEntity(
            id = UUID.randomUUID().toString(),
            title = "Купить молоко",
            shortDescription = "Купить молоко 1%",
            fullDescription = "Зайти в магазин по дороге на работу и купить молоко Весёлый молочник",
            deadline = date
        )

        // Вставляем задачу в таблицу
        underTest.insert(entity)

        // Получаем все задачи из БД и проверяем что кол-во задач 1 и
        // эта задача является той же, что мы создали выше
        underTest.loadAll().let {
            assertEquals(it.size, 1)
            assertEquals(it[0], entity)
        }

        // Удаляем
        underTest.delete(entity)

        // Проверяем что теперь таблица пуста
        assertEquals(underTest.loadAll().size, 0)
    }

    @Test
    fun update() {
        // Поставим дедлайн сегодня
        val date = Calendar.getInstance().time

        val entity = TaskEntity(
            id = UUID.randomUUID().toString(),
            title = "Написать статью",
            shortDescription = "Написать статью в блог",
            fullDescription = "Написать статью по миграциями БД в Room",
            deadline = date
        )

        underTest.insert(entity)

        val updated = entity.copy(
            title = "Добавить код", shortDescription = "Добавить проект на GitHub",
            fullDescription = "Создать проект и опубликовать его на GitHub"
        )

        underTest.update(updated)

        underTest.loadAll().let {
            assertEquals(it.size, 1)
            assertEquals(it[0], updated)
        }
    }
}