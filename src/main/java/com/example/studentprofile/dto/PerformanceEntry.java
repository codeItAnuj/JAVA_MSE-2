package com.example.studentprofile.dto;

/** Average, category and stronger subject of a student (Q2.1, Q2.2, Q2.3). */
public record PerformanceEntry(Integer studentId, String studentName, double programmingScore,
                               double mathematicsScore, double averageScore,
                               String performanceCategory, String strongerSubject) {
}
