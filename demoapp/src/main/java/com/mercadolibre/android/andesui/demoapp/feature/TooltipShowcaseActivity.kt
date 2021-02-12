package com.mercadolibre.android.andesui.demoapp.feature

import android.content.Context
import android.os.Bundle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.ArrayRes
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchy
import com.mercadolibre.android.andesui.checkbox.status.AndesCheckboxStatus
import com.mercadolibre.android.andesui.demoapp.feature.utils.PageIndicator
import com.mercadolibre.android.andesui.demoapp.R
import com.mercadolibre.android.andesui.snackbar.AndesSnackbar
import com.mercadolibre.android.andesui.tooltip.AndesTooltip
import com.mercadolibre.android.andesui.tooltip.actions.AndesTooltipAction
import com.mercadolibre.android.andesui.tooltip.actions.AndesTooltipLinkAction
import com.mercadolibre.android.andesui.tooltip.style.AndesTooltipStyle
import com.mercadolibre.android.andesui.tooltip.location.AndesTooltipLocation
import kotlinx.android.synthetic.main.andesui_tooltip_light_showcase.view.*

class TooltipShowcaseActivity : AppCompatActivity() {
    private lateinit var andesTooltipToShow: AndesTooltip

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
        configInputs(adapter.views[0])
    }

    private fun configInputs(container: View){
        container.body_text.text = "default body"
        ArrayAdapter.createFromResource(
                this,
                R.array.tooltip_style_spinner,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            container.style_spinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
                this,
                R.array.tooltip_location_spinner,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            container.orientation_spinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
                this,
                R.array.tooltip_action_type_spinner,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            container.action_type_spinner.adapter = adapter
            container.action_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when(position){
                        0 -> {
                            runOnUiThread {
                                container.main_action_config.visibility = View.VISIBLE
                                container.secondary_action_config.visibility = View.GONE
                                container.link_action_text.visibility = View.GONE
                            }

                        }
                        1 -> {
                            runOnUiThread {
                                container.main_action_config.visibility = View.VISIBLE
                                container.secondary_action_config.visibility = View.VISIBLE
                                container.link_action_text.visibility = View.GONE
                            }
                        }
                        2 -> {
                            runOnUiThread {
                                container.main_action_config.visibility = View.GONE
                                container.secondary_action_config.visibility = View.GONE
                                container.link_action_text.visibility = View.VISIBLE
                            }
                        }
                        3 -> {
                            runOnUiThread {
                                container.main_action_config.visibility = View.GONE
                                container.secondary_action_config.visibility = View.GONE
                                container.link_action_text.visibility = View.GONE
                            }
                        }
                    }
                }

            }
        }

        ArrayAdapter.createFromResource(
                this,
                R.array.tooltip_main_action_style_spinner,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            container.main_action_style_spinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
                this,
                R.array.tooltip_sec_action_style_spinner,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            container.secondary_action_style_spinner.adapter = adapter
        }

        updateAndesTooltip(container)

        container.andes_trigger_tooltip_top_left.setOnClickListener (onTriggerClickListener)
        container.andes_trigger_tooltip_top_right.setOnClickListener (onTriggerClickListener)
        container.andes_trigger_tooltip_right.setOnClickListener (onTriggerClickListener)
        container.andes_trigger_tooltip_left.setOnClickListener (onTriggerClickListener)
        container.andes_trigger_tooltip_bottom_left.setOnClickListener (onTriggerClickListener)
        container.andes_trigger_tooltip_bottom_right.setOnClickListener (onTriggerClickListener)
        container.andes_trigger_tooltip_centered.setOnClickListener (onTriggerClickListener)
        container.andes_centered_top_left.setOnClickListener (onTriggerClickListener)
        container.andes_centered_top_right.setOnClickListener (onTriggerClickListener)
        container.andes_centered_bottom_left.setOnClickListener (onTriggerClickListener)
        container.andes_centered_bottom_right.setOnClickListener (onTriggerClickListener)

        container.change_button.setOnClickListener {
            updateAndesTooltip(container)
        }
    }

    private val onTriggerClickListener = View.OnClickListener { andesTooltipToShow.show(it) }

    private fun updateAndesTooltip(container: View) {

        val builtTooltip: AndesTooltip

        val title: String? = container.title_text.text.takeIf { !it.isNullOrEmpty() }
        val body: String = if (!container.body_text.text.isNullOrEmpty()){container.body_text.text!!} else{"default body"}
        val isDismissible = container.dismissable_checkbox.status == AndesCheckboxStatus.SELECTED

        val location: AndesTooltipLocation =
        when(container.orientation_spinner.selectedItem){
            "top"-> AndesTooltipLocation.TOP
            "bottom"-> AndesTooltipLocation.BOTTOM
            "left"-> AndesTooltipLocation.LEFT
            "right"-> AndesTooltipLocation.RIGHT
            else-> AndesTooltipLocation.TOP
        }
        val style: AndesTooltipStyle =
        when(container.style_spinner.selectedItem){
            "light"-> AndesTooltipStyle.LIGHT
            else-> AndesTooltipStyle.LIGHT
        }

        when(container.action_type_spinner.selectedItem){
            "main action"-> {
                val text:String = if (!container.primary_action_text.text.isNullOrEmpty()){
                    container.primary_action_text.text!!
                } else {
                    container.primary_action_text.text = "default text"
                    "default text"
                }
                builtTooltip = AndesTooltip(
                        context = this,
                        isDismissible = isDismissible,
                        style = style,
                        title = title,
                        body = body,
                        tooltipLocation = location,
                        mainAction = buildMainAction(
                                text,
                                getHierarchyBySpinner(container.main_action_style_spinner, R.array.tooltip_main_action_style_spinner)
                        )
                )

            }
            "main and second"-> {
                val textPrimary:String = if (!container.primary_action_text.text.isNullOrEmpty()){
                    container.primary_action_text.text!!
                } else {
                    container.primary_action_text.text = "default text"
                    "default text"
                }
                val textSecondary:String = if (!container.secondary_action_text.text.isNullOrEmpty()){
                    container.secondary_action_text.text!!
                } else {
                    container.secondary_action_text.text = "default text2"
                    "default text2"
                }
                builtTooltip = AndesTooltip(
                        context = this,
                        isDismissible = isDismissible,
                        style = style,
                        title = title,
                        body = body,
                        tooltipLocation = location,
                        mainAction = buildMainAction(
                                textPrimary,
                                getHierarchyBySpinner(container.main_action_style_spinner, R.array.tooltip_main_action_style_spinner)
                        ),
                        secondaryAction = buildMainAction(
                                textSecondary,
                                getHierarchyBySpinner(container.secondary_action_style_spinner, R.array.tooltip_sec_action_style_spinner)
                        )
                )
            }
            "link"-> {
                val text:String = if (!container.link_action_text.text.isNullOrEmpty()){
                    container.link_action_text.text!!
                } else {
                    container.link_action_text.text = "default text"
                    "default text"
                }
                builtTooltip = AndesTooltip(
                        context = this,
                        isDismissible = isDismissible,
                        style = style,
                        title = title,
                        body = body,
                        tooltipLocation = location,
                        linkAction = buildLinkAction(text)
                )
            }
            else-> {
                builtTooltip = AndesTooltip(
                        context = this,
                        isDismissible = isDismissible,
                        style = style,
                        title = title,
                        body = body,
                        tooltipLocation = location
                )
            }
        }
        andesTooltipToShow = builtTooltip
    }

    private fun getHierarchyBySpinner(spinner: Spinner, @ArrayRes stringArrayRes: Int ): AndesButtonHierarchy {
        return when (spinner.selectedItem){
            "loud"-> AndesButtonHierarchy.LOUD
            "quiet"-> AndesButtonHierarchy.QUIET
            "transparent"-> AndesButtonHierarchy.TRANSPARENT
            else-> AndesButtonHierarchy.QUIET
        }
    }

    private fun buildMainAction(text: String, hierarchy: AndesButtonHierarchy): AndesTooltipAction{
        return AndesTooltipAction(text, hierarchy){ _, _ ->
            Toast.makeText(this, "$text was clicked" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildLinkAction(text: String): AndesTooltipLinkAction{
        return AndesTooltipLinkAction(text){ _, _ ->
            Toast.makeText(this, "$text was clicked" , Toast.LENGTH_SHORT).show()
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
