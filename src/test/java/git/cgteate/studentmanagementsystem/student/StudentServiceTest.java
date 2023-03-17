package git.cgteate.studentmanagementsystem.student;

import git.cgteate.studentmanagementsystem.student.exception.BadRequestException;
import git.cgteate.studentmanagementsystem.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private List<Student> studentList;

    @BeforeEach
    void setUp() {
        studentList = new ArrayList<>();
        studentList.add(new Student(1L, "Calvin", "calvin@gmail.com", Gender.MALE));
        studentList.add(new Student(2L, "Rosaria", "rosaria@gmail.com", Gender.FEMALE));
    }

    @Test
    void getAllStudents_ShouldReturnAllStudents() {
        // given
        when(studentRepository.findAll()).thenReturn(studentList);

        // when
        List<Student> result = studentService.getAllStudents();

        // then
        assertThat(result).isEqualTo(studentList);
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void addStudent_WithNonExistingEmail_ShouldSaveStudent() {
        // given
        Student newStudent = new Student("David", "david@gmail.com", Gender.MALE);
        when(studentRepository.selectExistsEmail(newStudent.getEmail())).thenReturn(false);

        // when
        studentService.addStudent(newStudent);

        // then
        verify(studentRepository, times(1)).save(any());
    }

    @Test
    void addStudent_WithExistingEmail_ShouldThrowBadRequestException() {
        // given
        Student existingStudent = new Student(1L, "Calvin", "calvin@gmail.com", Gender.FEMALE);
        when(studentRepository.selectExistsEmail(existingStudent.getEmail())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> studentService.addStudent(existingStudent))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + existingStudent.getEmail() + " taken");
        verify(studentRepository, never()).save(any());
    }

    @Test
    void deleteStudent_WithExistingId_ShouldDeleteStudent() {
        // given
        Long studentId = 1L;
        when(studentRepository.existsById(studentId)).thenReturn(true);

        // when
        studentService.deleteStudent(studentId);

        // then
        verify(studentRepository, times(1)).deleteById(studentId);
    }


    @Test
    void deleteStudent_WithNonExistingId_ShouldThrowStudentNotFoundException() {
        // given
        Long studentId = 3L;
        when(studentRepository.existsById(studentId)).thenReturn(false);

        // when
        // then
        assertThatThrownBy(() -> studentService.deleteStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exists");
        verify(studentRepository, never()).deleteById(any());
    }

    @Test
    void editStudent_ShouldSaveStudent() {
        Student updatedStudent = new Student(1L, "Calvin", "calvin@gmail.com", Gender.MALE);
    given(studentRepository.existsById(updatedStudent.getId())).willReturn(true);

    // when
    studentService.editStudent(updatedStudent);

    // then
    verify(studentRepository, times(1)).save(updatedStudent);
    }

    @Test
    void willThrowWhenEmailIsTaken_BDD() {
        // given
        Student student = new Student(
                "Calvin",
                "calvin@gmail.com",
                Gender.MALE
        );

        given(studentRepository.selectExistsEmail(anyString()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> studentService.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void canDeleteStudent_BDD() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(true);

        // when
        studentService.deleteStudent(id);

        // then
        verify(studentRepository).deleteById(id);
    }

    @Test
    void willThrowWhenDeleteStudentNotFound_BDD() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                 .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> studentService.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");

        verify(studentRepository, never()).deleteById(any());
    }

    @Test
    void editStudentThrowsStudentNotFoundException() {
        // given
        Student updatedStudent = new Student(3L, "Calvin", "calvin@gmail.com", Gender.MALE);
        given(studentRepository.existsById(updatedStudent.getId())).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> studentService.editStudent(updatedStudent))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + updatedStudent.getId() + " does not exists");
        verify(studentRepository, never()).save(updatedStudent);
    }

}
