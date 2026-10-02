package U1.EJ2;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class U1Ex2 {

    // Very generic names.
    final String INVALID_BIRTH_DATE = "Birthdate is invalid";
    final String INVALID_PHONE = "Phone is invalid";
    final String INVALID_PAY = "Pay is not valid";

    class Person {
        //
        private String name;
        private Date birthDate;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(Date inputBirthDate) {
            if (inputBirthDate == null || inputBirthDate.after(new Date())) {
                throw new IllegalArgumentException(INVALID_BIRTH_DATE);
            }
            birthDate = inputBirthDate;
        }

        public int getAge() {
            return Period.between(birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();
        }
    }


    class Client extends Person {

        //
        private String phone;
        private List<Company> clientOf = new ArrayList<>();

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            String phoneRegex = "^((\\+|00)\\d{2,3})?\\d{9}$";
            if (phone.matches(phoneRegex)) {
                this.phone = phone;
            } else {
                throw new IllegalArgumentException(INVALID_PHONE);

            }
        }
    }

    class Worker extends Person {
        private double basePay;

        public double getBasePay() {
            return basePay;
        }

        public void setBasePay(double basePay) {
            if (basePay <= 0) throw new IllegalArgumentException(INVALID_PAY);
            this.basePay = basePay;
        }
    }

    class Director extends Worker {
        private String category;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        private List<Worker> SupervisedWorkers = new ArrayList<>();

        // No code provided to add.
        public int getSupervisedWorkers() {
            return SupervisedWorkers.size();
        }


    }

    class Company {
        //
        private String name;
        private List<Worker> employees = new ArrayList<>();
        private List<Client> clients = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getEmployeeCount() {
            return employees.size();
        }

        public int getClientCount() {
            return clients.size();
        }
    }


}
