package com.schpro.project.core.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.schpro.project.R
import com.schpro.project.databinding.DialogSprintBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class SprintDialog constructor(
    private val context: Context,
    private val fragmentManager: FragmentManager
) {

    private lateinit var binding: DialogSprintBinding
    private lateinit var dialog: AlertDialog
    private val simpleDateFormat by lazy { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    init {
        initDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun initDialog() {
        binding = DialogSprintBinding.inflate(LayoutInflater.from(context))
        val dialogBuilder = MaterialAlertDialogBuilder(context, R.style.TransparentDialog)
        dialogBuilder.setView(binding.root)
        dialog = dialogBuilder.create()

        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
        val minTimMilis = calendar.timeInMillis
        calendar.set(
            calendar[Calendar.YEAR] + 50,
            calendar[Calendar.MONTH],
            calendar[Calendar.DATE]
        )
        val maxTimeMilis = calendar.timeInMillis

        val dateRangePicker = MaterialDatePicker
            .Builder
            .dateRangePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setStart(minTimMilis)
                    .setEnd(maxTimeMilis)
                    .setValidator(DateValidatorPointBackward.before(maxTimeMilis))
                    .build()
            )
            .build()

        dateRangePicker.addOnPositiveButtonClickListener {
            val fromDate = simpleDateFormat.format(Date(it.first))
            val toDate = simpleDateFormat.format(Date(it.second))

            binding.etDate.setText("$fromDate - $toDate")
        }

        binding.imgClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.etDate.setOnClickListener {
            dateRangePicker.show(
                fragmentManager,
                dateRangePicker.toString()
            )
        }
    }

    fun setTitle(title: String = "Add Sprint") = binding.tvTitle.apply {
        text = title
    }

    fun setButtonSave(action: (AlertDialog, title: String, date: String) -> Unit) {
        binding.btnSave.setOnClickListener {
            action.invoke(dialog, binding.etTitle.text.toString(), binding.etDate.text.toString())
        }
    }

    fun dismiss() = if (dialog.isShowing) dialog.dismiss() else null

    fun show(){
        binding.etTitle.setText("")
        binding.etDate.setText("")
        dialog.show()
    }
}