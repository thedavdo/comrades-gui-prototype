package edu.purdue.comradesgui.old;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.StringTokenizer;

public class CommunicatorOptionsFrameUCI implements ItemListener, ActionListener, ChangeListener, KeyListener {
	JTextField PATH_AREA;
	JFrame OPTIONS_FRAME;
	Communicator COMM;

	public CommunicatorOptionsFrameUCI(Communicator comm) {
		COMM = comm;
		ModifyDefaults();
	}

	public void IdLine(String S) {
		if (COMM.IS_NEW && S.startsWith("id name"))
			COMM.id = (S.substring(8));
	}

	public void ChangePath() {
		JFileChooser JFC = new JFileChooser(System.getProperty("user.dir"));
		int Value = JFC.showOpenDialog(JFC);
		if (Value != JFileChooser.APPROVE_OPTION)
			return;
		File FILE = JFC.getSelectedFile();
		ChangeValue("AbsolutePath", FILE.getAbsolutePath());
	}

	public void ChangeValue(String NAME, String VALUE) {
		int i;
		if (NAME.equals("AbsolutePath")) {
			PATH_AREA.setText(VALUE);
			COMM.path = (VALUE);
			return;
		}
		if (NAME.equals("MenuName")) {
			COMM.id = (VALUE);
			return;
		}
		if (NAME.equals("RunTimeOptions")) {
			COMM.RunTimeOptions = (VALUE);
			return;
		}
		for (i = 0; i < COMM.opt_count; i++)
			if (NAME.equals(COMM.OPT_NAME[i]))
				break;
		if (i >= COMM.opt_count)
			return;
		COMM.OPT_VALUE[i] = (VALUE);
	}

	public void itemStateChanged(ItemEvent item_evt) {
		JCheckBox checkBox = (JCheckBox) (item_evt.getItemSelectable());
		ChangeValue(checkBox.getText(), ("" + checkBox.isSelected()));
	}

	public void actionPerformed(ActionEvent act_evt) // JComboBox
	{
		String actionEvent = act_evt.getSource().getClass().getName();
		if (actionEvent.equals("javax.swing.JButton")) {
			JButton B = (JButton) (act_evt.getSource()); // incur ?
			if (act_evt.getActionCommand().equalsIgnoreCase("ChangePath"))
				ChangePath();
			if (act_evt.getActionCommand().equalsIgnoreCase("SAVE"))
				COMM.SaveCommunicator(OPTIONS_FRAME);
			if (act_evt.getActionCommand().equalsIgnoreCase("DELETE"))
				COMM.DeleteCommunicator(OPTIONS_FRAME);
		}
		if (actionEvent.equalsIgnoreCase("javax.swing.JComboBox")) {
			JComboBox B = (JComboBox) (act_evt.getSource());
			ChangeValue(act_evt.getActionCommand(), (String) (B.getSelectedItem()));
		}
	}

	public void keyTyped(KeyEvent key_evt) {
	}

	public void keyPressed(KeyEvent key_evt) {
	}

	public void keyReleased(KeyEvent key_evt) // unto "Released" (order)
	{
		JTextField textField = (JTextField) (key_evt.getSource());
		ChangeValue(textField.getName(), textField.getText());
	}

	public void stateChanged(ChangeEvent chg_evt) {
		JSpinner J = (JSpinner) (chg_evt.getSource());
		ChangeValue(J.getName(), J.getValue().toString());
	}

