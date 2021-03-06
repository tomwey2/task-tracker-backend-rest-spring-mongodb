package de.tom.demo.taskapp

object Constants {
    const val TASK_CREATED = "Created"
    const val TASK_OPEN = "Open"
    const val TASK_IN_PROGRESS = "In Progress"
    const val TASK_RESOLVED = "Resolved"
    const val TASK_CLOSED = "Closed"

    const val ROLE_USER = "ROLE_USER"
    const val ROLE_ADMIN = "ROLE_ADMIN"

    const val URI_LOCALHOST = "http://localhost"
    const val PATH_LOGIN = "/login"
    const val PATH_REGISTER = "/register"
    const val PATH_TASKS = "/api/tasks"
    const val PATH_USERS = "/api/users"
    const val PATH_REFRESHTOKEN = "/refreshtoken"

    const val ACCESS_TOKEN_EXPIRED_IN_MSEC: Long = 50 * 60 * 1000         // 50 minutes
    const val REFRESH_TOKEN_EXPIRED_IN_MSEC: Long = 24 * 60 * 60 * 1000   // 1 day
}