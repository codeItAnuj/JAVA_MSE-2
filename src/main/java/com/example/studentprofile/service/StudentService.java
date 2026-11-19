package com.example.studentprofile.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.studentprofile.dto.MeritEntry;
import com.example.studentprofile.dto.PerformanceEntry;
import com.example.studentprofile.dto.SameSkillGroup;
import com.example.studentprofile.model.Student;
import com.example.studentprofile.repository.StudentRepository;

@Service
public class StudentService {

    private static final String EMAIL_REGEX = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    // =====================  Q3: CRUD  =====================

    // Q3.1 Add a student (rejects duplicate ID / email, validates scores)
    public Student addStudent(Student s) {
        if (s.getStudentId() == null || s.getStudentId() <= 0) {
            throw new IllegalArgumentException("Student ID is required and must be a positive number");
        }
        validateName(s.getStudentName());
        validateEmail(s.getEmail());
        validateCourse(s.getCourseName());
        validateScore(s.getProgrammingScore(), "Programming score", true);
        validateScore(s.getMathematicsScore(), "Mathematics score", true);

        if (repository.existsById(s.getStudentId())) {
            throw new IllegalStateException("Student ID " + s.getStudentId() + " already exists");
        }
        String email = s.getEmail().trim();
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("Email already registered: " + email);
        }
        s.setStudentName(s.getStudentName().trim());
        s.setEmail(email);
        s.setCourseName(s.getCourseName().trim());
        s.setTechnicalSkills(cleanSkills(s.getTechnicalSkills()));
        return repository.save(s);
    }

    // Q3.2 Retrieve all students
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    // Q3.3 Search for a student by ID
    public Optional<Student> getStudentById(Integer id) {
        return repository.findById(id);
    }

    // Q3.4 Update student scores and skills (other fields are optional)
    public Optional<Student> updateStudent(Integer id, Student updated) {
        Optional<Student> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        Student existing = opt.get();

        if (updated.getProgrammingScore() != null) {
            validateScore(updated.getProgrammingScore(), "Programming score", true);
            existing.setProgrammingScore(updated.getProgrammingScore());
        }
        if (updated.getMathematicsScore() != null) {
            validateScore(updated.getMathematicsScore(), "Mathematics score", true);
            existing.setMathematicsScore(updated.getMathematicsScore());
        }
        if (updated.getTechnicalSkills() != null && !updated.getTechnicalSkills().isEmpty()) {
            existing.setTechnicalSkills(cleanSkills(updated.getTechnicalSkills()));
        }
        if (updated.getStudentName() != null) {
            validateName(updated.getStudentName());
            existing.setStudentName(updated.getStudentName().trim());
        }
        if (updated.getCourseName() != null) {
            validateCourse(updated.getCourseName());
            existing.setCourseName(updated.getCourseName().trim());
        }
        if (updated.getEmail() != null) {
            validateEmail(updated.getEmail());
            String email = updated.getEmail().trim();
            Optional<Student> other = repository.findByEmailIgnoreCase(email);
            if (other.isPresent() && !other.get().getStudentId().equals(id)) {
                throw new IllegalStateException("Email already used by another student: " + email);
            }
            existing.setEmail(email);
        }
        return Optional.of(repository.save(existing));
    }

    // Q3.5 Delete a student record
    public boolean deleteStudent(Integer id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    // =====================  Q2: requirements  =====================

    // Q2.1, Q2.2, Q2.3 - average, category and stronger subject for each student
    public List<PerformanceEntry> getPerformance() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Student::getStudentId))
                .map(s -> new PerformanceEntry(s.getStudentId(), s.getStudentName(),
                        s.getProgrammingScore(), s.getMathematicsScore(), s.getAverageScore(),
                        s.getPerformanceCategory(), s.getStrongerSubject()))
                .collect(Collectors.toList());
    }

    // Q2.4 - search by ID, email or course (any one or more of them)
    public List<Student> search(Integer id, String email, String course) {
        Set<Student> result = new LinkedHashSet<>();
        if (id != null) {
            repository.findById(id).ifPresent(result::add);
        }
        if (email != null && !email.isBlank()) {
            repository.findByEmailIgnoreCase(email.trim()).ifPresent(result::add);
        }
        if (course != null && !course.isBlank()) {
            result.addAll(repository.findByCourseNameIgnoreCase(course.trim()));
        }
        return new ArrayList<>(result);
    }

    // Q2.5 / Q3.6 - merit list sorted by average score (descending)
    public List<MeritEntry> getMeritList() {
        List<Student> sorted = repository.findAll().stream()
                .sorted(Comparator.comparingDouble(Student::getAverageScore).reversed()
                        .thenComparing(Student::getStudentName))
                .collect(Collectors.toList());

        List<MeritEntry> merit = new ArrayList<>();
        int rank = 0;
        double previousAvg = -1;
        for (int i = 0; i < sorted.size(); i++) {
            Student s = sorted.get(i);
            if (s.getAverageScore() != previousAvg) {
                rank = i + 1;               // equal averages share the same rank
                previousAvg = s.getAverageScore();
            }
            merit.add(new MeritEntry(rank, s.getStudentId(), s.getStudentName(), s.getCourseName(),
                    s.getAverageScore(), s.getPerformanceCategory()));
        }
        return merit;
    }

    // Q2.6 - students who have exactly the same set of technical skills
    public List<SameSkillGroup> getSameSkillGroups() {
        Map<String, List<Student>> groups = new LinkedHashMap<>();
        for (Student s : repository.findAll()) {
            if (s.getTechnicalSkills().isEmpty()) {
                continue;
            }
            String key = s.getTechnicalSkills().stream()
                    .map(String::toLowerCase)
                    .sorted()
                    .collect(Collectors.joining(","));
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        List<SameSkillGroup> result = new ArrayList<>();
        for (List<Student> group : groups.values()) {
            if (group.size() > 1) {
                List<String> skills = group.get(0).getTechnicalSkills().stream()
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .collect(Collectors.toList());
                List<String> names = group.stream()
                        .sorted(Comparator.comparing(Student::getStudentId))
                        .map(x -> x.getStudentId() + " - " + x.getStudentName())
                        .collect(Collectors.toList());
                result.add(new SameSkillGroup(skills, names));
            }
        }
        return result;
    }

    // extra: students having one particular skill
    public List<Student> getBySkill(String skill) {
        return repository.findAll().stream()
                .filter(s -> s.hasSkill(skill))
                .collect(Collectors.toList());
    }

    // Q2.7 - weak in programming (score below programmingBelow) but maths >= mathMin
    public List<Student> getNeedingProgrammingImprovement(double programmingBelow, double mathMin) {
        return repository.findAll().stream()
                .filter(s -> s.getProgrammingScore() != null && s.getMathematicsScore() != null)
                .filter(s -> s.getProgrammingScore() < programmingBelow && s.getMathematicsScore() >= mathMin)
                .sorted(Comparator.comparing(Student::getStudentId))
                .collect(Collectors.toList());
    }

    // =====================  Validation helpers (Q2.8)  =====================

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Student name is required");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.trim().matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("A valid email address is required");
        }
    }

    private void validateCourse(String course) {
        if (course == null || course.isBlank()) {
            throw new IllegalArgumentException("Course name is required");
        }
    }

    private void validateScore(Double score, String label, boolean required) {
        if (score == null) {
            if (required) {
                throw new IllegalArgumentException(label + " is required");
            }
            return;
        }
        if (score.isNaN() || score < 0 || score > 100) {
            throw new IllegalArgumentException(label + " must be between 0 and 100");
        }
    }

    // Removes blanks and case-insensitive duplicates from the skills list
    private List<String> cleanSkills(List<String> skills) {
        List<String> result = new ArrayList<>();
        if (skills == null) {
            return result;
        }
        for (String skill : skills) {
            if (skill == null || skill.isBlank()) {
                continue;
            }
            String trimmed = skill.trim();
            boolean exists = result.stream().anyMatch(x -> x.equalsIgnoreCase(trimmed));
            if (!exists) {
                result.add(trimmed);
            }
        }
        return result;
    }
}
