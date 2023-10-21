package com.schpro.project.core

const val HARUS_ISI = " Harus Isi"
const val COMMON_ERROR = "Error, Something Wrong";

object FireStoreCollection {
    const val USER = "user"
    const val PROJECT = "project"
    const val SPRINT = "sprint"
    const val TASK = "task"
}

object FireStoreFields {
    const val PROJECT_ID = "projectId"
    const val SPRINT_ID = "sprintId"
    const val STATUS = "status"
    const val SPRINT = "sprints"
    const val MEMBERS = "members"
    const val USER_ID = "userId"
    const val CREATED_DATE = "createdDate"
    const val BY_USER = "byUser.id"
}

object FirebaseStorageConstants {
    const val ROOT_DIR = "app"
    const val USER = "user"
}

object SharePrefsConstants {
    const val LOCAL_SHARED_PREF = "schpro_apps_pref"
    const val USER_SESSION = "user_session"
}