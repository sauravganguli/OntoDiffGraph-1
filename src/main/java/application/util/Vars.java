package application.util;


import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Vars{

	//App Vars
	public static final String TITLE = "OntoDiffGraph";
	public static final double FPS = 1.0/60.0;
	public static final String WEBSITE_URL = "www.di.uminho.pt/~gepl/OntoDiffGraph/";

	//Layout Vars
	public static int EDGE_LABEL_SPACING = 50;
	public static final int SELF_EDGE_LABEL_SPACING = 10;
	public static double VERTEX_SELF_EDGE_DISTANCE = 100;

	//Graph Vars
	//public static final Paint BACKGROUND_COLOR = Color.WHITE;
	public static final Paint BACKGROUND_COLOR = Paint.valueOf("#ECF0F1");

	//Graph Element Vars
	public static final int CLASS_RADIUS = 25;
	public static final Paint CLASS_FILL_COLOR = Paint.valueOf("#ACF");
	public static final Paint CLASS_STROKE_COLOR = Color.BLACK;

	public static final int UNION_NODE_RADIUS = 10;
	public static final Paint UNION_NODE_FILL_COLOR = Color.WHITE;
	public static final Paint UNION_NODE_STROKE_COLOR = Color.BLACK;
	public static final String UNION_NODE_LABEL = "∪";

	public static final int OBJP_WIDHT = 50;
	public static final int OBJP_HEIGHT = 15;
	public static final Paint OBJP_FILL_COLOR = Paint.valueOf("#ACF");
	public static final Paint OBJP_STROKE_COLOR = null;

	public static final int DATAP_WIDHT = 50;
	public static final int DATAP_HEIGHT = 15;
	public static final Paint DATAP_FILL_COLOR = Paint.valueOf("#9C6");
	public static final Paint DATAP_STROKE_COLOR = null;

	public static final int ANNP_WIDHT = 50;
	public static final int ANNP_HEIGHT = 15;
	public static final Paint ANNP_FILL_COLOR = Color.YELLOW;
	public static final Paint ANNP_STROKE_COLOR = null;

	public static final int DATATYPE_WIDHT = 50;
	public static final int DATATYPE_HEIGHT = 15;
	public static final Paint DATATYPE_FILL_COLOR = Paint.valueOf("#FC3");
	public static final Paint DATATYPE_STROKE_COLOR = Color.BLACK;

	//Diff Vars
	public static final Color ADD_COLOR = Color.color(0,1,0);//Paint.valueOf("#00ff00");
	public static final Color REMOVE_COLOR = Color.color(1,0,0);//Paint.valueOf("#ff0000");
	public static final int DIFF_VERTEX_BORDER_SIZE = 5;

	//Ontology Vars
	public static final String LITERAL_URI = "http://www.w3.org/2000/01/rdf-schema#Literal";
	public static final String THING_URI = "http://www.w3.org/2002/07/owl#Thing";

	//CSS Classes
	public static final Color UI_ADD_COLOR = Color.color(0,1,0,0.7);
	public static final Color UI_REMOVE_COLOR = Color.color(1,0,0,0.7);

	public static final String LISTCELL_ADD_CSS_CLASS = "element-added";
	public static final String LISTCELL_EDIT_CSS_CLASS = "element-edit";
	public static final String LISTCELL_REMOVE_CSS_CLASS = "element-removed";

	public static final String LABEL_ERROR_STYLE = "errorLabel";
	public static final String INPUT_ERROR_STYLE = "error";

	/* GitHub Credentials. It is IMPORTANT to note, that,
	 *	if you will not use this token one year, it might to be deprecated
	 *  In that case it is needed to simple generate new one (17.07.2017 - generated token date)
	 */
	public static final String GITHUB_TOKEN_PARAMETER_NAME = "Authorization";
	public static String GITHUB_TOKEN = Utils.getToken();

}
