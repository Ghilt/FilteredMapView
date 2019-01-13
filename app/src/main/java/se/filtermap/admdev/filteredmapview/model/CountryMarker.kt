package se.filtermap.admdev.filteredmapview.model

import com.google.android.gms.maps.model.Marker

//TODO make country maker show how many city markers in them that are shown
data class CountryMarker(
    val marker: Marker,
    val country: Country
)