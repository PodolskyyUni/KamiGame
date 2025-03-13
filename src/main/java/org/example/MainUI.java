package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.databaseShenanigans.Leaderboard;
import org.example.databaseShenanigans.PlayerController;
import org.example.databaseShenanigans.PlayerRepository;
import org.example.game.GameEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class MainUI extends Application {
    private static Stage primaryStage;
    private PlayerController playerController;
    private Leaderboard leaderboard;
    private VBox leaderboardBox;
    private Label messageLabel = new Label();
    private Label instructionLabel = new Label();
    private VBox inputBox;
    private TextField nameField;
    private PasswordField passwordField;
    private static ApplicationContext context;
    private Button startButton;
    private Button loginButton;
    private Button registerButton;
    private Button leaderboardButton;
    private Button confirmButton;
    private boolean isLoginMode = true;

    @Autowired
    private PlayerRepository playerRepository;

    public void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Kami Game");

        context = Main.getContext();
        if (context == null) {
            throw new IllegalStateException("Spring context is null! JavaFX cannot proceed.");
        }
        System.out.println("✅ JavaFX Successfully Started with Spring Context: " + context);

        playerController = context.getBean(PlayerController.class);
        leaderboard = context.getBean(Leaderboard.class);

        StackPane mainLayout = new StackPane();
        mainLayout.setStyle("-fx-background-color: #E6955C;"); // Soft orange background
        Scene mainScene = new Scene(mainLayout, 500, 500);

        VBox contentBox = createMainMenu();
        Button exitButton = createExitButton();

        mainLayout.getChildren().addAll(contentBox, exitButton);
        configureExitButton(exitButton);

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private VBox createMainMenu() {
        startButton = createStyledButton("Start", Color.DARKBLUE, Color.WHITE);
        startButton.setFont(Font.font("Okami", 30));
        startButton.setOnAction(e -> showUIElements());

        loginButton = createStyledButton("Login", Color.DARKBLUE, Color.WHITE);
        registerButton = createStyledButton("Register", Color.DARKBLUE, Color.WHITE);
        leaderboardButton = createStyledButton("Show Leaderboard", Color.DARKBLUE, Color.WHITE);

        loginButton.setOnAction(e -> showLogin());
        registerButton.setOnAction(e -> showRegister());
        leaderboardButton.setOnAction(e -> showLeaderboard());

        loginButton.setVisible(false);
        registerButton.setVisible(false);
        leaderboardButton.setVisible(false);

        inputBox = createInputBox();
        inputBox.setVisible(false);

        leaderboardBox = new VBox(10);
        leaderboardBox.setAlignment(Pos.CENTER);
        leaderboardBox.setPadding(new Insets(20));
        leaderboardBox.setStyle("-fx-background-color: white;");
        leaderboardBox.setVisible(false);

        HBox buttonContainer = new HBox(10, loginButton, registerButton);
        buttonContainer.setAlignment(Pos.CENTER);

        VBox menuBox = new VBox(15, startButton, buttonContainer, inputBox, leaderboardButton, leaderboardBox, messageLabel);
        menuBox.setAlignment(Pos.CENTER);
        return menuBox;
    }

    private void showLeaderboard() {
        leaderboardBox.getChildren().clear();
        Label leaderboardTitle = new Label("🏆 Leaderboard 🏆");
        leaderboardTitle.setFont(Font.font("Okami", 30));
        leaderboardTitle.setTextFill(Color.DARKBLUE);

        VBox playerList = new VBox(5);
        playerList.setAlignment(Pos.CENTER);

        leaderboard.getTopPlayers().forEach(player -> {
            Label playerLabel = new Label(player.getName() + " - " + player.getMaxScore() + " pts");
            playerLabel.setFont(Font.font("Okami", 20));
            playerLabel.setTextFill(Color.BLACK);
            playerList.getChildren().add(playerLabel);
        });

        Button backButton = new Button("Back");
        backButton.setFont(Font.font("Okami", 18));
        backButton.setOnAction(e -> leaderboardBox.setVisible(false));

        leaderboardBox.getChildren().addAll(leaderboardTitle, playerList, backButton);
        leaderboardBox.setVisible(true);
    }


    private VBox createInputBox() {
        instructionLabel.setFont(Font.font("Okami", 16));
        instructionLabel.setTextFill(Color.WHITE);

        nameField = new TextField();
        nameField.setPromptText("Enter Name");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        confirmButton = createStyledButton("Confirm", Color.DARKBLUE, Color.WHITE);
        confirmButton.setOnAction(e -> handleAction());

        VBox inputBox = new VBox(10, instructionLabel, nameField, passwordField, confirmButton);
        inputBox.setAlignment(Pos.CENTER);
        return inputBox;
    }

    private void showUIElements() {
        startButton.setVisible(false); // Hide start button
        loginButton.setVisible(true);
        registerButton.setVisible(true);
        leaderboardButton.setVisible(true);
    }

    private void showLogin() {
        instructionLabel.setText("Please enter your username and password to login");
        inputBox.setVisible(true);
        isLoginMode = true;
    }

    private void showRegister() {
        instructionLabel.setText("Please create yourself username and password");
        inputBox.setVisible(true);
        isLoginMode = false;
    }

    private void handleAction() {
        if (isLoginMode) {
            handleLogin(nameField.getText(), passwordField.getText());
        } else {
            handleRegister(nameField.getText(), passwordField.getText());
        }
    }

    private void handleLogin(String username, String password) {
        if (playerController == null) {
            messageLabel.setText("System Error: Backend Not Connected");
            messageLabel.setTextFill(Color.RED);
            return;
        }
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Error: Fields cannot be empty");
            messageLabel.setTextFill(Color.RED);
            return;
        }
        String response = playerController.login(username, password);
        messageLabel.setText(response);
        messageLabel.setTextFill(response.startsWith("Error") ? Color.RED : Color.GREEN);

        if (!response.startsWith("Error")) {
            GameEngine gameEngine = new GameEngine();
            new KamiGameUI(primaryStage, username, playerController, gameEngine, this);
        }
    }

    public void handleRegister(String name, String password) {
        if (name.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Error: Name and Password cannot be empty!");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            String result = playerController.registerPlayer(name, password);
            messageLabel.setTextFill(result.contains("already exists") ? Color.RED : Color.GREEN);
            messageLabel.setText(result);
        } catch (Exception e) {
            messageLabel.setText("Error: Player with this name already exists!");
            messageLabel.setTextFill(Color.RED);
        }
    }

    private Button createStyledButton(String text, Color bgColor, Color textColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Okami", 20));
        button.setTextFill(textColor);
        button.setBackground(new Background(new BackgroundFill(bgColor, new CornerRadii(5), Insets.EMPTY)));
        return button;
    }

    private Button createExitButton() {
        Button exitButton = createStyledButton("Exit", Color.RED, Color.WHITE);
        exitButton.setOnAction(e -> primaryStage.close());
        return exitButton;
    }

    private void configureExitButton(Button exitButton) {
        StackPane.setAlignment(exitButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(exitButton, new Insets(0, 20, 20, 0));
    }
}