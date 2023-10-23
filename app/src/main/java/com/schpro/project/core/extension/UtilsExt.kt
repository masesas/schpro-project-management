package com.schpro.project.core.extension

import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.schpro.project.core.base.Resource
import com.schpro.project.core.base.UiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun <T> UiState<T>.fromResource(result: Resource<T>): UiState<T> = when (result) {
    is Resource.Success -> UiState.Success(result.data)
    is Resource.Failure -> UiState.Failure(result.message)
}

fun <R, S> Resource<R>.toState(result: S): UiState<S> = when (this) {
    is Resource.Success -> UiState.Success(result)
    is Resource.Failure -> UiState.Failure(message)
}

fun <T> Resource<T>.toState(): UiState<T> = when (this) {
    is Resource.Success -> UiState.Success(data)
    is Resource.Failure -> UiState.Failure(message)
}

fun <T> Resource<T>.toData(): T? = when (this) {
    is Resource.Success -> data
    is Resource.Failure -> null
}

fun Long.formatDateMillis(): String {
    val simpleDateFormat by lazy { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    return simpleDateFormat.format(this);
}

fun EditText.datePicker(parentFragmentManager: FragmentManager) {
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    calendar.timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
    val minTimMilis = calendar.timeInMillis
    calendar.set(
        calendar[Calendar.YEAR] + 50, calendar[Calendar.MONTH], calendar[Calendar.DATE]
    )
    val maxTimeMilis = calendar.timeInMillis

    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR).setTitleText("Select date")
        .setCalendarConstraints(
            CalendarConstraints
                .Builder()
                .setStart(minTimMilis)
                .setEnd(maxTimeMilis)
                .setValidator(DateValidatorPointBackward.before(maxTimeMilis))
                .build()
        ).build()

    datePicker.addOnPositiveButtonClickListener {
        setText(it.formatDateMillis())
    }

    datePicker.show(parentFragmentManager, datePicker.toString())
}