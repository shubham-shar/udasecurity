module com.udacity.catpoint.security.SecurityService {
    requires java.desktop;
    requires miglayout.swing;
    requires java.prefs;
    requires gson;
    requires com.google.common;
    requires com.udacity.catpoint.imageService.imageProcessor;
    requires java.sql;
    opens com.udacity.catpoint.security.data;
}