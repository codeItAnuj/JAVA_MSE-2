-- =====================================================================
-- Q2: Student Profile and Skill Development Management System
-- Database + tables + 10 students + views (run in phpMyAdmin > SQL / Import)
-- Safe to run more than once. Compatible with XAMPP (MariaDB / MySQL).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS student_skill_db;
USE student_skill_db;

-- ---------------------------------------------------------------------
-- Table 1: students   (PRIMARY KEY => duplicate student IDs are rejected)
--          (CHECK     => scores must be between 0 and 100)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    student_id        INT          NOT NULL,
    student_name      VARCHAR(100) NOT NULL,
    email             VARCHAR(120) NOT NULL,
    course_name       VARCHAR(100) NOT NULL,
    programming_score DOUBLE       NOT NULL,
    mathematics_score DOUBLE       NOT NULL,
    PRIMARY KEY (student_id),
    UNIQUE KEY uk_students_email (email),
    CONSTRAINT chk_programming_score CHECK (programming_score BETWEEN 0 AND 100),
    CONSTRAINT chk_mathematics_score CHECK (mathematics_score BETWEEN 0 AND 100)
);

-- ---------------------------------------------------------------------
-- Table 2: student_skills   (one row per skill of a student)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS student_skills (
    student_id INT         NOT NULL,
    skill      VARCHAR(60) NOT NULL,
    PRIMARY KEY (student_id, skill),
    CONSTRAINT fk_skills_student FOREIGN KEY (student_id)
        REFERENCES students (student_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 10 student records
-- ---------------------------------------------------------------------
INSERT IGNORE INTO students
    (student_id, student_name, email, course_name, programming_score, mathematics_score)
VALUES
    (101, 'Aarav Sharma',  'aarav.sharma@example.com',  'B.Tech CSE', 92, 88),
    (102, 'Priya Singh',   'priya.singh@example.com',   'B.Tech CSE', 78, 85),
    (103, 'Rohan Verma',   'rohan.verma@example.com',   'BCA',        45, 82),
    (104, 'Sneha Patel',   'sneha.patel@example.com',   'B.Tech IT',  88, 90),
    (105, 'Karan Mehta',   'karan.mehta@example.com',   'BCA',        40, 76),
    (106, 'Ananya Gupta',  'ananya.gupta@example.com',  'MCA',        70, 65),
    (107, 'Vikram Rao',    'vikram.rao@example.com',    'B.Tech CSE', 35, 40),
    (108, 'Neha Joshi',    'neha.joshi@example.com',    'MCA',        95, 91),
    (109, 'Arjun Nair',    'arjun.nair@example.com',    'B.Tech IT',  60, 55),
    (110, 'Meera Iyer',    'meera.iyer@example.com',    'BCA',        48, 90);

-- skills (delete first so re-running this script never creates duplicates)
DELETE FROM student_skills WHERE student_id BETWEEN 101 AND 110;

INSERT INTO student_skills (student_id, skill) VALUES
    (101, 'Java'), (101, 'Spring Boot'), (101, 'MySQL'),
    (102, 'Python'), (102, 'SQL'), (102, 'Machine Learning'),
    (103, 'HTML'), (103, 'CSS'), (103, 'JavaScript'),
    (104, 'Java'), (104, 'Spring Boot'), (104, 'MySQL'),
    (105, 'Python'), (105, 'SQL'), (105, 'Machine Learning'),
    (106, 'HTML'), (106, 'CSS'), (106, 'JavaScript'),
    (107, 'C'), (107, 'Java'),
    (108, 'Java'), (108, 'Python'), (108, 'Docker'),
    (109, 'C++'), (109, 'DSA'),
    (110, 'Excel'), (110, 'Power BI');

-- ---------------------------------------------------------------------
-- View: average score, performance category, stronger subject (Q2.1 - Q2.3)
-- ---------------------------------------------------------------------
CREATE OR REPLACE VIEW student_performance AS
SELECT
    s.student_id,
    s.student_name,
    s.email,
    s.course_name,
    s.programming_score,
    s.mathematics_score,
    ROUND((s.programming_score + s.mathematics_score) / 2, 2) AS average_score,
    CASE
        WHEN (s.programming_score + s.mathematics_score) / 2 >= 85 THEN 'Excellent'
        WHEN (s.programming_score + s.mathematics_score) / 2 >= 70 THEN 'Good'
        WHEN (s.programming_score + s.mathematics_score) / 2 >= 50 THEN 'Satisfactory'
        ELSE 'Needs Improvement'
    END AS performance_category,
    CASE
        WHEN s.programming_score > s.mathematics_score THEN 'Programming'
        WHEN s.mathematics_score > s.programming_score THEN 'Mathematics'
        ELSE 'Both Equal'
    END AS stronger_subject
FROM students s;
