package irie;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static Stage main_stage, home_stage;
    protected static Scene splash_scene, home_scene;
    protected static StackPane splash_layout;
    protected static BorderPane home_layout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        main_stage = primaryStage;
        displaySplashScreen();
    }

    public static void displaySplashScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/SplashScreen.fxml"));
        splash_layout = loader.load();
        splash_scene = new Scene(splash_layout);
        main_stage.setScene(splash_scene);
        main_stage.initStyle(StageStyle.TRANSPARENT);
        main_stage.show();
    }

    public static void displayHomePage() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/HomeView.fxml"));
        home_layout = loader.load();
        home_scene = new Scene(home_layout);
        home_stage = new Stage();
        home_stage.setScene(home_scene);
        home_stage.setMaximized(true);
        home_stage.setTitle("Abscondo");
        home_stage.getIcons().add(new Image("irie/views/assets/icon.png"));
        home_stage.show();
        main_stage.close();
    }

    public static void displaySignOut() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("views/SplashScreen.fxml"));
        splash_layout = loader.load();
        splash_scene = new Scene(splash_layout);
        Stage signout_stage = new Stage();
        signout_stage.setScene(splash_scene);
        signout_stage.initStyle(StageStyle.TRANSPARENT);
        signout_stage.show();
    }
}
