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

// Se deben mostrar los estudiantes por curso

/** Lista de Tareas
 *
 * • Al seleccionar un curso, se navega a esta pantalla que muestra todos los estudiantes inscritos.
 * • La lista de los estudiantes debe mostrar el nombre del estudiante y el correo electrónico.
 * • Se puede crear un nuevo estudiante, editar o eliminar.
 * • Solo se muestran estudiantes asociados al curso.
 * • Implementar una notificación push (FCM) desde el backend cuando se registra un nuevo
 *      estudiante. Con el siguiente mensaje: “Estudiante: [nombre del estudiante], se ha inscrito
 *      al curso: [nombre del curso]”
 * • La lista de estudiantes debe poder verse inclusive si no hay conexión a internet, se debe
 *      aplicar OkHttp con Retrofit, y LocalStorage con Room.
 * • Mostrar un alert que indique cuando los datos están siendo cargados de LocalStorage o
 *      Caché.
 *
 */

class StudentsActivity : ComponentActivity() {                            //Debe mostrar la Lista de cursos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Primer_ExamenTheme {

            }
        }
    }
}