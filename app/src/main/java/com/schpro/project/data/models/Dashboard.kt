package com.schpro.project.data.models

data class Dashboard(
    val totalProject: Int = 0,
    val totalProjectOnGoing: Int = 0,
    val totalProjectComplete: Int = 0,
    val totalTaskTodo: Int = 0,
    val totalTaskOnGoing: Int = 0,
    val totalTaskDone: Int = 0,
)