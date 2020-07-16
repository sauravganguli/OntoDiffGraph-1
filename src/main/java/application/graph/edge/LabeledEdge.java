package application.graph.edge;

import application.graph.ResizableCanvas;
import application.graph.vertex.GraphVertex;
import application.graph.vertex.NodeText;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class LabeledEdge extends UnlabeledEdge{

	protected Paint labelFillColor = Color.WHITE;
	protected Paint labelStrokeColor = Color.BLACK;
	protected NodeText edgeLabel;

	public LabeledEdge(GraphVertex nodeA,GraphVertex nodeB,String label){
		super(nodeA,nodeB);
		this.edgeLabel = new NodeText(label,200);//TODO change limit
	}

	public void setLabelFillColor(Paint color){
		this.labelFillColor = color;
	}

	public void setLabelStrokeColor(Paint color){
		this.labelStrokeColor = color;
	}

	public NodeText getLabel(){
		return this.edgeLabel;
	}

	@Override public void draw(ResizableCanvas canvas){
		super.draw(canvas);
		drawLabel(canvas.getGraphicsContext2D());
	}

	public void drawLabel(GraphicsContext gc){
		final EdgeGroup group = vertexA.getEdgesTo(vertexB);
		final Point2D middlePoint = group.getEdgeMidpoint(this);

		double recWidth = edgeLabel.getWidth() + 20.0;

		if(labelStrokeColor != null){
			gc.setStroke(labelStrokeColor);
			gc.strokeRect(middlePoint.getX() - recWidth / 2,middlePoint.getY() - edgeLabel.getHeight() / 2,recWidth,edgeLabel.getHeight());
		}
		if(labelFillColor != null){
			gc.setFill(labelFillColor);
			gc.fillRect(middlePoint.getX() - recWidth / 2,middlePoint.getY() - edgeLabel.getHeight() / 2,recWidth,edgeLabel.getHeight());
		}

		edgeLabel.draw(gc,middlePoint.getX(),middlePoint.getY());
	}
}
