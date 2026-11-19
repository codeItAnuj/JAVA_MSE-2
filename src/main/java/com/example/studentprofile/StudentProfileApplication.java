package com.example.studentprofile;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import com.example.studentprofile.model.Student;
import com.example.studentprofile.repository.StudentRepository;

@SpringBootApplication
public class StudentProfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentProfileApplication.class, args);
    }

    // Inserts 10 sample students the first time the app runs (only if the table is empty)
    @Bean
    @Order(1)
    CommandLineRunner seedData(StudentRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.saveAll(List.of(
                    new Student(101, "Aarav Sharma",  "aarav.sharma@example.com",  "B.Tech CSE", 92.0, 88.0, List.of("Java", "Spring Boot", "MySQL")),
                    new Student(102, "Priya Singh",   "priya.singh@example.com",   "B.Tech CSE", 78.0, 85.0, List.of("Python", "SQL", "Machine Learning")),
                    new Student(103, "Rohan Verma",   "rohan.verma@example.com",   "BCA",        45.0, 82.0, List.of("HTML", "CSS", "JavaScript")),
                    new Student(104, "Sneha Patel",   "sneha.patel@example.com",   "B.Tech IT",  88.0, 90.0, List.of("Java", "Spring Boot", "MySQL")),
                    new Student(105, "Karan Mehta",   "karan.mehta@example.com",   "BCA",        40.0, 76.0, List.of("Python", "SQL", "Machine Learning")),
                    new Student(106, "Ananya Gupta",  "ananya.gupta@example.com",  "MCA",        70.0, 65.0, List.of("HTML", "CSS", "JavaScript")),
                    new Student(107, "Vikram Rao",    "vikram.rao@example.com",    "B.Tech CSE", 35.0, 40.0, List.of("C", "Java")),
                    new Student(108, "Neha Joshi",    "neha.joshi@example.com",    "MCA",        95.0, 91.0, List.of("Java", "Python", "Docker")),
                    new Student(109, "Arjun Nair",    "arjun.nair@example.com",    "B.Tech IT",  60.0, 55.0, List.of("C++", "DSA")),
                    new Student(110, "Meera Iyer",    "meera.iyer@example.com",    "BCA",        48.0, 90.0, List.of("Excel", "Power BI"))
                ));
                System.out.println(">>> 10 sample students inserted.");
            }
        };
    }
}
