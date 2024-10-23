package fr.cdrochon.multimedias.histogrammes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;

@Component
public class Descripteur64 {
    
    @Value("${images.list.file}")
    private String imagesListFile;  // Chemin vers le fichier texte listant les images
    
    @Value("${images.directory}")
    private String imagesDirectory;  // Chemin vers le répertoire contenant les images
    
    private static final int RED = 0, GREEN = 1, BLUE = 2;
    private int width, height;
    private float[][][] pixels;
    
    // Méthode principale pour calculer le descripteur de la première image avec 64 niveaux de gris
    public void calculateFirstImageGrayLevelHistogram() throws IOException {
        // Lire la première ligne du fichier texte
        List<String> imageNames = Files.readAllLines(Paths.get(imagesListFile));
        
        if (!imageNames.isEmpty()) {
            String firstImageName = imageNames.get(0);  // Prendre la première image
            
            // Construire le chemin complet de l'image dans le répertoire des images
            File imageFile = new File(imagesDirectory, firstImageName);
            
            // Vérifier si l'image existe dans le répertoire
            if (imageFile.exists() && (imageFile.getName().endsWith(".jpg") || imageFile.getName().endsWith(".png"))) {
                System.out.println("Traitement de l'image : " + firstImageName);
                
                // Lire l'image en utilisant la méthode `readImageFile` adaptée de JPictureFerecatu
                readImageFile(imageFile.getAbsolutePath());
                
                // Calculer l'histogramme en niveaux de gris avec 64 intervalles
                float[] grayHistogram = computeNormalizedGrayLevelHistogram();
                
                // Afficher l'histogramme normalisé
                displayNormalizedGrayLevelHistogram(grayHistogram);
                
            } else {
                System.out.println("L'image " + firstImageName + " n'existe pas dans le répertoire : " + imagesDirectory);
            }
        } else {
            System.out.println("Le fichier texte ne contient pas de lignes.");
        }
    }
    
    // Méthode pour lire l'image comme dans JPictureFerecatu
    private void readImageFile(String fileName) {
        try {
            BufferedImage img = ImageIO.read(new File(fileName));
            width = img.getWidth();
            height = img.getHeight();
            pixels = new float[width][height][3];  // Créer une matrice pour stocker les pixels RVB
            int[] rgbArray = img.getRGB(0, 0, width, height, null, 0, width);
            
            // Boucle pour lire les pixels et appliquer les corrections comme dans JPictureFerecatu
            System.out.println("Reading image file: " + fileName);
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    int pixel = rgbArray[y * width + x];
                    
                    // Extraire les composantes RVB et les normaliser entre 0 et 1
                    pixels[x][y][RED] = (float) ((pixel >> 16) & 0xff) / 255.0f;
                    pixels[x][y][GREEN] = (float) ((pixel >> 8) & 0xff) / 255.0f;
                    pixels[x][y][BLUE] = (float) (pixel & 0xff) / 255.0f;
                    
                    // Appliquer la même correction pour éviter les valeurs exactement égales à 1
                    if (pixels[x][y][RED] == 1.0f) pixels[x][y][RED] -= 1e-5f;
                    if (pixels[x][y][GREEN] == 1.0f) pixels[x][y][GREEN] -= 1e-5f;
                    if (pixels[x][y][BLUE] == 1.0f) pixels[x][y][BLUE] -= 1e-5f;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Méthode pour calculer l'histogramme en niveaux de gris normalisé avec 64 niveaux de gris
    private float[] computeNormalizedGrayLevelHistogram() {
        System.out.println("Executing: computeNormalizedGrayLevelHistogram 64 intervalles");
        int[] histogram = new int[64];  // Histogramme avec 64 niveaux de gris
        int totalPixels = width * height;  // Nombre total de pixels
        int binSize = 256 / 64;  // Taille des intervalles des bins, soit 256/64 = 4 niveaux de gris par bin
        
        // Parcourir tous les pixels de l'image pour calculer les niveaux de gris
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Calculer le niveau de gris pondéré en utilisant les valeurs RVB stockées
                float grayLevel = (float) (0.299 * pixels[x][y][RED] * 255.0 +
                        0.587 * pixels[x][y][GREEN] * 255.0 +
                        0.114 * pixels[x][y][BLUE] * 255.0);
                
                // Quantification sur 64 intervalles : chaque bin correspond à 4 niveaux de gris
                int binIndex = (int) grayLevel / binSize;
                
                // Incrémenter le bin correspondant dans l'histogramme
                histogram[binIndex]++;
            }
        }
        
        // Normaliser l'histogramme en divisant chaque bin par le nombre total de pixels
        float[] normalizedHistogram = new float[64];
        for (int i = 0; i < 64; i++) {
            normalizedHistogram[i] = (float) histogram[i] / totalPixels;  // Valeur normalisée entre 0 et 1
        }
        
        return normalizedHistogram;
    }
    
    // Méthode pour afficher l'histogramme en niveaux de gris normalisé (sur une seule ligne)
    private void displayNormalizedGrayLevelHistogram(float[] histogram) {
        System.out.print("Histogramme normalisé en niveaux de gris avec 64 intervalles : ");
        for (int i = 0; i < histogram.length; i++) {
            System.out.print(histogram[i]);
            if (i < histogram.length - 1) {
                System.out.print(" ");  // Ajouter un espace entre les valeurs
            }
        }
        System.out.println();  // Sauter une ligne à la fin
    }
}
