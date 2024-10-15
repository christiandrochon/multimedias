package fr.cdrochon.multimedias;

import fr.cdrochon.multimedias.ihm.ImageGridApp;
import javafx.application.Application;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultimediasApplication implements CommandLineRunner {
    
    public static void main(String[] args) {
        SpringApplication.run(MultimediasApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        Application.launch(ImageGridApp.class, args);
    }
}
