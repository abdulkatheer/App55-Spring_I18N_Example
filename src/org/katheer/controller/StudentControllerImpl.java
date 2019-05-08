package org.katheer.controller;

import org.katheer.dto.Student;
import org.katheer.event.create.StudentCreateEventPublisher;
import org.katheer.event.delete.StudentDeleteEventPublisher;
import org.katheer.event.search.StudentSearchEventPublisher;
import org.katheer.event.update.StudentUpdateEventPublisher;
import org.katheer.service.StudentService;
import org.katheer.test.Client;
import org.katheer.validator.StudentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller("studentController")
@Scope("prototype")
public class StudentControllerImpl implements StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentValidator studentValidator;
    private BufferedReader br;
    private MessageSource messageSource = (MessageSource) Client.context.getBean(
            "controllerMessageSource");
    private Locale locale = Client.locale;

    public StudentControllerImpl() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    private void printData(Student... students) {
        StudentSearchEventPublisher eventPublisher =
                (StudentSearchEventPublisher) Client.context.getBean(
                        "searchEventPublisher");
        System.out.println(messageSource.getMessage("printData.heading",
                null, locale));
        System.out.println(String.format("%-5s%-32s%-12s%-5s%-42s%-12s", "ID", "Student Name",
                "Dept", "CGPA", "Email", "Mobile"));
        if (students == null || students[0] == null) {
            System.out.println(messageSource.getMessage("printData.nodata",
                    null, locale));
            eventPublisher.publish("Student Search Failed. Status Code : " +
                    "norecordfound");
        } else {
            for (Student s : students) {
                System.out.println(String.format("%-5s%-32s%-12s%-5s%-42s%-12s",
                        String.valueOf(s.getId()), s.getName(), s.getDept(),
                        String.valueOf(s.getCgpa()), s.getEmail(), s.getMobile()));
            }
            eventPublisher.publish("Student Search Succeed");
        }
        System.out.println(messageSource.getMessage("printData.end", null, locale));
        System.out.println();
    }

    private boolean validateStudent(Student student) {
        Map<String, String> error = new HashMap<String, String>();
        MapBindingResult errors = new MapBindingResult(error, "org.katheer" +
                ".dto.Student");
        studentValidator.validate(student, errors);
        int errorCount = errors.getErrorCount();

        if (errorCount == 0) {
            return true;
        }

        List<ObjectError> allErrors = errors.getAllErrors();
        for (ObjectError error1 : allErrors) {
            System.out.println(error1.getDefaultMessage());
        }
        return false;
    }

    private Student getStudentDetails() throws Exception {
        System.out.println(messageSource.getMessage(
                "getStudentDetails.heading", null, locale));

        System.out.print(String.format("%s:\n", messageSource.getMessage(
                "getStudentDetails.name", null, locale)));
        String name = br.readLine().trim();

        System.out.print(String.format("%s:\n", messageSource.getMessage(
                "getStudentDetails.id", null, locale)));
        int id = Integer.parseInt(br.readLine());

        System.out.print(String.format("%s:\n", messageSource.getMessage(
                "getStudentDetails.dept", null, locale)));
        String dept = br.readLine().trim();

        System.out.print(String.format("%s:\n", messageSource.getMessage(
                "getStudentDetails.cgpa", null, locale)));
        float cgpa = Float.parseFloat(br.readLine());

        System.out.print(String.format("%s:\n", messageSource.getMessage(
                "getStudentDetails.email", null, locale)));
        String email = br.readLine().trim();

        System.out.print(String.format("%s:\n", messageSource.getMessage(
                "getStudentDetails.mobile", null, locale)));
        String mobile = br.readLine().trim();

        Student student = new Student();
        student.setName(name);
        student.setCgpa(cgpa);
        student.setDept(dept);
        student.setEmail(email);
        student.setId(id);
        student.setMobile(mobile);
        return student;
    }

    @Override
    public void addStudent() {
        StudentCreateEventPublisher eventPublisher = (StudentCreateEventPublisher)
                Client.context.getBean("createEventPublisher");
        try {
            System.out.println(messageSource.getMessage("addStudent.heading",
             null, locale));
            Student student = this.getStudentDetails();
            String status;

            if (validateStudent(student)) {
                status = studentService.addStudent(student);
            } else {
                status = "failed";
            }

            if (status.equals("success")) {
                System.out.println(messageSource.getMessage(
                        "addStudent.success", null, locale));
                eventPublisher.publish("Student Insertion Succeed");
            } else if (status.equals("exists")) {
                System.out.println(messageSource.getMessage(
                        "addStudent.exists", null, locale));
                eventPublisher.publish("Student Insertion failed. Status Code" +
                        " : exists");
            } else if (status.equals("failed")) {
                System.out.println(messageSource.getMessage("addStudent.failed", null, locale));
                eventPublisher.publish("Student Insertion Failed. Status Code" +
                        " : other");
            }
        } catch (Exception e) {
            System.out.println("***Exception in getting student details***");
            e.printStackTrace();
        }
    }

    @Override
    public void searchStudent() {
        while (true) {
            try {
                System.out.println(messageSource.getMessage(
                        "searchStudent.heading", null, locale));
                System.out.println(messageSource.getMessage(
                        "searchStudent.byId", null, locale));
                System.out.println(messageSource.getMessage(
                        "searchStudent.byName", null, locale));
                System.out.println(messageSource.getMessage(
                        "searchStudent.byDept", null, locale));
                System.out.println(messageSource.getMessage(
                        "searchStudent.exit", null, locale));
                System.out.print(messageSource.getMessage(
                        "searchStudent.enterChoice", null, locale));
                int choice = Integer.parseInt(br.readLine());
                switch (choice) {
                    case 1:
                        System.out.println(messageSource.getMessage(
                                "searchStudent.enterId", null, locale));
                        int id = Integer.parseInt(br.readLine());
                        printData(this.studentService.searchById(id));
                        break;
                    case 2:
                        System.out.println(messageSource.getMessage(
                                "searchStudent.enterName", null, locale));
                        String name = br.readLine().trim();
                        printData(this.studentService.searchByName(name));
                        break;
                    case 3:
                        System.out.println(messageSource.getMessage(
                                "searchStudent.enterDept", null, locale));
                        String dept = br.readLine().trim();
                        printData(this.studentService.searchByDept(dept));
                        break;
                    case 4:
                        System.out.println(messageSource.getMessage(
                                "searchStudent.thanks", null, locale));
                        return;
                    default:
                        System.out.println(messageSource.getMessage(
                                "searchStudent.wrong", null, locale));
                        System.out.println();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateStudent() {
        StudentUpdateEventPublisher eventPublisher =
                (StudentUpdateEventPublisher) Client.context.getBean(
                        "updateEventPublisher");
        try {
            int id;
            String status = "";
            System.out.println(messageSource.getMessage(
                    "updateStudent.heading", null, locale));
            System.out.print(messageSource.getMessage("updateStudent.id",
                    null, locale));
            id = Integer.parseInt(br.readLine());
            Student student = studentService.searchById(id);

            if (student == null) {
                status = "notexists";
            } else {
                System.out.println(String.format("%s : ",
                        messageSource.getMessage("updateStudent.name", null,
                         locale) + student.getName() + " ]"));
                String name = br.readLine().trim();

                System.out.println(String.format("%s : ",
                        messageSource.getMessage("updateStudent.dept", null,
                         locale) + student.getDept() + " ]"));
                String dept = br.readLine().trim();

                System.out.println(String.format("%s : ",
                        messageSource.getMessage("updateStudent.cgpa", null,
                         locale) + student.getCgpa() + " ]"));
                float cgpa = Float.parseFloat(br.readLine().trim());

                System.out.println(String.format("%s : ",
                        messageSource.getMessage("updateStudent.email", null,
                                locale) + student.getEmail() + " ]"));
                String email = br.readLine().trim();

                System.out.println(String.format("%s : ",
                        messageSource.getMessage("updateStudent.mobile",
                                null, locale) + student.getMobile() + " ]"));
                String mobile = br.readLine().trim();

                student.setName(name);
                student.setDept(dept);
                student.setCgpa(cgpa);
                student.setEmail(email);
                student.setMobile(mobile);


                if (validateStudent(student)) {
                    status = studentService.updateStudent(student);
                } else {
                    status = "failed";
                }
            }


            if (status.equals("success")) {
                System.out.println(messageSource.getMessage(
                        "updateStudent.success", null, locale));
                System.out.println();
                eventPublisher.publish("Student Update Succeed");
            } else if (status.equals("notexists")) {
                System.out.println(messageSource.getMessage(
                        "updateStudent.notexists", null, locale));
                System.out.println();
                eventPublisher.publish("Student Update Failed. Status Code : " +
                        "notexists");
            } else if (status.equals("failed")) {
                System.out.println(messageSource.getMessage(
                        "updateStudent.failed", null, locale));
                System.out.println();
                eventPublisher.publish("Student Update Failed. Status Code : " +
                        "other");
            }
        } catch (Exception e) {
            System.out.println("***Exception in getting student details***");
            e.printStackTrace();
        }
    }

    @Override
    public void removeStudent() {
        StudentDeleteEventPublisher eventPublisher =
                (StudentDeleteEventPublisher) Client.context.getBean(
                        "deleteEventPublisher");
        try {
            System.out.println(messageSource.getMessage(
                    "removeStudent.heading", null, locale));
            System.out.print(messageSource.getMessage("removeStudent.id",
                    null, locale));
            int id = Integer.parseInt(br.readLine());
            String status = studentService.removeStudent(id);

            if (status.equals("success")) {
                System.out.println(messageSource.getMessage(
                        "removeStudent.success", null, locale));
                System.out.println();
                eventPublisher.publish("Student Delete Succeed");
            } else if (status.equals("notexists")) {
                System.out.println(messageSource.getMessage(
                        "removeStudent.notexists", null, locale));
                System.out.println();
                eventPublisher.publish("Student Delete Failed. Status Code : " +
                        "notexists");
            } else {
                System.out.println(messageSource.getMessage(
                        "removeStudent.failed", null, locale));
                System.out.println();
                eventPublisher.publish("Student Delete Failed. Status Code : " +
                        "other");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
