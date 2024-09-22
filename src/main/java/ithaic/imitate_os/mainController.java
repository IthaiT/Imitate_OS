package ithaic.imitate_os;

import ithaic.imitate_os.fileManager.FileInteract;
import javafx.fxml.FXML;
import javafx.scene.control.*;
public class mainController {
    @FXML
    private TextField CommandInput;
    @FXML
    private Button button;


    @FXML
    private void initialize() {
        new FileInteract(CommandInput,button);
    }
}
