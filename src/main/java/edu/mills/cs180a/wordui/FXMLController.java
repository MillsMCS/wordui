package edu.mills.cs180a.wordui;

import java.net.URL;
import java.util.ResourceBundle;
import edu.mills.cs180a.wordui.model.SampleData;
import edu.mills.cs180a.wordui.model.WordRecord;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

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
                frequencyTextField.setText(Integer.toString(selectedWordRecord.getFrequency()));
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

    private SimpleBooleanProperty representsNonnegativeInteger(StringProperty sp) {
        try {
            int value = Integer.parseInt(sp.getValue());
            return new SimpleBooleanProperty(value >= 0);
        } catch (NumberFormatException e) {
            return new SimpleBooleanProperty(false);
        }
    }

    private void configureButtons() {
        // Disable the Remove button if nothing is selected in the ListView control.
        removeButton.disableProperty()
                .bind(listView.getSelectionModel().selectedItemProperty().isNull());

        // Disable the Update button if nothing is selected, no modifications have
        // been made, or any field is empty or invalid.
        updateButton.disableProperty()
                .bind(listView.getSelectionModel().selectedItemProperty().isNull()
                        .or(modifiedProperty.not())
                        .or(wordTextField.textProperty().isEmpty())
                        .or(representsNonnegativeInteger(frequencyTextField.textProperty()).not())
                        .or(definitionTextArea.textProperty().isEmpty()));

        // Disable the Create button if an existing entry is selected or any
        // field is empty or invalid.
        createButton.disableProperty()
                .bind(listView.getSelectionModel().selectedItemProperty().isNotNull()
                        .or(wordTextField.textProperty().isEmpty())
                        .or(representsNonnegativeInteger(frequencyTextField.textProperty()).not())
                        .or(definitionTextArea.textProperty().isEmpty()));
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
        WordRecord wordRecord =
                new WordRecord(wordTextField.getText(),
                        Integer.valueOf(frequencyTextField.getText()),
                        definitionTextArea.getText());
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
        entry.setDefinition(definitionTextArea.getText());
        listView.getSelectionModel().selectedItemProperty().addListener(wordRecordChangeListener);
        modifiedProperty.set(false);
    }
}
