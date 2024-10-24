package fr.cdrochon.multimedias.histogrammes.couleur;

import fr.cdrochon.multimedias.histogrammes.AbstractIndexDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DescripteurRVB666_1f extends AbstractIndexDatabase {
    
    @Value("${histogram.rgb666.output}")
    private String rgbHistogramOutputFile;  // Fichier de sortie pour les histogrammes de couleur
    
    private static final int BINS = 2;  // Diviser chaque axe R, V, B en 2 intervalles
    private static final float BIN_SIZE = 1.0f / BINS;  // Taille de chaque bin
    private static final int RED = 0, GREEN = 1, BLUE = 2;
    
    public void process() throws IOException {
        processDatabase();
    }
    
    @Override
    protected float[] computeHistogram() {
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
    
    @Override
    protected String getOutputFilePath() {
        return rgbHistogramOutputFile;
    }
}
