package fr.isen.etavard.androiderestaurant.basket

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import coil.request.ImageRequest
import fr.isen.etavard.androiderestaurant.R
import fr.isen.etavard.androiderestaurant.HomeActivity


class BasketActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BasketView() }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BasketView() {

    val context = LocalContext.current
    val basketItems = remember {
        mutableStateListOf<BasketItem>()
    }
    Column {
        CenterAlignedTopAppBar(title = {
            Text(
                "Mon Panier",
                fontSize = 35.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(30.dp),
                style = TextStyle(textDecoration = TextDecoration.Underline),
                fontFamily = FontFamily(Font(R.font.protest_riot_regular))
            )
        }, navigationIcon = {
            IconButton(onClick = {
                val intent = Intent(context, HomeActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Filled.Home, contentDescription = "Home"
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(176, 196, 255)
        )
        )

        LazyColumn(
            modifier = Modifier
                .background(Color(176, 196, 222))
                .fillMaxSize()
        ) {
            items(basketItems) {
                BasketItemView(it, basketItems)
            }
            val total = calculateTotal(basketItems)

            item {
                if (total > 0 || Basket.current(context).items.size != 0) {
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(Modifier.weight(1f))
                        Card(
                            shape = RoundedCornerShape(1.dp),
                            border = BorderStroke(2.dp, Color(255, 0, 0)),
                            colors = CardDefaults.cardColors(containerColor = Color(176, 196, 222))
                        ) {
                            Text(
                                " Total : " + total.toString() + " € ",
                                modifier = Modifier.padding(5.dp, 10.dp),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(255, 40, 40),
                                style = TextStyle(textDecoration = TextDecoration.Underline),
                                fontFamily = FontFamily(Font(R.font.protest_riot_regular))
                            )
                        }
                    }
                }
            }
        }
    }

    basketItems.addAll(Basket.current(context).items)
}

fun calculateTotal(basketItems: List<BasketItem>): Float {
    return basketItems.sumByDouble { it.count * it.dish.prices.first().price.toDouble() }.toFloat()
}

@Composable
fun BasketItemView(item: BasketItem, basketItems: MutableList<BasketItem>) {
    Card {
        val context = LocalContext.current
        Card(
            border = BorderStroke(2.dp, Color(255, 255, 255)), colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ), modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Row(Modifier.padding(8.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.dish.images.first()).build(),
                    null,
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    error = painterResource(R.drawable.ic_launcher_foreground),
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(10))
                        .padding(8.dp)
                        .clip(CircleShape)
                )
                Column(
                    Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(8.dp)
                ) {
                    Text(
                        item.dish.name,
                        modifier = Modifier.fillMaxWidth(0.70f),
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        "${item.dish.prices.first().price} €",
                        fontWeight = FontWeight.Bold,
                        color = Color(255, 0, 0)
                    )
                }

                Spacer(Modifier.weight(1f))
                Text(
                    item.count.toString(), Modifier.align(alignment = Alignment.CenterVertically)
                )
                TextButton(colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.size(width = 35.dp, height = 35.dp),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        // delete item and redraw view
                        Basket.current(context).delete(item, context)
                        basketItems.clear()
                        basketItems.addAll(Basket.current(context).items)
                    }) {
                    Text(
                        "X", color = Color.Black, modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
