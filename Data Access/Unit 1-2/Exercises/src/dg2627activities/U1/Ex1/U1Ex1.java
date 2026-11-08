package dg2627activities.U1.Ex1;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 5. Activities 1
public class U1Ex1 {
    static class Student {
        private String name;

        public Student(String name, int mark) {
            this.name = name;
            this.mark = mark;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private int mark;

        public int getMark() {
            return mark;
        }

        public void setMark(int mark) {
            if (mark >= 0 && mark <= 10) {
                this.mark = mark;
            }
        }

        public boolean passed() {
            return mark >= 5;
        }

    }

    static class Students {
        private final List<Student> studentList;

        public Students() {
            studentList = new ArrayList<>();
        }

        // Agrega un nuevo alumno a la lista
        //
        public void addStudent(Student student) {
            studentList.add(student);
        }

        // Devuelve el alumno que está en la posición num
        //
        public Student getStudent(int num) {
            if (num >= 0 && num < studentList.size()) {
                return studentList.get(num);
            }
            return null;
        }

        // Devuelve la nota media de los alumnos
        //
        public float getAverageGrade() {
            if (studentList.isEmpty()) {
                return 0;
            }
            float average = 0;
            for (Student student : studentList) {
                average += student.getMark();
            }
            return average / studentList.size();

        }
    }

    static class StudentFTC extends Student {
        String company;
        String tutor;
        String instructor;

        public StudentFTC(String name, int mark, String company, String tutor, String instructor) {
            super(name, mark);
            this.company = company;
            this.tutor = tutor;
            this.instructor = instructor;

        }
    }

    static class StudentErasmus extends Student {

        LocalDate startDate;
        LocalDate endDate;
        String originCountry;

        public StudentErasmus(String name, int mark,  LocalDate startDate, LocalDate endDate, String originCountry) {
            super(name, mark);
            this.startDate = startDate;
            this.endDate = endDate;
            this.originCountry = originCountry;
        }
    }
}
