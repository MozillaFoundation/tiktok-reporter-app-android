package org.mozilla.tiktokreporter.util

val emptyCallback: () -> Unit = { }

object Common {
    const val PREFERENCES_FIRST_ACCESS_KEY = "first_access"
    const val PREFERENCES_TERMS_ACCEPTED_KEY = "terms_accepted"
    const val PREFERENCES_ONBOARDING_COMPLETED_KEY = "onboarding_completed"
    const val PREFERENCES_SELECTED_STUDY_KEY = "selected_study"
    const val PREFERENCES_USER_EMAIL_KEY = "user_email"
}
