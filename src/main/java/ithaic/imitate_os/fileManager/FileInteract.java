package ithaic.imitate_os.fileManager;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class FileInteract {
    private static TextField CommandInput;
    private static Button certify;
    private String command;

    public FileInteract(TextField CommandInput, Button certify) {
        FileInteract.CommandInput = CommandInput;
        FileInteract.certify = certify;
        certify.setOnMouseClicked(e -> {
            String command = CommandInput.getText();
            HandleCommand(command);
        });
    }

    private void HandleCommand(String command) {
        if (command.equals("create")) {}
        else if (command.equals("delete")) {}
        else if (command.equals("type")) {}
        else if (command.equals("copy")) {}
        else if (command.equals("mkdir")) {}
        else if (command.equals("rmdir")) {}
    }
}
