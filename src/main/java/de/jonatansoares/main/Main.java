package de.jonatansoares.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

public class Main implements ActionListener {

	JFrame jFrame;
	JLabel sourceFilesLabel;
	JLabel targetDirectoryLabel;
	JTextField sourceFilesTextField;
	JTextField targetFileDirectoryTextField;
	JButton searchButtonSourceFiles;
	JButton searchButtonTargetDirectory;
	JButton mergeFilesButton;
	List<String> fileDirectories = new ArrayList<String>();

	Main() {
		jFrame = new JFrame("Merge PDF App");
		sourceFilesLabel = new JLabel();
		sourceFilesLabel.setText("Select the PDF source files: ");
		sourceFilesLabel.setBounds(10, 10, 180, 50);

		searchButtonSourceFiles = new JButton();
		searchButtonSourceFiles.setActionCommand(Action.SEARCH_SOURCE.name());
		searchButtonSourceFiles.setText("...");
		searchButtonSourceFiles.setBounds(250, 50, 30, 30);
		searchButtonSourceFiles.addActionListener(this);

		sourceFilesTextField = new JTextField();
		sourceFilesTextField.setEditable(false);
		sourceFilesTextField.setBounds(10, 50, 235, 30);

		targetDirectoryLabel = new JLabel();
		targetDirectoryLabel.setText("Choose the target directory: ");
		targetDirectoryLabel.setBounds(10, 100, 180, 50);

		searchButtonTargetDirectory = new JButton();
		searchButtonTargetDirectory.setActionCommand(Action.SEARCH_TARGET.name());
		searchButtonTargetDirectory.setText("...");
		searchButtonTargetDirectory.setBounds(250, 140, 30, 30);
		searchButtonTargetDirectory.addActionListener(this);

		targetFileDirectoryTextField = new JTextField();
		targetFileDirectoryTextField.setEditable(false);
		targetFileDirectoryTextField.setBounds(10, 140, 235, 30);

		mergeFilesButton = new JButton();
		mergeFilesButton.setActionCommand(Action.MERGE_FILES.name());
		mergeFilesButton.setText("Merge PDF Files in Only one");
		mergeFilesButton.setEnabled(false);
		mergeFilesButton.setBounds(45, 190, 200, 50);
		mergeFilesButton.addActionListener(this);

		jFrame.add(sourceFilesLabel);
		jFrame.add(searchButtonSourceFiles);
		jFrame.add(sourceFilesTextField);
		jFrame.add(targetDirectoryLabel);
		jFrame.add(targetFileDirectoryTextField);
		jFrame.add(searchButtonTargetDirectory);
		jFrame.add(mergeFilesButton);
		jFrame.setSize(300, 300);
		jFrame.setLayout(null);
		jFrame.setVisible(true);
		jFrame.setResizable(false);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());

		if (e.getActionCommand().equals(Action.SEARCH_SOURCE.name())) {
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.setDialogTitle("Select the multiple PDF files: ");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int option = fileChooser.showOpenDialog(null);
			if (option == JFileChooser.APPROVE_OPTION) {
				File[] selectedPDFFiles = fileChooser.getSelectedFiles();
				String selectedPDFNames = "";
				for (int i = 0; i < selectedPDFFiles.length; i++) {
					if (!selectedPDFFiles[i].getName().toLowerCase().endsWith(".pdf")) {
						JOptionPane.showMessageDialog(null, "One of the files doesn't end with the extension .jpeg");
						return;
					} else {
						selectedPDFNames += selectedPDFFiles.length > 1 ? selectedPDFFiles[i].getName().concat(",") : selectedPDFFiles[i].getName();
						fileDirectories.add(selectedPDFFiles[i].getAbsolutePath());
					}
				}

				sourceFilesTextField.setText(selectedPDFNames);

			}

		} else if (e.getActionCommand().equals(Action.SEARCH_TARGET.name())) {
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setDialogTitle("Select the multiple PDF files: ");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int option = fileChooser.showOpenDialog(null);
			if (option == JFileChooser.APPROVE_OPTION) {
				File directory = fileChooser.getSelectedFile();
				String directoryPath = directory.getAbsolutePath();
				targetFileDirectoryTextField.setText(directoryPath);
			}
		} else if (e.getActionCommand().equals(Action.MERGE_FILES.name())) {
			PDFMergerUtility mergeUtility = new PDFMergerUtility();
			
			String destination = targetFileDirectoryTextField.getText();
			mergeUtility.setDestinationFileName(destination + "/MPFD_" + LocalDate.now() + ".pdf");
			
			for (String file : fileDirectories) {
				try {
					mergeUtility.addSource(file);
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "One or more of the choosed PDF files aren't more available ");
				}
			}
			
			try {
				mergeUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			JOptionPane.showMessageDialog(null, "PDF file was generated!");
			fileDirectories = null;
			sourceFilesTextField.setText("");
			targetFileDirectoryTextField.setText("");
		}
		
		boolean fieldsAreFilled = checkIfTargetAndSourceFieldsAreFilled();
		
		if (fieldsAreFilled) {
			mergeFilesButton.setEnabled(true);
		}
	}

	private boolean checkIfTargetAndSourceFieldsAreFilled() {
		String sourceText = sourceFilesTextField.getText();
		String targetText = targetFileDirectoryTextField.getText();
		
		if (sourceText.length() > 0 && targetText.length() > 0) {
			return true;
		}
		
		return false;
	}

	public static void main(String[] args) {
		new Main();
	}

}
