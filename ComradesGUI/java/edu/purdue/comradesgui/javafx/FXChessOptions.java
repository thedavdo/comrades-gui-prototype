package edu.purdue.comradesgui.javafx;

import edu.purdue.comradesgui.src.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class FXChessOptions extends Application {


	private ComradesMain comradesMain;

	private ComboBox<ChessPlayer> playerComboBox;

	public FXChessOptions(ComradesMain comradesMain) {
		this.comradesMain = comradesMain;
	}

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("Options");
		primaryStage.setResizable(false);

		TabPane tabPane = new TabPane();
		Scene scene = new Scene(tabPane, 500, 650);

		Tab playerTab = new Tab("Engines");
		tabPane.getTabs().add(playerTab);
		BorderPane playerPane = new BorderPane();
		playerTab.setContent(playerPane);
		playerTab.setClosable(false);

		ScrollPane playerScrollPane = new ScrollPane();
		playerScrollPane.setFitToWidth(true);
		GridPane gridOptionList = new GridPane();

		playerPane.setPadding(new Insets(16, 16, 16, 16));

		gridOptionList.setAlignment(Pos.TOP_LEFT);
		gridOptionList.setHgap(8);
		gridOptionList.setVgap(8);
		gridOptionList.setPadding(new Insets(8, 8, 8, 8));

		playerComboBox = new ComboBox<>();

		playerComboBox.setItems(comradesMain.getPlayerList().filtered((player) -> (player instanceof ChessEngine)));

		playerComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {

			gridOptionList.getChildren().clear();
			if(newVal != null) {
				if(newVal instanceof ChessEngine) {
					ChessEngine chessEngine = (ChessEngine) newVal;
					int rowIndex = 0;
					for(ChessEngineOption option : chessEngine.getOptionList()) {
						ArrayList<Node> optionNodes = generateOptionNode(option);
						if(option != null) {
							if(!optionNodes.isEmpty()) {
								for(int i = 0; i < optionNodes.size(); i++)
									gridOptionList.add(optionNodes.get(i), i, rowIndex);
								rowIndex++;
							}
						}
					}
				}
			}
		});

		HBox topPlayerHBox = new HBox();
		topPlayerHBox.setAlignment(Pos.CENTER_LEFT);
		topPlayerHBox.setPadding(new Insets(8, 8, 8, 8));

		topPlayerHBox.getChildren().add(new Text("Select Engine: "));
		topPlayerHBox.getChildren().add(playerComboBox);

		playerScrollPane.setContent(gridOptionList);

		playerPane.setTop(topPlayerHBox);
		playerPane.setCenter(playerScrollPane);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private ArrayList<Node> generateOptionNode(ChessEngineOption engineOption) {

		ArrayList<Node> nodes = new ArrayList<>();

		if(engineOption instanceof ChessEngineOptionSpin) {

			ChessEngineOptionSpin optionSpin = (ChessEngineOptionSpin) engineOption;

			Label label = new Label(optionSpin.getName());
			nodes.add(label);

			TextField textField = new TextField();
			textField.setText("" + optionSpin.getSpinValue());
			textField.textProperty().addListener((observable, oldValue,  newValue) -> {

				if(newValue == null)
					textField.setText(oldValue);
				else if(newValue.isEmpty())
					textField.setText(oldValue);
				else if(!newValue.matches("\\d*"))
					textField.setText(oldValue);
				else {
					int val = Integer.parseInt(newValue);
					if(val > optionSpin.getMaxValue() || val < optionSpin.getMinValue())
						textField.setText(oldValue);
				}
			});
			nodes.add(textField);

			Button button = new Button("Save");
			button.setOnAction((actionEvent) -> optionSpin.setSpinValue(Integer.parseInt(textField.getText())));
			nodes.add(button);
		}
		else if(engineOption instanceof ChessEngineOptionCheck) {

			ChessEngineOptionCheck optionCheck = (ChessEngineOptionCheck) engineOption;
			CheckBox checkBox = new CheckBox(optionCheck.getName());

			checkBox.setSelected(optionCheck.isChecked());

			checkBox.selectedProperty().addListener(((observable, oldValue, newValue) -> optionCheck.setChecked(newValue)));

			nodes.add(checkBox);
		}
		else if(engineOption instanceof ChessEngineOptionButton) {

			ChessEngineOptionButton optionButton = (ChessEngineOptionButton) engineOption;

			Label label = new Label(optionButton.getName());
			nodes.add(label);

			Button button = new Button("Send");
			button.setOnAction((actionEvent) -> optionButton.pressButton());

			nodes.add(button);
		}
		else if(engineOption instanceof ChessEngineOptionString) {

			ChessEngineOptionString optionString = (ChessEngineOptionString) engineOption;

			Label label = new Label(optionString.getName());
			nodes.add(label);

			TextField textField = new TextField();
			textField.setText(optionString.getStringValue());

			nodes.add(textField);

			Button button = new Button("Save");
			button.setOnAction((actionEvent) -> optionString.setStringValue(textField.getText()));
			nodes.add(button);
		}

		return nodes;
	}
}