package fr.cdrochon.multimedias.images;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class MultimediaController {
    
    @Value("${images.directory}")
    private String imagesDirectory;
    
    @GetMapping("/hello")
    public String sayHell() {
        return "Hello";
    }
    
    @GetMapping("/images")
    public List<String> getImages() {
        File dir = new File(imagesDirectory);
        
        // Vérification que le répertoire existe et est un répertoire valide
        if(!dir.exists() || !dir.isDirectory()) {
            throw new RuntimeException("Directory not found or not valid: " + imagesDirectory);
        }
        
        // Liste des fichiers dans le répertoire
        List<File> files = Arrays.stream(dir.listFiles())
                                 .filter(file -> !file.isDirectory() && isImageFile(file))
                                 .collect(Collectors.toList());
        
        // Vérifier si on a assez d'images
        if(files.size() < 16) {
            throw new RuntimeException("Not enough images found in directory. Found " + files.size());
        }
        
        // Mélanger les fichiers et en sélectionner 16 aléatoirement
        Collections.shuffle(files);
        List<File> selectedFiles = files.subList(0, 16);
        
        // Retourner les chemins relatifs ou absolus des images sélectionnées
        return selectedFiles.stream()
                            .map(file -> "/images/" + file.getName())
                            .collect(Collectors.toList());
    }
    
    // Fonction utilitaire pour vérifier si le fichier est une image
    private boolean isImageFile(File file) {
        String[] extensions = {"jpg", "jpeg", "png", "gif", "bmp"};
        String fileName = file.getName().toLowerCase();
        return Arrays.stream(extensions).anyMatch(fileName::endsWith);
    }
}
