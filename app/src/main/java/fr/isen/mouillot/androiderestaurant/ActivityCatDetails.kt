package fr.isen.mouillot.androiderestaurant

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.mouillot.androiderestaurant.model.DataResult
import fr.isen.mouillot.androiderestaurant.model.Ingredients
import fr.isen.mouillot.androiderestaurant.model.Items
import fr.isen.mouillot.androiderestaurant.model.Prices
import org.json.JSONObject
import androidx.compose.runtime.LaunchedEffect
import java.io.File
import androidx.compose.material.Snackbar
import fr.isen.mouillot.androiderestaurant.model.iteminfo

class ActivityCatDetails : ComponentActivity() {
    @Composable
    fun coilImageFromUrl(imageUrl: String, modifier: Modifier = Modifier) {
        val painter: Painter = rememberImagePainter(
            data = imageUrl,
            builder = {
                // Ajoutez des options de configuration si nécessaire
                // ex. crossfade(true)
            }
        )
        Image(
            painter = painter,
            contentDescription = null, // Indiquez une description du contenu si nécessaire
            modifier = Modifier.size(200.dp)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cat = intent.getStringExtra("cle") ?: ""

        val dish = intent.getStringExtra("dish") ?: ""

        val price = intent.getStringExtra("price") ?: ""
        val priceint = price.toIntOrNull()

        val itemStateItems = mutableStateListOf<Items>()
        fetchDataItems(cat, itemStateItems)
        val itemStatePrices = mutableStateListOf<Prices>()
        fetchDataPrices(cat, dish, itemStatePrices)
        //val itemStateIngredients = mutableStateListOf<Ingredients>()
        //fetchDataIngredients(cat, itemStateIngredients)

        //fetchDataIngredients("YourCategoryTitle", itemStateIngredients)

        val ingredientState =  mutableStateListOf<Ingredients>()

        // Appeler fetchDataIngredients pour récupérer les ingrédients d'un plat spécifique
        fetchDataIngredients(cat, dish, ingredientState)

        setContent {
            fr.isen.mouillot.androiderestaurant.ui.theme.ui.theme.AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting3("Android")
                    actionBar?.title = dish

                    Box(
                        modifier = Modifier.fillMaxSize() // Remplir toute la taille de l'écran
                    ) {
                        val dishItem = itemStateItems.find { it.nameFr == dish }
                        //val dishIngredients = itemStateIngredients.filter { it.id == dishItem?.id }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                //verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.height(100.dp)) // Espace vertical entre le contenu principal et les boutons

                                //val priceString = itemStatePrices.getOrNull(0)?.price ?: "" // Suppose price est un String
                                //val price: Number = priceString.toInt() ?: 0.0 // Convertir le String en Double

                                val filename = "cart.json"
                                val file = File(filesDir, filename)

                                var quantity by remember { mutableIntStateOf(1) }
                                //var totalPrice = remember { mutableIntStateOf(quantity * (priceint ?: 0)) }

                                var totalPrice by remember { mutableStateOf(quantity * (priceint ?: 0)) }
                                var showSnackbar by remember { mutableStateOf(false) }


                                LaunchedEffect(quantity) {
                                    totalPrice = quantity * (priceint ?: 0)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "-",
                                        style = TextStyle(fontSize = 30.sp),
                                        color = Color.Black,
                                        modifier = Modifier.clickable {
                                            if (quantity > 1) {
                                                quantity--
                                                //totalPrice.value = quantity * price
                                                showToast("Decreased")
                                            }
                                        }
                                    )
                                    //var priceText by remember { mutableStateOf(TextFieldValue(price.toString())) }

                                    Text(
                                        text = quantity.toString(),
                                        modifier = Modifier.padding(8.dp),
                                        style = TextStyle(fontSize = 30.sp, color = Color.Black)
                                    )

                                    Text(
                                        text = "+",
                                        style = TextStyle(fontSize = 30.sp),
                                        color = Color.Black,
                                        modifier = Modifier.clickable {
                                            val cartItem = iteminfo(dish, priceint ?: 0, quantity)
                                            val jsonCartItem = Gson().toJson(cartItem)
                                            file.appendText(jsonCartItem)
                                            quantity++
                                            //totalPrice.value = quantity * price
                                            showToast("Increased")
                                            showSnackbar = true
                                        }
                                    )
                                    if (showSnackbar) {
                                        Snackbar(
                                            action = {
                                                // Action à effectuer lorsqu'on appuie sur le bouton de l'action
                                            },
                                            modifier = Modifier.padding(16.dp) // Modifier pour définir les propriétés du Snackbar
                                        ) {
                                            // Contenu du Snackbar
                                            Text("Plat ajouté au panier")
                                        }
                                    }
                                    Text(
                                        text = "Total : ${totalPrice}",
                                        style = TextStyle(fontSize = 30.sp),
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize() // Utiliser tout l'espace disponible
                        ) {

                            item {
                                dishItem?.let { item ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth() // Utiliser toute la largeur disponible
                                    ) {
                                        Spacer(modifier = Modifier.height(60.dp)) // Espace vertical entre le contenu principal et les boutons
                                        ImageCarousel(images = item.images)
                                        Text(
                                            text = item.nameFr ?: "",
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                            items(ingredientState) { ingredient ->
                                Text(
                                    text = ingredient.nameFr ?: "",
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(50.dp)) // Espace vertical entre le contenu principal et les boutons
                    }


                }
            }
        }
    }
    @Composable
    fun CoilImageFromUrl(imageUrl: String, modifier: Modifier = Modifier) {
        val painter: Painter = rememberImagePainter(
            data = imageUrl,
            builder = {
                // Ajoutez des options de configuration si nécessaire
                // ex. crossfade(true)
            }
        )
        Image(
            painter = painter,
            contentDescription = null, // Indiquez une description du contenu si nécessaire
            modifier = Modifier.size(200.dp)
        )
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun fetchDataItems(categoryTitle: String, itemState: SnapshotStateList<Items>) {
        val url = "http://test.api.catering.bluecodegames.com/menu"
        val jsonObject = JSONObject()
        jsonObject.put("id_shop", "1")

        // Créer une demande de requête POST
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            {
                // Traitement de la réponse ici
                // Vous pouvez extraire les données du menu de la réponse JSON
                Log.d("", "données en brut: $it")
                val result = Gson().fromJson(it.toString(), DataResult::class.java)
                val itemsFromCategory = result.data.find { it.nameFr == categoryTitle}?.items as? Collection<Items> ?: emptyList()
                itemState.addAll(itemsFromCategory)
            },
            {
                // Gérer les erreurs de la requête ici
                Log.d("", "ERREUR: $it")
            })

        // Ajouter la demande à la file d'attente de Volley pour l'exécution
        val requestQueue = Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
    private fun fetchDataPrices(categoryTitle: String, dishTitle: String, priceState: SnapshotStateList<Prices>) {
        val url = "http://test.api.catering.bluecodegames.com/menu"
        val jsonObject = JSONObject()
        jsonObject.put("id_shop", "1")

        // Créer une demande de requête POST
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            { response ->
                // Traitement de la réponse ici
                Log.d("", "données en brut: $response")
                val result = Gson().fromJson(response.toString(), DataResult::class.java)
                val category = result.data.find { it.nameFr == categoryTitle }
                val dish = category?.items?.find { it.nameFr == dishTitle }
                dish?.prices?.let { priceState.addAll(it) }
            },
            { error ->
                // Gérer les erreurs de la requête ici
                Log.d("", "ERREUR: $error")
            })

        // Ajouter la demande à la file d'attente de Volley pour l'exécution
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
    private fun fetchDataIngredients(categoryTitle: String, dishTitle: String, ingredientState: SnapshotStateList<Ingredients>) {
        val url = "http://test.api.catering.bluecodegames.com/menu"
        val jsonObject = JSONObject()
        jsonObject.put("id_shop", "1")

        // Créer une demande de requête POST
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            { response ->
                // Traitement de la réponse ici
                Log.d("", "données en brut: $response")
                val result = Gson().fromJson(response.toString(), DataResult::class.java)
                val category = result.data.find { it.nameFr == categoryTitle }
                val dish = category?.items?.find { it.nameFr == dishTitle }
                dish?.ingredients?.forEach { ingredient ->
                    ingredientState.add(ingredient)
                }
            },
            { error ->
                // Gérer les erreurs de la requête ici
                Log.d("", "ERREUR: $error")
            })

        // Ajouter la demande à la file d'attente de Volley pour l'exécution
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(images: List<String>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    HorizontalPager(state = pagerState ) { page ->
        //val index = page + 1 // Add 1 to the index of the page, as there are no images for item.images[0] but it doesn't work using index
        Image(
            painter = rememberImagePainter(images[page]),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Ajustez la hauteur selon vos besoins
        )
    }
}

@Composable
fun DisplayIngredientsList(ingredients: List<Ingredients>) {
    LazyColumn {
        items(ingredients) { ingredient ->
            Text(
                text = ingredient.nameFr ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun DisplayCategoryList(
    dish: String,
    itemStateItems: SnapshotStateList<Items>,
    coilImageFromUrl: @Composable (String, Modifier) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize() // Remplir toute la taille de l'écran
    ) {
        val dishItem = itemStateItems.find { it.nameFr == dish }
        //val dishIngredients = itemStateIngredients.filter { it.id == dishItem?.id }

        LazyColumn(
            modifier = Modifier.fillMaxSize() // Utiliser tout l'espace disponible
        ) {
            item {
                dishItem?.let { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth() // Utiliser toute la largeur disponible
                    ) {
                        coilImageFromUrl(
                            item.images.lastOrNull() ?: "",
                            Modifier
                                .size(50.dp)
                                .padding(8.dp)
                        )
                        Text(
                            text = item.nameFr ?: "",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    fr.isen.mouillot.androiderestaurant.ui.theme.ui.theme.AndroidERestaurantTheme {
        Greeting3("Android")
    }
}