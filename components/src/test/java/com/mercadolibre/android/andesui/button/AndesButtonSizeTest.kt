package com.mercadolibre.android.andesui.button

import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.content.ContextCompat
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.button.factory.IconConfig
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchyInterface
import com.mercadolibre.android.andesui.button.size.AndesLargeButtonSize
import com.mercadolibre.android.andesui.button.size.AndesMediumButtonSize
import com.mercadolibre.android.andesui.button.size.AndesSmallButtonSize
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

const val ANDES_ICON = "andes_navegacion_ajustes"

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
class AndesLargeButtonSizeTest {

    private var andesLargeButtonSize = Mockito.spy(AndesLargeButtonSize())
    private var context = RuntimeEnvironment.application
    private var icon = ContextCompat.getDrawable(context, R.drawable.andes_navegacion_ajustes)
    private var hierarchy = Mockito.mock(AndesButtonHierarchyInterface::class.java)

    @Test
    fun `Large button text size`() {
        assertEquals(andesLargeButtonSize.textSize(context), 16F)
    }

    @Test
    fun `Large button height`() {
        assertEquals(andesLargeButtonSize.height(context), 48F)
    }

    @Test
    fun `Large button text padding`() {
        assertEquals(andesLargeButtonSize.textLeftMargin(context), 8)
        assertEquals(andesLargeButtonSize.textRightMargin(context), 8)
        assertEquals(andesLargeButtonSize.lateralPadding(context), 16)
    }

    @Test
    fun `Large button left icon padding`() {
        assertEquals(andesLargeButtonSize.lateralPadding(context), 16)
        assertEquals(andesLargeButtonSize.leftIconRightMargin(context), 12)
    }

    @Test
    fun `Large button right icon padding `() {
        assertEquals(andesLargeButtonSize.rightIconLeftMargin(context), 12)
        assertEquals(andesLargeButtonSize.lateralPadding(context), 16)
    }

    @Test
    fun `Large button corner radius`() {
        assertEquals(andesLargeButtonSize.cornerRadius(context), 6F)
    }

    @Test
    fun `Large button icon null`() {
        assertEquals(andesLargeButtonSize.iconConfig(hierarchy, null, null, null, null, context), null)
    }

    @Test
    fun `Large button left icon`() {
        assertThat(shadowOf(IconConfig(icon, null).leftIcon!!).createdFromResId).isEqualTo(shadowOf(icon).createdFromResId)
    }

    @Test
    fun `Large button right icon`() {
        assertThat(shadowOf(IconConfig(null, icon).rightIcon!!).createdFromResId).isEqualTo(shadowOf(icon).createdFromResId)
    }

}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
class AndesMediumButtonSizeTest {
    private var andesMediumButtonSize = Mockito.spy(AndesMediumButtonSize())
    private var context = RuntimeEnvironment.application
    private var icon = Mockito.mock(BitmapDrawable::class.java)
    private var hierarchy = Mockito.mock(AndesButtonHierarchyInterface::class.java)

    @Test
    fun `Medium button text size`() {
        assertEquals(andesMediumButtonSize.textSize(context), 14F)
    }

    @Test
    fun `Medium button height`() {
        assertEquals(andesMediumButtonSize.height(context), 32F)
    }

    @Test
    fun `Medium button text padding`() {
        assertEquals(andesMediumButtonSize.lateralPadding(context), 12)
        assertEquals(andesMediumButtonSize.lateralPadding(context), 12)
    }

    @Test
    fun `Medium button left icon padding `() {
        assertEquals(andesMediumButtonSize.leftIconLeftMargin(context), 0)
        assertEquals(andesMediumButtonSize.leftIconRightMargin(context), 0)
    }

    @Test
    fun `Medium button right icon padding`() {
        assertEquals(andesMediumButtonSize.rightIconLeftMargin(context), 0)
        assertEquals(andesMediumButtonSize.rightIconRightMargin(context), 0)
    }

    @Test
    fun `Medium button corner radius`() {
        assertEquals(andesMediumButtonSize.cornerRadius(context), 5F)
    }

    @Test
    fun `Medium button icon null`() {
        assertNull(andesMediumButtonSize.iconConfig(hierarchy, null, null, null, null,context))
    }

    @Test
    fun `Medium button left icon`() {
        assertNull(andesMediumButtonSize.iconConfig(hierarchy, ANDES_ICON, null, null, null,context))
    }

    @Test
    fun `Medium button right icon`() {
        assertNull(andesMediumButtonSize.iconConfig(hierarchy, null, ANDES_ICON, null, null,context))
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
class AndesSmallButtonSizeTest {
    private var andesSmallButtonSize = Mockito.spy(AndesSmallButtonSize())
    private var context = RuntimeEnvironment.application
    private var icon = Mockito.mock(BitmapDrawable::class.java)
    private var hierarchy = Mockito.mock(AndesButtonHierarchyInterface::class.java)

    @Test
    fun `Small button text size`() {
        assertEquals(andesSmallButtonSize.textSize(context), 12F)
    }

    @Test
    fun `Small button height`() {
        assertEquals(andesSmallButtonSize.height(context), 24F)
    }

    @Test
    fun `Small button text padding`() {
        assertEquals(andesSmallButtonSize.lateralPadding(context), 8)
        assertEquals(andesSmallButtonSize.lateralPadding(context), 8)
    }

    @Test
    fun `Small button left icon padding `() {
        assertEquals(andesSmallButtonSize.leftIconLeftMargin(context), 0)
        assertEquals(andesSmallButtonSize.leftIconRightMargin(context), 0)
    }

    @Test
    fun `Small button right icon padding`() {
        assertEquals(andesSmallButtonSize.rightIconLeftMargin(context), 0)
        assertEquals(andesSmallButtonSize.rightIconRightMargin(context), 0)
    }

    @Test
    fun `Small button corner radius`() {
        assertEquals(andesSmallButtonSize.cornerRadius(context), 4F)
    }

    @Test
    fun `Small button icon null`() {
        assertNull(andesSmallButtonSize.iconConfig(hierarchy, null, null, null, null,context))
    }

    @Test
    fun `Small button left icon`() {
        assertNull(andesSmallButtonSize.iconConfig(hierarchy, ANDES_ICON, null, null, null,context))
    }

    @Test
    fun `Small button right icon`() {
        assertNull(andesSmallButtonSize.iconConfig(hierarchy, null, ANDES_ICON, null, null,context))
    }
}
