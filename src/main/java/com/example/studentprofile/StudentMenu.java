package com.example.studentprofile;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.studentprofile.dto.MeritEntry;
import com.example.studentprofile.dto.PerformanceEntry;
import com.example.studentprofile.dto.SameSkillGroup;
import com.example.studentprofile.model.Student;
import com.example.studentprofile.service.StudentService;

/**
 * Menu-driven console program for Q2.
 * Runs in the Eclipse Console after the application starts (the REST API keeps
 * working at the same time). Set app.menu.enabled=false in application.properties
 * to turn the menu off.
 */
@Component
@Order(2)   // runs after the sample-data runner (@Order(1))
@ConditionalOnProperty(name = "app.menu.enabled", havingValue = "true", matchIfMissing = true)
public class StudentMenu implements CommandLineRunner {

    private final StudentService service;
    private final ApplicationContext context;
    private final Scanner scanner = new Scanner(System.in);

    public StudentMenu(StudentService service, ApplicationContext context) {
        this.service = service;
        this.context = context;
    }

    @Override
    public void run(String... args) {
        System.out.println();
        System.out.println("Menu started. Click inside the Eclipse Console and type your choice.");
        while (true) {
            printMenu();
            String choice = read("Enter your choice: ");
            if (choice == null) {
                return;                     // no console input available
            }
            switch (choice.trim()) {
                case "1": showPerformance(); break;
                case "2": searchById(); break;
                case "3": searchByEmail(); break;
                case "4": searchByCourse(); break;
                case "5": showMeritList(); break;
                case "6": showSameSkills(); break;
                case "7": showNeedsImprovement(); break;
                case "8": addStudent(); break;
                case "0":
                    System.out.println("Goodbye!");
                    System.exit(SpringApplication.exit(context));
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number from the menu.");
            }
        }
    }

    // ---------------------------------------------------------------- menu
    private void printMenu() {
        System.out.println();
        System.out.println("==============================================================");
        System.out.println("   STUDENT PROFILE AND SKILL DEVELOPMENT MANAGEMENT SYSTEM");
        System.out.println("==============================================================");
        System.out.println(" 1. Average score, performance category, stronger subject");
        System.out.println(" 2. Search student by ID");
        System.out.println(" 3. Search student by email");
        System.out.println(" 4. Search students by course");
        System.out.println(" 5. Merit list (average score, highest first)");
        System.out.println(" 6. Students having the same technical skills");
        System.out.println(" 7. Need improvement in programming but maths >= 75");
        System.out.println(" 8. Add a new student (duplicate ID / score validation)");
        System.out.println(" 0. Exit");
        System.out.println("--------------------------------------------------------------");
    }

    // ------------------------------------------------- Q2.1, Q2.2, Q2.3
    private void showPerformance() {
        List<PerformanceEntry> list = service.getPerformance();
        System.out.println();
        System.out.printf("%-5s %-16s %-6s %-6s %-8s %-18s %s%n",
                "ID", "Name", "Prog", "Math", "Average", "Category", "Stronger Subject");
        System.out.println("-----------------------------------------------------------------------------------");
        for (PerformanceEntry p : list) {
            System.out.printf("%-5d %-16s %-6.1f %-6.1f %-8.2f %-18s %s%n",
                    p.studentId(), p.studentName(), p.programmingScore(), p.mathematicsScore(),
                    p.averageScore(), p.performanceCategory(), p.strongerSubject());
        }
    }

    // ------------------------------------------------------------ Q2.4
    private void searchById() {
        Integer id = parseInt(readOr("Enter student ID: "));
        if (id == null) {
            System.out.println("Invalid ID. Please enter a number.");
            return;
        }
        service.getStudentById(id).ifPresentOrElse(
                s -> printStudents(List.of(s)),
                () -> System.out.println("No student found with ID " + id));
    }

    private void searchByEmail() {
        String email = readOr("Enter email address: ").trim();
        List<Student> result = service.search(null, email, null);
        if (result.isEmpty()) {
            System.out.println("No student found with email " + email);
        } else {
            printStudents(result);
        }
    }

