package ml.bubblebath.maplibre_test.di

import ml.bubblebath.maplibre_test.model.DistanceCalculator
import ml.bubblebath.maplibre_test.ui.screens.main.MainScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { DistanceCalculator() }
    viewModel { MainScreenViewModel(distanceCalculator = get()) }
}