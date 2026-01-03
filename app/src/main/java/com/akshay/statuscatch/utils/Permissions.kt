package com.akshay.statuscatch.utils

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract

fun buildFolderPickerIntent(initialUri: Uri): Intent {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri)
    intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
    return intent
}