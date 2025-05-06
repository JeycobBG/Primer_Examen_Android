package com.moviles.primer_examen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.moviles.primer_examen.ui.theme.Primer_ExamenTheme

// Se debe mostrar la Pantalla de perfil por estudiante

/** Lista de Tareas
 *
 * • Muestra todos los detalles del estudiante y el nombre del curso al que está inscrito.
 * • Los detalles del estudiante deben poder verse inclusive si no hay conexión a internet, se debe
 *      aplicar OkHttp con Retrofit, y LocalStorage con Room.
 * • Mostrar un alert que indique cuando los datos están siendo cargados de LocalStorage o
 *      Caché.
 *
 */

class StudentDetailActivity : ComponentActivity() {                            //Debe mostrar la Lista de cursos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Primer_ExamenTheme {

            }
        }
    }
}