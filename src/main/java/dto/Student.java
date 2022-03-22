package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Student {
    private String lastName;
    private List<Subject> subjectList;

    private double average;

    public double getCorrectAverage(Student foundStudent){
        if (foundStudent.average != average) {
            return foundStudent.subjectList
                    .stream()
                    .mapToInt(Subject::getMark)
                    .average()
                    .orElse(0.0);
        }
        return average;
    }
}
