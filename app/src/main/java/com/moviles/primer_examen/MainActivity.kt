package com.moviles.primer_examen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.moviles.primer_examen.ui.theme.Primer_ExamenTheme

/** Lista de Tareas
 *
 * • Muestra un RecyclerView (Compose LazyColumn) con la lista de cursos disponibles (name,
 * description, image, schedule, professor).
 * • Incluye un botón para crear un nuevo curso.
 * • Cada curso puede ser editado o eliminado (CRUD).
 * • La lista de cursos debe poder verse inclusive si no hay conexión a internet, se debe aplicar
 * OkHttp con Retrofit, y LocalStorage con Room.
 * • Mostrar un alert que indique cuando los datos están siendo cargados de LocalStorage o
 * Caché.
 *
 */

class MainActivity : ComponentActivity() {                            //Debe mostrar la Lista de cursos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Primer_ExamenTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Primer_ExamenTheme {
        Greeting("Android")
    }
}