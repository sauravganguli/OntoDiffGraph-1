package application.owlgraph;

import application.diff.GroupDiffType;
import application.graph.edge.GraphEdge;
import application.graph.edge.LabeledEdge;
import application.graph.edge.UnlabeledEdge;
import application.graph.shape.CircleShape;
import application.graph.shape.RectangleShape;
import application.graph.vertex.GraphVertex;
import application.owlgraph.diff.DiffLabeledEdge;
import application.owlgraph.diff.DiffVertex;
import application.util.Utils;
import application.util.Vars;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class GraphElements {

	//Shape creators
	private static CircleShape getCircleShape(double radius, Paint strokeColor, Paint fillColor){
		CircleShape shape = new CircleShape(radius);
		shape.setStrokeColor(strokeColor);
		shape.setFillColor(fillColor);
		return shape;
	}
	private static RectangleShape getRectangleShape(double width, double height, Paint strokeColor, Paint fillColor){
		RectangleShape shape = new RectangleShape(width,height);
		shape.setStrokeColor(strokeColor);
		shape.setFillColor(fillColor);
		return shape;
	}

	private static CircleShape getClassShape(){
		return getCircleShape(Vars.CLASS_RADIUS,Vars.CLASS_STROKE_COLOR,Vars.CLASS_FILL_COLOR);
	}
	private static CircleShape getExternalClassShape(){
		return getCircleShape(Vars.CLASS_RADIUS,Vars.CLASS_STROKE_COLOR,Paint.valueOf("#36c"));
	}
	private static RectangleShape getObjPropertyShape(){
		return getRectangleShape(Vars.OBJP_WIDHT,Vars.OBJP_HEIGHT,Vars.OBJP_STROKE_COLOR,Vars.OBJP_FILL_COLOR);
	}
	private static RectangleShape getDataPropertyShape(){
		return getRectangleShape(Vars.DATAP_WIDHT,Vars.DATAP_HEIGHT,Vars.DATAP_STROKE_COLOR,Vars.DATAP_FILL_COLOR);
	}
	private static RectangleShape getAnnotationPropertyShape(){
		return getRectangleShape(Vars.ANNP_WIDHT,Vars.ANNP_HEIGHT,Vars.ANNP_STROKE_COLOR,Vars.ANNP_FILL_COLOR);
	}
	private static RectangleShape getExternalPropertyShape(){
		return getRectangleShape(Vars.DATAP_WIDHT,Vars.DATAP_HEIGHT,Vars.DATAP_STROKE_COLOR,Paint.valueOf("#36c"));
	}
	private static RectangleShape getDatatypeShape(){
		return getRectangleShape(Vars.DATATYPE_WIDHT,Vars.DATATYPE_HEIGHT,Vars.DATATYPE_STROKE_COLOR,Vars.DATATYPE_FILL_COLOR);
	}

	private static CircleShape getCircleDiff(CircleShape base, int borderSize, GroupDiffType diffType){
		return getCircleShape(base.getRadius()+borderSize/2.0,Color.BLACK,Utils.getColorFromDiff(diffType));
	}
	private static RectangleShape getRectangleDiff(RectangleShape base, int borderSize, GroupDiffType diffType){
		return getRectangleShape(base.getWidth()+borderSize,base.getHeight()+borderSize,Color.BLACK,Utils.getColorFromDiff(diffType));
	}

	//Class vertices
	public static GraphVertex createClassVertex(String label,GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) return new GraphVertex(getClassShape(),label);

		CircleShape base = getClassShape();
		CircleShape diff = getCircleDiff(base,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(base,diff,label,Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	public static GraphVertex createExternalClassVertex(String label,GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) return new GraphVertex(getExternalClassShape(),label);

		CircleShape base = getExternalClassShape();
		CircleShape diff = getCircleDiff(base,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(base,diff,label,Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	public static GraphVertex createAnonClassVertex(GroupDiffType diffType){
		return createClassVertex(null,diffType);
	}


	public static GraphVertex createEquivalentClassVertex(GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) {
			RectangleShape shape = getRectangleShape(100,15,null,Color.WHITE);
			return new GraphVertex(shape,"equivalentClasses");
		}

		RectangleShape innerShape = getRectangleShape(100,15,null,Color.WHITE);
		RectangleShape outerShape = getRectangleDiff(innerShape,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(innerShape,outerShape,"equivalentClasses",Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	public static GraphVertex createDisjointClassVertex(GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) {
			RectangleShape shape = getRectangleShape(100,15,null,Color.WHITE);
			return new GraphVertex(shape,"disjointClasses");
		}

		RectangleShape innerShape = getRectangleShape(100,15,null,Color.WHITE);
		RectangleShape outerShape = getRectangleDiff(innerShape,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(innerShape,outerShape,"disjointClasses",Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	//Object property vertices
	public static GraphVertex createObjPropVertex(String label,GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) return new GraphVertex(getObjPropertyShape(),label);

		RectangleShape base = getObjPropertyShape();
		RectangleShape diff = getRectangleDiff(base,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(base,diff,label,Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	public static GraphVertex createAnonObjPropVertex(GroupDiffType diffType){
		return createObjPropVertex(null,diffType);
	}

	public static GraphVertex createEquivalentObjPropVertex(GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) {
			RectangleShape shape = getRectangleShape(100,15,null,Color.WHITE);
			return new GraphVertex(shape,"equivalentObjProp");
		}

		RectangleShape innerShape = getRectangleShape(100,15,null,Color.WHITE);
		RectangleShape outerShape = getRectangleDiff(innerShape,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(innerShape,outerShape,"equivalentObjProp",Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	public static GraphVertex createDisjointObjPropVertex(GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) {
			RectangleShape shape = getRectangleShape(100,15,null,Color.WHITE);
			return new GraphVertex(shape,"disjointObjProp");
		}

		RectangleShape innerShape = getRectangleShape(100,15,null,Color.WHITE);
		RectangleShape outerShape = getRectangleDiff(innerShape,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(innerShape,outerShape,"disjointObjProp",Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	//Data property vertices
	public static GraphVertex createDataPropVertex(String label,GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) return new GraphVertex(getDataPropertyShape(),label);

		RectangleShape base = getDataPropertyShape();
		RectangleShape diff = getRectangleDiff(base,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(base,diff,label,Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	public static GraphVertex createAnonDataPropVertex(GroupDiffType diffType){
		return createDataPropVertex(null,diffType);
	}

	public static GraphVertex createEquivalentDataPropVertex(GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) {
			RectangleShape shape = getRectangleShape(100,15,null,Color.WHITE);
			return new GraphVertex(shape,"equivalentDataProp");
		}

		RectangleShape innerShape = getRectangleShape(100,15,null,Color.WHITE);
		RectangleShape outerShape = getRectangleDiff(innerShape,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(innerShape,outerShape,"equivalentDataProp",Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	public static GraphVertex createDisjointDataPropVertex(GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) {
			RectangleShape shape = getRectangleShape(100,15,null,Color.WHITE);
			return new GraphVertex(shape,"disjointDataProp");
		}

		RectangleShape innerShape = getRectangleShape(100,15,null,Color.WHITE);
		RectangleShape outerShape = getRectangleDiff(innerShape,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(innerShape,outerShape,"disjointDataProp",Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	//Other vertices
	public static GraphVertex createExternalPropVertex(String label,GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) return new GraphVertex(getExternalPropertyShape(),label);

		RectangleShape base = getExternalPropertyShape();
		RectangleShape diff = getRectangleDiff(base,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(base,diff,label,Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	public static GraphVertex createAttributeVertex(String label,GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) {
			RectangleShape shape = getRectangleShape(100,15,Color.BLACK,Color.WHITE);
			return new GraphVertex(shape,label);
		}

		RectangleShape innerShape = getRectangleShape(100,15,Color.BLACK,Color.WHITE);
		RectangleShape outerShape = getRectangleDiff(innerShape,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(innerShape,outerShape,label,Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	public static GraphVertex createDatatypeVertex(String text){
		return new GraphVertex(getDatatypeShape(),text);
	}


	public static GraphVertex createUnionVertex(){
		CircleShape shape = getCircleShape(Vars.UNION_NODE_RADIUS,Vars.UNION_NODE_STROKE_COLOR,Vars.UNION_NODE_FILL_COLOR);
		return new GraphVertex(shape,Vars.UNION_NODE_LABEL);
	}

	public static GraphVertex createAnnotationPropertyVertex(String label, GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) return new GraphVertex(getAnnotationPropertyShape(),label);

		RectangleShape innerShape = getAnnotationPropertyShape();
		RectangleShape outerShape = getRectangleDiff(innerShape,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		return new DiffVertex<>(innerShape,outerShape,label,Vars.DIFF_VERTEX_BORDER_SIZE);
	}

	//Edges
	public static GraphEdge createUnlabeledEdge(GraphVertex origin,GraphVertex end){
		return new UnlabeledEdge(origin,end);
	}

	public static GraphEdge createUnlabeledEdge(GraphVertex origin,GraphVertex end,GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) return createUnlabeledEdge(origin,end);
		GraphEdge ge = new UnlabeledEdge(origin,end);
		ge.setColor(Utils.getColorFromDiff(diffType));
		return ge;
	}

	public static GraphEdge createLabeledEdge(GraphVertex origin,GraphVertex end, String label){
		LabeledEdge le = new LabeledEdge(origin,end,label);
		le.setLabelFillColor(Color.WHITE);
		le.setLabelStrokeColor(null);
		return le;
	}

	public static GraphEdge createLabeledEdge(GraphVertex origin,GraphVertex end,String label,GroupDiffType diffType){
		if(diffType == GroupDiffType.UNCHANGED) return createLabeledEdge(origin,end,label);

		RectangleShape innerShape = getRectangleShape(Utils.getTextSize(label).getWidth()+20,15,Color.BLACK,Color.WHITE);
		RectangleShape outerShape = getRectangleDiff(innerShape,Vars.DIFF_VERTEX_BORDER_SIZE,diffType);

		LabeledEdge le = new DiffLabeledEdge(origin,end,label,outerShape,Vars.DIFF_VERTEX_BORDER_SIZE);
		le.setLabelFillColor(Color.WHITE);
		le.setLabelStrokeColor(null);
		return le;
	}

}
