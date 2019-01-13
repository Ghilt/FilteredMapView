package se.filtermap.admdev.filteredmapview.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.opencsv.CSVReader
import se.filtermap.admdev.filteredmapview.R
import se.filtermap.admdev.filteredmapview.model.City
import se.filtermap.admdev.filteredmapview.model.Country
import java.io.InputStreamReader

class FilterMapViewModel(app: Application) : AndroidViewModel(app) {

    private lateinit var populations: MutableLiveData<List<City>>
    private lateinit var countries: MutableLiveData<List<Country>>

    fun getPopulations(): LiveData<List<City>> {
        if (!::populations.isInitialized) {
            initialize()
        }
        return populations
    }

    fun getCountries(): LiveData<List<Country>> {
        if (!::countries.isInitialized) {
            initialize()
        }
        return countries
    }

    private fun initialize() {
        populations = MutableLiveData()
        countries = MutableLiveData()
        loadCityData()
    }

    private fun loadCityData() {

        val columnName = 0
        val columnLat = 2
        val columnLng = 3
        val columnCountry = 4
        val columnCapital = 8
        val columnPopulation = 9
        val capitalPrimary = "primary"

        val inputStream = getApplication<Application>().resources.openRawResource(R.raw.worldcities)
        val reader = CSVReader(InputStreamReader(inputStream))
        val cityValues = reader.readAll().drop(1)

        populations.value = cityValues
            .filter { csvRow -> csvRow[columnPopulation].toIntOrNull() != null }
            .map { csvRow ->
                City(
                    csvRow[columnName],
                    csvRow[columnPopulation].toInt(),
                    csvRow[columnLat].toDouble(),
                    csvRow[columnLng].toDouble()
                )
            }

        countries.value = cityValues
            .groupBy { it -> it[columnCountry] }
            .filter { it.value.isNotEmpty() }
            .map { countryGroup ->
                countryGroup.value.find { it[columnCapital] == capitalPrimary } ?: countryGroup.value[0]
            }
            .map { csvRow ->
                Country(
                    csvRow[columnCountry],
                    csvRow[columnLat].toDouble(),
                    csvRow[columnLng].toDouble()
                )
            }
    }
}