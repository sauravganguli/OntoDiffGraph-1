package application.graph.functionality;

import application.graph.GraphPane;
import application.graph.ResizableCanvas;
import application.graph.layout.ForceDirectedLayout;
import application.graph.vertex.GraphVertex;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Optional;

public class VertexDragFunctionality implements GraphFunctionality{

	private final GraphPane graph;
	private final ForceDirectedLayout layout;
	private final MouseButton button = MouseButton.PRIMARY;

	private Optional<GraphVertex> dragNode;
	private MouseEvent lastEvent;

	private EventHandler<MouseEvent> dragStartEventHandler;
	private EventHandler<MouseEvent> draggingEventHandler;
	private EventHandler<MouseEvent> dragStopEventHandler;

	public VertexDragFunctionality(GraphPane graph, ForceDirectedLayout layout){
		this.graph = graph;
		this.layout = layout;
	}

	@Override public void applyFunctionality(){
		ResizableCanvas canvas = graph.getCanvas();

		dragNode = Optional.empty();
		final List<GraphVertex> nodes = graph.getVertices();
		//TODO this seems ugly

		dragStartEventHandler = event -> {
			if(event.getButton() != button){
				return;
			}
			Point2D p = canvas.screenCoordToCanvas(event.getX(),event.getY());
			dragNode = nodes.stream().filter(node -> node.getShape().containsPoint(p.getX(),p.getY())).findFirst();
			dragNode.ifPresent(
					graphVertex -> layout.getRunnable().setVertexMobility(graphVertex, false)
			);
			lastEvent = event;
		};
		draggingEventHandler = event -> {
			if(event.getButton() != button || !dragNode.isPresent()){
				return;
			}
			Point2D p = canvas.screenDistanceToCanvas(event.getX() - lastEvent.getX(),event.getY() - lastEvent.getY());
			dragNode.get().move(p.getX(),p.getY());
			lastEvent = event;
		};
		dragStopEventHandler = event -> {
			if(event.getButton() != button || !dragNode.isPresent()){
				return;
			}
			layout.getRunnable().setVertexMobility(dragNode.get(),true);
		};

		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,dragStartEventHandler);
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,draggingEventHandler);
		canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,dragStopEventHandler);
	}

	@Override public void removeFunctionality(){
		ResizableCanvas canvas = graph.getCanvas();
		canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED,dragStartEventHandler);
		canvas.removeEventHandler(MouseEvent.MOUSE_DRAGGED,draggingEventHandler);
		canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED,dragStopEventHandler);
	}
}
