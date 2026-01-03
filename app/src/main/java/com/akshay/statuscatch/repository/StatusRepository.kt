package com.akshay.statuscatch.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import com.akshay.statuscatch.model.MEDIA_TYPE_IMAGE
import com.akshay.statuscatch.model.MEDIA_TYPE_VIDEO
import com.akshay.statuscatch.model.MediaModel
import com.akshay.statuscatch.utils.Constants
import com.akshay.statuscatch.utils.SharedPrefKeys
import com.akshay.statuscatch.utils.SharedPrefUtils
import com.akshay.statuscatch.utils.getFileExtension
import com.akshay.statuscatch.utils.isStatusExist
import com.akshay.statuscatch.utils.getLastModifiedMillis
import com.akshay.statuscatch.utils.getCreationMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.joinAll

class StatusRepository(val context: Context, private val metadataConcurrency: Int = 4) {

    val whatsAppStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val whatsAppBusinessStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()

    val activity = context as Activity

    private val wpStatusesList = ArrayList<MediaModel>()
    private val wpBusinessStatusesList = ArrayList<MediaModel>()
    
    private val TAG = "StatusRepo"

    fun getAllStatuses(whatsAppType: String = Constants.TYPE_WHATSAPP_MAIN) {
        // clear previous lists to avoid duplicates across multiple calls
        wpStatusesList.clear()
        wpBusinessStatusesList.clear()

        val treeUri = when (whatsAppType) {
            Constants.TYPE_WHATSAPP_MAIN -> {
                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, "")?.toUri()!!
            }

            else -> {
                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI, "")
                    ?.toUri()!!
            }
        }
        Log.d(TAG, "getAllStatuses: $treeUri")

        activity.contentResolver.takePersistableUriPermission(
            treeUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val fileDocument = DocumentFile.fromTreeUri(activity, treeUri)

        fileDocument?.let {
            it.listFiles().forEach { file->
                Log.d(TAG, "getAllStatuses: ${file.name}")
                if (file.name != ".nomedia" && file.isFile) {
                    val isDownloaded = context.isStatusExist(file.name!!)
                    Log.d(TAG, "getAllStatusesExtension: Extension: ${getFileExtension(file.name!!)} ||${file.name}")
                    val type = if (getFileExtension(file.name!!) == "mp4") {
                        MEDIA_TYPE_VIDEO
                    } else {
                        MEDIA_TYPE_IMAGE
                    }

                    // get last modified epoch millis and creation epoch millis (nullable)
                    val lastModifiedEpoch = try {
                        getLastModifiedMillis(context, file)
                    } catch (e: Exception) {
                        Log.d(TAG, "Error getting last modified: ${e.message}")
                        null
                    }
                    val creationEpoch = try {
                        getCreationMillis(context, file, type)
                    } catch (e: Exception) {
                        Log.d(TAG, "Error getting creation time: ${e.message}")
                        null
                    }

                    val model = MediaModel(
                        pathUri = file.uri.toString(),
                        fileName = file.name!!,
                        type = type,
                        isDownloaded = isDownloaded,
                        lastModifiedEpochMs = lastModifiedEpoch,
                        creationEpochMs = creationEpoch
                    )
                    when (whatsAppType) {
                        Constants.TYPE_WHATSAPP_MAIN -> {

                            wpStatusesList.add(model)
                        }

                        else -> {
                            wpBusinessStatusesList.add(model)
                        }

                    }

                }
            }

            // Post initial lists right away (unsorted or partially sorted)
            when (whatsAppType) {
                Constants.TYPE_WHATSAPP_MAIN -> {
                    whatsAppStatusesLiveData.postValue(ArrayList(wpStatusesList))
                }

                else -> {
                    whatsAppBusinessStatusesLiveData.postValue(ArrayList(wpBusinessStatusesList))
                }
            }

            // Launch background coroutines to fetch any missing/updated metadata per item
            CoroutineScope(Dispatchers.IO).launch {
                // controlled concurrency using a semaphore
                val concurrency = metadataConcurrency.coerceAtLeast(1)
                val semaphore = Semaphore(concurrency)

                val jobs = if (whatsAppType == Constants.TYPE_WHATSAPP_MAIN) {
                    wpStatusesList.map { model ->
                        launch {
                            semaphore.withPermit {
                                var updated = false
                                try {
                                    val doc = try { DocumentFile.fromSingleUri(context, model.pathUri.toUri()) } catch (_: Exception) { null }
                                    if (doc != null) {
                                        if (model.creationEpochMs == null) {
                                            val c = getCreationMillis(context, doc, model.type)
                                            if (c != null) {
                                                model.creationEpochMs = c
                                                updated = true
                                            }
                                        }
                                        if (model.lastModifiedEpochMs == null) {
                                            val lm = getLastModifiedMillis(context, doc)
                                            if (lm != null) {
                                                model.lastModifiedEpochMs = lm
                                                updated = true
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.d(TAG, "Error updating metadata for ${model.fileName}: ${e.message}")
                                }

                                if (updated) {
                                    withContext(Dispatchers.Main) {
                                        wpStatusesList.sortWith(compareByDescending<MediaModel> { it.creationEpochMs ?: it.lastModifiedEpochMs ?: 0L })
                                        whatsAppStatusesLiveData.postValue(ArrayList(wpStatusesList))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    wpBusinessStatusesList.map { model ->
                        launch {
                            semaphore.withPermit {
                                var updated = false
                                try {
                                    val doc = try { DocumentFile.fromSingleUri(context, model.pathUri.toUri()) } catch (_: Exception) { null }
                                    if (doc != null) {
                                        if (model.creationEpochMs == null) {
                                            val c = getCreationMillis(context, doc, model.type)
                                            if (c != null) {
                                                model.creationEpochMs = c
                                                updated = true
                                            }
                                        }
                                        if (model.lastModifiedEpochMs == null) {
                                            val lm = getLastModifiedMillis(context, doc)
                                            if (lm != null) {
                                                model.lastModifiedEpochMs = lm
                                                updated = true
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.d(TAG, "Error updating metadata for ${model.fileName}: ${e.message}")
                                }

                                if (updated) {
                                    withContext(Dispatchers.Main) {
                                        wpBusinessStatusesList.sortWith(compareByDescending<MediaModel> { it.creationEpochMs ?: it.lastModifiedEpochMs ?: 0L })
                                        whatsAppBusinessStatusesLiveData.postValue(ArrayList(wpBusinessStatusesList))
                                    }
                                }
                            }
                        }
                    }
                }

                // wait for all launched jobs to finish
                if (jobs.isNotEmpty()) {
                    joinAll(*jobs.toTypedArray())
                }
            }

        }


    }


}
