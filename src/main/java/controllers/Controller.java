package main.java.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.parser.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

public class Controller {

    File fileForParsing;
//Есть две кнопки и label для вывода информации пользователю
    @FXML
    Button button1;

    @FXML
    Button button2;

    @FXML
    Label label;

    //обработка нажатия первой кнопки - выбора файла
    @FXML
    private void handleChooseAction(ActionEvent event) {
        //Создание экземпляра класса выбора файла
        FileChooser fc = new FileChooser();
//установка title для окошка выбора файла
        fc.setTitle("Выберите файл с расширением .plx для парсинга . . .");
//начальная директория выбора файла
        fc.setInitialDirectory(new File("C:\\"));
//Получение выбранного файла и запуск диалогового окна выбора файла
        File file = fc.showOpenDialog(new Stage());
        //вывод информации о выбранном файле в label
        if (file != null) {
            label.setText("Выбран файл: " + file.getAbsolutePath());
            //Установка файла в поле, чтобы можно было его потом использовать
            fileForParsing = file;
        }
    }
//Обработка нажатия кнопки парсинга файла. Если выбран файл с расширением .plx, то парсинг начнётся,
//если другой файл - не начнётся.Информация об этом выводится в label
//Дальше всё то же, что уже было.
    @FXML
    private void handleStartParsingAction(ActionEvent event) {
        if (fileForParsing != null) {
            try {
                // инициализация объекта handler, класса SAXParser, в котором мы находимся
                DefaultHandler handler = new SAXParser();
                //Получение фабрики
                SAXParserFactory factory = SAXParserFactory.newInstance();

                factory.setValidating(false);
                //Получение парсера из фабрики
                javax.xml.parsers.SAXParser parser = factory.newSAXParser();
                //Старт парсинга, в параметрах передаём handler
                //После этого запускается метод startDocument()
                // -> дальше для каждого тега вгутри xml-файла (в нашем случае .plx) запукается метод startElement()
                // -> дальше также для каждого тега запускается метод endElement(),
                // но он в данном случае не используется(можно перенести в него сохранение объектов в списки, но необязательно)
                // после того, как всё распарсили, запускается метод endDocument()
                // В endDocument() находится основная логика сборки записей в одну строку по предметам
                // -> (то есть обрабатываем информацию, которую до этого распарсили)
                String fileName = fileForParsing.getName();
                if (fileName.contains(".plx")) {
                    label.setText("Запущен процесс парсинга файла " + fileName);
                    parser.parse(new File(fileForParsing.getAbsolutePath()), handler);
                    label.setText("Процесс парсинга файла " + fileName + " завершён");
                } else{
                    label.setText("Не выбран файл с расширением .plx");
                }
            } catch (SAXException | ParserConfigurationException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
