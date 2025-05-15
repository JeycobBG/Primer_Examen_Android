package com.moviles.primer_examen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.moviles.primer_examen.model.Course

@Composable
fun CourseDialog(
    course: Course?,
    onDismiss: () -> Unit,
    onSave: (Course) -> Unit
) {
    var name by remember { mutableStateOf(course?.name ?: "") }
    var description by remember { mutableStateOf(course?.description ?: "") }
    var imageUrl by remember { mutableStateOf(course?.imageUrl ?: "") }
    var schedule by remember { mutableStateOf(course?.schedule ?: "") }
    var professor by remember { mutableStateOf(course?.professor ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (course == null) "Add Course" else "Edit Course") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") })
                OutlinedTextField(value = schedule, onValueChange = { schedule = it }, label = { Text("Schedule") })
                OutlinedTextField(value = professor, onValueChange = { professor = it }, label = { Text("Professor") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    Course(
                        id = course?.id ?: 0, // Use 0 for new courses
                        name = name,
                        description = description,
                        imageUrl = imageUrl,
                        schedule = schedule,
                        professor = professor
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}