package com.moviles.primer_examen

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.primer_examen.model.Course
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult



@Composable
fun CourseDialog(
    course: Course?,
    onDismiss: () -> Unit,
    onSave: (Course) -> Unit
) {
    var name by remember { mutableStateOf(course?.name ?: "") }
    var description by remember { mutableStateOf(course?.description ?: "") }
    var imageUri by remember { mutableStateOf(course?.imageUrl ?: "") }
    var schedule by remember { mutableStateOf(course?.schedule ?: "") }
    var professor by remember { mutableStateOf(course?.professor ?: "") }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { imageUri = it.toString() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (course == null) "Add Course" else "Edit Course") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { launcher.launch("image/*") }) {
                        Text("Seleccionar Imagen")
                    }
                    if (imageUri.isNotBlank()) {
                        Text("Imagen seleccionada", modifier = Modifier.padding(start = 8.dp))
                    }
                }
                OutlinedTextField(value = schedule, onValueChange = { schedule = it }, label = { Text("Schedule") })
                OutlinedTextField(value = professor, onValueChange = { professor = it }, label = { Text("Professor") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank() && description.isNotBlank() && schedule.isNotBlank() && professor.isNotBlank()) {
                    onSave(
                        Course(
                            id = course?.id ?: 0,
                            name = name,
                            description = description,
                            imageUrl = imageUri, // Ahora guarda la URI seleccionada
                            schedule = schedule,
                            professor = professor
                        )
                    )
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}