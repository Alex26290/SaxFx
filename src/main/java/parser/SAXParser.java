package main.java.parser;

import main.java.outputData.TXTRow;
import main.java.data.addition.Course;
import main.java.data.addition.Semester;
import main.java.data.parsed.PlanNewHours;
import main.java.data.parsed.PlanString;
import main.java.data.parsed.ReferenceBookTypesOfWork;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class SAXParser extends DefaultHandler {

    private Map<Integer, PlanString> planStringMap = new HashMap<>();
    private Map<Integer, String> codeDisciplineMap = new HashMap<>();
    private Map<String, String> typesOfWorkMap = new HashMap<>();
    private Map<String, TXTRow> txtRowMap = new HashMap<>();
    private List<PlanString> planStringList = new ArrayList<>();
    private List<PlanNewHours> planList = new ArrayList<>();
    private List<TXTRow> txtRowList = new ArrayList<>();

    //Старт программы
    //Весь основной код находится в данном классе, все остальные классы - просто для хранения данных.
    //У них есть только поля и геттеры-сеттеры для установки-получения этих полей.
    //Стандартный способ инициализаии и запуска Sax парсера
//    public static void main(String[] args) throws Exception {
//        try {
//            // инициализация объекта handler, класса SAXParser, в котором мы находимся
//            DefaultHandler handler = new SAXParser();
//            //Получение фабрики
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//
//            factory.setValidating(false);
//            //Получение парсера из фабрики
//            SAXParser parser = factory.newSAXParser();
//            //Старт парсинга, в параметрах передаём handler
//            //После этого запускается метод startDocument()
//            // -> дальше для каждого тега вгутри xml-файла (в нашем случае .plx) запукается метод startElement()
//            // -> дальше также для каждого тега запускается метод endElement(),
//            // но он в данном случае не используется(можно перенести в него сохранение объектов в списки, но необязательно)
//            // после того, как всё распарсили, запускается метод endDocument()
//            // В endDocument() находится основная логика сборки записей в одну строку по предметам
//            // -> (то есть обрабатываем информацию, которую до этого распарсили)
//            parser.parse(new File("C:\\Users\\DNS\\Desktop\\parser\\1.plx"), handler);
//        } catch (SAXException ex) {
//            ex.printStackTrace();
//        }
//    }

    @Override
    public void startDocument() throws SAXException {
        System.out.println("Start parse XML...");
        //Это выходной файл
        File file = new File("output.txt");
        //Проверяем, существует ли он. Если да, то очищаем его от предыдущих записей
        if (file.exists()) {
            PrintWriter pWriter = null;
            try {
                pWriter = new PrintWriter(file);
                //Для очистки пишем в него пустую строку
                pWriter.write("");
                pWriter.flush();
                pWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {

        switch (qName) {
            //Парсим таблицу ПланыСтроки, для того, чтобы хранить эти данные - создан класс PlanString.
            // На каждую строку создаём объект PlanString и добавляем его в список(List) planStringList
            //Хеш-мапу используем для хранения пар значений, в данном случае кода из таблицы ПланыНовыеЧасы и названия предмета
            case ("ПланыСтроки"):
                PlanString p = new PlanString();
                //Получение аттрибута "Дисциплина" и установка значения объекту
                p.setDiscipline(attributes.getValue("Дисциплина"));
                //Получение аттрибута "Код" и установка значения объекту.
                // Метод replace() используется для того, чтобы убрать - перед значением
                int code = Integer.parseInt(attributes.getValue("Код").replace("-", ""));
                p.setCode(code);
                planStringList.add(p);
                codeDisciplineMap.put(code, attributes.getValue("Дисциплина"));
                break;
            //Парсим таблицу ПланыНовыеЧасы, для того, чтобы хранить эти данные - создан класс PlanNewHours.
            //Делаем то же самое, что и для ПланыСтроки, получаем значения и сетаем в поля объектов,
            //дальше объекты добавляем в список для хранения этих данных
            case ("ПланыНовыеЧасы"):
                PlanNewHours pl = new PlanNewHours();
                String objectCode = attributes.getValue("КодОбъекта").replace("-", "");
                String typeOfWorkCode = attributes.getValue("КодВидаРаботы");
                String curs = attributes.getValue("Курс");
                String semester = attributes.getValue("Семестр");
                String session = attributes.getValue("Сессия");
                String countHours = attributes.getValue("Количество");
                pl.setObjectCode(objectCode);
                pl.setTypeOfWorkCode(typeOfWorkCode);
                pl.setCourse(curs);
                pl.setSemester(semester);
                pl.setSession(session);
                pl.setHoursCount(countHours);
                planList.add(pl);
                break;
                //то же самое, здесь используем хеш-мап typesOfWorkMap, для того, чтобы хранить значения кода из СправочникВидыРабот и Название Вида Работы
            case ("СправочникВидыРабот"):
                ReferenceBookTypesOfWork rb = new ReferenceBookTypesOfWork();
                String typeCode = attributes.getValue("Код");
                String title = attributes.getValue("Название");
                typesOfWorkMap.put(typeCode, title);
                rb.setCode(typeCode);
                rb.setTitle(title);
                break;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        //Создаём итератор по нашему списку planList, который содержит объекты PlanNewHours
        Iterator<PlanNewHours> iterator = planList.iterator();
        //пока есть следующий элемент, выполняем код, находящийся внутри цикла while
        while (iterator.hasNext()) {
            PlanNewHours pl = iterator.next();
            String typeOfWorkCode = pl.getTypeOfWorkCode();
            String objCodeNext = pl.getObjectCode();
            String count = pl.getHoursCount();
            //Класс TXTRow - это наши выводимые в файл txt строки.
            //То есть каждая строка в txt с предметом и часами - объект классаTXTRow
            //Используем хеш-мапу для того, чтобы каждый предмет был в одну строку записан, а не в 20...
            // То есть это: txtRowMap.get(objCodeNext) == null
            // -> это проверка, что такой записи(относящейся к этому предмету, ещё нет)
            //
            if (txtRowMap.get(objCodeNext) == null) {
                //Если этого предмета ещё не было, то выполняем этот код
                TXTRow txtRow = new TXTRow();
                //Получаем название предмета, из мапы, куда до этого его сохранили
                String subj = codeDisciplineMap.get(Integer.parseInt(objCodeNext));
                //устанавливаем название предмета в объект txtRow
                txtRow.setSubject(subj);
                //В методе fillCourseData заполняем данные по курсам
                //Для того, чтобы удобнее хранить данные, создал два вспомогательных класса Course и Semester
                //Объект TXTRow имеет мапу с курсами(от 1 до 4), чтобы проверять, данные для каких курсов для данных предметов уже заполнены
                //Каждый курс имеет хеш-мапу с семестрами(1-2).
                // Всё это нужно, потому что иначе сложно хранить данные о часах, потому что на каждом курсе и в каждом семестре они разные
                fillCourseData(txtRow, pl, typeOfWorkCode, count);
                //Здесь заполняем данные о часах для каждого семестра
                fillSemesterData(txtRow, pl, typeOfWorkCode, count);
                txtRowMap.put(objCodeNext, txtRow);
                txtRowList.add(txtRow);
            } else {
                //Если был, то получаем этот предмет из хеш-мапы и работаем с ним, а не создаём новый
                TXTRow txtRow = txtRowMap.get(objCodeNext);
                //Методы  fillCourseData и  fillSemesterData выполняется в обоих блоках условия if-else
                //потому что эта операция выполняетя для каждого курса и для каждого семестра
                fillCourseData(txtRow, pl, typeOfWorkCode, count);
                fillSemesterData(txtRow, pl, typeOfWorkCode, count);
            }
            String subj = codeDisciplineMap.get(Integer.parseInt(objCodeNext));
        }
        //Здесь, после того, как сохранили и распределили все распарсенные данные,
        //начинаем формировать наши записи для txt
        createFullTxtDataForTxtRows(txtRowList);
        System.out.println(codeDisciplineMap);
        try {
            //Записываем данные в txt файл
            //Этот блок заключён в блок trycatch? потому что запись в файл может выбрасывать два типа проверяемых исключений -
            // IOException(файл не найден) или InterruptedException(ошибка во время записи в файл - например его удалили)
            writeToFile(txtRowList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createFullTxtDataForTxtRows(List<TXTRow> txtRowList) {
        //Перебираем TXTRow - это наши предметы
        for (TXTRow txtRow : txtRowList) {
            String full = "";
            Map<String, Course> coursesMap = txtRow.getCourses();
            //Для каждого находим курсы
            for (Map.Entry<String, Course> coursesEntry : coursesMap.entrySet()) {
                Course course = coursesEntry.getValue();
                String courseNumber = coursesEntry.getKey();
                Map<String, Semester> map = course.getSemesters();
                //Добавляем в выходную строку Курс
                full = full + " Курс - " + courseNumber;
                int defaultCount = 0;
                //Для каждого курса  находим семестры
                for (Map.Entry<String, Semester> entry : map.entrySet()) {
                    String semesterNumber = entry.getKey();
                    //Добавляем в выходную строку Семестр курса
                    full = full + " Семестр - " + semesterNumber;
                    Semester semester = entry.getValue();
                    //Получаем данные о количестве лаб в семестре
                    String labs = semester.getLabs();
                    if (labs != null) {
                        if (!labs.equalsIgnoreCase(""))
                            //Добавляем в выходную строку данные о  количестве лаб
                            full = full + " Лабораторные работы - " + labs;
                    } else {
                        //Если не было данных - то добавляем 0
                        full = full + " Лабораторные работы - " + defaultCount;
                    }
                    //Получаем данные о количестве лекций в семестре
                    String lectures = semester.getLectures();
                    if (lectures != null) {
                        if (!lectures.equalsIgnoreCase(""))
                            //Добавляем в выходную строку данные о  количестве лекций
                            full = full + " Лекционные занятия - " + lectures;
                    } else {
                        full = full + " Лекционные занятия - " + defaultCount;
                    }
                    //Получаем данные о количестве практик в семестре
                    String practice = semester.getPractice();
                    if (practice != null) {
                        if (!practice.equalsIgnoreCase(""))
                            //Добавляем в выходную строку данные о  количестве практик
                            full = full + " Практические занятия - " + practice;
                    } else {
                        full = full + " Практические занятия - " + defaultCount;
                    }
                    //Получаем данные о виде срс
                    String crc = semester.getCrc();
                    if (crc != null) {
                        if (!crc.equalsIgnoreCase(""))
                            //Добавляем в выходную строку данные о типе срс
                            full = full + " СРС - " + crc;
                    }
                }
                //Добавляем в поле titleFull полную строку с данными по предмету
                txtRow.setTitleFull(full);
            }
        }
    }

    private void fillCourseData(TXTRow txtRow, PlanNewHours pl, String typeOfWorkCode, String count) {
        Map<String, Course> map = txtRow.getCourses();
        Course course;
        String courseNumber = pl.getCourse();
        //также просто проверка, есть ли курс в мапе, если нет - добавляем, передавая ему значение из нашего объекта PlanNewHours pl
        if (map.get(courseNumber) == null) {
            course = new Course();
            course.setCourseNumber(courseNumber);
            //Добавляем курс в мапу
            map.put(courseNumber, course);
        }
    }

    private void fillSemesterData(TXTRow txtRow, PlanNewHours pl, String typeOfWorkCode, String count) {
        Map<String, Course> map = txtRow.getCourses();
        //Сначала получаем у нашего txtRow курсы, для каждого курса получаем мапу с семестрами
        for (Map.Entry<String, Course> entry : map.entrySet()) {
            Course course = entry.getValue();
            //Для каждого курса получаем мапу с семестрами
            Map<String, Semester> semestersMap = course.getSemesters();
            Semester semester;
            //Получаем текущий семестр из объекта PlanNewHours, куда эти данные сохранились при парсинге
            String semesterNumber = pl.getSemester();
            //то же самое. Если нет такого семестра ещё, то создаём
            if (semestersMap.get(semesterNumber) == null) {
                semester = new Semester();
                semestersMap.put(semesterNumber, semester);
            } else {
                //Если уже был, то берём из мапы семестров
                semester = semestersMap.get(semesterNumber);
            }
            //Получаем значение типа работы ("Лекционные занятия, Практические занятия и т.д.)
            //Устанавливаем эти значения в данные семестров(У семестра для данного предмета есть данные о часах лекций, лаб, практик и т.д.)
            String workType = typesOfWorkMap.get(typeOfWorkCode);
            System.out.println("typeOfWorkCode = " + typeOfWorkCode + " " + workType);
            switch (workType) {
                case ("Лекционные занятия"):
                    semester.setLectures(count);
                    break;
                case ("Практические занятия"):
                    semester.setPractice(count);
                    break;
                case ("Лабораторные занятия"):
                    semester.setLabs(count);
                    break;
                case ("Экзамен"):
                    semester.setCrc("Экзамен");
                    break;
                case ("Зачет"):
                    semester.setCrc("Зачет");
                    break;
                case ("Зачет с оценкой"):
                    semester.setCrc("Зачет с оценкой");
                    break;
            }
        }
    }

    public void writeToFile(List<TXTRow> txtRowList) throws IOException, InterruptedException {
        //Устанавливаем имя выходного файла
        String filename = "output.txt";
        File file = new File(filename);
        //Создаём писателя, который будет записывать данные в файл
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
        //Бегаем итератором по нашему списку txtRows, наших выходных записей
        Iterator<TXTRow> iterator = txtRowList.iterator();
        //Пока у итерато есть следующий элемент
        while (iterator.hasNext()) {
            //берём его
            TXTRow txtRow = iterator.next();
            if (!(txtRow.getSubject().startsWith("Преддипломная практика")
                    || txtRow.getSubject().startsWith("Дисциплины по выбору")
                    || txtRow.getSubject().startsWith("Научно-исследовательская работа")
                    || txtRow.getSubject().startsWith("Технологическая (проектно-технологическая) практика"))) {
                //и пишем в файл
                //txtRow.getSubject() - это название предмета, а txtRow.getTitleFull() - это наши данные о часах
                //System.lineSeparator() - это переход на новую строку
                writer.write(txtRow.getSubject() + txtRow.getTitleFull() + System.lineSeparator());
            }
        }
        //Открываем выходной файл по оканчании парсинга
        Desktop desktop = Desktop.getDesktop();
        desktop.open(file);
        //Закрываем writer. Так нужно, иначе будет ошибка
        writer.close();
    }
}
