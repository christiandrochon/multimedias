package fr.cdrochon.multimedias.histogrammes.gris;

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
public class Descripteur16 {
    
    static final int RGB_DEPTH = 3, RED = 0, GREEN = 1, BLUE = 2;
    int width, height;
    float[][][] pixels;  // Tableau pour stocker les valeurs RVB normalisées
    
    @Value("${images.list.file}")
    private String imagesListFile;  // Chemin vers le fichier texte listant les images
    
    @Value("${images.directory}")
    private String imagesDirectory;  // Chemin vers le répertoire contenant les images
    
    // Constructeur par défaut
    public Descripteur16() {
    }
    
    // Méthode principale pour calculer l'histogramme de la première image avec 16 niveaux de gris
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
                
                // Lire l'image en utilisant la méthode `readImageFile`
                readImageFile(imageFile.getAbsolutePath());
                
                // Calculer l'histogramme en niveaux de gris avec 16 intervalles
                float[] grayHistogram = computeNormalizedGrayLevelHistogram16Bins();
                
                // Afficher l'histogramme normalisé
                displayNormalizedGrayLevelHistogram(grayHistogram);
                
            } else {
                System.out.println("L'image " + firstImageName + " n'existe pas dans le répertoire : " + imagesDirectory);
            }
        } else {
            System.out.println("Le fichier texte ne contient pas de lignes.");
        }
    }
    
    // Méthode pour lire l'image en utilisant la méthode de lecture exacte
    private void readImageFile(String fileName) {
        try {
            BufferedImage img = ImageIO.read(new File(fileName));
            width = img.getWidth();
            height = img.getHeight();
            pixels = new float[width][height][RGB_DEPTH];  // Créer une matrice pour stocker les pixels RVB
            int[] rgbArray = img.getRGB(0, 0, width, height, null, 0, width);
            int pixel;
            
            // Lire chaque pixel et normaliser les valeurs RVB
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    pixel = rgbArray[y * width + x];
                    // Extraire les valeurs RGB normalisées entre 0 et 1
                    pixels[x][y][RED] = (float) ((pixel >> 16) & 0xff) / 255.0f;
                    pixels[x][y][GREEN] = (float) ((pixel >> 8) & 0xff) / 255.0f;
                    pixels[x][y][BLUE] = (float) (pixel & 0xff) / 255.0f;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Méthode pour calculer l'histogramme en niveaux de gris normalisé avec 16 niveaux de gris
    private float[] computeNormalizedGrayLevelHistogram16Bins() {
        int[] histogram = new int[16];  // Histogramme avec 16 niveaux de gris
        int totalPixels = width * height;  // Nombre total de pixels
        float binSize = 1.0f / 16;  // Taille des intervalles des bins dans l'intervalle [0, 1]
        
        // Parcourir tous les pixels de l'image pour calculer les niveaux de gris
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Calculer le niveau de gris pondéré (valeur entre 0 et 1)
                float grayLevel = (0.299f * pixels[x][y][RED] +
                        0.587f * pixels[x][y][GREEN] +
                        0.114f * pixels[x][y][BLUE]);
                
                // Quantification sur 16 intervalles : chaque bin correspond à une partie de l'intervalle [0, 1]
                int binIndex = (int) (grayLevel / binSize);
                
                // Assurer que le bin indexé est valide (éviter les dépassements)
                if (binIndex >= 16) {
                    binIndex = 15;
                }
                
                // Incrémenter le bin correspondant dans l'histogramme
                histogram[binIndex]++;
            }
        }
        
        // Normaliser l'histogramme en divisant chaque bin par le nombre total de pixels
        float[] normalizedHistogram = new float[16];
        for (int i = 0; i < 16; i++) {
            normalizedHistogram[i] = (float) histogram[i] / totalPixels;  // Valeur normalisée entre 0 et 1
        }
        
        return normalizedHistogram;
    }
    
    // Méthode pour afficher l'histogramme en niveaux de gris normalisé (sur une seule ligne)
    private void displayNormalizedGrayLevelHistogram(float[] histogram) {
        System.out.print("Histogramme normalisé en niveaux de gris avec 16 intervalles : ");
        for (int i = 0; i < histogram.length; i++) {
            System.out.print(histogram[i]);
            if (i < histogram.length - 1) {
                System.out.print(" ");  // Ajouter un espace entre les valeurs
            }
        }
        System.out.println();  // Sauter une ligne à la fin
    }
}
