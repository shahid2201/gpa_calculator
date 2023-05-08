import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;

public class Controller {

    ControlSQL cSql = new ControlSQL();

    private String semName;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Button addSemBtn;

    @FXML
    private Button addSubBtn;

    @FXML
    private Button calculateBtn;

    @FXML
    private TextField calculatedGpa;

    @FXML
    private TextField calculatedPercentage;

    @FXML
    private TextField classField;

    @FXML
    private TextField descriptionField;
    
    @FXML
    private TextField creditField;
    
    @FXML
    private TextField gradeField;

    @FXML
    private TableColumn<SubjectGrade, Integer> gradeCol;

    @FXML
    private TableColumn<SubjectGrade, Integer> creditCol;

    @FXML
    private TextField semField;

    @FXML
    private VBox semPane;

    @FXML
    private TableColumn<SubjectGrade, String> classCol;

    @FXML
    private TableColumn<SubjectGrade, String> descriptionCol;

    @FXML
    private TableView<SubjectGrade> subTable;

    @FXML
    private TextField titleField;

    void setSemName(String semName){
        this.semName = semName;
    }

    String getSemName(){
        return semName;
    }

    String getClassCode(){
        if (classField.getText().matches("[A-Z]{4}\\d{5}")){
            return classField.getText();
        } else {
            return "ERROR";
        }
    }

    String getDescription(){
        if (descriptionField.getText().trim().isEmpty()){
            return "ERROR";
        } else {
            return descriptionField.getText();
        }
    }

    int getCredit(){
        try {
            return Integer.parseInt(creditField.getText());
        } catch (Exception e) {
            return -1;
        }
    }

