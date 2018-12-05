package edu.purdue.comradesgui.src.javafx;

import edu.purdue.comradesgui.src.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class FXTabChessEngineOptions extends Tab {

	private FXComradesGUI comradesGUI;

	private GridPane gridOptionList;

	public FXTabChessEngineOptions(FXComradesGUI comradesGUI) {

		this.comradesGUI = comradesGUI;

		BorderPane playerPane = new BorderPane();
		this.setContent(playerPane);
		this.setClosable(false);

		ScrollPane centerScrollPane = new ScrollPane();
		playerPane.setCenter(centerScrollPane);
		centerScrollPane.setFitToWidth(true);

		gridOptionList = new GridPane();
		centerScrollPane.setContent(gridOptionList);
		gridOptionList.setAlignment(Pos.TOP_LEFT);
		gridOptionList.setHgap(8);
		gridOptionList.setVgap(8);
		gridOptionList.setPadding(new Insets(8, 8, 8, 8));

		TextField chessEngineName = new TextField();
		Button saveEngineName = new Button("Save Name");

		ComboBox<ChessPlayer> chessEngineComboBox = new ComboBox<>();
		chessEngineComboBox.setItems(comradesGUI.getComradesMain().getPlayerList().filtered((player) -> (player instanceof ChessEngine)));
		chessEngineComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			gridOptionList.getChildren().clear();
			if(newVal != null) {
				if(newVal instanceof ChessEngine) {
					ChessEngine chessEngine = (ChessEngine) newVal;

					Label useInfiniteLabel = new Label("Do Infinite");
					CheckBox useInfiniteCheckBox = new CheckBox();

					useInfiniteLabel.setGraphic(useInfiniteCheckBox);
					useInfiniteLabel.setContentDisplay(ContentDisplay.RIGHT);

					useInfiniteCheckBox.selectedProperty().addListener(((observable, oldValue, newValue) -> {
						chessEngine.getGoCommandBuilder().setInfinite(newValue);
					}));

					gridOptionList.add(useInfiniteLabel, 0, 0);

					int rowIndex = 1;
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
					chessEngineName.setText(chessEngine.getPlayerName());
					saveEngineName.setOnAction((action) -> {
						chessEngine.setPlayerName(chessEngineName.getText());
						chessEngineComboBox.setItems(comradesGUI.getComradesMain().getPlayerList().filtered((player) -> (player instanceof ChessEngine)));
						comradesGUI.refreshPlayerCombos();
					});

					saveEngineName.setDisable(false);
					chessEngineName.setDisable(false);
				}
			}
			else {
				saveEngineName.setOnAction(null);
				saveEngineName.setDisable(true);
				chessEngineName.setDisable(true);
				chessEngineName.clear();
			}
		});

		saveEngineName.setDisable(true);
		chessEngineName.setDisable(true);

		Label selectLabel = new Label("Select Engine:");
		selectLabel.setGraphic(chessEngineComboBox);
		selectLabel.setContentDisplay(ContentDisplay.RIGHT);

		VBox topPlayerVBox = new VBox();
		topPlayerVBox.setPadding(new Insets(8, 8, 8, 8));
		topPlayerVBox.setSpacing(8);

		HBox selectEngineBox = new HBox();
		selectEngineBox.setAlignment(Pos.CENTER_LEFT);
		selectEngineBox.setSpacing(4);
		selectEngineBox.getChildren().add(selectLabel);
		topPlayerVBox.getChildren().add(selectEngineBox);

		HBox engineNameBox = new HBox();
		engineNameBox.setSpacing(4);
		engineNameBox.getChildren().add(chessEngineName);
		engineNameBox.getChildren().add(saveEngineName);
		topPlayerVBox.getChildren().add(engineNameBox);

		centerScrollPane.setContent(gridOptionList);

		playerPane.setTop(topPlayerVBox);
		playerPane.setCenter(centerScrollPane);
	}

	private ArrayList<Node> generateOptionNode(ChessEngineOption engineOption) {

		ArrayList<Node> nodes = new ArrayList<>();

		if(engineOption instanceof ChessEngineOptionSpin) {

			ChessEngineOptionSpin optionSpin = (ChessEngineOptionSpin) engineOption;

			Label label = new Label(optionSpin.getName());
			Slider slider = new Slider();
			TextField textField = new TextField();

			nodes.add(label);

			label.setTooltip(new Tooltip("[min: " + optionSpin.getMinValue() + ", max: " + optionSpin.getMaxValue() +"]"));


			slider.setMax(optionSpin.getMaxValue());
			slider.setMin(optionSpin.getMinValue());
			slider.setValue(optionSpin.getSpinValue());
			slider.setSnapToTicks(true);
			slider.valueProperty().addListener(((observable, oldValue, newValue) -> {

				textField.setText(newValue.intValue() + "");
			}));

			nodes.add(slider);

			textField.setText("" + optionSpin.getSpinValue());
			textField.setMaxWidth(60);
			textField.textProperty().addListener((observable, oldValue,  newValue) -> {

				if(newValue == null)
					textField.setText(oldValue);
				else if(newValue.isEmpty())
					textField.setText(oldValue);
				else if(!isNumber(newValue))
					textField.setText(oldValue);
				else {
					int val = Integer.parseInt(newValue);
					if(val > optionSpin.getMaxValue() || val < optionSpin.getMinValue()) {
						textField.setText(oldValue);
					}
					else if(val != (int) slider.getValue())
						slider.setValue(val);

				}
			});
			nodes.add(textField);

			Button button = new Button("Save");
			button.setOnAction((actionEvent) -> optionSpin.setSpinValue(Integer.parseInt(textField.getText())));
			nodes.add(button);
		}
		else if(engineOption instanceof ChessEngineOptionCheck) {

			ChessEngineOptionCheck optionCheck = (ChessEngineOptionCheck) engineOption;

			Label label = new Label(optionCheck.getName());
			CheckBox checkBox = new CheckBox();
			label.setGraphic(checkBox);
			label.setContentDisplay(ContentDisplay.RIGHT);

			checkBox.setSelected(optionCheck.isChecked());

			checkBox.selectedProperty().addListener(((observable, oldValue, newValue) -> optionCheck.setChecked(newValue)));

			nodes.add(label);
		}
		else if(engineOption instanceof ChessEngineOptionButton) {

			ChessEngineOptionButton optionButton = (ChessEngineOptionButton) engineOption;

			Label label = new Label(optionButton.getName());
			Button button = new Button("Send");
			label.setGraphic(button);
			label.setContentDisplay(ContentDisplay.RIGHT);

			button.setOnAction((actionEvent) -> optionButton.pressButton());

			nodes.add(label);
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


	private boolean isNumber(String in) {

		try {
			int num = Integer.parseInt(in);
			return true;
		}
		catch(Exception e) { }

		return false;
	}
}
