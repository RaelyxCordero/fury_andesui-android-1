package com.mercadolibre.android.andesui.demoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.mercadolibre.android.andesui.demoapp.feature.specs.AndesSpecs
import com.mercadolibre.android.andesui.demoapp.feature.specs.launchSpecs
import com.mercadolibre.android.andesui.demoapp.feature.utils.SafeIntent
import kotlinx.android.synthetic.main.andesui_demoapp_main.*

/**
 * Main activity class
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.andesui_demoapp_main)

        setSupportActionBar(findViewById(R.id.andesui_nav_bar))
        supportActionBar?.title = resources.getString(R.string.andesui_demoapp_app_name)

        setupCoreComponents()
        setupExtras()
    }

    private fun setupCoreComponents() {
        andesui_carousel.visibility = View.GONE
        andesui_coachmark.visibility = View.GONE
        andesui_checkbox.visibility = View.GONE
        andesui_radiobutton.visibility = View.GONE
        andesui_tags.visibility = View.GONE
        andesui_badges.visibility = View.GONE
        andesui_textfield.visibility = View.GONE
        andesui_thumbnail.visibility = View.GONE
        andesui_dropdown.visibility = View.GONE
        andesui_list.visibility = View.GONE
        andesui_progress.visibility = View.GONE
        andesui_bottom_sheet.visibility = View.GONE
        andesui_date_picker.visibility = View.GONE

        andesui_tooltip.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/tooltip"))
        }
        andesui_carousel.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/carousel"))
        }
        andesui_coachmark.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/coachmark"))
        }
        andesui_card.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/card"))
        }
        andesui_checkbox.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/checkbox"))
        }
        andesui_radiobutton.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/radiobutton"))
        }
        andesui_snackbar.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/snackbar"))
        }
        andesui_tags.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/tag"))
        }
        andesui_badges.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/badge"))
        }
        andesui_buttons.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/button"))
        }
        andesui_messages.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/message"))
        }
        andesui_textfield.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/textfield"))
        }
        andesui_thumbnail.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/thumbnail"))
        }
        andesui_dropdown.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/dropdown"))
        }
        andesui_list.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/list"))
        }
        andesui_progress.setOnClickListener {
            startActivity(SafeIntent(this, "meli://andes/progress"))
        }
        andesui_bottom_sheet.setOnClickListener {
            startActivity(SafeIntent(this,"meli://andes/bottom_sheet"))
        }
        andesui_date_picker.setOnClickListener {
            startActivity(SafeIntent(this,"meli://andes/datepicker"))
        }
    }

    private fun setupExtras() {
        andesui_demoapp_changelog.setupPrimaryAction(
            getString(R.string.andesui_demoapp_whatsnew_main_action),
            View.OnClickListener { startActivity(SafeIntent(this, "meli://andes/whats-new")) }
        )

        andesui_demoapp_andes_specs.setOnClickListener {
            launchSpecs(this, AndesSpecs.HOME_PAGE)
        }

        andesui_demoapp_contribution.setOnClickListener {
            ContextCompat.startActivity(this,
                    Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://meli.workplace.com/notes/andes-ui/c%C3%B3mo-contribuir-en-andes-ui/2559399620854933")),
                    null)
        }
    }
}
