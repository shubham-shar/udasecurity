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
    exports com.udacity.catpoint.security;
    opens com.udacity.catpoint.security.application;
    opens com.udacity.catpoint.security.data;
    opens com.udacity.catpoint.security.service;
    opens com.udacity.catpoint.security;
}