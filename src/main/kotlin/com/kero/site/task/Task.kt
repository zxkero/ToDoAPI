package com.kero.site.task

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_generator")
    @SequenceGenerator(name = "task_generator", sequenceName = "task_seq", allocationSize = 1)
    var id: Int? = null,
    var title: String = "",
    var description: String = "",
    var isCompleted: Boolean = false,

    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(pattern = "HH:mm dd.MM.yyyy")
    var creationTime: LocalDateTime? = null
)