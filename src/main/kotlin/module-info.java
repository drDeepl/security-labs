module com.lab3.key_audit {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires com.github.kwhat.jnativehook;


    opens com.lab3.key_audit to javafx.fxml;
    exports com.lab3.key_audit;
}