    int getGrade(){
        try {
            if (Integer.parseInt(gradeField.getText()) <= 100 || Integer.parseInt(gradeField.getText()) >= 0){
                return Integer.parseInt(gradeField.getText());
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    boolean checkSubCriteria(){
        if (getClassCode().equals("ERROR") || getDescription().equals("ERROR") || getCredit() == -1 || getGrade() == -1) {
            return false;
        } else {
            return true;
        }
    }

    @FXML
    public void initialize() {
        classCol.setCellValueFactory(new PropertyValueFactory<>("classCode"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grades"));
        displaySem();
    }

    @FXML
    void handleAddSemBtn(ActionEvent event) {
        try {
            if (!semField.getText().trim().isEmpty()){
                String[] words = semField.getText().split("\\b");
                if (! (words.length > 1)) {
                    cSql.addSem(semField.getText());
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    String alertMsg = "Semester Field cannot have more than one word";
                    alert.setTitle("Error");
                    alert.setHeaderText("Error while adding semester");
                    alert.setContentText(alertMsg);
                    alert.showAndWait();
                }
            }
            else{
                Alert alert = new Alert(AlertType.ERROR);
                String alertMsg = "Semester Field cannot be left empty";
                alert.setTitle("Error");
                alert.setHeaderText("Error while adding semester");
                alert.setContentText(alertMsg);
                alert.showAndWait();
            }
        } catch (Exception e) {
            System.out.println("Exception occured while adding semester" + e);
        }
        semPane.getChildren().clear();
        displaySem();
    }

    @FXML
    void handleAddSubBtn(ActionEvent event) {
        try {
            if (checkSubCriteria() == true){
                if (!(getSemName() == null))
                    cSql.addSub(getSemName(), getClassCode(), getDescription(), getCredit(), getGrade());
                else {
                    Alert alert = new Alert(AlertType.ERROR);
                    String alertMsg = "You need to select a semester first";
                    alert.setTitle("Error");
                    alert.setHeaderText("No semester selected");
                    alert.setContentText(alertMsg);
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                String alertMsg = "";
                alert.setTitle("Error");
                alert.setHeaderText("Error while adding subject");

                if (getClassCode().equals("ERROR")){
                    alertMsg += "Class should be of the format (ABCD12345) \n";
                }
                if (getDescription().equals("ERROR")){
                    alertMsg += "Description cannot be empty \n";
                }
                if (getCredit() == -1){
                    alertMsg += "Please enter a valid number \n";
                }
                if (getGrade() == -1){
                    alertMsg += "Please enter a valid number >= 0 or <= 100";
                }

                alert.setContentText(alertMsg);
                alert.showAndWait();
            }
        } catch (Exception e) {
            System.out.println("Exception occured while adding semester" + e);
        }
    }

    @FXML
    void displaySem(){
        try {
            cSql.getSem();
            // Add 10 pixels of margin between each button
            Insets margin = new Insets(0, 0, 0, 0);

            for (int i = 0; i < cSql.getSem().size(); i++) {
                int index = i;
                Button btn = new Button(cSql.getSem().get(i));
                btn.setPrefWidth(semPane.getWidth());
                semPane.getChildren().add(btn);

                // Set the margin for the button
                VBox.setMargin(btn, margin);

                btn.setOnAction(e -> {
                    try {
                        displaySubs(cSql.getSem().get(index));
                        setSemName(cSql.getSem().get(index));
                    } catch (Exception ex) {
                        System.out.println("Exception occured while getting semesters" + ex);
                    }   
                });

                calculateBtn.setOnAction(e -> {
                    try {
                        calculateGrades(cSql.getSem().get(index));                   
                    } catch (Exception ex) {
                        System.out.println("Exception occured while calculating: " + ex );
                    }
                });

                // Listen for changes to the width of the pane and update the button width accordingly
                semPane.widthProperty().addListener((obs, oldVal, newVal) -> {
                    btn.setPrefWidth(newVal.doubleValue());
                });
            }
        } catch (Exception e) {
            System.out.println("Exception occured while getting semesters" + e);
        }
    }

    @FXML
    void displaySubs(String sem) throws SQLException{
        try {
            subTable.setItems(cSql.getSub(sem));
        } catch (Exception e){
            System.out.println("Error occurred while getting sub: " + e);
        }
    }

    @FXML
    void calculateGrades(String sem) throws SQLException{
        try {
            ObservableList<SubjectGrade> data = subTable.getItems();
            int grade = 0, count = 0;
            double percentage = 0.0, gpe = 0.0, wcv = 0.0, totalCredit = 0.0, totalGrade = 0.0, totalWCV = 0.0;

            for (SubjectGrade subjectGrade : data) {
                grade = subjectGrade.getGrades();
                int credit = subjectGrade.getCredit();

                if (grade <= 100 && grade >= 90){
                    gpe = 4.0;
                } else if (grade < 90 && grade >= 85){
                    gpe = 3.8;
                } else if (grade < 85 && grade >= 80){
                    gpe = 3.6;
                } else if (grade < 80 && grade >= 75){
                    gpe = 3.3;
                } else if (grade < 75 && grade >= 70){
                    gpe = 3.0;
                } else if (grade < 70 && grade >= 65){
                    gpe = 2.5;
                } else if (grade < 65 && grade >= 60){
                    gpe = 2.0;
                } else if (grade < 60 && grade >= 55){
                    gpe = 1.5;
                } else if (grade < 55 && grade >= 50){
                    gpe = 1.0;
                } else if (grade < 50){
                    gpe = 0;
                } else {
                    gpe = 0;
                }
                
                count ++; // this will still count even if it prints error message. check this later...
                wcv = credit * gpe;
                totalCredit += credit;
                totalGrade += grade;
                totalWCV += wcv;
            }

            percentage = totalGrade / count;
            calculatedPercentage.setText(String.format("%.2f", percentage));
            calculatedGpa.setText(String.format("%.2f", totalWCV / totalCredit));
        } catch (Exception e) {
            System.out.println("Error occurred while calculating grades: " + e);
        }
    }
}
