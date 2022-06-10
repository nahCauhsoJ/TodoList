package com.example.todolist

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.todolist.databinding.FragmentItemListBinding
import com.example.todolist.model.Event

class ItemListFragment : Fragment() {
    private val binding by lazy {
         FragmentItemListBinding.inflate(layoutInflater)
    }
    private val eventAdapter by lazy { ItemListAdapter() }
    private var newEvent: Event? = null
    private var daysRemain: Int? = null
    private var doneEvent: Event? = null

    private var adapter_late_file_get: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            newEvent = it.getSerializable("Event")?.let { it2 -> it2 as Event }
            doneEvent = it.getSerializable("DoneEvent")?.let { it2 -> it2 as Event }
            daysRemain = it.getInt("DaysRemain")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.todoListRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventAdapter
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_ItemListToEditFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (!adapter_late_file_get)
        {   // The retrieval isn't complete at onCreateView(). So...
            eventAdapter.refreshEventFromFile()
            adapter_late_file_get = true
        }

        newEvent?.let {
            eventAdapter.addEvent(it)
            newEvent = null
            arguments = null
            binding.noEventsText.visibility = View.INVISIBLE

            var eventNotifyMessage = ""
            if (it.notifyDaysBefore >= 0)
            {
                eventNotifyMessage = " You'll be notified " +
                        when (it.notifyDaysBefore) {
                            0 -> "on that day."
                            1 -> "on the day before."
                            else -> "${it.notifyDaysBefore} days before."
                        }
            }

            var eventName: String = getString(R.string.noEventName)
            if (it.name.isNotEmpty()) eventName = it.name
            Toast.makeText(
                context,
                String.format(
                    "Saved %s.%s",
                    eventName, eventNotifyMessage),
                Toast.LENGTH_LONG
            ).show()
        }

        doneEvent?.let {
            eventAdapter.removeEvent(it)
            doneEvent = null
            arguments = null
        }

        if (eventAdapter.itemCount == 0) binding.noEventsText.visibility = View.VISIBLE
    }
}