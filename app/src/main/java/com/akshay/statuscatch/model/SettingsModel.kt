package com.akshay.statuscatch.model

data class SettingsModel(
    val image :Int,
    val title:String,
    val desc:String
)
data class HowToUse(
    var heading:String,
    var desc:String
)