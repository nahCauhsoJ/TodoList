package com.example.todolist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.todolist.databinding.FragmentEditBinding
import com.example.todolist.model.Event
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

class EditFragment : Fragment() {

    private val binding by lazy { FragmentEditBinding.inflate(layoutInflater) }
    private val selectedCal: Calendar = Calendar.getInstance()
    private var eventNotifyValue: Int = 0
    private var editEvent: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            editEvent = it.getSerializable("EditEvent") as Event?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // User is only allowed to choose at least tomorrow. The
        //      default day should be the same after onCreateView().
        binding.eventDateEntry.minDate = System.currentTimeMillis() + 86400000
        selectedCal.timeInMillis = binding.eventDateEntry.minDate
        binding.eventDateEntry.setOnDateChangeListener { _, y, m, d ->
            selectedCal.set(y,m,d)
            val daysRemain: Int =
                ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    Instant.ofEpochMilli(selectedCal.timeInMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate() ).toInt()
            if (daysRemain > 0) binding.eventNotifyEntry.max = daysRemain
        }

        binding.eventNotifyToggle.setOnClickListener {
            if (binding.eventNotifyToggle.isChecked) {
                binding.eventNotifyEntry.visibility = View.VISIBLE
                binding.eventNotifyValue.visibility = View.VISIBLE
            } else {
                binding.eventNotifyEntry.visibility = View.GONE
                binding.eventNotifyValue.visibility = View.GONE
            }
        }
            binding.eventNotifyValue.text = eventNotifyDaysString(eventNotifyValue)
        binding.eventNotifyEntry.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    eventNotifyValue = progress
                    binding.eventNotifyValue.text = eventNotifyDaysString(eventNotifyValue)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        binding.saveEventBtn.setOnClickListener {
            Event(
                binding.eventNameEntry.text.toString(),
                binding.eventNotesEntry.text.toString(),
                selectedCal.timeInMillis,
                binding.eventCategoryEntry.text.toString(),
                if (binding.eventNotifyToggle.isChecked)
                    binding.eventNotifyEntry.progress
                else -1,
                editEvent?.uuid ?: UUID.randomUUID().toString()
            ).also {
                findNavController().navigate(R.id.action_EditToItemListFragment, bundleOf(
                    Pair("Event", it)
                ))
            }
        }

        return binding.root
    }

    private fun eventNotifyDaysString(days: Int): String {
        return when (eventNotifyValue) {
            0 -> "Same day"
            1 -> "The day before"
            else -> "$eventNotifyValue days before"
        }
    }

    override fun onResume() {
        super.onResume()
        editEvent?.let {
            binding.eventNameEntry.setText(it.name)
            binding.eventNotesEntry.setText(it.notes)
            binding.eventCategoryEntry.setText(it.category)
            binding.eventDateEntry.date = it.date
            if (it.notifyDaysBefore != -1)
            {
                binding.eventNotifyToggle.performClick()
                binding.eventNotifyEntry.progress = it.notifyDaysBefore
            }
            
            arguments = null
        }
    }
}