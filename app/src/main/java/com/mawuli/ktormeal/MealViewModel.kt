package com.mawuli.ktormeal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {

    private val _meals = MutableStateFlow(Meal(emptyList()))
    val meals: MutableStateFlow<Meal> = _meals


    fun getMeals() {
        viewModelScope.launch {

            try {
                val response = NetworkClient.client.get("https://www.themealdb.com/api/json/v1/1/search.php?f=a")
                val meals = response.body<Meal>()
                _meals.value = meals

            } catch (e: Exception) {
                Log.e("MealViewModel", "Error getting meals", e)
            }
        }
    }


}