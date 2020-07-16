package application.controllers;

import application.diff.DiffGroup;
import application.diff.GroupDiffType;
import application.diff.OntologyDiff;
import application.graph.GraphPane;
import application.graph.functionality.PanFunctionality;
import application.graph.functionality.VertexClickFunctionality;
import application.graph.functionality.VertexDragFunctionality;
import application.graph.functionality.ZoomFunctionality;
import application.graph.layout.ForceDirectedLayout;
import application.graph.vertex.GraphVertex;
import application.owlgraph.OntologyToGraphConverter;
import application.owlgraph.diff.DiffVertex;
import application.ui.*;
import application.util.OWLElementType;
import application.util.Utils;
import application.util.Vars;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainWindowController {

    @FXML
    private BorderPane mainPane;
    @FXML
    private GridPane elementData;

    @FXML
    private Separator leftSeparator;
    @FXML
    private Separator rightSeparator;

    //Left Tab Pane
    @FXML
    private ListView<IRIListCellData> classListView;
    @FXML
    private ListView<IRIListCellData> objectListView;
    @FXML
    private ListView<IRIListCellData> dataListView;
    @FXML
    private ListView<IRIListCellData> annotationListView;

    //Right Tab Pane
    @FXML
    private CheckBox minimalView;
    @FXML
    private Label fpsLabel;

    @FXML
    private TextField edgeSpacing;

    @FXML
    private TextField repulsionForce;
    @FXML
    private TextField attractionForce;
    @FXML
    private TextField springLength;
    @FXML
    private TextField timeStep;

    private FPSTracker fps;

    //Axiom Diff
    @FXML
    private ListView<AxiomDiffListCellData> axiomDiffListView;
    @FXML
    private TextField axiomFilter;
    private ListViewFilter<AxiomDiffListCellData> axiomListViewFilter;

    //Logging
    @FXML
    private TextArea loggingTextArea;

    //
    private GraphPane graphPane;
    private ForceDirectedLayout layout;

    private PanFunctionality panFunctionality;
    private ZoomFunctionality zoomFunctionality;
    private VertexDragFunctionality dragFunctionality;
    private VertexClickFunctionality clickFunctionality;

    private OntologyToGraphConverter ontologyConverter;

    @FXML
    private void initialize() {
        this.fps = new FPSTracker(fpsLabel.textProperty());

        this.graphPane = new GraphPane();
        this.layout = new ForceDirectedLayout(graphPane);
        this.panFunctionality = new PanFunctionality(graphPane);
        this.panFunctionality.applyFunctionality();
        this.zoomFunctionality = new ZoomFunctionality(graphPane);
        this.zoomFunctionality.applyFunctionality();
        this.dragFunctionality = new VertexDragFunctionality(graphPane, layout);
        this.dragFunctionality.applyFunctionality();
        this.clickFunctionality = new VertexClickFunctionality(this, graphPane, elementData);
        this.clickFunctionality.applyFunctionality();
        this.graphPane.setBackgroundColor(Vars.BACKGROUND_COLOR);

        this.mainPane.setCenter(graphPane);

        classListView.setCellFactory(listView -> new IRIListCell());
        objectListView.setCellFactory(listView -> new IRIListCell());
        dataListView.setCellFactory(listView -> new IRIListCell());
        annotationListView.setCellFactory(listView -> new IRIListCell());
        axiomDiffListView.setCellFactory(listView -> new AxiomDiffListCell());

        classListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> classSelected());
        objectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> objectSelected());
        dataListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> dataSelected());
        annotationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> annotationSelected());

        this.axiomListViewFilter = new ListViewFilter<>(axiomDiffListView, axiomFilter, (item, str) -> item.getAxiom().toString().toLowerCase().contains(str.toLowerCase()));

        //Setup logging
        Logger.getRootLogger().setLevel(Level.INFO);
        Logger.getLogger("org.semanticweb").setLevel(Level.WARN);

        Logger.getRootLogger().addAppender(new TextAreaAppender(loggingTextArea));
    }

    public void afterInitialize() {
        //getScene is null in initialize
        DoubleProperty maxWidth = new SimpleDoubleProperty();
        maxWidth.bind(mainPane.getScene().widthProperty().divide(2));
        DraggableSeparator.makeDraggable(leftSeparator, (Region) leftSeparator.getParent(), DraggableSeparator.GrowDirection.LR, new SimpleDoubleProperty(0), maxWidth);
        DraggableSeparator.makeDraggable(rightSeparator, (Region) rightSeparator.getParent(), DraggableSeparator.GrowDirection.RL, new SimpleDoubleProperty(0), maxWidth);
    }

    public OntologyToGraphConverter getOntologyConverter() {
        return this.ontologyConverter;
    }

    @FXML
    private void loadOntology() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("."));
        File ontologyFile = chooser.showOpenDialog(mainPane.getScene().getWindow());

        loadOntologyDiff(ontologyFile, ontologyFile);
    }

    @FXML
    private void createDiff() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("."));
        File ontologyFile1 = chooser.showOpenDialog(mainPane.getScene().getWindow());
        File ontologyFile2 = chooser.showOpenDialog(mainPane.getScene().getWindow());

        loadOntologyDiff(ontologyFile1, ontologyFile2);
    }

    // Additional method to obtain OWL files from GitHub
    @FXML
    private void createDiffGitHub() {
        ButtonType confirm = ButtonType.OK;
        ButtonType cancel = ButtonType.CANCEL;
        Alert alert = new Alert(Alert.AlertType.NONE, "", confirm, cancel);
        final Button btOk = (Button) alert.getDialogPane().lookupButton(confirm);
//        btOk.setDisable(true);

        alert.setTitle("Ontology Difference by GitHub URLs");
        alert.setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.getStylesheets().add(getClass().getResource("/views/error.css").toExternalForm());

        grid.add(new Label("GitHub Url 1"), 0, 0);
        TextField path1 = new TextField();
        path1.setPromptText("https://github.com/...");
        grid.add(path1, 1, 0);

        grid.add(new Label("GitHub Url 2"), 0, 1);
        TextField path2 = new TextField();
        path2.setPromptText("https://github.com/...");
        grid.add(path2, 1, 1);

        Label errorMessageLabel = new Label();

        grid.add(errorMessageLabel, 1, 2);
        errorMessageLabel.getStyleClass().add(Vars.LABEL_ERROR_STYLE);

        alert.getDialogPane().setContent(grid);

        btOk.addEventFilter(ActionEvent.ACTION, event -> {
//            System.out.println(path1.getText());
            if (path1.getText().trim().isEmpty()) {
                path1.getStyleClass().add(Vars.INPUT_ERROR_STYLE);
                event.consume();
            } else {
                path1.getStyleClass().remove(Vars.INPUT_ERROR_STYLE);
            }

            if (path2.getText().trim().isEmpty()) {
                path2.getStyleClass().add(Vars.INPUT_ERROR_STYLE);
                event.consume();
            } else {
                path2.getStyleClass().remove(Vars.INPUT_ERROR_STYLE);
            }
        });
        alert.showAndWait();


//		FileChooser chooser = new FileChooser();
//		chooser.setInitialDirectory(new File("."));
//		File ontologyFile1 = chooser.showOpenDialog(mainPane.getScene().getWindow());
//		File ontologyFile2 = chooser.showOpenDialog(mainPane.getScene().getWindow());

//		loadOntologyDiff(ontologyFile1,ontologyFile2);
    }

    @FXML
    private void showHelpModal() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Help");
        alert.setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.add(new Label("More information about this application can be seen in:"), 0, 0);

        Hyperlink link = new Hyperlink(Vars.WEBSITE_URL);

        link.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI(Vars.WEBSITE_URL));
            } catch (URISyntaxException e) {
                Logger.getRootLogger().error("Unable to create URI for link: " + Vars.WEBSITE_URL);
            } catch (IOException e) {
                Logger.getRootLogger().error("Unable to open link: " + Vars.WEBSITE_URL);
            }
        });
        grid.add(link, 0, 1);

        alert.getDialogPane().setContent(grid);

        alert.showAndWait();
    }

    private void loadOntologyDiff(File ontologyFile1, File ontologyFile2) {
        if (ontologyFile1 == null || ontologyFile2 == null) {
            return;
        }

        try {
            Logger.getRootLogger().info("Resetting UI");

            graphPane.reset();
            layout.stopLayout();

            Logger.getRootLogger().info("Loading ontologies...");

            OWLOntology o1 = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(ontologyFile1);
            OWLOntology o2 = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(ontologyFile2);
            OntologyDiff diff = new OntologyDiff(o1, o2);

            Logger.getRootLogger().info("Generating graph...");
            ontologyConverter = new OntologyToGraphConverter(o1, graphPane, diff);
            Logger.getRootLogger().info("Graph generated: " + graphPane.getVertices().size() + " Nodes, " + graphPane.getEdges().size() + " Edges");
            Logger.getRootLogger().info("Starting layout...");
            layout.applyLayout();

            Logger.getRootLogger().info("Populating UI...");
            initializeUI();
        } catch (Exception e) {
            Logger.getRootLogger().error(e.getMessage());
        }
    }

    @FXML
    private void initializeVars() {
        edgeSpacing.setText("" + Vars.EDGE_LABEL_SPACING);

        repulsionForce.setText("" + layout.getRunnable().getRepulsionStrength());
        attractionForce.setText("" + layout.getRunnable().getAttractionStrength());
        springLength.setText("" + layout.getRunnable().getSpringLength());
        Vars.VERTEX_SELF_EDGE_DISTANCE = layout.getRunnable().getSpringLength() / 2.0;
        timeStep.setText("" + layout.getRunnable().getTimeStep());
    }

    @FXML
    private void updateVars() {
        Vars.EDGE_LABEL_SPACING = Integer.parseInt(edgeSpacing.getText());

        layout.getRunnable().setRepulsionStrength(Double.parseDouble(repulsionForce.getText()));
        layout.getRunnable().setAttractionStrength(Double.parseDouble(attractionForce.getText()));
        layout.getRunnable().setSpringLength(Double.parseDouble(springLength.getText()));
        Vars.VERTEX_SELF_EDGE_DISTANCE = layout.getRunnable().getSpringLength() / 2.0;
        layout.getRunnable().setTimeStep(Double.parseDouble(timeStep.getText()));
    }

    @FXML
    private void resetAffine() {
        graphPane.resetAffine();
    }

    @FXML
    private void resetLayout() {
        layout.resetLayout();
        initializeVars();
    }

    @FXML
    private void stopLayout() {
        layout.stopLayout();
    }

    @FXML
    private void minimalViewClicked() {
        if (ontologyConverter != null) {
            boolean isSelected = minimalView.isSelected();

            if (isSelected) {
                graphPane.getVertices().forEach(graphVertex -> {
                    if (graphVertex instanceof DiffVertex == false) {
                        graphVertex.setVisible(false);
                    }
                });
                graphPane.getEdges().forEach(graphEdge -> {
                    if (graphEdge.getColor() != Vars.ADD_COLOR && graphEdge.getColor() != Vars.REMOVE_COLOR) {
                        graphEdge.setVisible(false);
                    } else {
                        graphEdge.getVertexA().setVisible(true);
                        graphEdge.getVertexB().setVisible(true);
                    }
                });

                graphPane.getVertices().forEach(graphVertex -> {
                    if (Utils.isUnionNode(graphVertex)) {
                        boolean containsDiff = graphVertex.getAllEdges().stream().anyMatch(graphEdge -> graphEdge.getColor() == Vars.ADD_COLOR || graphEdge.getColor() == Vars.REMOVE_COLOR);
                        if (containsDiff) {
                            graphVertex.getAllEdges().forEach(edge -> {
                                edge.setVisible(true);
                                edge.getVertexA().setVisible(true);
                                edge.getVertexB().setVisible(true);
                            });
                        }
                    }
                });
            } else {
                graphPane.getVertices().forEach(graphVertex -> graphVertex.setVisible(true));
                graphPane.getEdges().forEach(graphEdge -> graphEdge.setVisible(true));
            }
        }
    }

    private void initializeUI() {
        populateOntologyElementsMenu();
        populateAxiomMenu();
        initializeVars();

        minimalView.setSelected(false);
    }

    private void populateOntologyElementsMenu() {
        if (ontologyConverter == null) return;

        classListView.getItems().clear();
        objectListView.getItems().clear();
        dataListView.getItems().clear();
        annotationListView.getItems().clear();

        OntologyDiff diff = ontologyConverter.getDiff();

        BiFunction<Stream<? extends HasIRI>, OWLElementType, List<IRIListCellData>> cellListGenerator = (stream, type) -> stream
                .sorted(Comparator.comparing(o -> o.getIRI().getShortForm()))
                .map(hasIRI -> {
                    GroupDiffType diffType;
                    switch (type) {
                        case CLASS:
                            diffType = diff.getClassDiff().getDiffType((OWLClass) hasIRI);
                            break;
                        case OBJECT_PROPERTY:
                            diffType = diff.getObjDiff().getDiffType((OWLObjectProperty) hasIRI);
                            break;
                        case DATA_PROPERTY:
                            diffType = diff.getDataDiff().getDiffType((OWLDataProperty) hasIRI);
                            break;
                        case ANNOTATION_PROPERTY:
                            diffType = diff.getAnnotationsDiff().getDiffType((OWLAnnotationProperty) hasIRI);
                            break;
                        default:
                            diffType = null;
                            break;
                    }
                    return new IRIListCellData(hasIRI, Utils.getListCellClass(diffType));
                })
                .collect(Collectors.toList());

        classListView.getItems().addAll(
                cellListGenerator.apply(ontologyConverter.getClasses().stream(), OWLElementType.CLASS)
        );
        objectListView.getItems().addAll(
                cellListGenerator.apply(ontologyConverter.getObjectProperties().stream(), OWLElementType.OBJECT_PROPERTY)
        );
        dataListView.getItems().addAll(
                cellListGenerator.apply(ontologyConverter.getDataProperties().stream(), OWLElementType.DATA_PROPERTY)
        );
        annotationListView.getItems().addAll(
                cellListGenerator.apply(ontologyConverter.getAnnotationProperties().stream(), OWLElementType.ANNOTATION_PROPERTY)
        );
    }

    private void populateAxiomMenu() {
        axiomListViewFilter.stopFilter();
        axiomDiffListView.getItems().clear();

        DiffGroup<OWLAxiom> axiomDiff = ontologyConverter.getDiff().getAxiomsDiff();

        axiomDiff.getAllValuesWithDiff().stream()
                .sorted(Comparator.comparing(o -> o.getFirst().toString()))
                .forEach(axiomTuple -> {
                    AxiomDiffListCellData cellData;
                    switch (axiomTuple.getSecond()) {
                        case ADD:
                            cellData = new AxiomDiffListCellData(axiomTuple.getFirst(), Vars.LISTCELL_ADD_CSS_CLASS);
                            break;
                        case REMOVE:
                            cellData = new AxiomDiffListCellData(axiomTuple.getFirst(), Vars.LISTCELL_REMOVE_CSS_CLASS);
                            break;
                        default:
                            cellData = new AxiomDiffListCellData(axiomTuple.getFirst(), null);
                            break;
                    }
                    axiomDiffListView.getItems().add(cellData);
                });

        axiomListViewFilter.startFilter();
    }


    private void classSelected() {
        IRIListCellData cell = classListView.getSelectionModel().getSelectedItem();
        if (cell == null) return;

        GraphVertex vertex = ontologyConverter.getOWLClassVertex(cell.getValue().getIRI());
        Point2D position = vertex.getShape().getCenter();
        graphPane.getCanvas().centerTo(position.getX(), position.getY());

        clickFunctionality.clearElementData();
        clickFunctionality.displayOWLClass((OWLClass) cell.getValue());
    }

    private void objectSelected() {
        IRIListCellData cell = objectListView.getSelectionModel().getSelectedItem();
        if (cell == null) return;

        GraphVertex vertex = ontologyConverter.getOWLObjPropVertex(cell.getValue().getIRI());
        Point2D position = vertex.getShape().getCenter();
        graphPane.getCanvas().centerTo(position.getX(), position.getY());

        clickFunctionality.clearElementData();
        clickFunctionality.displayOWLObjectProperty((OWLObjectProperty) cell.getValue());
    }

    private void dataSelected() {
        IRIListCellData cell = dataListView.getSelectionModel().getSelectedItem();
        if (cell == null) return;

        GraphVertex vertex = ontologyConverter.getOWLDataPropertyVertex(cell.getValue().getIRI());
        Point2D position = vertex.getShape().getCenter();
        graphPane.getCanvas().centerTo(position.getX(), position.getY());

        clickFunctionality.clearElementData();
        clickFunctionality.displayOWLDataProperty((OWLDataProperty) cell.getValue());
    }

    private void annotationSelected() {
        IRIListCellData cell = annotationListView.getSelectionModel().getSelectedItem();
        if (cell == null) return;

        GraphVertex vertex = ontologyConverter.getOWLAnnotationPropertyVertex(cell.getValue().getIRI());
        Point2D position = vertex.getShape().getCenter();
        graphPane.getCanvas().centerTo(position.getX(), position.getY());

        clickFunctionality.clearElementData();
        clickFunctionality.displayOWLAnnotationProperty((OWLAnnotationProperty) cell.getValue());
    }
}
