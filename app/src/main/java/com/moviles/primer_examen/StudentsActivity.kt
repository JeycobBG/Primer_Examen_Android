package com.moviles.primer_examen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.messaging.FirebaseMessaging
import com.moviles.primer_examen.model.Student
import com.moviles.primer_examen.network.RetrofitInstance
import com.moviles.primer_examen.ui.theme.Primer_ExamenTheme
import com.moviles.primer_examen.viewmodel.StudentViewModel

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

class StudentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel(this)
        RetrofitInstance.init(applicationContext)
        subscribeToTopic()

        // Obtener el courseId desde el Intent
        val courseId = intent.getIntExtra("courseId", -1)

        if (courseId != -1) {
            setContent {
                Primer_ExamenTheme {
                    val viewModel: StudentViewModel = viewModel()

                    // Pasar courseId al Composable que maneja la vista
                    StudentScreen(viewModel, courseId)
                }
            }
        } else {
            // Manejar el caso en que no se pase un courseId válido
            Log.e("StudentsActivity", "No se ha recibido un courseId válido.")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(viewModel: StudentViewModel, courseId: Int) {
    val students by viewModel.students.collectAsState()
    val loading by viewModel.loading.collectAsState()  // Estado de carga
    val error by viewModel.error.collectAsState()  // Estado de error
    var showDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }

    LaunchedEffect(courseId, error) {
        if (courseId != -1) {
            viewModel.fetchStudentsByCourse(courseId)
        }

        if (error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Estudiantes") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedStudent = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Estudiante")
            }
        }
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {
            // Mostrar un indicador de carga si estamos esperando la respuesta
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // Si hay un error, mostrar un mensaje
            ConnectionStatusMessage(error)

            if (!loading && students.isNotEmpty()) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    itemsIndexed(students) { index, student ->
                        StudentItem(
                            student = student,
                            courseId = courseId,
                            onEdit = {
                                selectedStudent = it
                                showDialog = true
                            },
                            onDelete = {
                                viewModel.deleteStudent(student.id)  // Aquí pasamos el studentId
                            }
                        )
                    }
                }
            } else if (!loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay estudiantes en este curso.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        // 🔻 Mostrar el mensaje de conexión siempre que haya error
        ConnectionStatusMessage(error = error)
    }

    if (showDialog) {
        StudentDialog(
            student = selectedStudent,
            courseId = courseId,
            onDismiss = { showDialog = false },
            onSave = { student ->
                if (selectedStudent == null) {
                    viewModel.addStudent(student)
                } else {
                    viewModel.updateStudent(student)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun StudentItem(student: Student, courseId: Int, onEdit: (Student) -> Unit, onDelete: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(student.name, style = MaterialTheme.typography.titleLarge)
            Text(student.email, style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onEdit(student) }) {
                    Text("Editar", color = MaterialTheme.colorScheme.primary)
                }
                // Verificamos que el ID no sea nulo antes de eliminar
                TextButton(onClick = {
                    student.id?.let {
                        onDelete(it)  // Llamamos a onDelete solo si el id no es nulo
                    }
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun StudentDialog(
    student: Student?,  // Ahora solo Student, ya no StudentWithCourses
    courseId: Int,
    onDismiss: () -> Unit,
    onSave: (Student) -> Unit
) {
    // Inicializamos los campos del estudiante
    var name by remember { mutableStateOf(student?.name ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (student == null) "Agregar Estudiante" else "Editar Estudiante") },
        text = {
            Column {
                // Campo para el nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                // Campo para el correo
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") }
                )
                // Campo para el teléfono
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedStudent = Student(
                    id = student?.id,  // Si student es null, el id será null, sino tomamos el id existente
                    name = name,
                    email = email,
                    phone = phone,
                    courseId = courseId
                )
                onSave(updatedStudent)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ConnectionStatusMessage(error: String?) {
    if (error != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = error,
                color = Color.White,
                modifier = Modifier
                    .background(Color.Red, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


fun createNotificationChannel(context: Context) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val channelId = "primer_examen_channel"
        val channelName = "Primer Examen Reminders"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Notifies users about recent updates"
        }

        val notificationManager =
            context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}

fun subscribeToTopic() {
    FirebaseMessaging.getInstance().subscribeToTopic("primer_examen")
        .addOnCompleteListener { task ->
            var msg = "Subscription successful"
            if (!task.isSuccessful) {
                msg = "Subscription failed"
            }
            Log.d("FCM", msg)
        }
}