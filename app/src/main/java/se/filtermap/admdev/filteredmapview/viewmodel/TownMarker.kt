package se.filtermap.admdev.filteredmapview.viewmodel

import com.google.android.gms.maps.model.Marker

// Feel very unsure about storing these things in the viewModel as they are tied to the google map
data class TownMarker(
    val marker: Marker,
    val population: Int
)