package fr.isen.mouillot.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.mouillot.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.End
                    ) {
                        Spacer(modifier = Modifier.height(10.dp)) // Espace au-dessus du contenu
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Bienvenue",
                                    textAlign = TextAlign.Right,
                                    fontSize = 25.sp,
                                    color = Color(0xFFFF8519),
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .wrapContentWidth(Alignment.End)
                                        .clickable { goToCategory("hello jade") }
                                )
                                Text(
                                    text = "Chez",
                                    textAlign = TextAlign.Right,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 25.sp,
                                    color = Color(0xFFFF8519),
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .wrapContentWidth(Alignment.End)
                                        .clickable { goToCategory("hello jade") }
                                )
                                Spacer(modifier = Modifier.height(10.dp)) // Espace au-dessus du contenu
                                Text(
                                    text = "DroidRestaurant",
                                    textAlign = TextAlign.Right,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 23.sp,
                                    color = Color(0xFF92562A),
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .wrapContentWidth(Alignment.End)
                                        .clickable { goToCategory("hello jade") }
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp)) // Espace entre le texte et l'image
                            Image(
                                painter = painterResource(id = R.drawable.my_image),
                                contentDescription = "",
                                modifier = Modifier.size(width = 150.dp, height = 150.dp)
                            )
                        }
                    }

                    ThreeButtons { buttonName -> goToCategory(buttonName) }

                }
            }

        }

    }

    private fun goToCategory(buttonName: String) {
        Toast.makeText(
            this,
            "Clic sur le bouton $buttonName",
            Toast.LENGTH_SHORT
        ).show()
        val colorHex = String.format("#%06X", (0xFFFFFF)) // Convertir la couleur en format hexadécimal
        val intent = Intent(this, ActivityCatListe::class.java)
        intent.putExtra("cle", buttonName)
        intent.putExtra("color", colorHex) // Ajoutez la couleur sous forme de chaîne hexadécimale comme données supplémentaires
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Ajouter un log pour indiquer quand l'activité Home est détruite
        Log.d("HomeActivity", "L'activité Home est détruite")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, showToast: (String) -> Unit) {
    Text(
        text = "Bienvenue chez",
        textAlign = TextAlign.Center,
        fontSize = 30.sp,
        color = Color.Red,
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 16.dp)
            .wrapContentWidth(Alignment.End)
            .clickable { showToast("hello jade") }
    )
}

@Composable
fun ThreeButtons(goToCategory: (String) -> Unit) {
    val dividerWidth = 200.dp // Largeur fixe pour le Divider
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(220.dp)) // Espaceur pour déplacer les boutons plus bas dans la page
        Button(
            onClick = {
                goToCategory("Entrées")
            },
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Fond transparent
            //contentColor = Color(0xFF8519) // Couleur du texte
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp) // Pas d'élévation
        ) {
            Text("Entrées",color = Color(0xFFFF8519), fontSize = 32.sp)

        }
        Spacer(modifier = Modifier.height(8.dp)) // Espace après le premier bouton
        Divider(color = Color.Black, thickness = 2.dp, modifier = Modifier.width(dividerWidth)) // Ligne horizontale avec une largeur fixe
        Spacer(modifier = Modifier.height(8.dp)) // Espace après la ligne horizontale
        Button(
            onClick = {
                goToCategory("Plats")
            },
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // Fond transparent
                //contentColor = Color(0xFF8519) // Couleur du texte
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp) // Pas d'élévation
        ) {
            Text("Plats",color = Color(0xFFFF8519), fontSize = 32.sp)
        }
        Spacer(modifier = Modifier.height(8.dp)) // Espace après le premier bouton
        Divider(color = Color.Black, thickness = 2.dp, modifier = Modifier.width(dividerWidth)) // Ligne horizontale avec une largeur fixe
        Spacer(modifier = Modifier.height(8.dp)) // Espace après la ligne horizontale
        Button(
            onClick = {
                goToCategory("Desserts")
            },
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Fond transparent
                    //contentColor = Color(0xFF8519) // Couleur du texte
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp) // Pas d'élévation
        ) {
            Text("Desserts",color = Color(0xFFFF8519), fontSize = 32.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidERestaurantTheme {
        //Greeting("Android")
        //ThreeButtons{}
    }
}

