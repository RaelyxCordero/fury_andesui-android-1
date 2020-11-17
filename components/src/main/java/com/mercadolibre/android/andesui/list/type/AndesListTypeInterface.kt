package com.mercadolibre.android.andesui.list.type

import android.content.Context
import com.mercadolibre.android.andesui.R

/**
 *
 */
internal interface AndesListTypeInterface {
    /**
     *
     */
    fun getType(context: Context): AndesListType
}

internal object AndesListTypeSimple : AndesListTypeInterface {
    override fun getType(context: Context) = AndesListType.SIMPLE
}

internal object AndesListTypeChevron : AndesListTypeInterface {
    override fun getType(context: Context) = AndesListType.CHEVRON
}
