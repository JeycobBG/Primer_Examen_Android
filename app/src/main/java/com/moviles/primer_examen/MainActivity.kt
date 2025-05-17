package com.moviles.primer_examen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext


import androidx.compose.ui.unit.dp
import com.moviles.primer_examen.model.Course

import com.moviles.primer_examen.viewmodel.CourseViewModel

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
/**
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirigir a la actividad de estudiantes con courseId = 1
        val intent = Intent(this, StudentsActivity::class.java)
        intent.putExtra("courseId", 1)  // Establecer courseId = 1
        startActivity(intent)
        finish() // Cerrar MainActivity para evitar que se quede en la pila de actividades
    }
}*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa Retrofit (importante para que funcione la red)
        com.moviles.primer_examen.network.RetrofitInstance.init(applicationContext)

        setContent {
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<CourseViewModel>()
            CourseScreen(viewModel = viewModel)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(viewModel: CourseViewModel) {
    val courses by viewModel.courses.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchCourses()
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Courses") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { selectedCourse = null; showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Course")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
             }

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }

            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(courses) { course ->
                    CourseItem(
                        course = course,
                        onEdit = { selectedCourse = it; showDialog = true },
                        onDelete = { viewModel.deleteCourse(it.id) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        CourseDialog(
            course = selectedCourse,
            onDismiss = { showDialog = false },
            onSave = { course ->
                if (selectedCourse == null) {
                    viewModel.addCourse(course)
                } else {
                    viewModel.updateCourse(course)
                }
                showDialog = false
            }
        )
    }
}
@Composable
fun CourseItem(
    course: Course,
    onEdit: (Course) -> Unit,
    onDelete: (Course) -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Navegar a StudentsActivity con el courseId
                val intent = Intent(context, StudentsActivity::class.java)
                intent.putExtra("courseId", course.id)
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = course.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = course.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Horario: ${course.schedule}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Profesor: ${course.professor}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onEdit(course) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar curso")
                }
                IconButton(onClick = { onDelete(course) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar curso", tint = Color.Red)
                }
            }
        }
    }
}