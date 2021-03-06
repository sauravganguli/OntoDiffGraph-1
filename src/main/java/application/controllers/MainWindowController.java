package application.controllers;

import application.diff.DiffGroup;
import application.diff.FileDiff;
import application.diff.GroupDiffType;
import application.diff.OntologyDiff;
import application.github.Credentials;
import application.github.GitHubRequest;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    // File Diff
    @FXML
    private ListView<FileDiffListData> fileDiffListView;

    // File Diff Filter
    @FXML
    private TextField fileFilter;
    private ListViewFilter<FileDiffListData> fileListViewFilter;

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
    private FileDiff fileDiff;

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
        fileDiffListView.setCellFactory(listView -> new FileDiffList());

        classListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> classSelected());
        objectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> objectSelected());
        dataListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> dataSelected());
        annotationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> annotationSelected());

        this.axiomListViewFilter = new ListViewFilter<>(axiomDiffListView, axiomFilter, (item, str) -> item.getAxiom().toString().toLowerCase().contains(str.toLowerCase()));

        // File list filter
        this.fileListViewFilter = new ListViewFilter<>(fileDiffListView, fileFilter, (item, str) -> item.getFile().getName().toLowerCase().contains(str.toLowerCase()));

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

    // Validating input data and stiling them
    private boolean isValid(TextField textField){
        if (textField.getText().trim().isEmpty()) {
            textField.getStyleClass().add(Vars.INPUT_ERROR_STYLE);
            return false;
        } else {
            textField.getStyleClass().remove(Vars.INPUT_ERROR_STYLE);
            return true;
        }
    }

    // Input GitHub data dialog
    private void dialogInputData(Credentials credentials){
        ButtonType confirm = ButtonType.OK;
        ButtonType cancel = ButtonType.CANCEL;
        Button browseFile = new Button("Browse File");
        Button browseFolder = new Button("Browse Folder");

        Alert alert = new Alert(Alert.AlertType.NONE, "", confirm, cancel);
        final Button btOk = (Button) alert.getDialogPane().lookupButton(confirm);
        final Button btCl = (Button) alert.getDialogPane().lookupButton(cancel);

        alert.setTitle("GitHub file location");
        alert.setHeaderText("Set credentials");
        alert.initStyle(StageStyle.UNDECORATED);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.getStylesheets().add(getClass().getResource("/views/error.css").toExternalForm());

        grid.add(new Label("User Name"), 0, 0);
        TextField path1 = new TextField();
        path1.setPromptText("Input user name");
        grid.add(path1, 1, 0);

        grid.add(new Label("Repository \n Name"), 0, 1);
        TextField path2 = new TextField();
        path2.setPromptText("Input repository name");
        grid.add(path2, 1, 1);

        grid.add(new Label("Ontology \n Name"), 0, 2);
        TextField path3 = new TextField();
        path3.setPromptText("Input ontology name");
        path3.textProperty().addListener(
                (observable -> {
                    if(!path3.getText().isEmpty()) { browseFolder.setDisable(true); browseFile.setDisable(false);}
                    else { browseFolder.setDisable(false); browseFile.setDisable(true);}
                })
        );
        path3.setTooltip(new Tooltip("If you don't specified \n " +
                "this text field \n " +
                "then you choose \n " +
                "check differences \n" +
                "by file list"));
        grid.add(path3, 1, 2);

        // Local File Path with label and button
        grid.add(new Label("File Path"), 0, 3);
        Label pathLabel = new Label("");
        pathLabel.setMaxWidth(160);
        browseFile.setDisable(true);
        grid.add(pathLabel, 1, 3);
        grid.add(browseFile, 2, 3);

        // Local Directory Path with label and button
        grid.add(new Label("Folder Path"), 0, 4);
        Label folderLabel = new Label("");
        folderLabel.setMaxWidth(160);
        browseFolder.setDisable(false);
        grid.add(folderLabel, 1, 4);
        grid.add(browseFolder, 2, 4);

        // Error Label
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add(Vars.LABEL_ERROR_STYLE);
        grid.add(errorLabel, 1, 5);

        alert.getDialogPane().setContent(grid);

        // If file not found exception
        if(!credentials.getErrorMessage().equals("")){
            errorLabel.setText(credentials.getErrorMessage());
        }

        browseFile.addEventHandler(ActionEvent.ACTION, event -> {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Ontologies (*.owl/rdf/ttl)", "*.owl", "*.rdf", "*.ttl");
            chooser.getExtensionFilters().add(extensionFilter);
            chooser.setInitialDirectory(new File("."));
            File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
            if(file != null){
                credentials.setLocalFile(file);
                pathLabel.setText(file.getPath());
            }
        });

        browseFolder.addEventHandler(ActionEvent.ACTION, event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setInitialDirectory(new File("."));
            File selectedDirectory = chooser.showDialog(mainPane.getScene().getWindow());
            if(selectedDirectory != null){
                File dir = new File(selectedDirectory.getPath());
                // add files with ontological extensions
                String[] extensions = new String[]{"owl", "rdf", "ttl"};
                credentials.setFileList(new ArrayList<>(FileUtils.listFiles(dir, extensions, true)));
                folderLabel.setText(selectedDirectory.getAbsolutePath());
            }
        });

        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (!isValid(path1)) event.consume();
            else credentials.setUserName(path1.getText());

            if (!isValid(path2)) event.consume();
            else credentials.setUserRepo(path2.getText());

            if(!path3.getText().isEmpty())
                credentials.setOntoName(path3.getText());
            else credentials.setOntoName(".owl,.rdf,.ttl");
