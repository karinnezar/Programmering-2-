
// Karin Nezar Mustafa 

package se.su.inlupp;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.HashSet;
import java.util.NoSuchElementException;

import java.io.*;

public class MenuHandler {

    private final Gui gui;

    public MenuHandler(Gui gui) {
        this.gui = gui;
    }

    public MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem newMapItem = new MenuItem("New Map");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem saveImageItem = new MenuItem("Save Image");
        MenuItem exitItem = new MenuItem("Exit");

        newMapItem.setOnAction(e -> loadNewMap(stage));
        openItem.setOnAction(e -> openGraphFile(stage));
        saveItem.setOnAction(e -> saveGraphToFile(stage));
        saveImageItem.setOnAction(e -> saveMapSnapshot());
        exitItem.setOnAction(e -> {
            if (gui.confirmDiscardChanges()) {
                stage.close();
            }
        });

        fileMenu.getItems().addAll(newMapItem, openItem, saveItem, saveImageItem, exitItem);
        menuBar.getMenus().add(fileMenu);
        return menuBar;
    }

    public void loadNewMap(Stage stage) {
        if (!gui.confirmDiscardChanges()) return;
    
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Välj kartbild");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Bildfiler", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
    
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
  
                gui.getMapPane().getChildren().clear();
                gui.getGraph().clear();
                gui.getSelectedPlaces().clear();
    
    
                Image image = new Image(file.toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
    
                gui.setBackgroundImage(imageView);
    
                Pane mapPane = gui.getMapPane();
                mapPane.setPrefSize(image.getWidth(), image.getHeight());
                mapPane.setMinSize(image.getWidth(), image.getHeight());
                mapPane.setMaxSize(image.getWidth(), image.getHeight());
    
                mapPane.getChildren().add(0, imageView);  // lägg bilden längst bak
    
            
                stage.setWidth(image.getWidth() + 50);
                stage.setHeight(image.getHeight() + 100);
                stage.setMinWidth(image.getWidth() + 50);
                stage.setMinHeight(image.getHeight() + 100);
    
        
                gui.getButtonHandler().setButtonsDisabled(false);
                gui.setUnsavedChanges(true);
    
            } catch (Exception e) {
                gui.showAlert("Error", "Can not upload graph: " + e.getMessage());
            }
        }
    }
    

    public void openGraphFile(Stage stage) {
        if (!gui.confirmDiscardChanges()) return;
    
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Graph File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graph Files", "*.graph"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Pane mapPane = gui.getMapPane();
            mapPane.getChildren().clear();
            gui.getGraph().clear();
            gui.getSelectedPlaces().clear();
    
            String imageFilename = reader.readLine().trim();
            if (imageFilename.startsWith("file:")) {
                imageFilename = imageFilename.substring(5);
            }
            File imageFile = new File(file.getParentFile(), imageFilename);
            Image image = new Image(imageFile.toURI().toString());
    
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true); // bevara proportioner

    
            mapPane.setPrefSize(image.getWidth(), image.getHeight());
            mapPane.setMinSize(image.getWidth(), image.getHeight());
            mapPane.setMaxSize(image.getWidth(), image.getHeight());
            mapPane.getChildren().add(0, imageView);
            gui.setBackgroundImage(imageView);
    
            Stage stageRef = (Stage) mapPane.getScene().getWindow();
            stageRef.setWidth(image.getWidth() + 50);
            stageRef.setHeight(image.getHeight() + 100);
    
            String[] nodeParts = reader.readLine().split(";");
            for (int i = 0; i < nodeParts.length; i += 3) {
                String name = nodeParts[i];
                double x = Double.parseDouble(nodeParts[i + 1]);
                double y = Double.parseDouble(nodeParts[i + 2]);
                gui.addPlace(name, x, y); 
            }
    
            Set<String> addedConnections = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                String fromName = parts[0];
                String toName = parts[1];
                String connName = parts[2];
                int weight = Integer.parseInt(parts[3]);
    
      
                String key = fromName.compareTo(toName) < 0
                        ? fromName + "-" + toName
                        : toName + "-" + fromName;
                if (addedConnections.contains(key)) continue;
                addedConnections.add(key);
    
                Place from = getPlaceByName(fromName);
                Place to = getPlaceByName(toName);
                gui.getGraph().connect(from, to, connName, weight);
                mapPane.getChildren().add(new Line(from.getCenterX(), from.getCenterY(), to.getCenterX(), to.getCenterY()));
            }
    
            gui.setUnsavedChanges(false);
            gui.getButtonHandler().setButtonsDisabled(false);
    
        } catch (Exception e) {
            gui.showAlert("Error", "Could not load file: " + e.getMessage());
        }
    }
    

    public void saveGraphToFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Graph File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graph Files", "*.graph"));
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            ImageView bg = gui.getBackgroundImage();
            String fullImagePath = new File(new File(bg.getImage().getUrl()).toURI()).getName();
            out.println(fullImagePath);

            StringBuilder nodesLine = new StringBuilder();
            for (Place place : gui.getGraph().getNodes()) {
                nodesLine.append(place.getName()).append(";")
                        .append((int) place.getCenterX()).append(";")
                        .append((int) place.getCenterY()).append(";");
            }
            if (nodesLine.length() > 0) nodesLine.setLength(nodesLine.length() - 1);
            out.println(nodesLine);

            Set<String> written = new HashSet<>();
            for (Place from : gui.getGraph().getNodes()) {
                for (Edge<Place> edge : gui.getGraph().getEdgesFrom(from)) {
                    Place to = edge.getDestination();
                    String key = from.getName() + "-" + to.getName();
                    String reverseKey = to.getName() + "-" + from.getName();
                    if (!written.contains(reverseKey)) {
                        out.println(from.getName() + ";" + to.getName() + ";" + edge.getName() + ";" + edge.getWeight());
                        written.add(key);
                    }
                }
            }

            gui.setUnsavedChanges(false);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save");
            alert.setHeaderText(null);
            alert.setContentText("Graph saved successfully.");
            alert.showAndWait();

        } catch (Exception e) {
            gui.showAlert("Error", "Could not save graph: " + e.getMessage());
        }
    }

    public void saveMapSnapshot() {
        WritableImage image = gui.getMapPane().snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        File outputFile = new File("capture.png");
    
        try {
            ImageIO.write(bufferedImage, "png", outputFile);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save Image");
            alert.setHeaderText(null);
            alert.setContentText("The map was saved as capture.png!");
            alert.showAndWait();
        } catch (IOException e) {
            gui.showAlert("Error", "Could not save the image.");
        }
    }
    
    private Place getPlaceByName(String name) {
        for (Place place : gui.getGraph().getNodes()) {
            if (place.getName().equals(name)) return place;
        }
        throw new NoSuchElementException("No place with name: " + name);
    }
}

