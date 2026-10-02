package com.akshay.statuscatch.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DocumentFileUtils"

/**
 * Try to extract last modified time from a DocumentFile using multiple strategies.
 * Returns epoch milliseconds or null if not available.
 */
fun getLastModifiedMillis(context: Context, documentFile: DocumentFile): Long? {
    try {
        // 1) Prefer DocumentFile.lastModified() if it's available and > 0
        val lastModified = documentFile.lastModified()
        if (lastModified > 0L) {
            return lastModified
        }

        // 2) Try querying DocumentsContract for LAST_MODIFIED (API 19+)
        try {
            val treeUri: Uri = documentFile.uri
            val docId = DocumentsContract.getDocumentId(treeUri)
            val docUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, docId)

            val projection = arrayOf(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
            context.contentResolver.query(docUri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
                    if (idx >= 0) {
                        val v = cursor.getLong(idx)
                        if (v > 0L) return v
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "DocumentsContract query failed: ${e.message}")
        }

        // 3) Try MediaStore query for DATE_MODIFIED (convert to millis)
        try {
            val uri = documentFile.uri
            val projection = arrayOf(MediaStore.MediaColumns.DATE_MODIFIED)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)
                    if (idx >= 0) {
                        val seconds = cursor.getLong(idx)
                        if (seconds > 0L) return seconds * 1000L
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "MediaStore query failed: ${e.message}")
        }

    } catch (e: SecurityException) {
        Log.d(TAG, "SecurityException while getting last modified: ${e.message}")
    } catch (e: Exception) {
        Log.d(TAG, "Unexpected exception while getting last modified: ${e.message}")
    }

    return null
}

/**
 * Try to get a creation/recorded time for the document file. This prefers EXIF/DATE_TAKEN when available
 * (represents when the media was created/taken) and falls back to media store / metadata.
 */
fun getCreationMillis(context: Context, documentFile: DocumentFile, mediaType: String?): Long? {
    val uri = documentFile.uri

    // 1) Try MediaStore DATE_TAKEN / DATE_ADDED
    try {
        val projection = arrayOf(MediaStore.MediaColumns.DATE_TAKEN, MediaStore.MediaColumns.DATE_ADDED)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                // DATE_TAKEN is in millis (if available)
                val idxTaken = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_TAKEN)
                if (idxTaken >= 0) {
                    val v = cursor.getLong(idxTaken)
                    if (v > 0L) return v
                }
                // DATE_ADDED is in seconds since epoch
                val idxAdded = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED)
                if (idxAdded >= 0) {
                    val secs = cursor.getLong(idxAdded)
                    if (secs > 0L) return secs * 1000L
                }
            }
        }
    } catch (e: Exception) {
        Log.d(TAG, "MediaStore DATE_TAKEN/DATE_ADDED query failed: ${e.message}")
    }

    // 2) If image, try EXIF DateTimeOriginal or DateTime
    if (mediaType != null && mediaType.startsWith("image", ignoreCase = true)) {
        try {
            context.contentResolver.openInputStream(uri)?.use { input: InputStream ->
                val exif = ExifInterface(input)
                val dt = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                    ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
                if (!dt.isNullOrEmpty()) {
                    // EXIF datetime format: "yyyy:MM:dd HH:mm:ss"
                    val sdf = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
                    try {
                        val date = sdf.parse(dt)
                        if (date != null) return date.time
                    } catch (pe: ParseException) {
                        Log.d(TAG, "EXIF parse failed: ${pe.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exif extraction failed: ${e.message}")
        }
    }

    // 3) If video, try MediaMetadataRetriever METADATA_KEY_DATE or other metadata
    if (mediaType != null && mediaType.startsWith("video", ignoreCase = true)) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val dateMetadata = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
            if (!dateMetadata.isNullOrEmpty()) {
                // try parsing as long millis or seconds
                val digits = dateMetadata.filter { it.isDigit() }
                try {
                    if (digits.length >= 13) {
                        return digits.toLong() // assume millis
                    } else if (digits.length >= 10) {
                        return digits.toLong() * 1000L // assume seconds
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Video metadata date parse failed: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "MediaMetadataRetriever failed: ${e.message}")
        } finally {
            try { retriever.release() } catch (_: Exception) {}
        }
    }

    // 4) As a last attempt, fallback to lastModified
    return try {
        getLastModifiedMillis(context, documentFile)
    } catch (e: Exception) {
        Log.d(TAG, "Fallback to lastModified failed: ${e.message}")
        null
    }
}
