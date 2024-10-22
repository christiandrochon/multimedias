package fr.cdrochon.multimedias.histogrammes;

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
public class HistogrammeGrisController {
    
    @Value("${images.directory}")
    private String imagesDirectory;
    
    // API pour calculer l'histogramme d'une image en niveaux de gris
    @GetMapping("/histogramme-gris")
    public Map<String, Object> computeGrayLevelHistogram(@RequestParam int numBins) throws IOException {
        // Sélectionner une image aléatoire dans le répertoire
        File imageFile = selectRandomImage(imagesDirectory);
        BufferedImage image = ImageIO.read(imageFile);
        
        // Calculer l'histogramme en niveaux de gris
        int[] histogram = computeHistogram(image, numBins);
        
        // Préparer la réponse JSON
        Map<String, Object> response = new HashMap<>();
        response.put("histogram", histogram);
        response.put("imageUrl", "http://localhost:8087/images/" + imageFile.getName());  // URL relative de l'image
        
        return response;
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
    
    /**
     * Fonction qui calcule l'histogramme d'une image en niveaux de gris
     *
     * @param image   Image
     * @param numBins Nombre de niveaux de gris
     * @return Histogramme
     */
    private int[] computeHistogram(BufferedImage image, int numBins) {
        int[] histogram = new int[numBins];
        int width = image.getWidth();
        int height = image.getHeight();
        int binSize = 256 / numBins;
        
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int grayLevel = (rgb >> 16) & 0xff;
                int binIndex = grayLevel / binSize;
                histogram[binIndex]++;
            }
        }
        
        return histogram;
    }
}
