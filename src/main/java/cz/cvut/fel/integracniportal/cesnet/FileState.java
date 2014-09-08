package cz.cvut.fel.integracniportal.cesnet;

public enum FileState {
    REG("REG"), // Only on online medium
    MIG("MIG"), // Being migrated to offline medium
    ARC("ARC"), // Currently being archived
    DUL("DUL"), // Dual state - file on both online and offline medium
    OFL("OFL"), // Only on offline medium
    UNM("UNM"), // Being migrated back to online medium
    NMG("NMG"), // Cannot be migrated
    PAR("PAR"), // Partially migrated
    NA("N/A"),  // Unable to get state
    INV("INV"); // Invalid state

    private final String state;

    FileState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