    private void searchByCourse() {
        String course = readOr("Enter course name (e.g. BCA): ").trim();
        List<Student> result = service.search(null, null, course);
        if (result.isEmpty()) {
            System.out.println("No students found in course " + course);
        } else {
            printStudents(result);
        }
    }

    // ------------------------------------------------------------ Q2.5
    private void showMeritList() {
        List<MeritEntry> merit = service.getMeritList();
        System.out.println();
        System.out.printf("%-5s %-5s %-16s %-12s %-8s %s%n",
                "Rank", "ID", "Name", "Course", "Average", "Category");
        System.out.println("--------------------------------------------------------------------");
        for (MeritEntry m : merit) {
            System.out.printf("%-5d %-5d %-16s %-12s %-8.2f %s%n",
                    m.rank(), m.studentId(), m.studentName(), m.courseName(),
                    m.averageScore(), m.performanceCategory());
        }
    }

    // ------------------------------------------------------------ Q2.6
    private void showSameSkills() {
        List<SameSkillGroup> groups = service.getSameSkillGroups();
        if (groups.isEmpty()) {
            System.out.println("No students have the same set of technical skills.");
            return;
        }
        int n = 1;
        for (SameSkillGroup g : groups) {
            System.out.println();
            System.out.println("Group " + n++ + "  Skills: " + String.join(", ", g.skills()));
            for (String s : g.students()) {
                System.out.println("    " + s);
            }
        }
    }

    // ------------------------------------------------------------ Q2.7
    private void showNeedsImprovement() {
        Double below = parseDouble(readOr("Programming score below (press Enter for 50): "));
        Double mathMin = parseDouble(readOr("Mathematics score at least (press Enter for 75): "));
        double b = (below == null) ? 50 : below;
        double m = (mathMin == null) ? 75 : mathMin;
        List<Student> result = service.getNeedingProgrammingImprovement(b, m);
        System.out.println("Programming < " + b + " and Mathematics >= " + m + ":");
        if (result.isEmpty()) {
            System.out.println("No students match.");
        } else {
            printStudents(result);
        }
    }

    // ------------------------------------------------------------ Q2.8
    private void addStudent() {
        System.out.println("--- Add new student ---");
        Integer id = parseInt(readOr("Student ID: "));
        String name = readOr("Student name: ").trim();
        String email = readOr("Email address: ").trim();
        String course = readOr("Course name: ").trim();
        Double prog = parseDouble(readOr("Programming score (0-100): "));
        Double math = parseDouble(readOr("Mathematics score (0-100): "));
        String skillText = readOr("Technical skills (comma separated): ");
        List<String> skills = Arrays.asList(skillText.split(","));

        try {
            Student saved = service.addStudent(new Student(id, name, email, course, prog, math, skills));
            System.out.println("Student added successfully: " + saved.getStudentName()
                    + " (average " + saved.getAverageScore() + ", " + saved.getPerformanceCategory() + ")");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("Could not add student: " + ex.getMessage());
        }
    }

    // ------------------------------------------------------------ helpers
    private void printStudents(List<Student> students) {
        System.out.println();
        System.out.printf("%-5s %-16s %-26s %-11s %-6s %-6s %-8s %-18s %s%n",
                "ID", "Name", "Email", "Course", "Prog", "Math", "Average", "Category", "Skills");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------");
        for (Student s : students) {
            System.out.printf("%-5d %-16s %-26s %-11s %-6.1f %-6.1f %-8.2f %-18s %s%n",
                    s.getStudentId(), s.getStudentName(), s.getEmail(), s.getCourseName(),
                    s.getProgrammingScore(), s.getMathematicsScore(), s.getAverageScore(),
                    s.getPerformanceCategory(), String.join(", ", s.getTechnicalSkills()));
        }
    }

    // returns null when the console input has ended
    private String read(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    private String readOr(String prompt) {
        String value = read(prompt);
        return value == null ? "" : value;
    }

    private Integer parseInt(String text) {
        try {
            return Integer.valueOf(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String text) {
        try {
            return Double.valueOf(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
