package com.example.backend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}





































// package com.example.backend;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class BackendApplication implements CommandLineRunner {

//     @Autowired
//     private EmailSenderService emailSenderService;

//     public static void main(String[] args) {
//         SpringApplication.run(BackendApplication.class, args);
//     }

//     @Override
//     public void run(String... args) {
//         try {
//             emailSenderService.sendEmail(
//                 "hobori2155@wivstore.com",
//                 "Test Subject",
//                 "This is a test email body"
//             );
//             System.out.println("Test email sent successfully!");
//         } catch (Exception e) {
//             System.err.println("Error sending test email: " + e.getMessage());
//         }
//     }
// }