//            if (!isValid(path3)) event.consume();
//            else credentials.setOntoName(path3.getText());

            if (pathLabel.getText().isEmpty() && folderLabel.getText().isEmpty()){
                event.consume();
                errorLabel.setText("Choose local path!");
            }
        });

        // If window was cancelled, set values as "" instead of null
        btCl.addEventHandler(ActionEvent.ACTION, event -> {
            credentials.setUserName("");
            credentials.setUserRepo("");
            credentials.setOntoName("");
            credentials.setLocalFile(null);
            credentials.setFileList(new ArrayList<>());
        });

        alert.showAndWait();
    }

    // Additional method to obtain OWL files from GitHub
    @FXML
    private void createDiffGitHub() {

        Credentials credentials = new Credentials();
        GitHubRequest gitHubRequest = new GitHubRequest();

        // Initialize Credentials
        dialogInputData(credentials);

        if(!credentials.isValid()){
            Logger.getRootLogger().error("Wrong GitHub local file copy credentials!");
            return;
        }

        // Switch between ontology file and folder diff
        if(credentials.getFileList() != null){
            List<File> localFileList = credentials.getFileList();
            List<File> gitHubFileList = gitHubRequest.getGithubLocalFolderFileList(credentials);
            fileDiff = new FileDiff(localFileList, gitHubFileList);
            populateFileMenu();

            return;
        }

        File githubFile = gitHubRequest.getGithubLocalFilePath(credentials);
        if (githubFile == null){
            credentials.setErrorMessage("File or credentials are invalid");
            dialogInputData(credentials);
        }


        File localFile = credentials.getLocalFile();
        loadOntologyDiff(githubFile,localFile);
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



    private void populateFileMenu() {
        fileListViewFilter.stopFilter();
        fileDiffListView.getItems().clear();

        // Generate list of changes
        DiffGroup<File> fileDiffListDataDiffGroup = fileDiff.compareFilesList();

        fileDiffListDataDiffGroup.getAllValuesWithDiff().stream()
                .sorted(Comparator.comparing(o -> o.getFirst().getName()))
                .forEach(fileTuple -> {
                    FileDiffListData fileListData;
                    switch (fileTuple.getSecond()) {
                        case ADD:
                            fileListData = new FileDiffListData(fileTuple.getFirst(), Vars.LISTCELL_ADD_CSS_CLASS);
                            break;
                        case REMOVE:
                            fileListData = new FileDiffListData(fileTuple.getFirst(), Vars.LISTCELL_REMOVE_CSS_CLASS);
                            break;
                        default:
                            fileListData = new FileDiffListData(fileTuple.getFirst(), null);
                            break;
                    }
                    fileDiffListView.getItems().add(fileListData);
                });

        fileListViewFilter.startFilter();
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
