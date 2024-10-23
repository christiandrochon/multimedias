package fr.cdrochon.multimedias.histogrammes.couleur;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class HistogrammeCouleurController {
    
    @Value("${images.directory}")
    private String imagesDirectory;
    
    @GetMapping("/histogramme-couleur")
    public Map<String, Object> computeRGBHistogram(@RequestParam int numBins) throws IOException {
        // Sélectionner une image aléatoire
        File imageFile = selectRandomImage(imagesDirectory);
        BufferedImage image = ImageIO.read(imageFile);
        
        // Calculer les histogrammes RVB
        Map<String, int[]> rgbHistograms = computeRGBHistogram(image, numBins);
        
        Map<String, Object> response = new HashMap<>();
        response.put("redHistogram", rgbHistograms.get("red"));
        response.put("greenHistogram", rgbHistograms.get("green"));
        response.put("blueHistogram", rgbHistograms.get("blue"));
        response.put("imageUrl", "http://localhost:8087/images/" + imageFile.getName());
        
        return response;
    }
    
    /**
     * Fonction pour calculer les histogrammes de couleur RVB d'une image
     *
     * @param image   Image
     * @param numBins Nombre de bins pour chaque canal de couleur
     * @return Histogrammes RVB
     */
    private Map<String, int[]> computeRGBHistogram(BufferedImage image, int numBins) {
        int[] redHistogram = new int[numBins];
        int[] greenHistogram = new int[numBins];
        int[] blueHistogram = new int[numBins];
        int binSize = 256 / numBins;
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Parcourir tous les pixels
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xff;
                int green = (rgb >> 8) & 0xff;
                int blue = rgb & 0xff;
                
                redHistogram[red / binSize]++;
                greenHistogram[green / binSize]++;
                blueHistogram[blue / binSize]++;
            }
        }
        
        Map<String, int[]> histograms = new HashMap<>();
        histograms.put("red", redHistogram);
        histograms.put("green", greenHistogram);
        histograms.put("blue", blueHistogram);
        
        return histograms;
    }
    
    /**
     * Fonction qui sélectionne une image aléatoirement dans la bdd
     *
     * @param directory Répertoire contenant les images
     * @return Image aléatoire
     */
    private File selectRandomImage(String directory) {
        File dir = new File(directory);
        File[] imageFiles = dir.listFiles((d, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
        
        if(imageFiles == null || imageFiles.length == 0) {
            throw new RuntimeException("Aucune image trouvée dans le répertoire : " + directory);
        }
        
        Random random = new Random();
        return imageFiles[random.nextInt(imageFiles.length)];
    }
}
