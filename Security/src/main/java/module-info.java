module security {
    requires java.desktop;
    requires miglayout.swing;
    requires java.prefs;
    requires gson;
    requires guava;
    requires imageProcessor;
    requires java.sql;
    exports com.udacity.catpoint.security.application;
    exports com.udacity.catpoint.security.data;
    exports com.udacity.catpoint.security.service;
}