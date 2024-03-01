package fr.isen.etavard.androiderestaurant

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.etavard.androiderestaurant.ui.theme.AndroidERestaurantTheme

enum class DishType {
    STARTER, MAIN, DESSERT;

    @Composable
    fun title(): String {
        return when (this) {
            STARTER -> stringResource(id = R.string.menu_starter)
            MAIN -> stringResource(id = R.string.menu_main)
            DESSERT -> stringResource(id = R.string.menu_dessert)
        }
    }
}

interface MenuInterface {
    fun dishPressed(dishType: DishType)
}

class HomeActivity : ComponentActivity(), MenuInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    SetupView(this)
                }
            }
        }
    }

    override fun dishPressed(dishType: DishType) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra(MenuActivity.CATEGORY_EXTRA_KEY, dishType)
        startActivity(intent)
    }

    override fun onPause() {
        Log.d("lifeCycle", "Home Activity - OnPause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifeCycle", "Home Activity - OnResume")
    }

    override fun onDestroy() {
        Log.d("lifeCycle", "Home Activity - onDestroy")
        super.onDestroy()
    }
}

@Composable
fun SetupView(menu: MenuInterface) {
    val imageModifier = Modifier
        .size(100.dp)
        .clip(CircleShape)
        .border(BorderStroke(15.dp, (Color(255, 100, 10))), CircleShape)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.background(Color(176, 196, 222))
    ) {
        Image(
            painterResource(R.drawable.ogusteau),
            null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
        Text(
            text = "Cookie Zen",
            fontSize = 66.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(255, 181, 59),
            fontFamily = FontFamily(Font(R.font.lobster_regular)),
            fontStyle = FontStyle.Italic,
            style = TextStyle(
                shadow = Shadow(
                    color = Color(255, 0, 0), offset = Offset(5.0f, 5.0f), blurRadius = 3f
                )
            )
        )
        Divider()
        CustomButton(type = DishType.STARTER, menu)
        Divider()
        CustomButton(type = DishType.MAIN, menu)
        Divider()
        CustomButton(type = DishType.DESSERT, menu)
    }
}

@Composable
fun CustomButton(type: DishType, menu: MenuInterface) {
    TextButton(onClick = { menu.dishPressed(type) }) {
        Text(
            type.title(),
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0, 0, 0),
            style = TextStyle(textDecoration = TextDecoration.Underline),
            fontFamily = FontFamily(Font(R.font.protest_riot_regular))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidERestaurantTheme {
        SetupView(HomeActivity())
    }
}