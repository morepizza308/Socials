package de.joergmorbitzer.Socials.entities;

public enum PrivacyTarget {
    GLOBAL("Visible to All"),
    FRIENDS("Visible to Friends only"),
    SELECT("Select visibility");

    private final String displayValue;

    private PrivacyTarget(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
