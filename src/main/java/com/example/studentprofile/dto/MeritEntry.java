package com.example.studentprofile.dto;

/** One row of the merit list (Q2.5 / Q3.6). */
public record MeritEntry(int rank, Integer studentId, String studentName, String courseName,
                         double averageScore, String performanceCategory) {
}
