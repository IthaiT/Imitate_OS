package ithaic.imitate_os.fileManager;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.Arrays;

public class FileInteract {
    private static TextField CommandInput;
    private static Button certify;
    private String command;

    public FileInteract(TextField CommandInput, Button certify) {
        FileInteract.CommandInput = CommandInput;
        FileInteract.certify = certify;
        certify.setOnMouseClicked(e -> {
            command = CommandInput.getText();
            HandleCommand();
        });
    }

    private void HandleCommand() {
        String[] commandArray = command.trim().split("\\s+");
        String[] directoryArray = commandArray[1].split("/");
        System.out.println(commandArray[0]);
        System.out.println(commandArray[1]);
        for (String s : directoryArray) {
            System.out.println(s);
        }

        if (command.equals("create")) {

        }
        else if (command.equals("delete")) {}
        else if (command.equals("type")) {}
        else if (command.equals("copy")) {}
        else if (command.equals("mkdir")) {}
        else if (command.equals("rmdir")) {}
    }
}
