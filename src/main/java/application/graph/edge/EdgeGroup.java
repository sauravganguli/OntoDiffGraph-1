package application.graph.edge;

import application.graph.vertex.GraphVertex;
import application.util.Utils;
import application.util.Vars;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class EdgeGroup{
	private GraphVertex vertexA;
	private GraphVertex vertexB;

	private final List<GraphEdge> edges = new ArrayList<>();

	public EdgeGroup(GraphEdge ge){
		this.vertexA = ge.getVertexA();
		this.vertexB = ge.getVertexB();
		this.edges.add(ge);
	}

	public void addEdge(GraphEdge graphEdge){
		this.edges.add(graphEdge);
	}

	public void setVertexA(GraphVertex vertexA){
		this.vertexA = vertexA;
	}

	public void setVertexB(GraphVertex vertexB){
		this.vertexB = vertexB;
	}

	public List<GraphEdge> getEdges(){
		return this.edges;
	}

	// Can return null if no intersection is found!
	public Point2D getStartPoint(GraphEdge edge){
		return vertexA.getShape().getPointIntersection(getEdgeMidpoint(edge));
		//return vertexA.getShape().getCenter();
	}

	// Can return null if no intersection is found!
	public Point2D getEndPoint(GraphEdge edge){
		return vertexB.getShape().getPointIntersection(getEdgeMidpoint(edge));
		//return vertexB.getShape().getCenter();
	}

	public boolean isMultipleEdgeGroup(){
		return edges.size() > 1;
	}

	public Point2D getEdgeMidpoint(GraphEdge edge){
		int index = edges.indexOf(edge);
		int size = edges.size();

		if(vertexA == vertexB){
			double dist = 2*Math.PI/size;
			return vertexA.getShape().getCenter().add(
					Vars.VERTEX_SELF_EDGE_DISTANCE*Math.sin(dist*(index+1)),
					-Vars.VERTEX_SELF_EDGE_DISTANCE*Math.cos(dist*(index+1))
			);
		}
		else{
			return getCenter().add(getEdgeSpacing(edge));
		}
	}

	private Point2D getEdgeSpacing(GraphEdge edge){
		int index = edges.indexOf(edge);
		int size = edges.size();
		int isEven = (size % 2 == 0) ? 1 : 0;
		Point2D normal = Utils.getNormal(Utils.getVector(vertexA.getShape().getCenter(),vertexB.getShape().getCenter()));
		return new Point2D(normal.getX()*Vars.EDGE_LABEL_SPACING*(size/2-index-0.5*isEven),normal.getY()*Vars.EDGE_LABEL_SPACING*(size/2-index-0.5*isEven));
	}

	public Point2D getCenter(){
		return vertexA.getShape().getCenter().midpoint(vertexB.getShape().getCenter());
	}
}
