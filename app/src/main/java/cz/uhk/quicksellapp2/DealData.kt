package cz.uhk.quicksellapp2

import java.io.Serializable

data class DealData(val title: String, val foreign: Boolean, val distance: Double) : Serializable
