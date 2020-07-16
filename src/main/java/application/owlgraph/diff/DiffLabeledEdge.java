package application.owlgraph.diff;

import application.graph.ResizableCanvas;
import application.graph.edge.EdgeGroup;
import application.graph.edge.LabeledEdge;
import application.graph.shape.GraphShape;
import application.graph.vertex.GraphVertex;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class DiffLabeledEdge extends LabeledEdge{

	private GraphShape shape;
	private int borderSize = 5;

	public DiffLabeledEdge(GraphVertex nodeA,GraphVertex nodeB,String label,GraphShape shape,int borderSize){
		super(nodeA,nodeB,label);
		this.color = shape.getFillColor();
		this.shape = shape;
		this.borderSize = borderSize;
	}

	@Override public void draw(ResizableCanvas canvas){
		final Point2D[] path = getPath();
		final GraphicsContext gc = canvas.getGraphicsContext2D();

		final double recWidth = edgeLabel.getWidth()+20;
		final EdgeGroup group = vertexA.getEdgesTo(vertexB);
		final Point2D middlePoint = group.getEdgeMidpoint(this);

		drawLine(gc,path);
		drawArrow(gc,path);

		shape.setPosition(middlePoint.getX()-recWidth/2.0-borderSize/2.0,middlePoint.getY()-edgeLabel.getHeight()/2.0-borderSize/2.0);
		shape.draw(canvas);

		super.drawLabel(gc);
	}
}
