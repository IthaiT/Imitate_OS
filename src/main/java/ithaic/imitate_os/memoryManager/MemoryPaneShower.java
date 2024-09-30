package ithaic.imitate_os.memoryManager;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MemoryPaneShower {
   private static HBox hBox;
    private static VBox leftBox;
   Rectangle[] rectangles=new Rectangle[512];
    public MemoryPaneShower(HBox Box,VBox Vbox){
        MemoryPaneShower.hBox = Box;
        MemoryPaneShower.leftBox=Vbox;
        for (int i = 0; i < 512; i++) {
            rectangles[i]=new Rectangle(30,20);
            rectangles[i].setFill(Color.RED);
            rectangles[i].widthProperty().bind(leftBox.widthProperty().divide(512));
        }

        for (int i = 0; i < 150; i++) {
            rectangles[i].setFill(Color.GREEN);
        }
        hBox.getChildren().addAll(rectangles);
    }
    private void getMemoryBlocks(){
        MemoryBlock blocks=Memory.getInstance().getMemoryBlock();
        while (blocks.getNext()!=null){
            int start=blocks.getAddress();
            int end=blocks.getSize();
        }
    }
}
