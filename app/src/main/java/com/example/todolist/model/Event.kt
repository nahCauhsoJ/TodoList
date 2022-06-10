package com.example.todolist.model

import java.io.Serializable

data class Event(
    val name: String,
    val notes:String,
    val date: Long,
    val category: String,
    val notifyDaysBefore: Int,
    val uuid: String
) : Serializable
