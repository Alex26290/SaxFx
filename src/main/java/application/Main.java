package main.java.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    //Запуск приложения JavaFX в методе start(). Он используется в JavaFX вместо метода main().
    //Так же в этой папке лежит файл ресурса(sample.fxml). Его можно открывать и редактировать через SceneBuilder.
    //
    @Override
    public void start(Stage primaryStage) throws Exception{
        //Cтандартный код запуска приложения
        //Дальше обработка событий(нажатий кнопок) происходит в классе Controller(пакет controllers)
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Приложение для парсинга файлов .plx");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
