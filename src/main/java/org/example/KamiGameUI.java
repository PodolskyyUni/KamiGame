package org.example;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.databaseShenanigans.PlayerController;
import org.example.game.GameEngine;
import org.example.game.Sector;

public class KamiGameUI {
    private final Stage stage;
    private final String playerName;
    private final PlayerController playerController;
    private GameEngine game;
    private final Label scoreLabel;
    private final MainUI mainMenuUI;
    private Sector selectedSector = null;
    private Group gameGroup;
    private final Label errorLabel = new Label();
    private HBox colorSelectionBox;
    private Rectangle selectedSectorRect = null;
    private double spacing;

    public KamiGameUI(Stage stage, String playerName, PlayerController playerController, GameEngine game, MainUI mainMenuUI) {
        this.stage = stage;
        this.playerName = playerName;
        this.playerController = playerController;
        this.game = game;
        this.mainMenuUI = mainMenuUI;

        scoreLabel = new Label("Score: 0");
        colorSelectionBox = createColorSelectionBox();
        colorSelectionBox.setVisible(false);

        setupUI();
    }

    private void setupUI() {
        Label nameLabel = new Label("Player: " + playerName);

        gameGroup = new Group();
        updateGrid();

        Button exitButton = new Button("Exit to Main Menu");
        exitButton.setOnAction(e -> exitToMainMenu());

        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox topContainer = new VBox(nameLabel, scoreLabel, errorLabel);
        topContainer.setAlignment(Pos.TOP_CENTER);
        topContainer.setSpacing(10);

        HBox bottomButtons = new HBox(10, exitButton);
        bottomButtons.setAlignment(Pos.CENTER);

        VBox bottomContainer = new VBox(bottomButtons, colorSelectionBox);
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.setSpacing(10);

        BorderPane root = new BorderPane();
        root.setTop(topContainer);
        root.setCenter(gameGroup);
        root.setBottom(bottomContainer);
        BorderPane.setAlignment(bottomContainer, Pos.BOTTOM_CENTER);

        Scene gameScene = new Scene(root);
        setupKeyListener(gameScene);

        stage.setFullScreen(true);
        stage.setScene(gameScene);
        stage.show();

        // ✅ Ensure the grid resizes dynamically when window size changes
        stage.widthProperty().addListener((obs, oldVal, newVal) -> updateGrid());
        stage.heightProperty().addListener((obs, oldVal, newVal) -> updateGrid());
    }

    private void updateGrid() {
        gameGroup.getChildren().clear();
        int length = game.grid.getLength();
        int height = game.grid.getHeight();

        // ✅ Limit grid size to avoid taking over the entire screen
        double maxGridSize = Math.min(stage.getWidth(), stage.getHeight()) * 0.8; // Max 80% of screen
        spacing = maxGridSize / (Math.max(length, height) + 2);
        double sectorSize = spacing * 0.9;

        for (int x = 0; x < length; x++) {
            for (int y = 0; y < height; y++) {
                Sector sector = game.grid.getBoardSector(x, y);
                final int finalX = x;
                final int finalY = y;

                Rectangle sectorRect = new Rectangle(sectorSize, sectorSize);
                sectorRect.setFill(getColor(sector.getColor().toString()));
                sectorRect.setLayoutX((x + 1) * spacing);
                sectorRect.setLayoutY((y + 1) * spacing);
                sectorRect.setOnMouseClicked(e -> handleSectorClick(finalX, finalY));

                gameGroup.getChildren().add(sectorRect);
            }
        }
    }

    private void handleSectorClick(int x, int y) {
        Sector newSelectedSector = game.grid.getBoardSector(x, y);
        if (newSelectedSector == null) return;

        // If clicking the same sector again, cancel selection
        if (selectedSector == newSelectedSector) {
            clearSelection();
            return;
        }

        // Remove previous highlight
        if (selectedSectorRect != null) {
            selectedSectorRect.setStroke(null);
        }

        // Set new selected sector
        selectedSector = newSelectedSector;
        selectedSectorRect = getSectorRectangle(x, y);
        if (selectedSectorRect != null) {
            selectedSectorRect.setStroke(Color.PURPLE);
            selectedSectorRect.setStrokeWidth(4);
        }

        colorSelectionBox.setVisible(true);
    }

    private void applyColorChange(String colorName) {
        if (selectedSector != null) {
            org.example.game.Color newColor = org.example.game.Color.valueOf(colorName);

            // Prevent changing to the same color
            if (selectedSector.getColor() == newColor) {
                errorLabel.setText("❌ Cannot choose the same color!");
                return;
            }

            game.changeColor(newColor, selectedSector);
            scoreLabel.setText("Score: " + game.getPoints());
            clearSelection();
            updateGrid();

            // Check if round is over
            if (game.roundOver()) {
                startNewRound();
            }
        }
    }

    private void setupKeyListener(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("X")) {
                clearSelection();
            }
        });
    }

    private void clearSelection() {
        if (selectedSectorRect != null) {
            selectedSectorRect.setStroke(null); // Remove border
        }
        selectedSector = null;
        selectedSectorRect = null;
        colorSelectionBox.setVisible(false);
        errorLabel.setText(""); // Reset error message
    }

    private void startNewRound() {
        errorLabel.setText("✅ Round Completed! New round starting...");
        game = new GameEngine(); // Reset the game state
        updateGrid();
    }

    private void exitToMainMenu() {
        playerController.updateMaxScore(playerName, game.getPoints());
        mainMenuUI.start(stage);
    }

    private Color getColor(String color) {
        return switch (color.toUpperCase()) {
            case "RED" -> Color.RED;
            case "BLUE" -> Color.BLUE;
            case "GREEN" -> Color.GREEN;
            case "YELLOW" -> Color.YELLOW;
            case "BLACK" -> Color.BLACK;
            case "ORANGE" -> Color.ORANGE;
            case "GRAY" -> Color.GRAY;
            case "PINK" -> Color.PINK;
            default -> Color.WHITE;
        };
    }

    private HBox createColorSelectionBox() {
        HBox colorBox = new HBox(10);
        colorBox.setAlignment(Pos.CENTER);

        for (String colorName : new String[]{"RED", "BLUE", "GREEN", "YELLOW", "BLACK", "ORANGE", "GRAY", "PINK"}) {
            Button colorButton = new Button();
            colorButton.setMinSize(40, 40);
            colorButton.setStyle("-fx-background-color: " + colorName.toLowerCase() + ";");
            colorButton.setOnAction(e -> applyColorChange(colorName));
            colorBox.getChildren().add(colorButton);
        }
        return colorBox;
    }

    private Rectangle getSectorRectangle(int x, int y) {
        for (javafx.scene.Node node : gameGroup.getChildren()) {
            if (node instanceof Rectangle rect) {
                if (rect.getLayoutX() == (x + 1) * spacing && rect.getLayoutY() == (y + 1) * spacing) {
                    return rect;
                }
            }
        }
        return null;
    }
}
