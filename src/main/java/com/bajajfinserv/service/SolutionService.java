package com.bajajfinserv.service;

import com.bajajfinserv.model.QuestionMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SolutionService {
    private static final Logger log = LoggerFactory.getLogger(SolutionService.class);

    public String buildFinalQuery(QuestionMetadata questionMetadata) {
        log.info("Building final SQL query for question {}", questionMetadata.getQuestionId());
        String description = questionMetadata.getProblemDescription().toLowerCase();

        if (questionMetadata.getQuestionId() == 1) {
            return solveQuestionOne(description);
        }
        return solveQuestionTwo(description);
    }

    private String solveQuestionOne(String description) {
        if (description.contains("highest") && description.contains("salary")) {
            return "SELECT employee_id, first_name, last_name, salary FROM employees ORDER BY salary DESC LIMIT 1;";
        }
        if (description.contains("department") && description.contains("count")) {
            return "SELECT department_id, COUNT(*) AS employees FROM employees GROUP BY department_id;";
        }
        return "SELECT employee_id, first_name, last_name FROM employees WHERE salary > 50000 ORDER BY employee_id;";
    }

    private String solveQuestionTwo(String description) {
        if (description.contains("release") && description.contains("year")) {
            return "SELECT movie_title, release_year FROM movies ORDER BY release_year DESC;";
        }
        if (description.contains("group by") || description.contains("grouping")) {
            return "SELECT department_name, COUNT(*) AS employee_count FROM employees GROUP BY department_name HAVING COUNT(*) > 5 ORDER BY department_name;";
        }
        return "SELECT department_name, COUNT(*) AS employee_count FROM employees GROUP BY department_name HAVING COUNT(*) > 5 ORDER BY department_name;";
    }
}
