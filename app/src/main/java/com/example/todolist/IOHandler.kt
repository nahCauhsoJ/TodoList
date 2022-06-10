package com.example.todolist

import com.example.todolist.model.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import java.lang.reflect.Type

object IOHandler {
    private const val file_name = "events.json"
    // Initialized by MainActivity,
    //      supposedly <root directory>/
    private lateinit var file_path: String
    private var eventList: MutableList<Event>? = null

    fun init(filePath: String) {
        file_path = filePath
        eventList = getEventsData()
    }

    fun addEvent(event: Event) {
        eventList?.let {
            it.removeAll { it2 -> it2.uuid == event.uuid }
            it.add(event)
        }
        storeEventsData()
    }
    fun removeEvent(event: Event) {
        eventList?.let {
            it.removeAll { it2 -> it2.uuid == event.uuid }
        }
        storeEventsData()
    }

    // Let's keep this for now.
    fun getEvent(): MutableList<Event>? = eventList

    private fun storeEventsData() {
        FileOutputStream(File(file_path, file_name)).apply {
            this.write(Gson().toJson(eventList).toByteArray())
            this.flush()
            this.close()
        }
    }

    private fun getEventsData(): MutableList<Event>? {
        FileInputStream(makeFile()).let {
            val stringBuilder: StringBuilder = StringBuilder()
            val bufferedReader= BufferedReader(InputStreamReader(it))

            var inputString: String?
            do {
                inputString = bufferedReader.readLine()
                inputString?.let { it2 -> stringBuilder.append(it2) }
            } while (inputString != null)
            val eventJsonString: String = stringBuilder.toString()

            it.close()

            return if (eventJsonString.isNotEmpty()) {
                val returnType: Type = TypeToken.getParameterized(
                    MutableList::class.java, Event::class.java).type
                Gson().fromJson(eventJsonString, returnType)
            } else mutableListOf()
        }
    }

    private fun makeFile(): File {
        val file = File(file_path, file_name)
        file.createNewFile()
        return file
    }
}