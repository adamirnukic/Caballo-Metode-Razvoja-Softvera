module main.caballo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;
    requires org.apache.pdfbox;
    requires java.desktop;

    opens main.caballo to javafx.fxml;
    exports main.caballo;

    exports main.caballo.controller;

    opens main.caballo.controller to javafx.fxml;
}