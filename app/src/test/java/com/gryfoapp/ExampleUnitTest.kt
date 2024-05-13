package com.gryfoapp

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Method

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun bitMapConvertorTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val imageView = ImageView(appContext)
        imageView.setImageResource(android.R.drawable.ic_menu_camera)
        val drawable = imageView.drawable as BitmapDrawable
        val expectedBitmap = drawable.bitmap

        val mainActivity = MainActivity()
        val actualBitmap = invokePrivateMethod<Bitmap>(mainActivity, "bitMapConvertor",
            ImageView::class.java.toString(), imageView.toString()
        )

        // Verifica se o bitmap retornado é igual ao bitmap esperado
        assertEquals(expectedBitmap, actualBitmap)
    }

    @Test
    fun base64ConvertorTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val imageView = ImageView(appContext)
        imageView.setImageResource(android.R.drawable.ic_menu_camera)
        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap

        val mainActivity = MainActivity()
        val base64String = invokePrivateMethod<String>(mainActivity, "base64Convertor",
            Bitmap::class.java.toString(), bitmap.toString()
        )

        // Verifica se a string base64 não está vazia
        assert(base64String.isNotEmpty())
    }

    @Test
    fun createImageUriTest() {
        val mainActivity = MainActivity()
        val uri = invokePrivateMethod<Any>(mainActivity, "createImageUri",
            String::class.java.toString(), "test_image.png")

        // Verifica se a URI não é nula
        assert(uri != null)
    }

    // Função para invocar métodos privados usando reflexão
    private fun <T> invokePrivateMethod(obj: Any, methodName: String, vararg argTypes: String, vararg params: Any): T {
        val method: Method = obj.javaClass.getDeclaredMethod(methodName, *argTypes)
        method.isAccessible = true
        return method.invoke(obj, *params) as T
    }
}