	public void BoxFill_UCI(Box BOX) // pained for parsage (UCI)
	{
		{
			Box boxLayout = new Box(BoxLayout.X_AXIS);
			JButton saveDefButton = new JButton("Save as Default");
			saveDefButton.setActionCommand("SAVE");
			saveDefButton.addActionListener(this);
			saveDefButton.setFont(saveDefButton.getFont().deriveFont(20.0f));
			saveDefButton.setAlignmentX(0.0f);
			boxLayout.add(saveDefButton);
			JLabel blankLabel = new JLabel("                    ");
			boxLayout.add(blankLabel);
			JButton deleteButton = new JButton("Delete Communicator");
			deleteButton.setActionCommand("DELETE");
			deleteButton.addActionListener(this);
			deleteButton.setFont(deleteButton.getFont().deriveFont(16.0f));
			deleteButton.setAlignmentX(1.0f);
			boxLayout.add(deleteButton);
			boxLayout.setAlignmentX(0.0f);
			BOX.add(boxLayout);
		}

		{
			Box boxLayout = new Box(BoxLayout.X_AXIS);
			JLabel menuNameLabel = new JLabel("MenuName:  ");
			boxLayout.add(menuNameLabel);
			JTextField textFieldComm = new JTextField(COMM.id);
			textFieldComm.setBackground(new Color(225, 215, 235));
			textFieldComm.setPreferredSize(new Dimension(250, 16));
			textFieldComm.setMaximumSize(new Dimension(250, 16));
			textFieldComm.setName("MenuName");
			textFieldComm.addKeyListener(this);
			boxLayout.add(textFieldComm);
			boxLayout.setAlignmentX(0.0f);
			BOX.add(boxLayout);
		}

		{
			Box boxLayout = new Box(BoxLayout.X_AXIS);
			JLabel pathLabel = new JLabel("Path: ");
			boxLayout.add(pathLabel);
			PATH_AREA = new JTextField(COMM.path);
			boxLayout.add(PATH_AREA);
			PATH_AREA.setBackground(new Color(225, 215, 235));
			PATH_AREA.setPreferredSize(new Dimension(400, 16));
			PATH_AREA.setMaximumSize(new Dimension(400, 16));
			PATH_AREA.setEditable(false);
			JButton changeButton = new JButton("Change");
			changeButton.setActionCommand("ChangePath");
			changeButton.addActionListener(this);
			boxLayout.add(changeButton);
			boxLayout.setAlignmentX(0.0f);
			BOX.add(boxLayout);
		}

		{
			Box boxLayout = new Box(BoxLayout.X_AXIS);
			JLabel runTimeLabel = new JLabel("RunTimeOptions: ");
			boxLayout.add(runTimeLabel);
			JTextField textFieldRunTime = new JTextField(COMM.RunTimeOptions);
			textFieldRunTime.setBackground(new Color(225, 215, 235));
			textFieldRunTime.setPreferredSize(new Dimension(250, 16));
			textFieldRunTime.setMaximumSize(new Dimension(250, 16));
			textFieldRunTime.setName("RunTimeOptions");
			textFieldRunTime.addKeyListener(this);
			boxLayout.add(textFieldRunTime);
			boxLayout.setAlignmentX(0.0f);
			BOX.add(boxLayout);
		}

		for (int i = 0; i < COMM.opt_count; i++) {
			StringBuffer SB = new StringBuffer(COMM.options[i]);
			int nameIndex = SB.indexOf(" name ");
			int typeIndex = SB.indexOf(" type ");
			String name = SB.substring(nameIndex + 6, typeIndex);
			StringTokenizer ST = new StringTokenizer(SB.substring(typeIndex + 6));
			COMM.OPT_NAME[i] = (name);
			String TYPE = ST.nextToken();
			if (TYPE.equals("button")) {
				Box boxLayout = new Box(BoxLayout.X_AXIS);
				JLabel labelPushOnLoad = new JLabel("PushOnLoad:  ");
				labelPushOnLoad.setBackground(new Color(235, 225, 215));
				boxLayout.add(labelPushOnLoad);
				JCheckBox checkBox = new JCheckBox(name);
				if (COMM.IS_NEW)
					checkBox.setSelected(false);
				else
					checkBox.setSelected(COMM.OPT_VALUE[i].equals("true"));
				checkBox.addItemListener(this);
				boxLayout.add(checkBox);
				boxLayout.setAlignmentX(0.0f);
				BOX.add(boxLayout);
				if (COMM.IS_NEW) {
					COMM.OPT_TYPE[i] = ("button");
					COMM.OPT_VALUE[i] = ("false");
				}
			}
			if (TYPE.equals("check")) {
				JCheckBox checkBox = new JCheckBox(name);
				if (COMM.IS_NEW) {
					if (ST.countTokens() < 2)
						checkBox.setSelected(false);
					else {
						ST.nextToken();
						if (ST.nextToken().equals("true"))
							checkBox.setSelected(true);
						else
							checkBox.setSelected(false);
					}
				}
				else
					checkBox.setSelected(COMM.OPT_VALUE[i].equals("true"));
				checkBox.addItemListener(this);
				checkBox.setAlignmentX(0.0f);
				BOX.add(checkBox);
				if (COMM.IS_NEW) {
					COMM.OPT_TYPE[i] = ("check");
					if (checkBox.isSelected())
						COMM.OPT_VALUE[i] =("true");
					else
						COMM.OPT_VALUE[i] = ("false");
				}
			}
			if (TYPE.equals("combo")) // ?
			{
				Box boxLayout = new Box(BoxLayout.X_AXIS);
				JLabel nameLabel = new JLabel(name + ":  ");
				JComboBox comboBox = new JComboBox();
				nameLabel.setBackground(new Color(195, 225, 255));
				boxLayout.add(nameLabel);
				String DEFAULT = null;

				String selectedOption = (COMM.options[i]);
				int varIndex = selectedOption.indexOf("var ");
				int defaultIndex = selectedOption.indexOf("default ");
				if(varIndex > defaultIndex)
					varIndex = defaultIndex;
				selectedOption = selectedOption.substring(varIndex);

				while(varIndex != -1) {
					selectedOption = selectedOption.substring(varIndex);
					if (selectedOption.startsWith("var "))
						selectedOption = selectedOption.substring(4);
					else {
						varIndex = selectedOption.indexOf("var ");
						String selStr;
						if (varIndex == -1)
							selStr = selectedOption;
						else
							selStr = selectedOption.substring(0, varIndex - 1);
						if (COMM.IS_NEW)
							DEFAULT = (selStr);
						else
							DEFAULT = (COMM.OPT_VALUE[i]);
					}
					varIndex = selectedOption.indexOf("var ");
					defaultIndex = selectedOption.indexOf("default ");
					if (varIndex == -1 && defaultIndex == -1)
						comboBox.addItem(selectedOption);
					if (varIndex == -1 && defaultIndex != -1)
						comboBox.addItem(selectedOption.substring(0, defaultIndex - 1));
					if (varIndex != -1 && defaultIndex == -1)
						comboBox.addItem(selectedOption.substring(0, varIndex - 1));
					if (varIndex != -1 && defaultIndex != -1) {
						if (varIndex > defaultIndex)
							varIndex = defaultIndex;
						comboBox.addItem(selectedOption.substring(0, varIndex - 1));
					}
				}
				comboBox.setActionCommand(name);
				comboBox.addActionListener(this);
				comboBox.setSelectedItem(DEFAULT);
				comboBox.setPreferredSize(new Dimension(250, 20));
				comboBox.setMaximumSize(new Dimension(250, 20));
				boxLayout.add(comboBox);
				boxLayout.setAlignmentX(0.0f);
				BOX.add(boxLayout);
				if (COMM.IS_NEW) {
					COMM.OPT_TYPE[i] =("combo");
					COMM.OPT_VALUE[i] = (DEFAULT);
				}
			}
			if (TYPE.equals("string")) {
				Box boxLayout = new Box(BoxLayout.X_AXIS);
				JLabel nameLabel = new JLabel(name + ":  ");
				nameLabel.setBackground(new Color(235, 225, 215));
				boxLayout.add(nameLabel);
				String DEFAULT;
				if (COMM.IS_NEW) {
					if (ST.countTokens() < 2)
						DEFAULT = ("NULL");
					else {
						ST.nextToken();
						DEFAULT = (ST.nextToken());
					}
				}
				else {
					if (ST.hasMoreTokens())
						ST.nextToken();
					if (ST.hasMoreTokens())
						ST.nextToken();
					DEFAULT = (COMM.OPT_VALUE[i]);
				}
				JTextField textFieldString = new JTextField(DEFAULT);
				textFieldString.setBackground(new Color(225, 215, 235));
				textFieldString.setPreferredSize(new Dimension(250, 16));
				textFieldString.setMaximumSize(new Dimension(250, 16));
				textFieldString.setName(name);
				textFieldString.addKeyListener(this);
				boxLayout.add(textFieldString);
				boxLayout.setAlignmentX(0.0f);
				BOX.add(boxLayout);
				if (COMM.IS_NEW) {
					COMM.OPT_TYPE[i] = ("string");
					COMM.OPT_VALUE[i] =(DEFAULT);
				}
			}
			if (TYPE.equals("spin")) {
				if (ST.countTokens() < 6)
					continue;
				Box boxLayout = new Box(BoxLayout.X_AXIS);
				JLabel spinLabel = new JLabel(name + ":  ");
				spinLabel.setBackground(new Color(205, 195, 235));
				boxLayout.add(spinLabel);
				Integer min = null, max = null, def = null;
				while (ST.hasMoreTokens()) {
					String S = ST.nextToken();
					Integer m = Integer.valueOf(ST.nextToken());
					if (S.equals("max"))
						max = m;
					if (S.equals("min"))
						min = m;
					if (S.equals("default"))
						def = m;
				}
				if (!COMM.IS_NEW)
					def = Integer.valueOf(COMM.OPT_VALUE[i]);
				if (def > max)
					def = (max);
				if (def < min)
					def = (min);
				SpinnerNumberModel MODEL = new SpinnerNumberModel(def, min, max, Integer.valueOf(1));
				JSpinner spinner = new JSpinner(MODEL);
				spinner.setName(name);
				spinner.addChangeListener(this);
				spinner.setPreferredSize(new Dimension(60, 16));
				spinner.setMaximumSize(new Dimension(60, 16));
				boxLayout.add(spinner);
				JLabel minMaxLabel = new JLabel("  min " + min + " max " + max);
				minMaxLabel.setFont(new Font("Monospaced", 0, 12));
				boxLayout.add(minMaxLabel);
				boxLayout.setAlignmentX(0.0f);
				BOX.add(boxLayout);
				if (COMM.IS_NEW) {
					COMM.OPT_TYPE[i] =("spin");
					COMM.OPT_VALUE[i] = (def.toString());
				}
			}
		}
	}

	public void AddOptions_UCI(JFrame OPT) {
		Box BOX = new Box(BoxLayout.Y_AXIS);
		BoxFill_UCI(BOX);
		JScrollPane OPT_JSP = new JScrollPane(BOX);
		OPT_JSP.getViewport().add(BOX);
		OPT.add(OPT_JSP);
	}

	public void ModifyDefaults() {
		String defaultString;
		if (COMM.IS_NEW)
			defaultString = "";
		else
			defaultString = COMM.id;
		OPTIONS_FRAME = new JFrame("Default Options " + defaultString);
		AddOptions_UCI(OPTIONS_FRAME);
		OPTIONS_FRAME.setBackground(Color.lightGray);
		OPTIONS_FRAME.pack();
		OPTIONS_FRAME.setSize(600, 600); // demand
		OPTIONS_FRAME.setResizable(false);
		OPTIONS_FRAME.setVisible(true);
	}
}