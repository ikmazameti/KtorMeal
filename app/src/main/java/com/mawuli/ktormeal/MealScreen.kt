package com.mawuli.ktormeal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.collections.orEmpty
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MealScreen(
    modifier: Modifier = Modifier,
    viewModel: MealViewModel = viewModel(),
) {
    val mealsState by viewModel.meals.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getMeals()
    }

    if (mealsState.meals.orEmpty().isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(mealsState.meals.orEmpty()) { meal ->
                meal?.let {
                    MealCard(meal = meal)
                }
            }
        }
    }
}


@Composable
fun MealCard(
    meal: MealsItem,
    modifier: Modifier = Modifier
) {
    var imageLoaded by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (imageLoaded) 1f else 0f,
        animationSpec = tween(900, easing = LinearOutSlowInEasing)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .glassEffect(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {

        Column {

            // IMAGE HEADER
            Box(
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
            ) {
                // Meal Image (no blur)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(meal.strMealThumb)
                        .crossfade(true)
                        .build(),
                    contentDescription = meal.strMeal,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .alpha(alphaAnim),
                    onSuccess = { imageLoaded = true },
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.65f)
                                ),
                                startY = 80f
                            )
                        )
                )

                // Text on top of gradient
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = meal.strMeal ?: "",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${meal.strCategory} • ${meal.strArea}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }

            // INGREDIENT PREVIEW BELOW IMAGE
            val ingredients = extractIngredients(meal)

            Text(
                text = ingredients.take(3).joinToString(", "),
                modifier = Modifier.padding(16.dp),
                fontSize = 14.sp,
                color = Color(0xFF444444),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


fun Modifier.glassEffect(): Modifier {
    return this
        .background(
            brush = Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0.05f)
                )
            ), shape = RoundedCornerShape(20.dp)
        )
        .border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.3f),
            shape = RoundedCornerShape(20.dp)
        )
        .clip(RoundedCornerShape(20.dp))
}

fun extractIngredients(meal: MealsItem) = listOfNotNull(
    meal.strIngredient1,
    meal.strIngredient2,
    meal.strIngredient3,
    meal.strIngredient4,
    meal.strIngredient5,
    meal.strIngredient6
).filter { it.isNotBlank() }

// -------- PREVIEW DATA --------
private val sampleMeal = MealsItem(
    idMeal = "1234",
    strMeal = "Jollof Rice with Grilled Chicken",
    strMealThumb = "https://www.themealdb.com/images/media/meals/58oia61564916529.jpg",
    strCategory = "African",
    strArea = "Ghana",
    strIngredient1 = "Rice",
    strIngredient2 = "Tomato",
    strIngredient3 = "Onion",
    strIngredient4 = "Pepper",
    strIngredient5 = "Chicken",
    strIngredient6 = "Seasoning"
)

@Composable
@Preview(showBackground = true)
fun MealCardPreview() {
    MealCard(meal = sampleMeal)
}

@Composable
@Preview(showBackground = true)
fun MealScreenPreview() {
    val fakeState = Meal(
        meals = listOf(sampleMeal, sampleMeal, sampleMeal)
    )

    // Fake VM-less screen
    LazyColumn {
        items(fakeState.meals.orEmpty()) { meal ->
            meal?.let { MealCard(meal = it) }
        }
    }
}
