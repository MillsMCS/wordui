package edu.mills.cs180a.wordui.model;

import java.util.Comparator;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WordRecord {
    /*
     * private static Comparator<WordRecord> azComparator = new Comparator<WordRecord>() {
     *
     * @Override public int compare(WordRecord wr1, WordRecord wr2) { return
     * wr1.getWord().compareToIgnoreCase(wr2.getWord()); } };
     */
    private static Comparator<WordRecord> azComparator =
            (wr1, wr2) -> wr1.getWord().compareToIgnoreCase(wr2.getWord());
    private static Comparator<WordRecord> zaComparator =
            (wr1, wr2) -> wr2.getWord().compareToIgnoreCase(wr1.getWord());
    private static Comparator<WordRecord> increasingFreqComparator =
            (wr1, wr2) -> wr1.getFrequency().compareTo(wr2.getFrequency());
    private static Comparator<WordRecord> decreasingFreqComparator =
            (wr1, wr2) -> wr2.getFrequency().compareTo(wr1.getFrequency());

    public enum SortOrder {
        ALPHABETICALLY_FORWARD("alphabetically (A-Z)", azComparator),
        ALPHABETICALLY_BACKWARD("alphabetically (Z-A)", zaComparator),
        BY_FREQ_ASCENDING("by frequency (low to high)", increasingFreqComparator),
        BY_FREQ_DESCENDING("by frequency (high to low)", decreasingFreqComparator);

        private final String name;
        private final Comparator<WordRecord> comparator;

        private SortOrder(String name, Comparator<WordRecord> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

        @Override
        public String toString() {
            return name;
        }

        public Comparator<WordRecord> getComparator() {
            return comparator;
        }
    }

    private final StringProperty word = new SimpleStringProperty(this, "word", "");
    private final IntegerProperty frequency = new SimpleIntegerProperty(this, "frequency");
    private final StringProperty definition =
            new SimpleStringProperty(this, "definition", "sample definition");

    public WordRecord() {}

    public WordRecord(String word, int frequency, String definition) {
        this.word.set(word);
        this.frequency.set(frequency);
        this.definition.set(definition);
    }

    public String getWord() {
        return word.get();
    }

    public StringProperty wordProperty() {
        return word;
    }

    public void setWord(String word) {
        this.word.set(word);
    }

    public Integer getFrequency() {
        return frequency.get();
    }

    public IntegerProperty frequencyProperty() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency.set(frequency);
    }

    public String getDefinition() {
        return definition.get();
    }

    public StringProperty definitionProperty() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition.set(definition);
    }

    @Override
    public String toString() {
        return word.get() + " (" + frequency.get() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        WordRecord record = (WordRecord) obj;
        return Objects.equals(word, record.word) && Objects.equals(frequency, record.frequency)
                && Objects.equals(definition, record.definition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, frequency, definition);
    }
}
