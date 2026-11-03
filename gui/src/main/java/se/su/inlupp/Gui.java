
// Karin Nezar Mustafa 
package se.su.inlupp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class Gui extends Application {

    private boolean addingNewPlace = false;
    private boolean unsavedChanges = false;

    private final Graph<Place> graph = new ListGraph<>();
    private final List<Place> selectedPlaces = new ArrayList<>();

    private Pane mapPane;
    private ButtonHandler buttonHandler;
    private MenuHandler menuHandler;
    private ImageView backgroundImage;

    @Override
    public void start(Stage stage) {
        mapPane = new Pane();
        mapPane.setPrefSize(800, 600);

        menuHandler = new MenuHandler(this);
        MenuBar menuBar = menuHandler.createMenuBar(stage);

        buttonHandler = new ButtonHandler(this);
        HBox buttonBox = buttonHandler.createButtonBox();
        buttonHandler.setButtonsDisabled(true);

        mapPane.setOnMouseClicked(e -> buttonHandler.handleMapClick(e));

        StackPane centerArea = new StackPane(mapPane);
        centerArea.setAlignment(Pos.CENTER);

        VBox root = new VBox(menuBar, buttonBox, centerArea);
        VBox.setVgrow(centerArea, Priority.ALWAYS);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("PathFinder");

        stage.setOnCloseRequest(event -> {
            if (!confirmDiscardChanges()) {
                event.consume();
            }
        });

        stage.show();
    }



    public void addPlace(String name, double x, double y) {
        Place place = new Place(name, x, y);
        place.setRadius(10);
        place.setFill(Color.BLUE);
        graph.add(place);

        place.setOnMouseClicked(e -> {
            e.consume();
            toggleSelection(place);
        });

        mapPane.getChildren().add(place);
        setUnsavedChanges(true);

    }

    private void toggleSelection(Place place) {
        if (selectedPlaces.contains(place)) {
            place.setFill(Color.BLUE);
            selectedPlaces.remove(place);
        } else if (selectedPlaces.size() < 2) {
            place.setFill(Color.RED);
            selectedPlaces.add(place);
        }
    }
    
    
    public boolean confirmDiscardChanges() {
        if (!unsavedChanges) return true;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved changes.");
        alert.setContentText("Do you want to discard them and continue?");
        ButtonType ok = new ButtonType("OK");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(ok, cancel);

        return alert.showAndWait().filter(result -> result == ok).isPresent();
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Pane getMapPane() {
        return mapPane;
    }

    public ButtonHandler getButtonHandler() {
        return buttonHandler;
    }

    public ImageView getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(ImageView backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setUnsavedChanges(boolean value) {
        this.unsavedChanges = value;
    }

    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }

    public Graph<Place> getGraph() {
        return graph;
    }


    public List<Place> getSelectedPlaces() {
        return selectedPlaces;
    }

    public void setAddingNewPlace(boolean value) {
        this.addingNewPlace = value;
    }

    public boolean hasAddingNewPlace() {
        return addingNewPlace;
    }

    public static void main(String[] args) {
        launch(args);
    }
}




