package application.ui;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Separator;
import javafx.scene.layout.Region;

public class DraggableSeparator{
	public enum GrowDirection{
		LR,RL
	}

	public static void makeDraggable(Separator sp,Region parent,GrowDirection direction,DoubleProperty minWidth,DoubleProperty maxWidth){

		sp.setCursor(Cursor.H_RESIZE);

		sp.setOnMouseDragged(event -> {

			double width;
			if(direction == GrowDirection.LR){
				width = event.getSceneX();
			}
			else{
				width = parent.getScene().getWidth()-event.getSceneX();
			}

			//Clamp between minWidth and maxWidth
			if(minWidth != null && width < minWidth.get()) width = minWidth.get();
			if(maxWidth != null && width > maxWidth.get()) width = maxWidth.get();

			parent.setPrefWidth(width);
		});
	}
}
