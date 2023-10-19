package com.schpro.project.data.models

enum class Roles(val optionalName: String)  {
    ProjectManager("Project Manager"),
    ProjectTeam("Project Team");

    companion object {
        fun fromString(value: String): Roles {
            val copy = value.lowercase()
            if (copy.contains("manager") || copy == "project manager") {
                return Roles.ProjectManager;
            }

            return Roles.ProjectTeam
        }
    }
}