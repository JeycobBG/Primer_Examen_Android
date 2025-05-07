import androidx.room.Embedded
import androidx.room.Relation
import com.moviles.primer_examen.model.Course
import com.moviles.primer_examen.model.Student

data class StudentWithCourse(
    @Embedded val student: Student,
    @Relation(
        parentColumn = "courseId",
        entityColumn = "id"
    )
    val course: Course
)
