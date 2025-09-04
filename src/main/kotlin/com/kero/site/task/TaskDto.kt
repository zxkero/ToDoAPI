package com.kero.site.task

data class TaskResponseDto(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val creationTime: String
)
