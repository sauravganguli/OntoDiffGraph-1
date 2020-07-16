package application.graph.functionality;

import application.graph.GraphPane;
import application.graph.ResizableCanvas;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public class PanFunctionality implements GraphFunctionality{

	private final ResizableCanvas canvas;

	private MouseEvent lastPanEvent;
	private EventHandler<MouseEvent> panStartEventHandler;
	private EventHandler<MouseEvent> panningEventHandler;

	public PanFunctionality(GraphPane graph){
		this.canvas = graph.getCanvas();
	}

	@Override public void applyFunctionality(){
		panStartEventHandler = event -> {
			if(!event.isSecondaryButtonDown()){
				return;
			}
			lastPanEvent = event;
		};
		panningEventHandler = event -> {
			if(!event.isSecondaryButtonDown()){
				return;
			}

			Point2D diff = canvas.screenDistanceToCanvas(event.getX() - lastPanEvent.getX(),event.getY() - lastPanEvent.getY());
			canvas.getGraphicsContext2D().translate(diff.getX(),diff.getY());
			lastPanEvent = event;
		};

		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,panStartEventHandler);
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,panningEventHandler);
	}

	@Override public void removeFunctionality(){
		canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED,panStartEventHandler);
		canvas.removeEventHandler(MouseEvent.MOUSE_DRAGGED,panningEventHandler);
	}
}
