package fr.cdrochon.multimedias.histogrammes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class DescripteurGris256_1f {
    
    @Value("${images.list.file}")
    private String imagesListFile;  // Chemin vers le fichier texte listant les images
    
    @Value("${images.directory}")
    private String imagesDirectory;  // Chemin vers le répertoire contenant les images
    @Value("${histogram.gray256.output}")
    private String grayHistogramOutputFile;  // Fichier de sortie pour les histogrammes en niveaux de gris
    
    @Value("${images.list.output}")
    private String imagesListOutputFile;  // Chemin vers le fichier de sauvegarde pour la copie du fichier texte
    
    private static final int RED = 0, GREEN = 1, BLUE = 2;
    private int width, height;
    private float[][][] pixels;
    
    /**
     * Méthode principale pour calculer le descripteur de la première image avec 256 niveaux de gris
     *
     * @throws IOException en cas d'erreur de lecture
     */
    public void calculateAllImageGrayLevelHistogram() throws IOException {
        // Lire la première ligne du fichier texte
        List<String> imageNames = Files.readAllLines(Paths.get(imagesListFile));
        
        // Créer un fichier de sortie pour les histogrammes en niveaux de gris
        try(FileWriter grayWriter = new FileWriter(grayHistogramOutputFile)) {
            // Parcourir chaque image et calculer son histogramme
            for(String imageName : imageNames) {
                // Construire le chemin complet de l'image dans le répertoire des images
                File imageFile = new File(imagesDirectory, imageName);
                
                // Vérifier si l'image existe dans le répertoire
                if(imageFile.exists() && (imageFile.getName().endsWith(".jpg") || imageFile.getName().endsWith(".png"))) {
                    System.out.println("Traitement de l'image : " + imageName);
                    
                    // Lire l'image en utilisant la méthode `readImageFile`
                    readImageFile(imageFile.getAbsolutePath());
                    
                    // Calculer l'histogramme en niveaux de gris avec 64 intervalles
                    float[] grayHistogram = computeNormalizedGrayLevelHistogram();
                    
                    // Sauvegarder l'histogramme dans le fichier de sortie
                    saveGrayHistogram(grayWriter, imageName, grayHistogram);
                    
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
    
    /**
     * Méthode pour lire l'image comme dans JPictureFerecatu
     *
     * @param fileName le nom du fichier image
     */
    private void readImageFile(String fileName) {
        try {
            BufferedImage img = ImageIO.read(new File(fileName));
            width = img.getWidth();
            height = img.getHeight();
            pixels = new float[width][height][3];  // Créer une matrice pour stocker les pixels RVB
            int[] rgbArray = img.getRGB(0, 0, width, height, null, 0, width);
            
            for(int x = 0; x < width; ++x) {
                for(int y = 0; y < height; ++y) {
                    int pixel = rgbArray[y * width + x];
                    
                    // Extraire les composantes RVB et les normaliser entre 0 et 1
                    pixels[x][y][RED] = (float) ((pixel >> 16) & 0xff) / 255.0f;
                    pixels[x][y][GREEN] = (float) ((pixel >> 8) & 0xff) / 255.0f;
                    pixels[x][y][BLUE] = (float) (pixel & 0xff) / 255.0f;
                    
                    // Correction pour éviter 1 exact
                    if(pixels[x][y][RED] == 1)
                        pixels[x][y][RED] -= 1e-5f;
                    if(pixels[x][y][GREEN] == 1)
                        pixels[x][y][GREEN] -= 1e-5f;
                    if(pixels[x][y][BLUE] == 1)
                        pixels[x][y][BLUE] -= 1e-5f;
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Méthode pour calculer l'histogramme en niveaux de gris normalisé avec 256 niveaux de gris, basé sur l'intervalle [0, 1]
     *
     * @return un tableau d'histogramme normalisé
     */
    private float[] computeNormalizedGrayLevelHistogram() {
        int[] histogram = new int[256];  // Histogramme avec 256 niveaux de gris
        int totalPixels = width * height;  // Nombre total de pixels
        
        // Parcourir tous les pixels de l'image pour calculer les niveaux de gris
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                // Calculer le niveau de gris pondéré en utilisant les valeurs RVB normalisées entre 0 et 1
                float grayLevel = 0.299f * pixels[x][y][RED] +
                        0.587f * pixels[x][y][GREEN] +
                        0.114f * pixels[x][y][BLUE];
                
                // Quantifier le niveau de gris en 256 bins (0 à 1)
                int binIndex = (int) (grayLevel * 255.0f);
                
                // Incrémenter le bin correspondant dans l'histogramme
                histogram[binIndex]++;
            }
        }
        
        // Normaliser l'histogramme en divisant chaque bin par le nombre total de pixels
        float[] normalizedHistogram = new float[256];
        for(int i = 0; i < 256; i++) {
            normalizedHistogram[i] = (float) histogram[i] / totalPixels;  // Valeur normalisée entre 0 et 1
        }
        
        return normalizedHistogram;
    }
    
    /**
     * Méthode pour sauvegarder l'histogramme en niveaux de gris dans un fichier
     *
     * @param writer    le fichier de sortie
     * @param imageName le nom de l'image
     * @param histogram l'histogramme en niveaux de gris
     * @throws IOException en cas d'erreur d'écriture
     */
    private void saveGrayHistogram(FileWriter writer, String imageName, float[] histogram) throws IOException {
        //        writer.write("Image: " + imageName + "\n");
        for(float value : histogram) {
            writer.write(value + " ");
        }
        writer.write("\n");  // Sauter une ligne après chaque histogramme
    }
    
    // Méthode pour copier le fichier de référence des images dans un fichier de sortie
    private void copyImagesListFile() throws IOException {
        Files.copy(Paths.get(imagesListFile), Paths.get(imagesListOutputFile));
        System.out.println("Le fichier de référence des images a été copié.");
    }
    
    // Méthode pour afficher l'histogramme en niveaux de gris normalisé (sur une seule ligne)
    //    private void displayNormalizedGrayLevelHistogram(float[] histogram) {
    //        System.out.print("Histogramme normalisé en niveaux de gris (basé sur l'intervalle [0, 1]) : ");
    //        for (int i = 0; i < histogram.length; i++) {
    //            System.out.print(histogram[i]);
    //            if (i < histogram.length - 1) {
    //                System.out.print(" ");  // Ajouter un espace entre les valeurs
    //            }
    //        }
    //        System.out.println();  // Sauter une ligne à la fin
    //    }
}
