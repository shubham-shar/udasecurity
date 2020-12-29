module imageProcessor {
    requires java.desktop;
    requires miglayout.swing;
    requires org.slf4j;
    requires software.amazon.awssdk.services.rekognition;
    requires software.amazon.awssdk.auth;
    requires software.amazon.awssdk.regions;
    requires software.amazon.awssdk.core;
    exports com.udacity.catpoint.imageService.service;
    opens com.udacity.catpoint.imageService.service;
}