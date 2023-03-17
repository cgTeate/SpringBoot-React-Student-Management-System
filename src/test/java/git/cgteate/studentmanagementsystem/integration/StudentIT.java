package git.cgteate.studentmanagementsystem.integration;

import git.cgteate.studentmanagementsystem.student.Gender;
import git.cgteate.studentmanagementsystem.student.Student;
import git.cgteate.studentmanagementsystem.student.StudentRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-it.properties"
)
@AutoConfigureMockMvc
public class StudentIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    private final Faker faker = new Faker();

    @Test
    void canRegisterNewStudent() throws Exception {
        // given
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        Student student = new Student(
                name,
                String.format("%s@washburn.edu",
                        StringUtils.trimAllWhitespace(name.trim().toLowerCase())),
                Gender.FEMALE
        );

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)));
        // then
        resultActions.andExpect(status().isOk());
        List<Student> students = studentRepository.findAll();
        assertThat(students)
                .usingElementComparatorIgnoringFields("id")
                .contains(student);
    }

    @Test
    void canDeleteStudent() throws Exception {
        // given
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        String email = String.format("%s@washburn.edu",
                StringUtils.trimAllWhitespace(name.trim().toLowerCase()));

        Student student = new Student(
                name,
                email,
                Gender.FEMALE
        );

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk());

        MvcResult getStudentsResult = mockMvc.perform(get("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = getStudentsResult
                .getResponse()
                .getContentAsString();

        List<Student> students = objectMapper.readValue(
                contentAsString,
                new TypeReference<>() {
                }
        );

        long id = students.stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .map(Student::getId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "student with email: " + email + " not found"));

        // when
        ResultActions resultActions = mockMvc
                .perform(delete("/api/v1/students/" + id));

        // then
        resultActions.andExpect(status().isOk());
        boolean exists = studentRepository.existsById(id);
        assertThat(exists).isFalse();
    }

    @Test
    void canUpdateStudent() throws Exception {
        // given
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        String email = String.format("%s@washburn.edu",
                StringUtils.trimAllWhitespace(name.trim().toLowerCase()));

        Student student = new Student(
                name,
                email,
                Gender.FEMALE
        );

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk());

        MvcResult getStudentsResult = mockMvc.perform(get("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = getStudentsResult
                .getResponse()
                .getContentAsString();

        List<Student> students = objectMapper.readValue(
                contentAsString,
                new TypeReference<>() {}
        );

        long id = students.stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .map(Student::getId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "student with email: " + email + " not found"));

        String updatedName = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        Student updatedStudent = new Student(
                id,
                updatedName,
                email,
                Gender.MALE
        );

        // when
        ResultActions resultActions = mockMvc
                .perform(put("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)));

        // then
        resultActions.andExpect(status().isOk());

        Student retrievedStudent = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("student with id: " + id + " not found"));

       assertThat(retrievedStudent.getName()).isEqualTo(updatedName);
       assertThat(retrievedStudent.getGender()).isEqualTo(Gender.MALE);
    }    

    @Test
    void cannotAddStudentWithDuplicateEmail() throws Exception {
        // given
        String name1 = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        String email = String.format("%s@washburn.edu",
                StringUtils.trimAllWhitespace(name1.trim().toLowerCase()));

        Student student1 = new Student(
                name1,
                email,
                Gender.FEMALE
        );

        String name2 = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        Student student2 = new Student(
                name2,
                email,
                Gender.MALE
        );

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student1)))
                .andExpect(status().isOk());

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student2)));

        // then
        resultActions.andExpect(status().isBadRequest());
    }
    @Test
    void cannotDeleteNonexistentStudent() throws Exception {
        // given
        long nonexistentStudentId = 9999L;

        // when
        ResultActions resultActions = mockMvc
                .perform(delete("/api/v1/students/" + nonexistentStudentId));

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void cannotUpdateNonexistentStudent() throws Exception {
        // given
        long nonexistentStudentId = 9999L;

        String name = String.format(
               "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        String email = String.format("%s@washburn.edu",
                StringUtils.trimAllWhitespace(name.trim().toLowerCase()));

        Student nonexistentStudent = new Student(
                name,
                email,
                Gender.FEMALE
        );
        nonexistentStudent.setId(nonexistentStudentId);

        // when
        ResultActions resultActions = mockMvc
                .perform(put("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonexistentStudent)));

        // then
        resultActions.andExpect(status().isNotFound());
    }

}