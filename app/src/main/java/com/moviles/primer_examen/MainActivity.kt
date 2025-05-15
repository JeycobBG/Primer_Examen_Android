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
                Text(error!!, color = Color.Red, modifier = Modifier.padding(16.dp))
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
}@Composable
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
fun CourseItem(course: Course, onEdit: (Course) -> Unit, onDelete: (Course) -> Unit) {
    // Implementación de cada ítem del curso (puedes personalizarlo)
    Text(text = course.name)
}