package edu.mills.cs180a.wordui;

import java.net.*;
import java.util.*;
import edu.mills.cs180a.wordui.model.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

public class FXMLController implements Initializable {
    @FXML
    private TextField wordTextField;
    @FXML
    private TextField frequencyTextField;
    @FXML
    private TextArea definitionTextArea;
    @FXML
    private Button removeButton;
    @FXML
    private Button createButton;
    @FXML
    private Button updateButton;
    @FXML
    private ListView<WordRecord> listView;

    private final ObservableList<WordRecord> wordRecordList = FXCollections.observableArrayList();

    private WordRecord selectedWordRecord;
    private final BooleanProperty modifiedProperty = new SimpleBooleanProperty(false);
    private ChangeListener<WordRecord> wordRecordChangeListener = new WordRecordChangeListener();

    private class WordRecordChangeListener implements ChangeListener<WordRecord> {
        @Override
        public void changed(ObservableValue<? extends WordRecord> observable, WordRecord oldValue,
                WordRecord newValue) {
            // newValue can be null if nothing is selected.
            System.out.println("Selected item: " + newValue);
            selectedWordRecord = newValue;
            modifiedProperty.set(false);
            if (newValue != null) {
                wordTextField.setText(selectedWordRecord.getWord());
                frequencyTextField.setText(selectedWordRecord.getFrequency().toString());
                definitionTextArea.setText(selectedWordRecord.getDefinition());
            } else {
                wordTextField.setText("");
                frequencyTextField.setText("");
                definitionTextArea.setText("");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the list.
        SampleData.fillSampleData(wordRecordList);

        configureButtons();

        // Sort list alphabetically.
        SortedList<WordRecord> sortedList = new SortedList<>(wordRecordList);
        sortedList.setComparator((wr1, wr2) -> wr1.getWord().compareToIgnoreCase(wr2.getWord()));
        listView.setItems(sortedList);

        addListeners();

        // Pre-select the first item.
        listView.getSelectionModel().selectFirst();
    }

    private void configureButtons() {
        // Disable the Remove button if nothing is selected in the ListView control.
        removeButton.disableProperty()
                .bind(listView.getSelectionModel().selectedItemProperty().isNull());

        // Disable the Update button if nothing is selected, no modifications have
        // been made, or any field is empty or invalid.
        updateButton.disableProperty()
                .bind(listView.getSelectionModel().selectedItemProperty().isNull()
                        .or(modifiedProperty.not()).or(wordTextField.textProperty().isEmpty())
                        .or(frequencyTextField.textProperty().isEmpty())
                        .or(definitionTextArea.textProperty().isEmpty()));

        // TODO: Disable the Create button if an existing entry is selected or any
        // field is empty or invalid.
    }

    private void addListeners() {
        listView.getSelectionModel().selectedItemProperty().addListener(wordRecordChangeListener);
    }

    @FXML
    private void handleKeyAction(KeyEvent keyEvent) {
        modifiedProperty.set(true);
    }

    @FXML
    private void createButtonAction(ActionEvent actionEvent) {
        System.out.println("Create");
        WordRecord wordRecord = new WordRecord(wordTextField.getText(),
                Integer.parseInt(frequencyTextField.getText()), definitionTextArea.getText());
        wordRecordList.add(wordRecord);
        listView.getSelectionModel().select(wordRecord); // select the new item
    }

    @FXML
    private void removeButtonAction(ActionEvent actionEvent) {
        System.out.println("Remove " + selectedWordRecord);
        wordRecordList.remove(selectedWordRecord);
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
        System.out.println("Update " + selectedWordRecord);
        WordRecord entry = listView.getSelectionModel().getSelectedItem();
        listView.getSelectionModel().selectedItemProperty()
                .removeListener(wordRecordChangeListener);
        entry.setWord(wordTextField.getText());
        entry.setFrequency(Integer.parseInt(frequencyTextField.getText()));
        entry.setDefinition(definitionTextArea.getText());
        listView.getSelectionModel().selectedItemProperty().addListener(wordRecordChangeListener);
        modifiedProperty.set(false);
    }
}
