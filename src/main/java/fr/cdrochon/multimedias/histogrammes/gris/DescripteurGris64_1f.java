package fr.cdrochon.multimedias.histogrammes.gris;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;

@Component
public class DescripteurGris64_1f {
    
    static final int RGB_DEPTH = 3, RED = 0, GREEN = 1, BLUE = 2;
    int width, height;
    float[][][] pixels;  // Tableau pour stocker les valeurs RVB normalisées
    
    @Value("${images.list.file}")
    private String imagesListFile;  // Chemin vers le fichier texte listant les images
    
    @Value("${images.directory}")
    private String imagesDirectory;  // Chemin vers le répertoire contenant les images
    
    @Value("${histogram.gray64.output}")
    private String grayHistogramOutputFile;  // Fichier de sortie pour les histogrammes en niveaux de gris
    
    @Value("${images.list.output}")
    private String imagesListOutputFile;  // Chemin vers le fichier de sauvegarde pour la copie du fichier texte
    
    // Constructeur par défaut
    public DescripteurGris64_1f() {
    }
    
    // Méthode principale pour calculer l'histogramme de toutes les images avec 64 niveaux de gris
    public void calculateAllImagesGrayLevelHistogram() throws IOException {
        // Lire toutes les lignes du fichier texte
        List<String> imageNames = Files.readAllLines(Paths.get(imagesListFile));
        
        // Créer un fichier de sortie pour les histogrammes en niveaux de gris
        try (FileWriter grayWriter = new FileWriter(grayHistogramOutputFile)) {
            // Parcourir chaque image et calculer son histogramme
            for (String imageName : imageNames) {
                // Construire le chemin complet de l'image dans le répertoire des images
                File imageFile = new File(imagesDirectory, imageName);
                
                // Vérifier si l'image existe dans le répertoire
                if (imageFile.exists() && (imageFile.getName().endsWith(".jpg") || imageFile.getName().endsWith(".png"))) {
                    System.out.println("Traitement de l'image : " + imageName);
                    
                    // Lire l'image en utilisant la méthode `readImageFile`
                    readImageFile(imageFile.getAbsolutePath());
                    
                    // Calculer l'histogramme en niveaux de gris avec 64 intervalles
                    float[] grayHistogram = computeNormalizedGrayLevelHistogram64Bins();
                    
                    // Sauvegarder l'histogramme dans le fichier de sortie
//                    if(Files.exists(Paths.get(grayHistogramOutputFile))) {
//                        Files.delete(Paths.get(grayHistogramOutputFile));
                        saveGrayHistogram(grayWriter, imageName, grayHistogram);
//                    }
                    
                } else {
                    System.out.println("L'image " + imageName + " n'existe pas dans le répertoire : " + imagesDirectory);
                }
            }
        }
        
        // Copier le fichier de référence des images dans le fichier de sortie
        if(!Files.exists(Paths.get(imagesListOutputFile))) {
            copyImagesListFile();
        }
        System.out.println("L'indexation des images est terminée.");
    }
    
    // Méthode pour lire l'image
    private void readImageFile(String fileName) throws IOException {
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
    }
    
    // Méthode pour calculer l'histogramme en niveaux de gris normalisé avec 64 niveaux de gris
    private float[] computeNormalizedGrayLevelHistogram64Bins() {
        int[] histogram = new int[64];  // Histogramme avec 64 niveaux de gris
        int totalPixels = width * height;  // Nombre total de pixels
        float binSize = 1.0f / 64;  // Taille des intervalles des bins dans l'intervalle [0, 1]
        
        // Parcourir tous les pixels de l'image pour calculer les niveaux de gris
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Calculer le niveau de gris pondéré (valeur entre 0 et 1)
                float grayLevel = (0.299f * pixels[x][y][RED] +
                        0.587f * pixels[x][y][GREEN] +
                        0.114f * pixels[x][y][BLUE]);
                
                // Quantification sur 64 intervalles : chaque bin correspond à une partie de l'intervalle [0, 1]
                int binIndex = (int) (grayLevel / binSize);
                
                // Assurer que le bin indexé est valide (éviter les dépassements)
                if (binIndex >= 64) {
                    binIndex = 63;
                }
                
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
    
    // Méthode pour sauvegarder l'histogramme en niveaux de gris dans un fichier
    private void saveGrayHistogram(FileWriter writer, String imageName, float[] histogram) throws IOException {
//        writer.write("Image: " + imageName + "\n");
        for (float value : histogram) {
            writer.write(value + " ");
        }
        writer.write("\n");  // Sauter une ligne après chaque histogramme
    }
    
    // Méthode pour copier le fichier de référence des images dans un fichier de sortie
    private void copyImagesListFile() throws IOException {
        Files.copy(Paths.get(imagesListFile), Paths.get(imagesListOutputFile));
        System.out.println("Le fichier de référence des images a été copié.");
    }
}
