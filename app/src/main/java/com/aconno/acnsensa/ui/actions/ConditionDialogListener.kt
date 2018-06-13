package com.aconno.acnsensa.ui.actions

import com.aconno.acnsensa.domain.interactor.filter.ReadingType

interface ConditionDialogListener {

    fun onSetClicked(readingType: ReadingType, constraint: String, value: String)
}