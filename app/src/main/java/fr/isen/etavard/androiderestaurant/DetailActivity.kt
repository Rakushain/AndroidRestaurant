package fr.isen.etavard.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.isen.etavard.androiderestaurant.basket.Basket
import fr.isen.etavard.androiderestaurant.basket.BasketActivity
import fr.isen.etavard.androiderestaurant.network.Dish
import kotlin.math.max

class DetailActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dish = intent.getSerializableExtra(DISH_EXTRA_KEY) as? Dish
        setContent {
            val context = LocalContext.current
            val count = remember {
                mutableIntStateOf(1)
            }
            val ingredient =
                dish?.ingredients?.map { it.name }?.joinToString(separator = "\n") ?: ""
            val pagerState = rememberPagerState(pageCount = {
                dish?.images?.count() ?: 0
            })
            val cartitems = Basket.current(context).items
            val cartsize = cartitems.sumBy { it.count }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .background(Color(176, 196, 222))
                    .fillMaxSize()
            ) {
                TopAppBar(title = {
                    Text(
                        dish?.name ?: "",
                        fontSize = 21.sp,
                        modifier = Modifier.padding(10.dp),
                        style = TextStyle(textDecoration = TextDecoration.Underline),
                        fontFamily = FontFamily(Font(R.font.protest_riot_regular))
                    )
                },

                    colors = TopAppBarDefaults.topAppBarColors(
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

                HorizontalPager(state = pagerState) {
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(LocalContext.current)
                            .data(dish?.images?.get(it)).build(),
                        null,
                        placeholder = painterResource(R.drawable.ic_launcher_foreground),
                        error = painterResource(R.drawable.ic_launcher_foreground),
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }

                Text(
                    ingredient,
                    modifier = Modifier.padding(8.dp),
                    fontStyle = FontStyle.Italic,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.protest_riot_regular))
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onClick = {
                        count.value = max(1, count.value - 1)
                    }) {
                        Text("-")
                    }
                    Text(count.value.toString())
                    OutlinedButton(onClick = {
                        count.value = count.value + 1
                    }) {
                        Text("+")
                    }
                    Spacer(Modifier.weight(1f))
                }
                Column(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(0.dp, 30.dp, 0.dp, 0.dp)
                ) {

                    Button(modifier = Modifier.padding(25.dp, 10.dp, 0.dp, 0.dp),
                        colors = ButtonDefaults.buttonColors(contentColor = Color.Yellow),
                        onClick = {
                            if (dish != null) {
                                Basket.current(context).add(dish, count.value, context)
                            }
                        }) {
                        Text("Commander")
                    }
                    Button(modifier = Modifier.padding(15.dp, 10.dp, 0.dp, 0.dp),
                        colors = ButtonDefaults.buttonColors(contentColor = Color.Yellow),
                        onClick = {
                            val intent = Intent(context, BasketActivity::class.java)
                            context.startActivity(intent)
                        }) {
                        Text("Voir mon panier")
                    }
                }
            }
        }
    }

    companion object {
        val DISH_EXTRA_KEY = "DISH_EXTRA_KEY"
    }
}