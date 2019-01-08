package se.filtermap.admdev.filteredmapview.model

import com.google.android.gms.maps.model.Marker

data class CityMarker(
    val marker: Marker,
    val city: City
)