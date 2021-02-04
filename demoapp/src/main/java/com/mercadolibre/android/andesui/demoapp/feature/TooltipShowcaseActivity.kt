package com.mercadolibre.android.andesui.demoapp.feature

import android.content.Context
import android.os.Bundle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchy
import com.mercadolibre.android.andesui.demoapp.feature.utils.PageIndicator
import com.mercadolibre.android.andesui.demoapp.R
import com.mercadolibre.android.andesui.tooltip.AndesTooltip
import com.mercadolibre.android.andesui.tooltip.actions.AndesTooltipAction
import com.mercadolibre.android.andesui.tooltip.actions.AndesTooltipLinkAction
import com.mercadolibre.android.andesui.tooltip.style.AndesTooltipStyle
import com.mercadolibre.android.andesui.tooltip.AndesTooltipTipOrientation
import kotlinx.android.synthetic.main.andesui_tooltip_light_showcase.view.*

class TooltipShowcaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.andesui_showcase_main)

        setSupportActionBar(findViewById(R.id.andesui_nav_bar))
        supportActionBar?.title = resources.getString(R.string.andesui_demoapp_screen_tooltip)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewPager = findViewById<ViewPager>(R.id.andesui_viewpager)
        viewPager.adapter = AndesShowcasePagerAdapter(this)
        val indicator = findViewById<PageIndicator>(R.id.page_indicator)
        indicator.attach(viewPager)

        val adapter = viewPager.adapter as AndesShowcasePagerAdapter
        addLoudButtons(adapter.views[0])
    }

    private fun addLoudButtons(container: View) {
        val andesTooltipMainAction2 = AndesTooltip(
                context = this,
                style = AndesTooltipStyle.LIGHT,
                title = "My tooltip title",
                body = resources.getString(R.string.body_text),
                tipOrientation = AndesTooltipTipOrientation.LEFT,
                mainAction = AndesTooltipAction(resources.getString(R.string.andes_card_link), AndesButtonHierarchy.LOUD){ _, tooltip ->
                    tooltip.dismiss()
                }
        )

        container.andes_trigger_tooltip_centered.setOnClickListener {
            andesTooltipMainAction2.showAlignRight(it)
        }
        val andesTooltipMainAction = AndesTooltip(
                context = this,
                style = AndesTooltipStyle.LIGHT,
                title = "My tooltip title",
                body = resources.getString(R.string.body_text),
                tipOrientation = AndesTooltipTipOrientation.LEFT,
                mainAction = AndesTooltipAction(resources.getString(R.string.andes_card_link), AndesButtonHierarchy.LOUD){ _, tooltip ->
                    tooltip.dismiss()
                }
        )

        container.andes_trigger_tooltip_top_left.setOnClickListener {
            andesTooltipMainAction.showAlignRight(it)
        }

        val andesTooltipMainPlusSecondaryAction = AndesTooltip(
                context = this,
                style = AndesTooltipStyle.LIGHT,
                title = "My tooltip title",
                body = resources.getString(R.string.body_text),
                tipOrientation = AndesTooltipTipOrientation.RIGHT,
                mainAction = AndesTooltipAction(resources.getString(R.string.andes_button_text), AndesButtonHierarchy.LOUD){ _, tooltip ->
                    tooltip.dismiss()
                },
                secondaryAction = AndesTooltipAction(resources.getString(R.string.andes_card_link), AndesButtonHierarchy.QUIET){ _, tooltip ->
                    tooltip.dismiss()
                }
        )


        container.andes_trigger_tooltip_top_right.setOnClickListener {
            andesTooltipMainPlusSecondaryAction.showAlignLeft(it)
        }

        val andesTooltipLink = AndesTooltip(
                context = this,
                style = AndesTooltipStyle.LIGHT,
                title = "My tooltip title",
                body = resources.getString(R.string.body_text),
                tipOrientation = AndesTooltipTipOrientation.LEFT,
                linkAction = AndesTooltipLinkAction(resources.getString(R.string.andes_card_link)){ _, tooltip ->
                    tooltip.dismiss()
                }
        )

        container.andes_trigger_tooltip_left.setOnClickListener {
            andesTooltipLink.showAlignRight(it)
        }

        val andesTooltipNoAction = AndesTooltip(
                context = this,
                style = AndesTooltipStyle.LIGHT,
                title = "My tooltip title",
                body = resources.getString(R.string.body_text),
                tipOrientation = AndesTooltipTipOrientation.RIGHT
        )
        container.andes_trigger_tooltip_right.setOnClickListener {
            andesTooltipNoAction.showAlignLeft(it)
        }

        val andesTooltipJustBody = AndesTooltip(
                context = this,
                style = AndesTooltipStyle.LIGHT,
                body = resources.getString(R.string.body_text),
                tipOrientation = AndesTooltipTipOrientation.BOTTOM
        )
        container.andes_trigger_tooltip_bottom_left.setOnClickListener {
            andesTooltipJustBody.showAlignTop(it)
        }

        val andesTooltipJustBody2 = AndesTooltip(
                context = this,
                style = AndesTooltipStyle.LIGHT,
                body = resources.getString(R.string.body_text),
                tipOrientation = AndesTooltipTipOrientation.TOP
        )
        container.andes_trigger_tooltip_bottom_right.setOnClickListener {
            andesTooltipJustBody2.showAlignBottom(it)
        }

    }

    class AndesShowcasePagerAdapter(private val context: Context) : PagerAdapter() {

        var views: List<View>

        init {
            views = initViews()
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            container.addView(views[position])
            return views[position]
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View?)
        }

        override fun isViewFromObject(view: View, other: Any): Boolean {
            return view == other
        }

        override fun getCount(): Int = views.size

        private fun initViews(): List<View> {
            val inflater = LayoutInflater.from(context)
            val layoutLoudButtons = inflater.inflate(
                R.layout.andesui_tooltip_light_showcase,
                null,
                false
            )

            return listOf<View>(layoutLoudButtons)
        }
    }
}
