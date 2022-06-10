package com.example.todolist

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController

import com.example.todolist.databinding.TodoCardBinding
import com.example.todolist.model.Event
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ItemListAdapter(
    private val eventList: MutableList<Event> = mutableListOf()
) : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun refreshEventFromFile() {
        IOHandler.getEvent()?.let {
            eventList.clear()
            eventList.addAll(it)
            eventList.sortBy { x -> x.date }
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addEvent(event: Event) {
        eventList.add(event)
        IOHandler.addEvent(event)
        eventList.sortBy { x -> x.date }
        notifyDataSetChanged()
        //notifyItemInserted(eventList.indexOf(event))
    }
    fun removeEvent(event: Event) {
        // List is already sorted before and after removing.
        val index: Int = eventList.indexOf(event)
        eventList.removeAt(index)
        IOHandler.removeEvent(event)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            TodoCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size

    inner class ViewHolder(
        private val binding: TodoCardBinding
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind (event: Event) {
            if (event.name.isNotEmpty()) binding.eventName.text = event.name
            if (event.category.isNotEmpty()) binding.eventCategory.text = event.category
            binding.eventDate.text = Instant.ofEpochMilli(event.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(dateFormatDisplay)

            binding.root.setOnClickListener {
                it.findNavController().navigate(
                    R.id.action_ItemListToDetailsFragment, bundleOf(
                        Pair("EventDetail", event)
                    )
                )
            }
        }
    }

    companion object {
        val dateFormatDisplay: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
    }
}