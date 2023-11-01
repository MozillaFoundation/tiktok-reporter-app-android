package org.mozilla.tiktokreporter.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mozilla.tiktokreporter.data.model.Policy
import org.mozilla.tiktokreporter.data.model.StudyDetails
import org.mozilla.tiktokreporter.data.model.StudyOverview
import org.mozilla.tiktokreporter.data.model.toPolicy
import org.mozilla.tiktokreporter.data.model.toStudyDetails
import org.mozilla.tiktokreporter.data.model.toStudyOverview
import org.mozilla.tiktokreporter.data.remote.TikTokReporterService
import org.mozilla.tiktokreporter.util.sharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TikTokReporterRepository @Inject constructor(
    private val tikTokReporterService: TikTokReporterService,
    @ApplicationContext private val context: Context
) {

    var selectedStudyId by context.sharedPreferences(name = "selected_study", defaultValue = "")
        private set
    var onboardingCompleted by context.sharedPreferences(name = "onboarding_completed", defaultValue = false)
        private set

    private var selectedStudy: StudyDetails? = null

    suspend fun getAppTermsAndConditions(): Policy? {
        val policies = withContext(Dispatchers.IO) {
            return@withContext tikTokReporterService.getAppTermsAndConditions()
                .map { it.toPolicy() }
        }

        return policies.firstOrNull { it.type == Policy.Type.TermsAndConditions }
    }

    suspend fun fetchStudies(): List<StudyOverview> {
        val remoteStudies = tikTokReporterService.getStudies()
            .map {
                it.toStudyOverview(
                    isSelected = it.id == selectedStudyId
                )
            }

        return remoteStudies
    }

    suspend fun fetchStudyById(studyId: String): StudyDetails {
        val remoteStudy = tikTokReporterService.getStudyById(studyId)

        return remoteStudy.toStudyDetails()
    }

    suspend fun getSelectedStudy(): StudyDetails {
        if (selectedStudy == null) {
            val study = fetchStudyById(selectedStudyId)
            selectedStudy = study
        }

        return selectedStudy!!
    }

    suspend fun selectStudy(studyId: String) {
        withContext(Dispatchers.IO) {
            selectedStudyId = studyId
        }
    }
}