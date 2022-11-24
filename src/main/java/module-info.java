module vista {
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires javafx.media;
	requires java.desktop;
    requires javafx.fxml;
	requires javafx.base;
    
    opens controlador to javafx.fxml;
	exports vista;
}
