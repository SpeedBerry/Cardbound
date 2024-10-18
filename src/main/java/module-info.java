module com.cs2212.cardbound {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    requires com.google.gson;

    opens com.cs2212.cardbound to javafx.fxml;
    exports com.cs2212.cardbound;
    exports com.cs2212.cardbound.gameplay;
    opens com.cs2212.cardbound.gameplay to javafx.fxml, com.google.gson;
    exports com.cs2212.cardbound.system;
    opens com.cs2212.cardbound.system to javafx.fxml;

}