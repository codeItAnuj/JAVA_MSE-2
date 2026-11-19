package com.example.studentprofile.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentprofile.dto.MeritEntry;
import com.example.studentprofile.dto.PerformanceEntry;
import com.example.studentprofile.dto.SameSkillGroup;
import com.example.studentprofile.model.Student;
import com.example.studentprofile.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // ---------------- Q3 REST APIs ----------------

    // Q3.1 Add a student
    @PostMapping
    public ResponseEntity<Student> add(@RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addStudent(student));
    }

    // Q3.2 Retrieve all students
    @GetMapping
    public List<Student> getAll() {
        return service.getAllStudents();
    }

    // Q3.3 Search for a student by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
        Optional<Student> student = service.getStudentById(id);
        if (student.isPresent()) {
            return ResponseEntity.ok(student.get());
        }
        return notFound(id);
    }

    // Q3.4 Update student scores and skills
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody Student student) {
        Optional<Student> updated = service.updateStudent(id, student);
        if (updated.isPresent()) {
            return ResponseEntity.ok(updated.get());
        }
        return notFound(id);
    }

    // Q3.5 Delete a student record
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("id") Integer id) {
        if (service.deleteStudent(id)) {
            return ResponseEntity.ok(Map.of("message", "Student " + id + " deleted successfully"));
        }
        return notFound(id);
    }

    // Q3.6 Display the merit list
    @GetMapping("/merit-list")
    public List<MeritEntry> meritList() {
        return service.getMeritList();
    }

    // ---------------- Q2 requirement endpoints ----------------

    // Q2.1 - Q2.3 average, performance category, stronger subject
    @GetMapping("/performance")
    public List<PerformanceEntry> performance() {
        return service.getPerformance();
    }

    // Q2.4 search by ID, email or course:  /search?id=101  /search?email=..  /search?course=..
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name = "id", required = false) Integer id,
                                    @RequestParam(name = "email", required = false) String email,
                                    @RequestParam(name = "course", required = false) String course) {
        if (id == null && email == null && course == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Provide at least one of: id, email, course"));
        }
        return ResponseEntity.ok(service.search(id, email, course));
    }

    // Q2.6 students having the same technical skills
    @GetMapping("/same-skills")
    public List<SameSkillGroup> sameSkills() {
        return service.getSameSkillGroups();
    }

    // extra: students having one skill
    @GetMapping("/skill/{skill}")
    public List<Student> bySkill(@PathVariable("skill") String skill) {
        return service.getBySkill(skill);
    }

    // Q2.7 weak in programming (< 50 by default) but mathematics >= 75
    @GetMapping("/needs-programming-improvement")
    public List<Student> needsProgrammingImprovement(
            @RequestParam(name = "programmingBelow", defaultValue = "50") double programmingBelow,
            @RequestParam(name = "mathMin", defaultValue = "75") double mathMin) {
        return service.getNeedingProgrammingImprovement(programmingBelow, mathMin);
    }

    // ---------------- Error handling ----------------

    private ResponseEntity<Map<String, String>> notFound(Integer id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Student not found with id " + id));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }
}
