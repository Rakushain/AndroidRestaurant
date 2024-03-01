package fr.isen.etavard.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import fr.isen.etavard.androiderestaurant.basket.Basket
import fr.isen.etavard.androiderestaurant.basket.BasketActivity
import fr.isen.etavard.androiderestaurant.network.Category
import fr.isen.etavard.androiderestaurant.network.Dish
import fr.isen.etavard.androiderestaurant.network.MenuResult
import fr.isen.etavard.androiderestaurant.network.NetworkConstants
import org.json.JSONObject

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = intent.getSerializableExtra(CATEGORY_EXTRA_KEY) as? DishType ?: DishType.STARTER
        setContent {
            MenuView(type)
        }
        Log.d("lifeCycle", "Menu Activity - OnCreate")
    }

    override fun onPause() {
        Log.d("lifeCycle", "Menu Activity - OnPause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifeCycle", "Menu Activity - OnResume")
    }

    override fun onDestroy() {
        Log.d("lifeCycle", "Menu Activity - onDestroy")
        super.onDestroy()
    }

    companion object {
        val CATEGORY_EXTRA_KEY = "CATEGORY_EXTRA_KEY"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuView(type: DishType) {
    val context = LocalContext.current
    val cartitems = Basket.current(context).items

    val cartsize = cartitems.sumBy { it.count}
    val category = remember {
        mutableStateOf<Category?>(null)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(176, 196, 222))
            .fillMaxSize()
    ) {
        CenterAlignedTopAppBar(title = {
            Text(
                type.title(), fontSize = 30.sp,
                modifier = Modifier.padding(30.dp),
                style = TextStyle(textDecoration = TextDecoration.Underline),
                fontFamily = FontFamily(Font(R.font.protest_riot_regular)),
            )
        }, colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color(176, 196, 255)
        ), navigationIcon = {
            IconButton(onClick = {
                val intent = Intent(context, HomeActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Filled.Home, contentDescription = "Home"
                )
            }
        }, actions = {
            BasketIconWithBadge(cartsize, onClick = {
                val intent = Intent(context, BasketActivity::class.java)
                context.startActivity(intent)
            })
        })

        LazyColumn {
            category.value?.let {
                items(it.items) { dishes ->
                    DishRow(dishes)
                }
            }
        }
    }
    PostData(type, category)
}


@Composable
fun BasketIconWithBadge(
    itemCount: Int, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ShoppingCart, contentDescription = "Basket"
        )
        if (itemCount > 0) {
            Text(
                text = itemCount.toString(),
                color = Color.Red,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(15.dp, 19.dp, 0.dp, 0.dp)
            )
        }
    }
}


@Composable
fun DishRow(dish: Dish) {
    val context = LocalContext.current
    Card(border = BorderStroke(2.dp, Color(255, 255, 255)), colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ), modifier = Modifier
        .fillMaxWidth()
        .padding(6.dp)
        .clickable {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.DISH_EXTRA_KEY, dish)
            context.startActivity(intent)
        }) {
        Row {
            AsyncImage(
                model = coil.request.ImageRequest.Builder(LocalContext.current)
                    .data(dish.images.first()).build(),
                null,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(10))
                    .padding(15.dp)
                    .clip(CircleShape)
            )
            Text(
                dish.name,
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .padding(10.dp)
                    .align(alignment = Alignment.CenterVertically)
                    .fillMaxWidth(0.70f)
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${dish.prices.first().price} €",
                fontWeight = FontWeight.Bold,
                color = Color(255, 0, 0),
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(0.dp, 0.dp, 11.dp, 0.dp)
            )
        }
    }
}


@Composable
fun PostData(type: DishType, category: MutableState<Category?>) {
    val currentCategory = type.title()
    val context = LocalContext.current
    val queue = Volley.newRequestQueue(context)

    val params = JSONObject()
    params.put(NetworkConstants.ID_SHOP, "1")

    val request = JsonObjectRequest(Request.Method.POST,
        NetworkConstants.URL,
        params,
        { response -> // si pas ça, it.
            Log.d("requests", response.toString(2))
            val result =
                GsonBuilder().create().fromJson(response.toString(), MenuResult::class.java)
            val filteredResult = result.data.first { category -> category.name == currentCategory }
            category.value = filteredResult
        },
        {
            Log.e("requests", it.toString())
        })

    queue.add(request)
}