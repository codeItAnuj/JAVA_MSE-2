package com.example.studentprofile.dto;

import java.util.List;

/** A group of students having exactly the same set of technical skills (Q2.6). */
public record SameSkillGroup(List<String> skills, List<String> students) {
}
