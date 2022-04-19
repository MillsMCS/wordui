package edu.mills.cs180a.wordui.model;

import javafx.collections.*;

public class SampleData {
    public static void fillSampleData(ObservableList<WordRecord> backingList) {
        backingList.add(new WordRecord("buffalo", 5153, "The North American bison."));
        backingList.add(new WordRecord("school", 23736, "A large group of aquatic animals."));
        backingList.add(
                new WordRecord("Java", 179, "An island of Indonesia in the Malay Archipelago"));
        backingList.add(
                new WordRecord("random", 794, "Having no specific pattern, purpose, or objective"));
    }
}
