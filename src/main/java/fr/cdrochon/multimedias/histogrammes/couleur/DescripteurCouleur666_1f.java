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
public class DescripteurCouleur666_1f {
    
    @Value("${images.list.file}")
    private String imagesListFile;  // Chemin vers le fichier texte listant les images
    
    @Value("${images.directory}")
    private String imagesDirectory;  // Chemin vers le répertoire contenant les images
    
    @Value("${histogram.rgb666.output}")
    private String rgbHistogramOutputFile;  // Fichier de sortie pour les histogrammes de couleur
    
    @Value("${images.list.output}")
    private String imagesListOutputFile;  // Fichier de sortie pour la copie du fichier texte
    
    private static final int RED = 0, GREEN = 1, BLUE = 2;
    private int width, height;
    private float[][][] pixels;
    
    private static final int BINS = 6;  // Diviser chaque axe R, V, B en 6 intervalles
    private static final float BIN_SIZE = 1.0f / BINS;  // Taille de chaque bin
    
    /**
     * Méthode principale pour calculer l'histogramme de couleur avec 6x6x6 bins pour toutes les images
     *
     * @throws IOException en cas d'erreur de lecture
     */
    public void calculateAllImageColorHistogramme() throws IOException {
        // Lire la liste des images à partir du fichier texte
        List<String> imageNames = Files.readAllLines(Paths.get(imagesListFile));
        
        // Créer un fichier de sortie pour les histogrammes de couleur
        try(FileWriter rgbWriter = new FileWriter(rgbHistogramOutputFile)) {
            // Parcourir chaque image et calculer son histogramme
            for(String imageName : imageNames) {
                // Construire le chemin complet de l'image dans le répertoire des images
                File imageFile = new File(imagesDirectory, imageName);
                
                // Vérifier si l'image existe dans le répertoire
                if(imageFile.exists() && (imageFile.getName().endsWith(".jpg") || imageFile.getName().endsWith(".png"))) {
                    System.out.println("Traitement de l'image : " + imageName);
                    
                    // Lire l'image en utilisant la méthode `readImageFile`
                    readImageFile(imageFile.getAbsolutePath());
                    
                    // Calculer l'histogramme de couleur avec 6x6x6 bins
                    float[] colorHistogram = computeColorHistogram666();
                    
                    // Sauvegarder l'histogramme dans le fichier de sortie
                    saveColorHistogram(rgbWriter, colorHistogram);
                    
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
     * Méthode pour calculer l'histogramme de couleur normalisé avec 6x6x6 bins
     *
     * @return l'histogramme de couleur normalisé
     */
    private float[] computeColorHistogram666() {
        int[] histogram = new int[BINS * BINS * BINS];  // Histogramme avec 6x6x6 bins
        int totalPixels = width * height;  // Nombre total de pixels
        
        // Parcourir tous les pixels de l'image pour calculer les niveaux de gris
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                // Quantification de chaque composante R, G, B (valeurs entre 0 et 1)
                int rBin = Math.min((int) (pixels[x][y][RED] / BIN_SIZE), BINS - 1);
                int gBin = Math.min((int) (pixels[x][y][GREEN] / BIN_SIZE), BINS - 1);
                int bBin = Math.min((int) (pixels[x][y][BLUE] / BIN_SIZE), BINS - 1);
                
                // Calculer l'index du bin dans l'histogramme 3D
                int binIndex = rBin * BINS * BINS + gBin * BINS + bBin;
                
                // Incrémenter le bin correspondant dans l'histogramme
                histogram[binIndex]++;
            }
        }
        
        // Normaliser l'histogramme en divisant chaque bin par le nombre total de pixels
        float[] normalizedHistogram = new float[BINS * BINS * BINS];
        for(int i = 0; i < BINS * BINS * BINS; i++) {
            normalizedHistogram[i] = (float) histogram[i] / totalPixels;  // Valeur normalisée entre 0 et 1
        }
        
        return normalizedHistogram;
    }
    
    /**
     * Méthode pour sauvegarder l'histogramme de couleur dans un fichier
     *
     * @param writer    le fichier de sortie
     * @param histogram l'histogramme de couleur normalisé
     * @throws IOException en cas d'erreur d'écriture
     */
    private void saveColorHistogram(FileWriter writer, float[] histogram) throws IOException {
        for(float value : histogram) {
            writer.write(value + " ");
        }
        writer.write("\n");  // Sauter une ligne après chaque histogramme
    }
    
    /**
     * Méthode pour copier le fichier de référence des images dans un fichier de sortie
     *
     * @throws IOException en cas d'erreur de copie
     */
    private void copyImagesListFile() throws IOException {
        Files.copy(Paths.get(imagesListFile), Paths.get(imagesListOutputFile));
        System.out.println("Le fichier de référence des images a été copié.");
    }
}
