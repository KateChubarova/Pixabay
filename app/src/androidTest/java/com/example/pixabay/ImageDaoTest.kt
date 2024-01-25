package com.example.pixabay

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.example.pixabay.db.ImageDao
import com.example.pixabay.db.ImageDataBase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.empty

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ImageDaoTest {

    private lateinit var imageDao: ImageDao
    private lateinit var db: ImageDataBase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ImageDataBase::class.java
        ).allowMainThreadQueries().build()
        imageDao = db.imageDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getImages_test() = runBlocking {
        val image = Image(
            0, "url", "user", "preview", "large", "tags",
            3, 5, 6, 100, 100
        )
        val image1 = image.copy(id = 1, userName = "tiger", tags = "user")
        val image2 = image.copy(id = 2, userName = "user")
        val image3 = image.copy(id = 3, userName = "tiger")
        val image4 = image.copy(id = 4, userName = "cat")
        val images = listOf(image1, image2, image3, image4)

        imageDao.insertAllImages(images)

        val result = imageDao.getImagesByQuery("user")
        val expectedResult = listOf(image1, image2)
        assertThat(result, `is`(expectedResult))
    }

    @Test
    fun nonExistImages_test() = runBlocking {
        val result = imageDao.getImagesByQuery("empty")
        assertThat(result, empty())
    }
}