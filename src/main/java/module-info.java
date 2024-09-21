module ithaic.imitate_os {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;
    requires static lombok;


    opens ithaic.imitate_os to javafx.fxml;
    exports ithaic.imitate_os;
    exports Test to junit;
}
