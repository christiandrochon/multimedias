package fr.cdrochon.multimedias.ihm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.Random;

public class ImageGridApp extends Application {

    private static final int GRID_SIZE = 4;
    private static final int BORDER_SIZE = 5;
    private static final String IMAGE_DIR = "ferecatu/Base10000/images";

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(BORDER_SIZE);
        gridPane.setVgap(BORDER_SIZE);

        File dir = new File(IMAGE_DIR);
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

        if (files != null && files.length >= GRID_SIZE * GRID_SIZE) {
            Random random = new Random();
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    int randomIndex = random.nextInt(files.length);
                    Image image = new Image(files[randomIndex].toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    gridPane.add(imageView, j, i);
                }
            }
        } else {
            System.err.println("Not enough images in the directory.");
        }

        Scene scene = new Scene(gridPane);
        primaryStage.setTitle("Image Grid");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
