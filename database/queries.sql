-- =====================================================================
-- Q2 requirement queries.  Run in phpMyAdmin > student_skill_db > SQL tab
-- (run database.sql first so the tables, data and view exist)
-- =====================================================================
USE student_skill_db;

-- Q2.1 + Q2.2 + Q2.3  Average score, performance category, stronger subject
SELECT student_id, student_name, programming_score, mathematics_score,
       average_score, performance_category, stronger_subject
FROM student_performance
ORDER BY student_id;

-- Q2.4  Search students by ID, email or course
SELECT * FROM students WHERE student_id = 101;
SELECT * FROM students WHERE email = 'priya.singh@example.com';
SELECT * FROM students WHERE course_name = 'BCA';

-- Q2.5  Merit list (sorted by average score, descending) with rank
SELECT RANK() OVER (ORDER BY average_score DESC) AS merit_rank,
       student_id, student_name, course_name, average_score, performance_category
FROM student_performance
ORDER BY average_score DESC;
-- (if your XAMPP version is old and RANK() gives an error, use this instead)
-- SELECT student_id, student_name, course_name, average_score, performance_category
-- FROM student_performance ORDER BY average_score DESC;

-- Q2.6  Students who have the same technical skills (identical skill sets)
SELECT t.skill_set,
       GROUP_CONCAT(CONCAT(t.student_id, ' - ', t.student_name) ORDER BY t.student_id SEPARATOR ', ') AS students,
       COUNT(*) AS student_count
FROM (
    SELECT s.student_id, s.student_name,
           GROUP_CONCAT(k.skill ORDER BY k.skill SEPARATOR ', ') AS skill_set
    FROM students s
    JOIN student_skills k ON k.student_id = s.student_id
    GROUP BY s.student_id, s.student_name
) t
GROUP BY t.skill_set
HAVING COUNT(*) > 1;

-- Q2.7  Need improvement in programming (score below 50) but mathematics >= 75
SELECT student_id, student_name, programming_score, mathematics_score
FROM students
WHERE programming_score < 50 AND mathematics_score >= 75
ORDER BY student_id;

-- Q2.8  Duplicate IDs and invalid scores are rejected by the table design.
--       Try these (each one SHOULD give an error):
-- INSERT INTO students VALUES (101, 'Dup Student', 'dup@example.com', 'BCA', 50, 50);   -- Duplicate entry (primary key)
-- INSERT INTO students VALUES (111, 'Bad Score',  'bad@example.com', 'BCA', 150, 50);  -- CHECK constraint fails (score > 100)
-- INSERT INTO students VALUES (112, 'Bad Score2', 'bad2@example.com','BCA', -5, 50);   -- CHECK constraint fails (score < 0)
