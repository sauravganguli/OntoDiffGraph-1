package application.graph.edge;


import application.graph.ResizableCanvas;
import application.graph.vertex.GraphVertex;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.awt.geom.Line2D;
import java.util.Map;

public abstract class GraphEdge{

	protected Paint color = Color.BLACK;

	protected GraphVertex vertexA;
	protected GraphVertex vertexB;

	protected boolean visible = true;

	public GraphEdge(GraphVertex vertexA,GraphVertex vertexB){
		this.vertexA = vertexA;
		this.vertexB = vertexB;

		updateEdgeGroup();
	}

	public GraphVertex getVertexA(){
		return vertexA;
	}

	public GraphVertex getVertexB(){
		return vertexB;
	}

	public void setVertexA(GraphVertex vertexA){
		getEdgeGroup().getEdges().remove(this);
		this.vertexA = vertexA;
		updateEdgeGroup();
	}

	public void setVertexB(GraphVertex vertexB){
		getEdgeGroup().getEdges().remove(this);
		this.vertexB = vertexB;
		updateEdgeGroup();
	}

	public boolean isVisible(){
		return visible;
	}

	public void setVisible(boolean visible){
		this.visible = visible;
	}

	private void updateEdgeGroup(){
		if(this.vertexA == null || this.vertexB == null) return;
		final Map<GraphVertex,EdgeGroup> edgesA = this.vertexA.getEdges();
		final Map<GraphVertex,EdgeGroup> edgesB = this.vertexB.getEdges();

		if(!edgesA.containsKey(vertexB)){
			EdgeGroup eg = new EdgeGroup(this);
			edgesA.put(vertexB,eg);
			edgesB.put(vertexA,eg);
		}
		else{
			edgesA.get(vertexB).addEdge(this);
		}
	}

	public boolean intersects(Rectangle2D rectangle){
		Point2D[] path = getPath();
		double[][] rectangleLines = new double[][]{
				new double[]{rectangle.getMinX(),rectangle.getMinY(),rectangle.getMaxX(),rectangle.getMinY()},
				new double[]{rectangle.getMaxX(),rectangle.getMinY(),rectangle.getMaxX(),rectangle.getMaxY()},
				new double[]{rectangle.getMaxX(),rectangle.getMaxY(),rectangle.getMinX(),rectangle.getMaxY()},
				new double[]{rectangle.getMinX(),rectangle.getMaxY(),rectangle.getMinX(),rectangle.getMinY()}
		};

		// Check if the lines from the edge intersects the lines from the rectangle
		for(int i = 0; i < path.length-1; i++){
			Point2D point1 = path[i];
			Point2D point2 = path[i+1];

			if(rectangle.contains(point1) || rectangle.contains(point2)) return true;

			for(double[] rectangleLine: rectangleLines){
				if(Line2D.linesIntersect(point1.getX(),point1.getY(),point2.getX(),point2.getY(),rectangleLine[0],rectangleLine[1],rectangleLine[2],rectangleLine[3])){
					return true;
				}
			}
		}

		return false;
	}

	public Point2D getMidpoint(){
		return getEdgeGroup().getEdgeMidpoint(this);
	}

	public void setColor(String hexColor){
		if(hexColor == null){
			this.color = null;
			return;
		}
		this.color = Paint.valueOf(hexColor);
	}

	public void setColor(Paint color){
		this.color = color;
	}
	public Paint getColor() {
		return color;
	}

	public abstract Point2D[] getPath();
	public abstract EdgeGroup getEdgeGroup();
	public abstract void draw(ResizableCanvas canvas);
}
