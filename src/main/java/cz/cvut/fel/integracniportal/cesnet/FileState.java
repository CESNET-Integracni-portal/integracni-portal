package cz.cvut.fel.integracniportal.cesnet;

public enum FileState {
    REG("REG"),
    MIG("MIG"),
    ARC("ARC"),
    DUL("DUL"),
    OFL("OFL"),
    UNM("UNM"),
    NMG("NMG"),
    PAR("PAR"),
    INV("INV");

    private final String state;

    FileState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
