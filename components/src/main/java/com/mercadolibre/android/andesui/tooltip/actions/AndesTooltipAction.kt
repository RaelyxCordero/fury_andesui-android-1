package com.mercadolibre.android.andesui.tooltip.actions

import android.view.View
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchy
import com.mercadolibre.android.andesui.tooltip.AndesTooltip

data class AndesTooltipAction(val label: String, val hierarchy: AndesButtonHierarchy, val onActionClicked: (view: View, tooltip: AndesTooltip) -> Unit)

data class AndesTooltipLinkAction(val label: String, val onActionClicked: (view: View, tooltip: AndesTooltip) -> Unit)
