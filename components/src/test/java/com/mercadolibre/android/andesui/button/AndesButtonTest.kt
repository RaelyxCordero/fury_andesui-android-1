package com.mercadolibre.android.andesui.button

import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.common.logging.FLog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.listener.RequestLoggingListener
import com.facebook.soloader.SoLoader
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchy
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonIcon
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonIconOrientation
import com.mercadolibre.android.andesui.button.size.AndesButtonSize
import com.mercadolibre.android.andesui.icons.IconProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
class AndesButtonTest {
    private var context = RuntimeEnvironment.application
    private lateinit var andesButton: AndesButton

    companion object {
        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            SoLoader.setInTestMode()
        }
    }

    @Before
    fun setUp() {
        val requestListeners = setOf<RequestListener>(RequestLoggingListener())
        val config = ImagePipelineConfig.newBuilder(context)
                // other setters
                .setRequestListeners(requestListeners)
                .build()
        Fresco.initialize(context, config)
        FLog.setMinimumLoggingLevel(FLog.VERBOSE)
    }

    @Test
    fun `Only context constructor`() {
        andesButton = AndesButton(context)
        val textParams = andesButton.textComponent.layoutParams as ConstraintLayout.LayoutParams

        assertEquals(andesButton.textComponent.textSize, 16F)
        assertEquals(textParams.goneStartMargin, 8)
        assertEquals(textParams.goneEndMargin, 8)
        assertEquals(andesButton.paddingRight, 16)
        assertEquals(andesButton.paddingLeft, 16)
        assertNull(andesButton.leftIconComponent.drawable)
        assertNull(andesButton.rightIconComponent.drawable)
        assertEquals(andesButton.isLoading, false)
    }

    @Test
    fun `Hierarchy and size constructor`() {
        andesButton = AndesButton(context, AndesButtonSize.MEDIUM, AndesButtonHierarchy.QUIET)
        val textParams = andesButton.textComponent.layoutParams as ConstraintLayout.LayoutParams

        assertEquals(andesButton.textComponent.textSize, 14F)
        assertEquals(textParams.goneStartMargin, 0)
        assertEquals(textParams.goneEndMargin, 0)
        assertEquals(andesButton.paddingRight, 12)
        assertEquals(andesButton.paddingLeft, 12)
        assertNull(andesButton.leftIconComponent.drawable)
        assertNull(andesButton.rightIconComponent.drawable)
        assertEquals(andesButton.isLoading, false)
    }

    @Test
    fun `Hierarchy, size and icon constructor`() {
        val icon = AndesButtonIcon(ANDES_ICON, AndesButtonIconOrientation.LEFT)
        andesButton = AndesButton(context, AndesButtonSize.LARGE, AndesButtonHierarchy.TRANSPARENT, icon)
        val textParams = andesButton.textComponent.layoutParams as ConstraintLayout.LayoutParams
        val leftIconParams = andesButton.leftIconComponent.layoutParams as ConstraintLayout.LayoutParams

        assertEquals(andesButton.textComponent.textSize, 16F)
        assertEquals(leftIconParams.marginStart, 0)
        assertEquals(textParams.marginStart, 12)
        assertEquals(textParams.goneEndMargin, 8)
        assertEquals(andesButton.paddingRight, 16)
        assertEquals(andesButton.paddingLeft, 16)
        assertThat(andesButton.leftIconComponent.drawable).isEqualToComparingOnlyGivenFields(icon)
        assertNull(andesButton.rightIconComponent.drawable)
        assertEquals(andesButton.isLoading, false)
    }

    @Test
    fun `Hierarchy, size and right icon constructor`() {
        val icon = AndesButtonIcon(ANDES_ICON, AndesButtonIconOrientation.RIGHT)
        andesButton = AndesButton(context, AndesButtonSize.LARGE, AndesButtonHierarchy.TRANSPARENT, icon)
        val textParams = andesButton.textComponent.layoutParams as ConstraintLayout.LayoutParams
        val rightIconParams = andesButton.rightIconComponent.layoutParams as ConstraintLayout.LayoutParams

        assertEquals(andesButton.textComponent.textSize, 16F)
        assertEquals(textParams.marginEnd, 12)
        assertEquals(rightIconParams.marginEnd, 0)
        assertEquals(textParams.goneStartMargin, 8)
        assertEquals(andesButton.paddingRight, 16)
        assertEquals(andesButton.paddingLeft, 16)
        assertNull(andesButton.leftIconComponent.drawable)
        assertThat(andesButton.rightIconComponent.drawable).isEqualToComparingOnlyGivenFields(icon)
        assertEquals(andesButton.isLoading, false)
    }

    @Test
    fun `Drawable left is visible after set`() {
        andesButton = AndesButton(context, AndesButtonSize.LARGE, AndesButtonHierarchy.TRANSPARENT, null)
        assertEquals(andesButton.leftIconComponent.drawable, null)
        val drawable = IconProvider(context)
            .loadIcon("andes_ui_placeholder_imagen_24") as BitmapDrawable
        andesButton.setIconDrawable(drawable, AndesButtonIconOrientation.LEFT)
        assertEquals(andesButton.leftIconComponent.visibility, View.VISIBLE)
    }

    @Test
    fun `Drawable right is visible after set`() {
        andesButton = AndesButton(context, AndesButtonSize.LARGE, AndesButtonHierarchy.TRANSPARENT, null)
        assertNull(andesButton.rightIconComponent.drawable)
        val drawable = IconProvider(context)
            .loadIcon("andes_ui_placeholder_imagen_24") as BitmapDrawable
        andesButton.setIconDrawable(drawable, AndesButtonIconOrientation.RIGHT)
        assertNotNull(andesButton.rightIconComponent.drawable)
    }
}
