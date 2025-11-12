package details;

public class DetailsEntry {
    private final String label;
    private final String value;

    public DetailsEntry(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() { return label; }
    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetailsEntry)) return false;
        DetailsEntry that = (DetailsEntry) o;
        return label.equals(that.label) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return 31 * label.hashCode() + value.hashCode();
    }

    @Override
    public String toString() {
        return "DetailEntry{" +
                "label='" + label + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}

