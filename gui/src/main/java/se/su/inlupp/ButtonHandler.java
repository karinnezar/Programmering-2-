
// Karin  Mustafa 

package se.su.inlupp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.*;

public class ButtonHandler {

    private final Gui gui;

    private Button findPathButton;
    private Button showConnectionButton;
    private Button newPlaceButton;
    private Button newConnectionButton;
    private Button changeConnectionButton;

    public ButtonHandler(Gui gui) {
        this.gui = gui;
        initButtons();
    }

    private void initButtons() {
        findPathButton = new Button("Find Path");
        showConnectionButton = new Button("Show Connection");
        newPlaceButton = new Button("New Place");
        newConnectionButton = new Button("New Connection");
        changeConnectionButton = new Button("Change Connection");

        findPathButton.setOnAction(e -> handleFindPath());
        showConnectionButton.setOnAction(e -> handleShowConnection());
        newPlaceButton.setOnAction(e -> activateNewPlaceMode());
        newConnectionButton.setOnAction(e -> createConnection());
        changeConnectionButton.setOnAction(e -> handleChangeConnection());
    }

    public HBox createButtonBox() {
        HBox buttonBox = new HBox(10, findPathButton, showConnectionButton, newPlaceButton,
                newConnectionButton, changeConnectionButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        return buttonBox;
    }

    public void setButtonsDisabled(boolean disabled) {
        findPathButton.setDisable(disabled);
        showConnectionButton.setDisable(disabled);
        newPlaceButton.setDisable(disabled);
        newConnectionButton.setDisable(disabled);
        changeConnectionButton.setDisable(disabled);
    }

    private void activateNewPlaceMode() {
        gui.setAddingNewPlace(true);
        newPlaceButton.setDisable(true);
        gui.getMapPane().setCursor(javafx.scene.Cursor.CROSSHAIR);
    }

    private void createConnection() {
        if (gui.getSelectedPlaces().size() != 2) {
            gui.showAlert("Error", "Two places must be selected!");
            return;
        }

        Place from = gui.getSelectedPlaces().get(0);
        Place to = gui.getSelectedPlaces().get(1);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Connection");
        dialog.setHeaderText("Connection from " + from.getName() + " to " + to.getName());

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label timeLabel = new Label("Time:");
        TextField timeField = new TextField();

        VBox vbox = new VBox(10, nameLabel, nameField, timeLabel, timeField);
        dialog.getDialogPane().setContent(vbox);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okButtonType) {
            String name = nameField.getText().trim();
            String time = timeField.getText().trim();

            if (name.isEmpty() || !time.matches("\\d+")) {
                gui.showAlert("Error", "Name cannot be empty and time must be digits.");
                return;
            }

            try {
                gui.getGraph().connect(from, to, name, Integer.parseInt(time));
                Line connection = new Line(from.getCenterX(), from.getCenterY(), to.getCenterX(), to.getCenterY());
                connection.setStrokeWidth(2);
                connection.setStroke(Color.BLACK);
                gui.getMapPane().getChildren().add(connection);

                for (Place p : gui.getSelectedPlaces()) p.setFill(Color.BLUE);
                gui.getSelectedPlaces().clear();
                gui.setUnsavedChanges(true);
            } catch (Exception e) {
                gui.showAlert("Error", e.getMessage());
            }
        }
    }

    private void handleFindPath() {
        if (gui.getSelectedPlaces().size() != 2) {
            gui.showAlert("Error", "You must select exactly two places.");
            return;
        }
    
        Place from = gui.getSelectedPlaces().get(0);
        Place to = gui.getSelectedPlaces().get(1);
    
        List<Edge<Place>> path = gui.getGraph().getPath(from, to);
        if (path == null || path.isEmpty()) {
            gui.showAlert("Error", "No path exists between the selected places.");
            return;
        }
    
        StringBuilder sb = new StringBuilder();
        int totalTime = 0;
    
        for (Edge<Place> edge : path) {
            sb.append("to ").append(edge.getDestination().getName())
              .append(" by ").append(edge.getName())
              .append(" takes ").append(edge.getWeight()).append("\n");
            totalTime += edge.getWeight();
        }
    
        sb.append("Total ").append(totalTime);
    
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Path");
        info.setHeaderText("The Path from " + from.getName() + " to " + to.getName() + ":");
        info.setContentText(sb.toString());
        info.showAndWait();
    }
    

    private void handleShowConnection() {
        if (gui.getSelectedPlaces().size() != 2) {
            gui.showAlert("Error", "You must select exactly two places.");
            return;
        }

        Place p1 = gui.getSelectedPlaces().get(0);
        Place p2 = gui.getSelectedPlaces().get(1);

        Edge<Place> edge = gui.getGraph().getEdgeBetween(p1, p2);
        if (edge == null) {
            gui.showAlert("Error", "No connection exists between the selected places.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Connection Info");
        dialog.setHeaderText("Connection between " + p1.getName() + " and " + p2.getName());

        TextField nameField = new TextField(edge.getName());
        nameField.setEditable(false);

        TextField timeField = new TextField(String.valueOf(edge.getWeight()));
        timeField.setEditable(false);

        VBox content = new VBox(10, new Label("Name:"), nameField, new Label("Time:"), timeField);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    private void handleChangeConnection() {
        if (gui.getSelectedPlaces().size() != 2) {
            gui.showAlert("Error", "You must select exactly two places.");
            return;
        }

        Place p1 = gui.getSelectedPlaces().get(0);
        Place p2 = gui.getSelectedPlaces().get(1);

        Edge<Place> edge = gui.getGraph().getEdgeBetween(p1, p2);
        if (edge == null) {
            gui.showAlert("Error", "No connection exists between the selected places.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Connection");
        dialog.setHeaderText("Change connection between " + p1.getName() + " and " + p2.getName());

        TextField nameField = new TextField(edge.getName());
        nameField.setEditable(false);

        TextField timeField = new TextField(String.valueOf(edge.getWeight()));

        VBox content = new VBox(10, new Label("Name:"), nameField, new Label("New Time:"), timeField);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    int newTime = Integer.parseInt(timeField.getText());
                    if (newTime < 0) throw new NumberFormatException();
                    gui.getGraph().setConnectionWeight(p1, p2, newTime);
                    gui.setUnsavedChanges(true);
                } catch (NumberFormatException e) {
                    gui.showAlert("Error", "Invalid input. Must be a non-negative integer.");
                }
            }
        });
    }

    public void handleMapClick(MouseEvent event) {
        if (!gui.hasAddingNewPlace()) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Name");
        dialog.setHeaderText(null);
        dialog.setContentText("Name of place:");

        dialog.showAndWait().ifPresent(name -> {
            gui.addPlace(name, event.getX(), event.getY());
            gui.setAddingNewPlace(false);
            gui.getMapPane().setCursor(Cursor.DEFAULT);
            newPlaceButton.setDisable(false);
        });
    }

}

