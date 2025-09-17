package model;

public enum TransactionType {
    INCOME("Thu"),
    EXPENSE("Chi");

    private final String label;
    TransactionType(String label) { this.label = label; }
    public String getLabel() { return label; }

    @Override public String toString() {   // <- Swing sẽ dùng toString để vẽ
        return label;
    }
}
