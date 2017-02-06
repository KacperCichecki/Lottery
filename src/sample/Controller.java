package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.*;
import javafx.stage.Window;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;

    //figure out dimensions of screen
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int centerX = screenSize.width / 2;
    private int centerY = screenSize.height / 2;

    //list including numbers of drawed person, first person of listOfAddedPeople
    // has first person(nr of person) of person in this listOfResults
    private ArrayList<Integer> listOfResults;

    //sound of Santa
    private String pathWayToSantasVoice = Controller.class.getResource("/sample/pic/Merry.mp3").toString();
    private final AudioClip SANTA_VOICE = new AudioClip(pathWayToSantasVoice);

    //need that list to build a table
    public ObservableList<Person> listOfAddedPeople;

    boolean drawButtonWasClicked = false;
    public static boolean programStarted;

    //main stage of the program
    private Stage stage = null;

    //timer for counting elapsed time
    static Timer timer = null;

    @FXML
    private VBox vb;

    @FXML
    private TextArea infoLabel;

    @FXML
    private TextField nameInput;

    @FXML
    private Button drawButton;

    @FXML
    private Button deleteButton;

    @FXML
    private MenuItem saveList;

    @FXML
    private ImageView santa;

    @FXML
    private TableView<Person> table;
    @FXML
    private TableColumn<Person, Integer> col1;
    @FXML
    private TableColumn<Person, String> col2;
    @FXML
    private TableColumn<Person, String> col3;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.setLevel(Level.FINEST);
        try {
            fileTxt = new FileHandler("Logging.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        LOGGER.addHandler(fileTxt);

        LOGGER.entering(getClass().getName(), "initialize");

        programStarted = true;
        startLabelInfo();

        listOfAddedPeople = FXCollections.observableArrayList();

        col1.setCellValueFactory(new PropertyValueFactory<Person, Integer>("id"));
        col2.setCellValueFactory(new PropertyValueFactory<Person, String>("name"));
        col3.setCellValueFactory(new PropertyValueFactory<Person, String>("nameDrawedPerson"));

        col2.setOnEditCommit(e -> firstNameCol_OnEditCommit(e));
        col2.setCellFactory(TextFieldTableCell.forTableColumn());
        col2.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        table.setItems(listOfAddedPeople);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setPlaceholder(new Label("Your Table is Empty\n\nEnter some name"));

        LOGGER.exiting(getClass().getName(), "initialize");
    }

    public void firstNameCol_OnEditCommit(TableColumn.CellEditEvent<Person, String> e) {
        LOGGER.entering(getClass().getName(), "firstNameCol_OnEditCommit");

        TableColumn.CellEditEvent<Person, String> cellEditEvent = e;
        Person person = cellEditEvent.getRowValue();
        person.setName(cellEditEvent.getNewValue());

        LOGGER.exiting(getClass().getName(), "firstNameCol_OnEditCommit");
    }


    public void addPerson() {
        LOGGER.entering(getClass().getName(), "addPerson");

        String input = nameInput.getText();

        boolean result = false;
        boolean contains = false;
        Pattern p = Pattern.compile("[-_?@&żźćśńąęŻŹĆŚŃĄĘa-zA-Z_0-9]+");
        Matcher m = p.matcher(input);

        if (m.matches()) {
            for (Person per : listOfAddedPeople) {
                contains = per.getName().equalsIgnoreCase(input);
                if (contains) break;
            }
            if (contains) {
                showErrorWindow("You can't add the same person more than once");
                nameInput.clear();
            }
            if (!contains) {
                result = listOfAddedPeople.add(new Person(input));
                Person.counter++;
            }
            if (result) {
                nameInput.clear();
                nameInput.requestFocus();
            }
        } else {
            showErrorWindow("You are allowed to add only letters and numbers");
            nameInput.clear();
        }

        if (listOfAddedPeople.size() > 0) {
            deleteButton.setDisable(false);
        }
        if (listOfAddedPeople.size() > 1) {
            drawButton.setDisable(false);
            saveList.setDisable(false);
            deleteButton.setDisable(false);
        }

        LOGGER.exiting(getClass().getName(), "addPerson");
    }

    //show Pop up window with custom text
    void showErrorWindow(String message) {
        LOGGER.entering(getClass().getName(), "showErrorWindow");

        Toolkit.getDefaultToolkit().beep();
        if (stage == null) stage = (Stage) nameInput.getScene().getWindow();

        final Popup popup = new Popup();
        popup.setX(centerX - 200);
        popup.setY(centerY - 200);

        Button hide = new Button("OK");
        hide.setOnAction((a) -> popup.hide());
        hide.setDefaultButton(true);

        Image wImage = new Image("sample/pic/warning1.png");
        ImageView err = new ImageView(wImage);
        err.resize(50, 50);

        Label label = new Label(message);
        label.setWrapText(true);

        VBox layout1 = new VBox();
        layout1.setAlignment(Pos.CENTER);
        layout1.setStyle("-fx-background-color: linear-gradient(#D0D0D0 , #FFCC99); " +
                "-fx-padding: 10; " +
                "-fx-border-color: rgb(49, 89, 23);\n" +
                "-fx-border-radius: 5;");

        layout1.getChildren().addAll(err, label, hide);

        popup.getContent().addAll(layout1);
        popup.show(stage);

        LOGGER.exiting(getClass().getName(), "showErrorWindow");
    }

    public void santaVoice(MouseEvent mouseEvent) {
        LOGGER.entering(getClass().getName(), "santaVoice");

        if (!SANTA_VOICE.isPlaying()) {
            SANTA_VOICE.play();
        }

        LOGGER.exiting(getClass().getName(), "santaVoice");
    }

    public void drawPeople(ActionEvent actionEvent) {
        LOGGER.entering(getClass().getName(), "drawPeople");

        ascribeNumbersToPeople();

        //give each person in listOfAddedPeople nr of drawed person
        int i = 0;
        for (Person p : listOfAddedPeople) {
            LOGGER.info(p.getId() + " " + p.getName() + " --> chose " + listOfResults.get(i));
            p.setIdDrawedPerson(listOfResults.get(i++));
        }

        for (Person p : listOfAddedPeople) {
            int IdOfDrawedPerson = p.getIdDrawedPerson();
            LOGGER.info("We're looking for person withe such id: " + IdOfDrawedPerson);
            p.setDrawedPerson(givePersonByNumber(IdOfDrawedPerson));
        }
        santa.setVisible(true);
        drawButtonWasClicked = true;

        LOGGER.exiting(getClass().getName(), "drawPeople");
    }

    private Person givePersonByNumber(int idOfPerson) {
        LOGGER.entering(getClass().getName(), "givePersonByNumber");

        Person person = null;
        for (Person p : listOfAddedPeople) {
            if (p.getId() == idOfPerson) person = p;
        }

        LOGGER.exiting(getClass().getName(), "givePersonByNumber");
        return person;
    }

    //ascribe number of drawed person for each person in table, first person of listOfAddedPeople (first in table)
    // has first person(nr of person) of person in setResultsLottery
    private void ascribeNumbersToPeople() {
        LOGGER.entering(getClass().getName(), "ascribeNumbersToPeople");

        Set<Integer> temporarySet = new HashSet<Integer>();
        listOfResults = new ArrayList();

        int nunberOfAddedPeople = listOfAddedPeople.size();

        for (int i = 1; i <= nunberOfAddedPeople; i++) {

            boolean added = false;
            while (!added) {
                int drawedNr = (int) (Math.random() * nunberOfAddedPeople) + 1;
                if (i == drawedNr && i == nunberOfAddedPeople) {
                    listOfResults.clear();
                    temporarySet.clear();
                    i = 1;
                    LOGGER.info("Peculiar exception: last number to choose: " + i + "is the same as chosen number: " + drawedNr);
                }
                if (i != drawedNr) {
                    added = temporarySet.add(drawedNr);
                    if (added) {
                        listOfResults.add(drawedNr);
                        LOGGER.info(i + ": number added: " + drawedNr);
                    } else LOGGER.info(i + ": " + drawedNr + " exist in the list so can't add it");
                }
            }
        }
        LOGGER.info("Chosen numbers " + listOfResults.toString());

        LOGGER.exiting(getClass().getName(), "ascribeNumbersToPeople");
    }

    public void deletePerson(ActionEvent actionEvent) {
        LOGGER.entering(getClass().getName(), "deletePerson");

        if (listOfAddedPeople.size() == 0) drawButtonWasClicked = false;
        if (table.getSelectionModel().getSelectedItems().toString().equals("[]")) drawButtonWasClicked = false;
        if (drawButtonWasClicked) {
            String deletedPerson = (table.getSelectionModel().getSelectedItems().toString());
            int numberOfDeletedPerson = giveNumberContainedInString(deletedPerson);

            boolean removed = listOfResults.remove(givePersonByNumber(numberOfDeletedPerson));
            if (removed) LOGGER.info("Removed from list :" + givePersonByNumber(numberOfDeletedPerson));
        }
        listOfAddedPeople.removeAll(table.getSelectionModel().getSelectedItems());
        table.getSelectionModel().clearSelection();
        Person.counter -= 1;

        int i = 1;
        for (Person p : listOfAddedPeople) {
            p.setId(i++);
        }

        LOGGER.exiting(getClass().getName(), "deletePerson");
    }

    private int giveNumberContainedInString(String deletedPerson) {
        LOGGER.entering(getClass().getName(), "giveNumberContainedInString");

        int searchingNr = 0;
        String stringNr = "";
        for (int i = 0; i < deletedPerson.length(); i++) {
            if (Character.isDigit(deletedPerson.charAt(i)))
                stringNr += deletedPerson.charAt(i);
        }
        searchingNr = Integer.parseInt(stringNr);

        LOGGER.exiting(getClass().getName(), "giveNumberContainedInString");
        return searchingNr;
    }

    public void clearList(ActionEvent actionEvent) {
        LOGGER.entering(getClass().getName(), "clearList");

        if (!listOfAddedPeople.isEmpty()) {

            //create alert to inform before deleting
            Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
            Window owner = (drawButton).getScene().getWindow();
            deleteAlert.setContentText("Confirm deleting list\n\nTHIS CANNOT BE UNDONE");
            deleteAlert.initModality(Modality.APPLICATION_MODAL);
            deleteAlert.initOwner(owner);
            deleteAlert.showAndWait();

            if (deleteAlert.getResult() == ButtonType.OK) {
                listOfAddedPeople.clear();
                Person.counter = 1;
                saveList.setDisable(true);
                drawButton.setDisable(true);
                santa.setVisible(false);
                deleteButton.setDisable(true);
                drawButtonWasClicked = false;
            } else {
                deleteAlert.close();
            }
        }

        LOGGER.exiting(getClass().getName(), "clearList");
    }

    public void loadFile(ActionEvent actionEvent) {
        LOGGER.entering(getClass().getName(), "loadFile");

        drawButtonWasClicked = true;
        saveList.setDisable(false);
        deleteButton.setDisable(false);

        Stage secondaryStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load person table");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*"),
                new FileChooser.ExtensionFilter("serialized files", "*.ser"));
        File file = fileChooser.showOpenDialog(secondaryStage);
        if (file != null) {
            load(file);
        }

        LOGGER.exiting(getClass().getName(), "loadFile");
    }

    private void load(File file) {
        LOGGER.entering(getClass().getName(), "load");

        listOfAddedPeople.clear();

        drawButton.setDisable(false);
        santa.setVisible(true);
        int i = 1;
        ArrayList<Person> loadedList = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            loadedList = (ArrayList<Person>) ois.readObject();
            ois.close();
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "Problem with loading file : " + file, e);
            e.printStackTrace();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (Person p : loadedList) {
            LOGGER.info("Name of person added from loaded list :" + p.getSerName());
            listOfAddedPeople.add(new Person(i++, p.getSerName(), p.getSerNameDrawedPerson()));
        }
        Person.counter = listOfAddedPeople.size() + 1;
        LOGGER.info("listOfAddedPeople after all people are loaded: " + listOfAddedPeople.toString());
        LOGGER.info("listOfAddedPeople.size after all people are loaded: " + listOfAddedPeople.size());

        LOGGER.exiting(getClass().getName(), "load");
    }

    public void saveList(ActionEvent actionEvent) {
        LOGGER.entering(getClass().getName(), "saveList");

        Stage secondaryStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save person table");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        if (listOfAddedPeople.isEmpty()) {
            Window owner = drawButton.getScene().getWindow();
            secondaryStage.initOwner(owner);
            Alert emptyTableAlert = new Alert(Alert.AlertType.ERROR);
            emptyTableAlert.setContentText("You have nothing to save");
            emptyTableAlert.initModality(Modality.APPLICATION_MODAL);
            emptyTableAlert.initOwner(owner);
            emptyTableAlert.showAndWait();
            if (emptyTableAlert.getResult() == ButtonType.OK) {
                emptyTableAlert.close();
            }
        } else {
            File file = fileChooser.showSaveDialog(secondaryStage);
            if (file != null) {
                serializeFile(file);
            }
        }

        LOGGER.exiting(getClass().getName(), "saveList");
    }

    private void serializeFile(File file) {
        LOGGER.entering(getClass().getName(), "serializeFile");

        String serFile = file.toString();
        serFile += ".ser";
        ArrayList<Person> savedList = new ArrayList<>(listOfAddedPeople);
        try {
            FileOutputStream fos = new FileOutputStream(serFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(savedList);
            oos.close();
            System.out.println("saved list: " + savedList.toString());
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.FINE, "Problem with saving file: " + file, e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.exiting(getClass().getName(), "serializeFile");
    }


    // create filechooser to chose directory and save file in txt format (using saveFile function)
    public void saveTxt(ActionEvent actionEvent) {
        LOGGER.entering(getClass().getName(), "saveTxt");

        Stage secondaryStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save person table");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        if (listOfAddedPeople.isEmpty()) {
            Window owner = drawButton.getScene().getWindow();
            secondaryStage.initOwner(owner);
            Alert emptyTableAlert = new Alert(Alert.AlertType.ERROR);
            emptyTableAlert.setContentText("You have nothing to save");
            emptyTableAlert.initModality(Modality.APPLICATION_MODAL);
            emptyTableAlert.initOwner(owner);
            emptyTableAlert.showAndWait();
            if (emptyTableAlert.getResult() == ButtonType.OK) {
                emptyTableAlert.close();
            }
        } else {
            File file = fileChooser.showSaveDialog(secondaryStage);
            if (file != null) {
                saveFile(file);
            }
        }

        LOGGER.exiting(getClass().getName(), "saveTxt");
    }

    // create text file given by filechooser and
    private void saveFile(File file) {
        LOGGER.entering(getClass().getName(), "saveFile");

        try {
            BufferedWriter outWriter = new BufferedWriter(new FileWriter(file));

            for (Person p : listOfAddedPeople) {
                outWriter.write(p.printPersonInfoSer());
                outWriter.newLine();
            }
            System.out.println(listOfAddedPeople.toString());
            outWriter.close();
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "Problem with saving list " + listOfAddedPeople + " to file: " + file, e);
            Alert ioAlert = new Alert(Alert.AlertType.ERROR, "OOPS!", ButtonType.OK);
            ioAlert.setContentText("Sorry. An error has occurred.");
            ioAlert.showAndWait();
            if (ioAlert.getResult() == ButtonType.OK) {
                ioAlert.close();
            }
        }

        LOGGER.exiting(getClass().getName(), "saveFile");
    }


    public void chooseSnowyTheme(ActionEvent actionEvent) {
        LOGGER.entering(getClass().getName(), "chooseSnowyTheme");

        Main.scene.getStylesheets().clear();
        Main.scene.getStylesheets().add("snowy.css");

        LOGGER.exiting(getClass().getName(), "chooseSnowyTheme");
    }

    public void chooseChristmasTheme(ActionEvent actionEvent) {
        LOGGER.entering(getClass().getName(), "chooseChristmasTheme");

        Main.scene.getStylesheets().clear();
        Main.scene.getStylesheets().add("christmas.css");

        LOGGER.exiting(getClass().getName(), "chooseChristmasTheme");
    }

    //show pop up with info about program
    public void showInfo(ActionEvent actionEvent) {
        LOGGER.entering(getClass().getName(), "showInfo");

        if (stage == null) stage = (Stage) nameInput.getScene().getWindow();

        final Popup popup = new Popup();
        popup.setX(centerX - 200);
        popup.setY(centerY - 200);

        Button hide = new Button("close");
        hide.setOnAction((a) -> popup.hide());
        hide.setDefaultButton(true);

        Image gift = new Image("sample/pic/gift.png");
        ImageView imageView = new ImageView(gift);
        imageView.resize(70, 70);

        Label label1 = new Label("This is simple program made in javaFx \n\tMain task of this program" +
                " is creating list of people in one column and other list of group of the same people in " +
                "second column but in such order" +
                "that every person always draws other person. " +
                "\n \tIf you want to hear some interesting about this program just click Santa ;)" +
                "\n \n");
        label1.setWrapText(true);
        label1.setMaxWidth(400);

        Label label2 = new Label("Author: Kacper Cichecki\nDecember 2016\nkacper.cichecki.90@gmail.com");
        label2.setWrapText(true);
        label2.setMaxWidth(400);
        label2.setFont(javafx.scene.text.Font.font(10));


        VBox layout1 = new VBox();
        layout1.setStyle("-fx-background-color: linear-gradient(#D0D0D0 , #FFCC99); " +
                "-fx-padding: 10; " +
                "-fx-border-color: rgb(49, 89, 23);\n" +
                "-fx-border-radius: 5;");
        layout1.setAlignment(Pos.CENTER);
        layout1.getChildren().addAll(imageView, label1, label2, hide);

        popup.getContent().addAll(layout1);
        popup.show(stage);

        LOGGER.exiting(getClass().getName(), "showInfo");
    }

    public void chooseDefaultTheme(ActionEvent actionEvent) {
        LOGGER.entering(getClass().getName(), "chooseDefaultTheme");

        Main.scene.getStylesheets().clear();
        Main.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);

        LOGGER.exiting(getClass().getName(), "chooseDefaultTheme");
    }

    public void startLabelInfo() {
        long startingPoint = System.nanoTime();

        class Task extends TimerTask {

            @Override
            public void run() {
                long currnetTime = System.nanoTime();
                int lastingOfProgram = Math.round((currnetTime - startingPoint) / 1000000000);

                int minuts = lastingOfProgram / 60;
                int sec = lastingOfProgram % 60;
                infoLabel.setText(String.format("%0,2d : %0,2d", minuts, sec) +
                        "\n" + listOfAddedPeople.size() + " added");
            }
        }
        timer = new Timer();
        timer.schedule(new Task(), 0, 1000);
    }

    public void showLogFile(ActionEvent actionEvent) {
        LOGGER.entering(getClass().getName(), "showLogFile");

        if (stage == null) stage = (Stage) nameInput.getScene().getWindow();

        final Popup popup = new Popup();
        popup.setX(centerX - 200);
        popup.setY(centerY - 200);

        Label label = new Label("Contents of log file");

        Button hide = new Button("close");
        hide.setOnAction((a) -> popup.hide());
        hide.setDefaultButton(true);

        File file = new File("Logging.txt");
        StringBuilder contents = new StringBuilder();

        try {
            BufferedReader input = new BufferedReader(new FileReader(file));
            try {
                String line = null;
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        TextArea textArea = new TextArea(contents.toString());
        textArea.setWrapText(true);

        VBox layout1 = new VBox();
        layout1.setAlignment(Pos.CENTER);
        layout1.setStyle("-fx-background-color: linear-gradient(#D0D0D0 , #FFCC99); " +
                "-fx-padding: 10; " +
                "-fx-border-color: rgb(49, 89, 23);\n" +
                "-fx-border-radius: 5;");

        layout1.getChildren().addAll(label, textArea, hide);

        popup.getContent().addAll(layout1);
        popup.show(stage);

        LOGGER.exiting(getClass().getName(), "showLogFile");
    }
}




