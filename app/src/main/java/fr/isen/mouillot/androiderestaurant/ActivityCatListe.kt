package fr.isen.mouillot.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
import fr.isen.mouillot.androiderestaurant.ui.theme.AndroidERestaurantTheme
import android.graphics.Color // Importer la classe Color
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.isen.mouillot.androiderestaurant.model.DataResult
import fr.isen.mouillot.androiderestaurant.model.Items
import fr.isen.mouillot.androiderestaurant.model.Prices
import org.json.JSONObject
import com.google.gson.Gson
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter

class ActivityCatListe : ComponentActivity() {

    private fun onDishClicked(dish: String) {
        val intent = Intent(this, ActivityCatDetails::class.java)
        intent.putExtra("dish", dish)
        startActivity(intent)
    }

    // Créer une fonction pour effectuer la requête
    private fun fetchDataItems(categoryTitle: String, itemState: SnapshotStateList<Items>) {
        val url = "http://test.api.catering.bluecodegames.com/menu"
        val jsonObject = JSONObject()
        jsonObject.put("id_shop", "1")

        // Créer une demande de requête POST
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonObject,
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

    private fun fetchDataPrices(categoryTitle: String, priceState: SnapshotStateList<Prices>) {
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
                category?.items?.forEach { item ->
                    item.prices.forEach { price ->
                        priceState.add(price)
                    }
                }
            },
            { error ->
                // Gérer les erreurs de la requête ici
                Log.d("", "ERREUR: $error")
            })

        // Ajouter la demande à la file d'attente de Volley pour l'exécution
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

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

        val colorHex = intent.getStringExtra("color")
        val cbg = Color.parseColor(colorHex)

        val cat = intent.getStringExtra("cle") ?: ""

        val itemStateItems = mutableStateListOf<Items>()
        fetchDataItems(cat, itemStateItems)

        val itemsStatePrices = mutableStateListOf<Prices>()
        fetchDataPrices(cat, itemsStatePrices)

        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    //window.decorView.setBackgroundColor(cbg)
                ) {
                    //Greeting2("Android")
                    // Récupérer la catégorie depuis l'intention

                    actionBar?.title = cat

                    DisplayCategoryList(
                        itemStateItems = itemStateItems,
                        itemStatePrices = itemsStatePrices,
                        onDishClicked = { dishName ->
                            onDishClicked(dishName)
                        },
                        coilImageFromUrl = { imageUrl, modifier ->
                            coilImageFromUrl(imageUrl = imageUrl, modifier = modifier)
                        }
                    )

                    //coilImageFromUrl(
                        //imageUrl = "https://picsum.photos/200/300",
                        //modifier = Modifier.size(100.dp) // Spécifiez la taille de l'image si nécessaire
                   //)

                }
            }

        }
    }
}

@Composable
fun CategoryComponent(category:String, dishes: List<String>, onDishClicked: (String) -> Unit) {
    Column {
        Text(
            text = "Hello $category",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(32.dp)
                .fillMaxWidth()
        )
        LazyColumn(Modifier.padding(24.dp)) {
            items(dishes) { dish ->
                Text(text = dish,
                    Modifier.padding(0.dp, 8.dp)
                        .clickable { onDishClicked(dish) }
                )
            }
        }
    }
}

@Composable
fun DisplayCategoryList(itemStateItems: SnapshotStateList<Items>, itemStatePrices: SnapshotStateList<Prices>, onDishClicked: (String) -> Unit, coilImageFromUrl: @Composable (String, Modifier) -> Unit) {
    LazyColumn {
        items(itemStateItems) { item ->
            //val itemPrice = itemStatePrices.find { it.id == item.id } // Recherchez le prix correspondant à l'élément
            Row(verticalAlignment = Alignment.CenterVertically) {
                coilImageFromUrl(
                    item.images.last() ?: "", // Utilisez l'URL de l'image de l'élément
                    Modifier
                        .size(50.dp) // Taille de l'image, ajustez selon vos besoins
                        .padding(8.dp)
                )
                Text(
                    text = item.nameFr ?: "",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable {
                            item.nameFr?.let { name ->
                                onDishClicked(name) // Appel à onDishClicked avec name
                            }
                        }
                )
                //val itemPrice = itemStatePrices.find { it.id == item.id } // Recherchez le prix correspondant à l'élément
                //itemPrice?.let {
                    //Text(
                        //text = it.price ?: "", // Affichez le prix trouvé à partir de l'objet itemPrice
                        //textAlign = TextAlign.Right,
                        //modifier = Modifier.padding(vertical = 8.dp)
                    //)
                //}
            }
            //itemPrice?.let {
            //Text(
                //text = itemPrice.price ?: "", // Affichez le prix trouvé à partir de l'objet itemPrice
                //textAlign = TextAlign.Center,
                //modifier = Modifier.padding(vertical = 8.dp)
                //)
            //}
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}