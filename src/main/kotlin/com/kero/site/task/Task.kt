package com.kero.site.task

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class Task(
    @Id @GeneratedValue
    val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false
)
