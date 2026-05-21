package com.example.data

import kotlinx.coroutines.flow.Flow

class ContentRepository(private val db: AppDatabase) {
    val allDrafts: Flow<List<PostDraft>> = db.postDraftDao().getAllDrafts()
    val allTemplates: Flow<List<ContentTemplate>> = db.contentTemplateDao().getAllTemplates()
    val allProfiles: Flow<List<CollaborationProfile>> = db.collaborationProfileDao().getAllProfiles()

    suspend fun getDraftById(id: Int): PostDraft? {
        return db.postDraftDao().getDraftById(id)
    }

    suspend fun insertDraft(draft: PostDraft): Long {
        return db.postDraftDao().insertDraft(draft)
    }

    suspend fun updateDraft(draft: PostDraft) {
        db.postDraftDao().updateDraft(draft)
    }

    suspend fun deleteDraftById(id: Int) {
        db.postDraftDao().deleteDraftById(id)
    }

    suspend fun insertTemplate(template: ContentTemplate) {
        db.contentTemplateDao().insertTemplate(template)
    }

    suspend fun insertProfile(profile: CollaborationProfile) {
        db.collaborationProfileDao().insertProfile(profile)
    }
}
