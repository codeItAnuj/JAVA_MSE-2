package com.example.studentprofile.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

/**
 * Q1(a) Student class.
 * Uses encapsulation (private fields), constructors, getters/setters and
 * object methods (toString, equals, hashCode, helper/business methods).
 * The student ID is supplied by the user (not auto-generated) so that
 * duplicate IDs can be detected and rejected (Q2 requirement 8).
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "student_name", nullable = false, length = 100)
    private String studentName;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "course_name", length = 100)
    private String courseName;

    @Column(name = "programming_score")
    private Double programmingScore;

    @Column(name = "mathematics_score")
    private Double mathematicsScore;

    // List of technical skills -> stored in the separate table "student_skills"
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_skills", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "skill", length = 60)
    private List<String> technicalSkills = new ArrayList<>();

    // ---------- Constructors ----------
    public Student() {
    }

    public Student(Integer studentId, String studentName, String email, String courseName,
                   Double programmingScore, Double mathematicsScore) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
    }

    public Student(Integer studentId, String studentName, String email, String courseName,
                   Double programmingScore, Double mathematicsScore, List<String> technicalSkills) {
        this(studentId, studentName, email, courseName, programmingScore, mathematicsScore);
        setTechnicalSkills(technicalSkills);
    }

    // ---------- Getters and Setters ----------
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(Double programmingScore) { this.programmingScore = programmingScore; }

    public Double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(Double mathematicsScore) { this.mathematicsScore = mathematicsScore; }

    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = (technicalSkills == null) ? new ArrayList<>() : new ArrayList<>(technicalSkills);
    }

    // ---------- Object / business methods (Q2: 1, 2, 3) ----------
    private double prog() {
        return programmingScore == null ? 0.0 : programmingScore;
    }

    private double math() {
        return mathematicsScore == null ? 0.0 : mathematicsScore;
    }

    // Q2.1 - average score (rounded to 2 decimals for display)
    public double getAverageScore() {
        return Math.round(((prog() + math()) / 2.0) * 100.0) / 100.0;
    }

    // Q2.2 - performance category (uses the exact, unrounded average)
    public String getPerformanceCategory() {
        double avg = (prog() + math()) / 2.0;
        if (avg >= 85) return "Excellent";
        if (avg >= 70) return "Good";
        if (avg >= 50) return "Satisfactory";
        return "Needs Improvement";
    }

    // Q2.3 - stronger subject
    public String getStrongerSubject() {
        if (prog() > math()) return "Programming";
        if (math() > prog()) return "Mathematics";
        return "Both Equal";
    }

    public boolean hasSkill(String skill) {
        if (skill == null) return false;
        return technicalSkills.stream().anyMatch(s -> s.equalsIgnoreCase(skill.trim()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student other = (Student) o;
        return Objects.equals(studentId, other.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return "Student{id=" + studentId + ", name='" + studentName + "', email='" + email
                + "', course='" + courseName + "', programming=" + programmingScore
                + ", mathematics=" + mathematicsScore + ", skills=" + technicalSkills + "}";
    }
}
