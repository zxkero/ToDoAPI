package com.kero.site.task

import jakarta.persistence.*

@Entity
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_generator")
    @SequenceGenerator(name = "task_generator", sequenceName = "task_seq", allocationSize = 1)
    var id: Int? = null,
    var title: String = "",
    var isCompleted: Boolean = false
)