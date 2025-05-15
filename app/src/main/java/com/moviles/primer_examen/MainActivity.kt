package com.moviles.primer_examen

import android.content.Intent
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirigir a la actividad de estudiantes con courseId = 1
        val intent = Intent(this, StudentsActivity::class.java)
        intent.putExtra("courseId", 2)  // Establecer courseId = 1
        startActivity(intent)
        finish() // Cerrar MainActivity para evitar que se quede en la pila de actividades
    }
}