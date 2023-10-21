package com.schpro.project.data.models

data class Dashboard(
    var totalProject: Int = 0,
    var totalProjectOnGoing: Int = 0,
    var totalProjectComplete: Int = 0,
    var totalTaskTodo: Int = 0,
    var totalTaskOnGoing: Int = 0,
    var totalTaskDone: Int = 0,
)