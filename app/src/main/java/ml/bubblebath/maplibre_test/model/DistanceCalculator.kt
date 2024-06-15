package ml.bubblebath.maplibre_test.model

import org.maplibre.android.geometry.LatLng

class DistanceCalculator {
    private val points = mutableListOf<LatLng>()
    private var distanceLastAndSecond = 0.0
    private var totalLineDistance = 0.0

    fun addWaypoint(latLng: LatLng) {
        points.add(latLng)
        if (points.size >= 2) {
            calculateLastAndPreviousDistance()
            calculateTotalDistance()
        }
    }

    private fun calculateLastAndPreviousDistance() {
        distanceLastAndSecond = points[points.size - 2].distanceTo(points[points.size - 1])
    }

    private fun calculateTotalDistance() {
        totalLineDistance += distanceLastAndSecond
    }

    fun getDistance(units: DistanceUnits): Pair<Double, Double>? {
        return if (points.size >= 2) {
            when (units) {
                DistanceUnits.M -> distanceLastAndSecond to totalLineDistance
                DistanceUnits.KM -> distanceLastAndSecond / 1000.0 to totalLineDistance / 1000.0
            }
        } else {
            null
        }
    }

    fun getPoints() = points.toList()

    fun reset() {
        points.clear()
        distanceLastAndSecond = 0.0
        totalLineDistance = 0.0
    }
}
