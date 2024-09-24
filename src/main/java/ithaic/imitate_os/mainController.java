package ithaic.imitate_os;

import ithaic.imitate_os.fileManager.FileInteract;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class mainController {
    public VBox mainVBox;
    public HBox topHBox;
    public VBox bottom_leftBox;
    public VBox bottom_midBox;
    public VBox bottom_rightBox;
    public HBox bottom_Box;
    public HBox queueBox;
    public VBox commandBox;
    @FXML
    private TextField CommandInput;
    @FXML
    private Button button;

    @FXML
    private void initialize() {
        new FileInteract(CommandInput,button);
        initializeBox();
    }
    private void initializeBox() {
        topHBox.setPrefHeight(mainVBox.getPrefHeight() * 0.2);

        bottom_Box.setPrefHeight(mainVBox.getPrefHeight() * 0.8);


        bottom_leftBox.setPrefHeight(bottom_Box.getPrefHeight());
        bottom_leftBox.setPrefWidth(bottom_Box.getPrefWidth() * 0.4);
        bottom_leftBox.prefHeightProperty().bind(bottom_Box.prefHeightProperty());
        bottom_leftBox.prefWidthProperty().bind(bottom_Box.prefWidthProperty());
            queueBox.setPrefHeight(bottom_leftBox.getPrefHeight() * 0.4);
//            绑定queueBox的高度是父组件高度的0.4倍
            queueBox.prefHeightProperty().bind(Bindings.multiply(0.4,bottom_leftBox.heightProperty()));

            commandBox.setPrefHeight(bottom_leftBox.getPrefHeight() * 0.6);
//            绑定commandBox的高度是父组件高度的0.6倍
            commandBox.prefHeightProperty().bind(Bindings.multiply(0.6,bottom_leftBox.heightProperty()));


        bottom_midBox.setPrefHeight(bottom_Box.getPrefHeight() );
        bottom_midBox.setPrefWidth(bottom_Box.getPrefWidth() *0.2);
        bottom_midBox.prefHeightProperty().bind(bottom_Box.prefHeightProperty());
        bottom_midBox.prefWidthProperty().bind(bottom_Box.prefWidthProperty());

        bottom_rightBox.setPrefHeight(bottom_Box.getPrefHeight());
        bottom_rightBox.setPrefWidth(bottom_Box.getPrefWidth() * 0.4);
        bottom_rightBox.prefHeightProperty().bind(bottom_Box.prefHeightProperty());
        bottom_rightBox.prefWidthProperty().bind(bottom_Box.prefWidthProperty());

    }
}
