package org.katheer.test;

import org.katheer.controller.StudentController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class Client {
    public static ApplicationContext context;
    private static BufferedReader br;
    private static StudentController studentController;
    private static MessageSource messageSource;
    public static Locale locale;

    static {
        context = new ClassPathXmlApplicationContext("org" +
                "/katheer/resource/applicationContext.xml");
        messageSource = (MessageSource) context.getBean("clientMessageSource");

        try {
            br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("***Application Starts***");

        System.out.println("CHOOSE APPLICATION LANGUGAE");
        System.out.println("1.TAMIL");
        System.out.println("2.ENGLISH");
        System.out.print("YOUR CHOICE : ");
        int lang = 0;

        while(true) {
            try {
                lang = Integer.parseInt(br.readLine().trim());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(lang == 1) {
                locale = new Locale("tm", "IN");
                break;
            } else if(lang == 2) {
                locale = new Locale("en", "US");
                break;
            } else {
                System.out.println("WRONG CHOICE! TRY AGAIN!!");
                continue;
            }
        }
    }

    public static void main(String[] args) {
        /*
        Custom Events:
        --------------
        1) Event class - contains event related features
        2) Publisher class - Publishes event
        3) Listener class - contains code to be executed while event occurs

        This Application has following features:
        1) Bean Validation
        2) Modularization
        3) MVC pattern followed
        4) Event Handling enabled - events logged
        5) I18N with two languages - English and Tamil
         */

        studentController = (StudentController) context.getBean(
                "studentController");

        int choice = 0;
        while (true) {
            try {
                System.out.println(messageSource.getMessage(
                        "heading", null, locale));
                System.out.println(messageSource.getMessage("add",
                        null, locale));
                System.out.println(messageSource.getMessage("update",
                        null, locale));
                System.out.println(messageSource.getMessage("search",
                        null, locale));
                System.out.println(messageSource.getMessage("delete",
                        null, locale));
                System.out.println(messageSource.getMessage("exit",
                        null, locale));
                System.out.print(messageSource.getMessage("enterChoice", null
                        , locale));
                choice = Integer.parseInt(br.readLine());

                switch (choice) {
                    case 1:
                        studentController.addStudent();
                        break;
                    case 2:
                        studentController.updateStudent();
                        break;
                    case 3:
                        studentController.searchStudent();
                        break;
                    case 4:
                        studentController.removeStudent();
                        break;
                    case 5:
                        System.out.println(messageSource.getMessage("thanks",
                         null, locale));
                        return;
                    default:
                        System.out.println(messageSource.getMessage("wrong",
                                null, locale));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
