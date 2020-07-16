package application.graph.functionality;

import application.graph.GraphPane;
import application.graph.ResizableCanvas;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;

public class ZoomFunctionality implements GraphFunctionality{

	private final double zoomDelta = 0.1;
	private final ResizableCanvas canvas;
	private EventHandler<ScrollEvent> zoomEventEventHandler;

	public ZoomFunctionality(GraphPane graph){
		this.canvas = graph.getCanvas();
	}

	@Override public void applyFunctionality(){
		zoomEventEventHandler = event -> {
			Affine transf = canvas.getGraphicsContext2D().getTransform();
			double currentZoom = transf.getMxx();
			if((currentZoom < 0.2 && event.getDeltaY() < 0) || (currentZoom > 2.0 && event.getDeltaY() >= 0)){
				return;
			}
			//System.out.println(transf.toString());

			Point2D canvasCenter = canvas.getCenter();

			if(event.getDeltaY() < 0){
				transf.appendScale(1.0-zoomDelta,1.0-zoomDelta,canvasCenter);
				//System.out.println("Zoom out");
			}
			else{
				transf.appendScale(1.0+zoomDelta,1.0+zoomDelta,canvasCenter);
				//System.out.println("Zoom in");
			}

			canvas.getGraphicsContext2D().setTransform(transf);
		};

		canvas.addEventHandler(ScrollEvent.SCROLL,zoomEventEventHandler);
	}

	@Override public void removeFunctionality(){
		canvas.removeEventHandler(ScrollEvent.SCROLL,zoomEventEventHandler);
	}
}
