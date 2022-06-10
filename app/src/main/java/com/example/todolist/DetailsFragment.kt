package com.example.todolist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.todolist.databinding.FragmentDetailsBinding
import com.example.todolist.model.Event
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class DetailsFragment : Fragment() {
    private val binding by lazy { FragmentDetailsBinding.inflate(layoutInflater) }
    private var selectedEvent : Event ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedEvent = it.getSerializable("EventDetail") as Event?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.completeEventBtn.setOnClickListener {
            findNavController().navigate(R.id.action_DetailsToItemListFragment, bundleOf(
                Pair("DoneEvent", selectedEvent)
            ))
        }

        binding.editEventBtn.setOnClickListener {
            findNavController().navigate(R.id.action_DetailsToEditFragment, bundleOf(
                Pair("EditEvent", selectedEvent)
            ))
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        selectedEvent?.let {
            binding.eventNameDetails.text =
                getString(R.string.eventDetailNameLabel,
                    it.name.ifEmpty { getString(R.string.noEventName) })
            binding.eventNotesDetails.text =
                getString(R.string.eventDetailNotesLabel,
                    it.notes.ifEmpty { getString(R.string.noEventNotes) })
            binding.eventCategoryDetails.text =
                getString(R.string.eventDetailCategoryLabel,
                    it.category.ifEmpty { getString(R.string.noEventCategory) })

            val date: LocalDate = Instant.ofEpochMilli(it.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            binding.eventDateDetails.text =
                getString(R.string.eventDetailDateLabel,
                    date.format(ItemListAdapter.dateFormatDisplay))
            binding.eventDateDetailsInDays.text =
                getString(R.string.eventDetailDateDaysLabel,
                    ChronoUnit.DAYS.between(LocalDate.now(),date))

            binding.eventNotifyDetails.text =
                getString(R.string.eventDetailNotifyLabel,
                    when (it.notifyDaysBefore) {
                        -1 -> "Not set"
                        0 -> "On due day"
                        1 -> "1 day before due date"
                        else -> "${it.notifyDaysBefore} days before due date"
                    })
        }
    }
}