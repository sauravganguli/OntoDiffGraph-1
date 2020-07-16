package application.graph.vertex;

import application.graph.ResizableCanvas;
import application.graph.edge.EdgeGroup;
import application.graph.edge.GraphEdge;
import application.graph.shape.GraphShape;
import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphVertex{

	protected boolean visible = true;
	protected GraphShape graphShape;
	protected NodeText label;
	protected final Map<GraphVertex,EdgeGroup> edges = new HashMap<>();

	public GraphVertex(GraphShape shape){
		this.graphShape = shape;
	}

	public GraphVertex(GraphShape shape, String label){
		this.graphShape = shape;
		if(label != null){
			this.label = new NodeText(label,shape.getLabelWidthLimit());
		}
	}

	public boolean isVisible(){
		return visible;
	}

	public void setVisible(boolean visible){
		this.visible = visible;
	}

	public GraphShape getShape(){
		return this.graphShape;
	}

	public NodeText getLabel(){
		return this.label;
	}

	public Map<GraphVertex,EdgeGroup> getEdges(){
		return this.edges;
	}

	public List<GraphEdge> getAllEdges(){
		return this.edges.values().stream().flatMap(group -> group.getEdges().stream()).collect(Collectors.toList());
	}

	public EdgeGroup getEdgesTo(GraphVertex gv){
		return edges.get(gv);
	}

	public void setText(String nodeLabel){
		if(nodeLabel == null) return;
		double width = graphShape.getLabelWidthLimit();
		if(label == null){
			label = new NodeText(nodeLabel,width);
		}
		else{
			label.setText(nodeLabel,width);
		}
	}

	public void move(double x, double y){
		graphShape.movePosition(x,y);
	}

	public void draw(ResizableCanvas canvas){
		this.graphShape.draw(canvas);


		if(label != null){
			Point2D center = this.graphShape.getCenter();
			this.label.draw(canvas.getGraphicsContext2D(),center.getX(),center.getY());
		}
	}
}
