package application.util;

import application.diff.GroupDiffType;
import application.graph.shape.CircleShape;
import application.graph.vertex.GraphVertex;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class Utils{

	public static double clamp(double num, double min, double max){
		if(num > max) return max;
		if(num < min) return min;
		return num;
	}

	public static Bounds getTextSize(String text){
		Text txt = new Text(text);
		txt.applyCss();
		return txt.getLayoutBounds();
	}

	//TODO needs improvement
	public static boolean isImported(OWLOntology ontology, OWLEntity entity) throws IllegalArgumentException{
		Optional<IRI> iri = ontology.getOntologyID().getOntologyIRI();
		String entityIRI = entity.getIRI().toString();

		URI ontologyNS;
		if(iri.isPresent()){
			ontologyNS = iri.get().toURI();
		}
		else{
			ontologyNS = URI.create("");
		}

		return !entityIRI.contains(ontologyNS.toString());
	}

	public static boolean isUnionNode(GraphVertex vertex){
		if(vertex == null) return false;
		return vertex.getShape() instanceof CircleShape &&
				vertex.getLabel() != null &&
				Vars.UNION_NODE_LABEL.equals(vertex.getLabel().getFullText()) &&
				((CircleShape) vertex.getShape()).getRadius() == Vars.UNION_NODE_RADIUS &&
				vertex.getShape().getStrokeColor() == Vars.UNION_NODE_STROKE_COLOR &&
				vertex.getShape().getFillColor() == Vars.UNION_NODE_FILL_COLOR;
	}

	public static Paint getColorFromDiff(GroupDiffType type){
		if(type == null) return null;
		switch(type){
			case ADD: return Vars.ADD_COLOR;
			case REMOVE: return Vars.REMOVE_COLOR;
			case UNCHANGED: return null;
			default: return null;
		}
	}

	public static Color getUIColorFromDiff(GroupDiffType type){
		if(type == null) return null;
		switch(type){
			case ADD: return Vars.UI_ADD_COLOR;
			case REMOVE: return Vars.UI_REMOVE_COLOR;
			case UNCHANGED: return null;
			default: return null;
		}
	}

	public static String getListCellClass(GroupDiffType type){
		if(type == null) return null;
		switch(type){
			case ADD: return Vars.LISTCELL_ADD_CSS_CLASS;
			case REMOVE: return Vars.LISTCELL_REMOVE_CSS_CLASS;
			case UNCHANGED: return null;
			default: return null;
		}
	}

	public static void drawTriangle(GraphicsContext gc, double angle, int sideSize, Point2D topCorner, Point2D direction){
		Point2D pointA = topCorner.add(rotateVector(direction,Math.PI-angle/2).multiply(sideSize));
		Point2D pointB = topCorner.add(rotateVector(direction,-Math.PI+angle/2).multiply(sideSize));

		gc.fillPolygon(
				new double[]{
						topCorner.getX(),
						pointA.getX(),
						pointB.getX()
				},
				new double[]{
						topCorner.getY(),
						pointA.getY(),
						pointB.getY()
				},
				3
		);
	}

	public static Point2D rotateVector(Point2D vector, double radians){
		return new Point2D(
				vector.getX()*Math.cos(radians)-vector.getY()*Math.sin(radians),
				vector.getX()*Math.sin(radians)+vector.getY()*Math.cos(radians)
		);
	}

	public static Point2D getVector(Point2D start, Point2D end){
		return end.subtract(start).normalize();
	}

	public static Point2D getNormal(Point2D vector){
		return new Point2D(-vector.getY(),vector.getX());
	}

	public static double getAngle(Point2D vector){
		double res = -Math.atan2(vector.getY(),vector.getX());

		res = Math.toDegrees(res);
		if(res < 0){
			res += 360;
		}

		return res;
	}

	public static Point2D getLineIntersection(Point2D startLineA,Point2D endLineA,Point2D startLineB,Point2D endLineB){
		Point2D vectorA = endLineA.subtract(startLineA);
		Point2D vectorB = endLineB.subtract(startLineB);

		double sdiv = -vectorB.getX() * vectorA.getY() + vectorA.getX() * vectorB.getY();

		if(sdiv == 0) return null;

		double s = (-vectorA.getY() * (startLineA.getX() - startLineB.getX()) + vectorA.getX() * (startLineA.getY() - startLineB.getY())) / sdiv;
		double t = ( vectorB.getX() * (startLineA.getY() - startLineB.getY()) - vectorB.getY() * (startLineA.getX() - startLineB.getX())) / sdiv;

		if (s >= 0.0 && s <= 1.0 && t >= 0.0 && t <= 1.0){
			return new Point2D(
					startLineA.getX() + (t * vectorA.getX()),
					startLineA.getY() + (t * vectorA.getY())
			);
		}

		return null;
	}
}
