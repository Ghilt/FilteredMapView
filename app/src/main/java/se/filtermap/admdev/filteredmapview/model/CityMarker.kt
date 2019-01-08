package se.filtermap.admdev.filteredmapview.model

import com.google.android.gms.maps.model.Marker

// Feel very unsure about storing these things in the viewModel as they are tied to the google map
data class CityMarker(
    val marker: Marker,
    val population: Int
)