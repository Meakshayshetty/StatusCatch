package com.akshay.statuscatch.model

import java.io.Serializable

const val MEDIA_TYPE_IMAGE = "image"
const val MEDIA_TYPE_VIDEO = "video"

data class MediaModel(
    val pathUri: String,
    val fileName: String,
    val type: String = MEDIA_TYPE_IMAGE,
    var isDownloaded: Boolean = false,
    var lastModifiedEpochMs: Long? = null, // epoch millis when the file was last modified (nullable)
    var creationEpochMs: Long? = null // epoch millis when the file was created/taken (nullable)
):Serializable