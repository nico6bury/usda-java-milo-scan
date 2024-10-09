/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import IJM.IJProcess;
import IJM.SumResult;
import Main.Controller;
import Main.Controller.InterfaceMessage;
import Utils.Constants;
import Utils.Result;
import View.Dialog.AreaFlagDialog;
import View.Dialog.ScanAreaDialog;
import View.Dialog.ThresholdDialog;
import View.Dialog.UnsharpDialog;
import View.DisplayTask.DisplayTaskCaller;
import View.IJTask.IJTaskCaller;

/**
 *
 * @author Nicholas.Sixbury
 */
public class MainWindow extends javax.swing.JFrame implements DisplayTaskCaller, IJTaskCaller {
	private Controller root;
	private JFileChooser selectFilesChooser = new JFileChooser();
	
	// where displayed image was last selected from
	public LastSelectedFrom lastSelectedFrom = LastSelectedFrom.NoSelection;
	// dialog boxes we can re-use
	public AreaFlagDialog areaFlagDialog = new AreaFlagDialog(this, true);
	public ThresholdDialog thresholdDialog = new ThresholdDialog(this, true);
	public UnsharpDialog unsharpDialog = new UnsharpDialog(this, true);
	public ScanAreaDialog scanAreaDialog = new ScanAreaDialog(this, true);
	// progress bar for imagej processing
	ProgressMonitor progressMonitor;

	/**
	 * enum added for use in keeping track of whether displayed image was selected from QueueList or OutputTable
	 */
	public enum LastSelectedFrom {
		QueueList,
		OutputTable,
		NoSelection
	}

	/**
	 * Creates new form MainWindow
	 */
	public MainWindow(Controller root) {
		this.root = root;
		// try and figure out if we should use dark mode
		boolean useDarkMode = false;
		// set the application theme / look and feel
		if (useDarkMode) { FlatDarkLaf.setup(); }
		else { FlatLightLaf.setup(); }

		// set up file listeners
		selectFilesChooser.addActionListener(selectFilesListener);
		selectFilesChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		selectFilesChooser.setMultiSelectionEnabled(true);
		try {
			String jar_location = new File(IJProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toString();
			String output_folder_storage = jar_location + File.separator + Constants.SCANNED_IMAGES_FOLDER_NAME;
			selectFilesChooser.setCurrentDirectory(new File(output_folder_storage));
		} catch (URISyntaxException e) {System.out.println("Couldn't figure out jar location. Weird.");}
		// ijProcFileChooser.addActionListener(ijProcFileListener);
		
				initComponents();

		// maximize the window
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		// build title block
		StringBuilder tb = new StringBuilder();
		tb.append(Constants.LOCATION);
		tb.append("\t");
		tb.append(Constants.PROGRAM_NAME);
		tb.append("\n");
		tb.append(Constants.DATE());
		tb.append("\t");
		tb.append(Constants.VERSION);
		tb.append("\t");
		tb.append(Constants.PEOPLE);
		tb.append("\n");
		tb.append("To interface with EPSON V600 Scanner\n");
		tb.append("To collect reflective image of milo sample in a plastic grid\n");
		tb.append("Process image to do something eventually");
		uxTitleBlockTxt.setText(tb.toString());

		// configure the listbox model
		DefaultListModel<String> listModel = new DefaultListModel<>();
		uxQueueList.setModel(listModel);

		// configure the table model
		ListSelectionListener lsl = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int idx = uxOutputTable.getSelectedRow();
					String selected_filename = uxOutputTable.getModel().getValueAt(idx, 0).toString();
					// call method to update image label
					updateImageDisplay(selected_filename);
					// update properties display
					uxImagePropertiesTxt.setText("Image: " + selected_filename + "\nSelected From: OutputTable[" + idx + "]");
					// update flags and such
					lastSelectedFrom = LastSelectedFrom.OutputTable;
				}//end if the value is done adjusting
			}//end valueChanged(e)
		};
		uxOutputTable.getSelectionModel().addListSelectionListener(lsl);
		uxOutputTable.setDefaultRenderer(Double.class, new CustomRenderer());
		uxOutputTable.setDefaultRenderer(Integer.class, new CustomRenderer());

		// mess with the jtable so that column text is centered
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		uxOutputTable.setDefaultRenderer(String.class, centerRenderer);
	}//end MainWindow constructor

	/**
	 * Shows a pretty generic message box giving the name and message of supplied error. Uses JOptionPane
	 * @param e The exception that was generated.
	 */
	public static void showGenericExceptionMessage(Exception e) {
		JOptionPane.showMessageDialog(null, "While attempting the chosen command, the program encountered an exception of type " + e.getClass().getName() + ".\nThe exception message was " + e.getMessage(), "Unhandled Exception Caught.", JOptionPane.ERROR_MESSAGE);
	}//end genericExceptionMessage(e)

	@SuppressWarnings({ "rawtypes", "unchecked" })
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        uxStatusTxt = new javax.swing.JTextArea();
        uxConnectToScannerBtn = new javax.swing.JButton();
        uxScanBigBtn = new javax.swing.JButton();
        uxScanQueueBtn = new javax.swing.JButton();
        uxAddFilesBtn = new javax.swing.JButton();
        uxProcessAllBtn = new javax.swing.JButton();
        uxEmptyQueueBtn = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        uxTitleBlockTxt = new javax.swing.JTextArea();
        uxOverwriteName = new javax.swing.JTextField();
        uxShouldOverwriteName = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        uxShouldOutputKernImages = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        uxQueueList = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        uxImagePropertiesTxt = new javax.swing.JTextArea();
        uxPrevImageBtn = new javax.swing.JButton();
        uxNextImageBtn = new javax.swing.JButton();
        uxOpenFileBtn = new javax.swing.JButton();
        uxImageLabelE = new javax.swing.JLabel();
        uxClearOutputBtn = new javax.swing.JButton();
        uxOpenOutputFile = new javax.swing.JButton();
        uxImageLabelK = new javax.swing.JLabel();
        uxImageLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        uxOutputTable = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        uxInitMenu = new javax.swing.JMenu();
        uxConnectScannerBtn = new javax.swing.JMenuItem();
        uxResetScanner = new javax.swing.JMenuItem();
        uxRunMenu = new javax.swing.JMenu();
        uxScanBtn = new javax.swing.JMenuItem();
        uxIjBtn = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        uxSetThresholdMenuBtn = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        uxSetAreaFlagMenuBtn = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        uxSetUnsharpMenuBtn = new javax.swing.JMenuItem();
        uxScanAreaMenuBtn = new javax.swing.JMenuItem();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(jTable1);

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("USDA-ARS-MiloScan-Java");

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jSplitPane1.setDividerLocation(690);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jSplitPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jSplitPane2.setDividerLocation(450);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        uxStatusTxt.setEditable(false);
        uxStatusTxt.setColumns(20);
        uxStatusTxt.setFont(uxStatusTxt.getFont());
        uxStatusTxt.setRows(5);
        jScrollPane1.setViewportView(uxStatusTxt);

        uxConnectToScannerBtn.setFont(uxConnectToScannerBtn.getFont().deriveFont(uxConnectToScannerBtn.getFont().getSize()+2f));
        uxConnectToScannerBtn.setText("Connect to Scanner");
        uxConnectToScannerBtn.setToolTipText("Attempts to make a connection to the scanner, if one is connected.");
        uxConnectToScannerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxConnectToScannerBtnActionPerformed(evt);
            }
        });

        uxScanBigBtn.setFont(uxScanBigBtn.getFont().deriveFont(uxScanBigBtn.getFont().getSize()+2f));
        uxScanBigBtn.setText("Scan");
        uxScanBigBtn.setToolTipText("Scans and saves an image without adding it to the queue.");
        uxScanBigBtn.setEnabled(false);
        uxScanBigBtn.setVisible(false);
        uxScanBigBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxScanBigBtnActionPerformed(evt);
            }
        });

        uxScanQueueBtn.setFont(uxScanQueueBtn.getFont().deriveFont(uxScanQueueBtn.getFont().getSize()+2f));
        uxScanQueueBtn.setText("Scan + Add to Queue");
        uxScanQueueBtn.setToolTipText("Scans and saves an image, then adds it to the processing queue.");
        uxScanQueueBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxScanQueueBtnActionPerformed(evt);
            }
        });

        uxAddFilesBtn.setFont(uxAddFilesBtn.getFont().deriveFont(uxAddFilesBtn.getFont().getSize()+2f));
        uxAddFilesBtn.setText("Add Files to Queue");
        uxAddFilesBtn.setToolTipText("Select image files to add to the queue for processing.");
        uxAddFilesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxAddFilesBtnActionPerformed(evt);
            }
        });

        uxProcessAllBtn.setFont(uxProcessAllBtn.getFont().deriveFont(uxProcessAllBtn.getFont().getSize()+2f));
        uxProcessAllBtn.setText("Process Queue");
        uxProcessAllBtn.setToolTipText("Processes all images in the queue, displaying results in the output table.");
        uxProcessAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxProcessAllBtnActionPerformed(evt);
            }
        });

        uxEmptyQueueBtn.setFont(uxEmptyQueueBtn.getFont().deriveFont(uxEmptyQueueBtn.getFont().getSize()+2f));
        uxEmptyQueueBtn.setText("Empty Queue");
        uxEmptyQueueBtn.setToolTipText("Clears all files from the processing queue.");
        uxEmptyQueueBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxEmptyQueueBtnActionPerformed(evt);
            }
        });

        uxTitleBlockTxt.setEditable(false);
        uxTitleBlockTxt.setColumns(20);
        uxTitleBlockTxt.setFont(uxTitleBlockTxt.getFont());
        uxTitleBlockTxt.setRows(5);
        jScrollPane6.setViewportView(uxTitleBlockTxt);

        uxOverwriteName.setFont(uxOverwriteName.getFont().deriveFont(uxOverwriteName.getFont().getSize()+2f));

        uxShouldOverwriteName.setFont(uxShouldOverwriteName.getFont());
        uxShouldOverwriteName.setText("Auto-Gen Image Name");
        uxShouldOverwriteName.setToolTipText("If selected, then the program will automatically generate a name for scanned images.");
        uxShouldOverwriteName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxShouldOverwriteNameActionPerformed(evt);
            }
        });

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getSize()+1f));
        jLabel1.setText("Scanned Image Name");

        uxShouldOutputKernImages.setFont(uxShouldOutputKernImages.getFont().deriveFont(uxShouldOutputKernImages.getFont().getSize()+2f));
        uxShouldOutputKernImages.setText("Output Thresholded images of individual kernels");
        uxShouldOutputKernImages.setToolTipText("If selected, then the program will output images of individual kernels, including threshold information.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addComponent(jScrollPane6)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(uxConnectToScannerBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxScanBigBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxScanQueueBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxShouldOverwriteName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(uxAddFilesBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxProcessAllBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxEmptyQueueBtn)))
                .addGap(117, 132, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxOverwriteName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(uxShouldOutputKernImages)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uxConnectToScannerBtn)
                    .addComponent(uxScanBigBtn)
                    .addComponent(uxScanQueueBtn)
                    .addComponent(uxShouldOverwriteName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uxAddFilesBtn)
                    .addComponent(uxProcessAllBtn)
                    .addComponent(uxEmptyQueueBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uxOverwriteName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uxShouldOutputKernImages)
                .addContainerGap(143, Short.MAX_VALUE))
        );

        jSplitPane2.setTopComponent(jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        uxQueueList.setFont(uxQueueList.getFont().deriveFont(uxQueueList.getFont().getSize()+2f));
        uxQueueList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        uxQueueList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                uxQueueListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(uxQueueList);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane2.setRightComponent(jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jSplitPane1.setLeftComponent(jPanel3);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jSplitPane3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jSplitPane3.setDividerLocation(450);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        uxImagePropertiesTxt.setEditable(false);
        uxImagePropertiesTxt.setColumns(1);
        uxImagePropertiesTxt.setFont(uxImagePropertiesTxt.getFont().deriveFont(uxImagePropertiesTxt.getFont().getSize()+1f));
        uxImagePropertiesTxt.setLineWrap(true);
        uxImagePropertiesTxt.setRows(1);
        uxImagePropertiesTxt.setEnabled(false);
        uxImagePropertiesTxt.setPreferredSize(new java.awt.Dimension(102, 84));
        jScrollPane3.setViewportView(uxImagePropertiesTxt);

        uxPrevImageBtn.setFont(uxPrevImageBtn.getFont().deriveFont(uxPrevImageBtn.getFont().getSize()+2f));
        uxPrevImageBtn.setText("Previous Image");
        uxPrevImageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxPrevImageBtnActionPerformed(evt);
            }
        });

        uxNextImageBtn.setFont(uxNextImageBtn.getFont().deriveFont(uxNextImageBtn.getFont().getSize()+2f));
        uxNextImageBtn.setText("Next Image");
        uxNextImageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxNextImageBtnActionPerformed(evt);
            }
        });

        uxOpenFileBtn.setFont(uxOpenFileBtn.getFont().deriveFont(uxOpenFileBtn.getFont().getSize()+2f));
        uxOpenFileBtn.setText("Open in File Explorer");
        uxOpenFileBtn.setToolTipText("Opens the Selected Image in File Explorer");
        uxOpenFileBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxOpenFileBtnActionPerformed(evt);
            }
        });

        uxImageLabelE.setFont(uxImageLabelE.getFont().deriveFont(uxImageLabelE.getFont().getSize()+2f));
        uxImageLabelE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        uxClearOutputBtn.setFont(uxClearOutputBtn.getFont().deriveFont(uxClearOutputBtn.getFont().getSize()+2f));
        uxClearOutputBtn.setText("Clear Output");
        uxClearOutputBtn.setToolTipText("Clears data from the Output Table below");
        uxClearOutputBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxClearOutputBtnActionPerformed(evt);
            }
        });

        uxOpenOutputFile.setFont(uxOpenOutputFile.getFont().deriveFont(uxOpenOutputFile.getFont().getSize()+2f));
        uxOpenOutputFile.setText("Open Output Dir");
        uxOpenOutputFile.setToolTipText("Opens directory containing output files in File Explorer");
        uxOpenOutputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxOpenOutputFileActionPerformed(evt);
            }
        });

        uxImageLabelK.setFont(uxImageLabelK.getFont().deriveFont(uxImageLabelK.getFont().getSize()+2f));
        uxImageLabelK.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        uxImageLabel.setFont(uxImageLabel.getFont().deriveFont(uxImageLabel.getFont().getSize()+2f));
        uxImageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(uxPrevImageBtn)
                    .addComponent(uxOpenOutputFile)
                    .addComponent(uxOpenFileBtn)
                    .addComponent(uxClearOutputBtn)
                    .addComponent(uxNextImageBtn)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uxImageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uxImageLabelK, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uxImageLabelE, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxPrevImageBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxNextImageBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxOpenFileBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxOpenOutputFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxClearOutputBtn))
                    .addComponent(uxImageLabelE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(uxImageLabelK, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(uxImageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jSplitPane3.setTopComponent(jPanel5);

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        uxOutputTable.setFont(uxOutputTable.getFont());
        uxOutputTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "FileID", "GridIdx", "Pixels1", "Pixels2", "%Area"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        uxOutputTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        uxOutputTable.setColumnSelectionAllowed(true);
        uxOutputTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        uxOutputTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        uxOutputTable.setShowGrid(true);
        jScrollPane5.setViewportView(uxOutputTable);
        uxOutputTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (uxOutputTable.getColumnModel().getColumnCount() > 0) {
            uxOutputTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            uxOutputTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        }

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane3.setRightComponent(jPanel6);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3)
        );

        jSplitPane1.setRightComponent(jPanel4);

        uxInitMenu.setText("Init");
        uxInitMenu.setFocusable(false);
        uxInitMenu.setFont(uxInitMenu.getFont().deriveFont(uxInitMenu.getFont().getSize()+2f));

        uxConnectScannerBtn.setFont(uxConnectScannerBtn.getFont().deriveFont(uxConnectScannerBtn.getFont().getSize()+2f));
        uxConnectScannerBtn.setText("Connect Scanner");
        uxConnectScannerBtn.setToolTipText("Attempt to establish a connection to a scanner.");
        uxConnectScannerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxConnectScannerBtnActionPerformed(evt);
            }
        });
        uxInitMenu.add(uxConnectScannerBtn);

        uxResetScanner.setFont(uxResetScanner.getFont().deriveFont(uxResetScanner.getFont().getSize()+2f));
        uxResetScanner.setText("Reset Scanner");
        uxResetScanner.setToolTipText("Resets the connection to the scanner by attempting to close and reopen a connection.");
        uxResetScanner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxResetScannerActionPerformed(evt);
            }
        });
        uxInitMenu.add(uxResetScanner);

        jMenuBar1.add(uxInitMenu);

        uxRunMenu.setText("Run");
        uxRunMenu.setFont(uxRunMenu.getFont().deriveFont(uxRunMenu.getFont().getSize()+2f));

        uxScanBtn.setFont(uxScanBtn.getFont().deriveFont(uxScanBtn.getFont().getSize()+2f));
        uxScanBtn.setText("Scan");
        uxScanBtn.setToolTipText("Scans and saves an image without adding it to the queue.");
        uxScanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxScanBtnActionPerformed(evt);
            }
        });
        uxRunMenu.add(uxScanBtn);

        uxIjBtn.setFont(uxIjBtn.getFont().deriveFont(uxIjBtn.getFont().getSize()+2f));
        uxIjBtn.setText("ImageJ Process");
        uxIjBtn.setToolTipText("Processes the images in the queue, displaying results in the output table.");
        uxIjBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxIjBtnActionPerformed(evt);
            }
        });
        uxRunMenu.add(uxIjBtn);

        jMenuBar1.add(uxRunMenu);

        jMenu1.setText("Threshold");
        jMenu1.setFont(jMenu1.getFont().deriveFont(jMenu1.getFont().getSize()+2f));

        uxSetThresholdMenuBtn.setFont(uxSetThresholdMenuBtn.getFont().deriveFont(uxSetThresholdMenuBtn.getFont().getSize()+2f));
        uxSetThresholdMenuBtn.setText("Set Threshold");
        uxSetThresholdMenuBtn.setToolTipText("Allows you to set a threshold for image processing.");
        uxSetThresholdMenuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxSetThresholdMenuBtnActionPerformed(evt);
            }
        });
        jMenu1.add(uxSetThresholdMenuBtn);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Area Flag");
        jMenu2.setFont(jMenu2.getFont().deriveFont(jMenu2.getFont().getSize()+2f));

        uxSetAreaFlagMenuBtn.setFont(uxSetAreaFlagMenuBtn.getFont().deriveFont(uxSetAreaFlagMenuBtn.getFont().getSize()+2f));
        uxSetAreaFlagMenuBtn.setText("Set %Area Flags");
        uxSetAreaFlagMenuBtn.setToolTipText("Allows you to set thresholds for which average %area will be flagged.");
        uxSetAreaFlagMenuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxSetAreaFlagMenuBtnActionPerformed(evt);
            }
        });
        jMenu2.add(uxSetAreaFlagMenuBtn);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Scanner Correction");
        jMenu3.setFont(jMenu3.getFont().deriveFont(jMenu3.getFont().getSize()+2f));

        uxSetUnsharpMenuBtn.setFont(uxSetUnsharpMenuBtn.getFont().deriveFont(uxSetUnsharpMenuBtn.getFont().getSize()+2f));
        uxSetUnsharpMenuBtn.setText("Unsharp Mask Settings");
        uxSetUnsharpMenuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxSetUnsharpMenuBtnActionPerformed(evt);
            }
        });
        jMenu3.add(uxSetUnsharpMenuBtn);

        uxScanAreaMenuBtn.setFont(uxScanAreaMenuBtn.getFont().deriveFont(uxScanAreaMenuBtn.getFont().getSize()+2f));
        uxScanAreaMenuBtn.setText("Set Scan Area");
        uxScanAreaMenuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uxScanAreaMenuBtnActionPerformed(evt);
            }
        });
        jMenu3.add(uxScanAreaMenuBtn);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void uxScanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxScanBtnActionPerformed
		// PerformScan();
		uxScanQueueBtnActionPerformed(evt);
	}//GEN-LAST:event_uxScanBtnActionPerformed

	private void uxIjBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxIjBtnActionPerformed
		uxProcessAllBtnActionPerformed(evt);
	}//GEN-LAST:event_uxIjBtnActionPerformed

	private ActionListener selectFilesListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(e.getActionCommand());
			if (e.getActionCommand() == "ApproveSelection") {
				File[] selectedFiles = selectFilesChooser.getSelectedFiles();
				// uxScannedFileTxt.setText(lastScannedFile.getPath());
				for (int i = 0; i < selectedFiles.length; i++) {
					uxStatusTxt.append("\"" + selectedFiles[i].getAbsolutePath() + "\"\n");
					// root.getImageQueue().add(selectedFiles[i]);
					// allImages.add(selectedFiles[i]);
				}//end adding each selected file to the queue
				root.handleMessage(InterfaceMessage.AddFilesToQueue, selectedFiles);
			}//end if selection was approved
			else if (e.getActionCommand() == "CancelSelection") {
				uxStatusTxt.append("File selection cancelled.\n");
			}//end else if selection was cancelled
		}//end actionPerformed
	};

	private void uxConnectScannerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxConnectScannerBtnActionPerformed
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// check for scanner already initialized
		boolean is_scanner_connected = (boolean) root.handleMessage(InterfaceMessage.ConnectScanner, null);
		if (is_scanner_connected) {uxStatusTxt.append("Connected to scanner.\n");}
		setCursor(Cursor.getDefaultCursor());
	}//GEN-LAST:event_uxConnectScannerBtnActionPerformed

	private void uxResetScannerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxResetScannerActionPerformed
		boolean did_scanner_disconnect = (boolean) root.handleMessage(InterfaceMessage.ResetScanner, null);
		if (did_scanner_disconnect) {uxStatusTxt.append("Disconnected from scanner.\n");}
	}//GEN-LAST:event_uxResetScannerActionPerformed

	/**
	 * This method should be called whenever the image queue is updated, in order to show the changes in the list.
	 */
	public void updateQueueList() {
		uxQueueList.removeAll();
		DefaultListModel<String> model = (DefaultListModel<String>)uxQueueList.getModel();
		model.clear();
		// String[] imageArray = new String[root.getImageQueue().size()];
		for(int i = 0; i < root.getImageQueue().size(); i++) {
			model.addElement(root.getImageQueue().get(i).getName());
		}//end adding each image file to the array
		// uxQueueList.setListData(imageArray);
	}//end UpdateQueueList()

	/**
	 * Figures out where currently selected image is located, finds image after it, then changes selection of displayed image.
	 */
	private void uxNextImageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxNextImageBtnActionPerformed
		if (lastSelectedFrom == LastSelectedFrom.QueueList) {
			int curIdx = uxQueueList.getSelectedIndex();
			if (curIdx + 1 < uxQueueList.getModel().getSize() && curIdx + 1 >= 0) {
				uxQueueList.setSelectedIndex(curIdx + 1);
			}//end if there's another index to go to
		}//end if last selection was from queue list
		else if (lastSelectedFrom == LastSelectedFrom.OutputTable) {
			int curIdx = uxOutputTable.getSelectedRow();
			if (curIdx + 1 < uxOutputTable.getRowCount() && curIdx + 1 >= 0) {
				uxOutputTable.changeSelection(curIdx + 1, 0, false, false);
			}//end if there's another index to go to
		}//end else if last selection was from output table
	}//GEN-LAST:event_uxNextImageBtnActionPerformed

	/**
	 * Figures out where currently selected image is located, finds image before it, then changes selection of displayed image.
	 */
	private void uxPrevImageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxPrevImageBtnActionPerformed
		if (lastSelectedFrom == LastSelectedFrom.QueueList) {
			int curIdx = uxQueueList.getSelectedIndex();
			if (curIdx - 1 >= 0) {
				uxQueueList.setSelectedIndex(curIdx - 1);
			}//end if there's another index to go to
		}//end if last selection was from queue list
		else if (lastSelectedFrom == LastSelectedFrom.OutputTable) {
			int curIdx = uxOutputTable.getSelectedRow();
			if (curIdx - 1 >= 0) {
				uxOutputTable.changeSelection(curIdx - 1, 0, false, false);
			}//end if there's another index to go to
		}//end else if last selection was from output table
	}//GEN-LAST:event_uxPrevImageBtnActionPerformed

	/**
	 * Finds which image is currently selected and displayed, 
	 * finds the path of that image, and 
	 * then opens + selects that file in file explorer
	 */
	private void uxOpenFileBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxOpenFileBtnActionPerformed
		// open the file in file explorer
		try {
			String selectedValue = "";
			if (lastSelectedFrom == LastSelectedFrom.QueueList) {selectedValue = uxQueueList.getSelectedValue();}
			else if (lastSelectedFrom == LastSelectedFrom.OutputTable) {selectedValue = uxOutputTable.getModel().getValueAt(uxOutputTable.getSelectedRow(), 0).toString();}
			File imageMatch = getSelectedFileFromAll(selectedValue);
			if (imageMatch == null) {JOptionPane.showMessageDialog(this, "Could not find file that matches selected image, or no image selected.");}
			Runtime.getRuntime().exec("explorer.exe /select," + imageMatch.getAbsolutePath());
		}//end trying to open file explorer
		catch(Exception e) {System.out.println("Couldn't open file explorer"); uxStatusTxt.append("Couldn't open file explorer\n");}
	}//GEN-LAST:event_uxOpenFileBtnActionPerformed

	/**
	 * Displays the newly selected file image and results to the right. 
	 * Due to how all the architecture is currently set up, this is somewhat awkward.
	 * @param evt The list selection events
	 */
	private void uxQueueListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_uxQueueListValueChanged
		if (!evt.getValueIsAdjusting() && uxQueueList.getSelectedIndex() != -1) {
			// update the displayed image
			updateImageDisplay(uxQueueList.getSelectedValue());
			// update image properties text
			uxImagePropertiesTxt.setText("Image: " + uxQueueList.getSelectedValue() + "\nSelected From: QueueList[" + uxQueueList.getSelectedIndex() + "]");
			// update some flags and such
			lastSelectedFrom = LastSelectedFrom.QueueList;
		}//end if we're not in a series of adjustments?
	}//GEN-LAST:event_uxQueueListValueChanged

	/**
	 * This method updates the image label with the image which has the specified filename (as from File.getName()).
	 * This is somewhat awkward, as we have to search through a list of all files to find the one with a matching name.
	 * It's also possible that this could cause problems if two images are loaded with the same filename (but different directories).  
	 * Use filename sentinel value "%=empty" to remove the image
	 * @param filename The filename (as from File.getName()) of the image to display.
	 */
	public void updateImageDisplay(String filename) {
		if (filename == "%=empty") {uxImageLabel.setIcon(null); return;}
		// pick throug the list of files that have been loaded into queue to find the one that matches the selected file name
		File imageMatch = getSelectedFileFromAll(filename);
		// edge case validation
		if (imageMatch == null) {JOptionPane.showMessageDialog(this, "Could not find matching file for selection."); return;}
		// display the image in the label
		DisplayTask displayWorker = new DisplayTask(this, imageMatch);
		displayWorker.imgWidth = uxImageLabel.getWidth();
		displayWorker.imgHeight = uxImageLabel.getHeight();
		displayWorker.execute();
	}//end updateImageDisplay(filename)

	/**
	 * Handler to be called by DisplayTask once it finishes
	 * making the icons.
	 * @param finishedIcons 3 finished icons to be displayed
	 */
	public void giveFinishedImageDisplay(ImageIcon plain_icon, ImageIcon kern_icon, ImageIcon endo_icon) {
		uxImageLabel.setIcon(plain_icon);
		uxImageLabelK.setIcon(kern_icon);
		uxImageLabelE.setIcon(endo_icon);
	}//end giveFinishedImageDisplay

	/**
	 * A helper method written for uxQueueListValueChange(). This method loops through all 
	 * the files in the queue until it finds one whose name matches the file name selected in uxQueueList.
	 * @return This method returns the File that matches if found, or null if it couldn't find a match.
	 */
	private File getSelectedFileFromAll(String filename) {
		// search through imageQueue for File which matches
		File imageMatch = null;
		for (File this_image : root.getImageQueue()) {
			if (this_image.getName().equals(filename)) {
				imageMatch = this_image;
				break;
			}//end if we found a match
		}//end looping over processing queue
		if (imageMatch == null) {
			for (File this_image : root.getProcessedImages()) {
				if (this_image.getName().equals(filename)) {
					imageMatch = this_image;
					break;
				}//end if we found a match
			}//end looping over processed images
		}//end if we haven't already found a match
		return imageMatch;
	}//end getSelectedFileFromQueue()

	/**
	 * Updates the output table with the provided results from image processing.
	 * Working with JTables is kinda jank, so column ordering is hardcoded here.
	 * This is also where the string formatting for numeric columns in the table is handled.
	 * @param groupedResults A grouped list of SumResults, likely generated by passing IJM.IJProcess.lastProcResult to IJM.SumResult.GroupResultsByFile().
	 */
	public void updateOutputTable(List<List<SumResult>> groupedResults) {
		DefaultTableModel this_table_model = (DefaultTableModel)uxOutputTable.getModel();
		for (List<SumResult> resultGroup : groupedResults) {
			for (SumResult res : resultGroup) {
				int total_area = (int)Math.floor(res.getResValSentinel("Area"));
				int endosperm_area = (int)Math.floor(res.getResValSentinel("EndospermArea"));
				double endo_percent = (endosperm_area * 100) / total_area;
				Object[] this_row = new Object[5];
				this_row[0] = res.file.getName();
				this_row[1] = res.rrr.gridCellIdx + 1;
				this_row[2] = total_area;
				this_row[3] = endosperm_area;
				this_row[4] = endo_percent;
				this_table_model.addRow(this_row);
			}//end looping over each results
		}//end looping over each result group
	}//end updateOutputTable(groupedResults)

	/**
	 * Shows the dialog for changing area flag thresholds. 
	 */
	private void uxSetAreaFlagMenuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxSetAreaFlagMenuBtnActionPerformed
		areaFlagDialog.setVisible(true);
		root.getConfigStoreH().area_threshold_lower = areaFlagDialog.firstFlag;
		root.getConfigStoreH().area_threshold_upper = areaFlagDialog.secondFlag;
		root.getConfigScribe().write_config(root.getConfigStoreH(), root.getConfigStoreC());
	}//GEN-LAST:event_uxSetAreaFlagMenuBtnActionPerformed

	/**
	 * Shows the dialog for changing the image processing threshold. 
	 */
	private void uxSetThresholdMenuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxSetThresholdMenuBtnActionPerformed
		thresholdDialog.setVisible(true);
		root.getConfigStoreH().proc_threshold = thresholdDialog.thresholdToReturn;
		root.getConfigScribe().write_config(root.getConfigStoreH(), root.getConfigStoreC());
	}//GEN-LAST:event_uxSetThresholdMenuBtnActionPerformed

	/**
	 * Shows the dialog for changing the unsharp settings.
	 */
	private void uxSetUnsharpMenuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxSetUnsharpMenuBtnActionPerformed
		unsharpDialog.setVisible(true);
		root.getConfigStoreH().unsharp_sigma = unsharpDialog.unsharp_sigma;
		root.getConfigStoreH().unsharp_weight = unsharpDialog.unsharp_weight;
		root.getConfigStoreH().unsharp_skip = unsharpDialog.unsharp_skip;
		root.getConfigStoreH().unsharp_rename = unsharpDialog.unsharp_rename;
		root.getConfigScribe().write_config(root.getConfigStoreH(), root.getConfigStoreC());
	}//GEN-LAST:event_uxSetUnsharpMenuBtnActionPerformed

	/**
	 * Shows the dialog for changing the scan area.
	 */
	private void uxScanAreaMenuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxScanAreaMenuBtnActionPerformed
		scanAreaDialog.setVisible(true);
		root.getConfigStoreH().scan_x1 = scanAreaDialog.X1;
		root.getConfigStoreH().scan_y1 = scanAreaDialog.Y1;
		root.getConfigStoreH().scan_x2 = scanAreaDialog.X2;
		root.getConfigStoreH().scan_y2 = scanAreaDialog.Y2;
		root.getConfigScribe().write_config(root.getConfigStoreH(), root.getConfigStoreC());
	}//GEN-LAST:event_uxScanAreaMenuBtnActionPerformed

	/**
	 * Clears the output table
	 */
	private void uxClearOutputBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxClearOutputBtnActionPerformed
		// clear text from output table
		try {
			uxClearOutputTable();
			uxClearOutputTable();
		} catch (ArrayIndexOutOfBoundsException e) {}
		// clear image display
		if (lastSelectedFrom == LastSelectedFrom.OutputTable) {
			updateImageDisplay("%=empty");
			uxImagePropertiesTxt.setText("");
			lastSelectedFrom = LastSelectedFrom.NoSelection;
		}//end if we need to clear selected image
	}//GEN-LAST:event_uxClearOutputBtnActionPerformed
		
	private void uxClearOutputTable() {
		DefaultTableModel this_table_model = (DefaultTableModel)uxOutputTable.getModel();
		this_table_model.getDataVector().removeAllElements();
		this_table_model.fireTableDataChanged();
		uxOutputTable.revalidate();
	}//end uxClearOutputTable()

	/**
	 * This function finds the directory of output files and opens it in file explorer.
	 */
	private void uxOpenOutputFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxOpenOutputFileActionPerformed
		String jar_location;
		try {
			// Get path of output folder
			jar_location = new File(IJProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toString();
			String output_folder_storage = jar_location + File.separator + Constants.IMAGEJ_OUTPUT_FOLDER_NAME;
			Runtime.getRuntime().exec("explorer.exe " + output_folder_storage);
		} catch (Exception e) {
			showGenericExceptionMessage(e);
		}//end catching any file-related exceptions
	}//GEN-LAST:event_uxOpenOutputFileActionPerformed

	private void uxShouldOverwriteNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxShouldOverwriteNameActionPerformed
		if (uxShouldOverwriteName.isSelected()) {uxOverwriteName.setEnabled(false);}
		else {uxOverwriteName.setEnabled(true);}
	}//GEN-LAST:event_uxShouldOverwriteNameActionPerformed

	private void uxEmptyQueueBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxEmptyQueueBtnActionPerformed
		root.getImageQueue().clear();
		updateQueueList();
		// clear image display
		if (lastSelectedFrom == LastSelectedFrom.QueueList) {
			updateImageDisplay("%=empty");
			uxImagePropertiesTxt.setText("");
			lastSelectedFrom = LastSelectedFrom.NoSelection;
		}//end if we should clear image
	}//GEN-LAST:event_uxEmptyQueueBtnActionPerformed

	/**
	 * This method contains the code for initiating processing of all images in the queue and updating things afterwards.
	 * @param evt
	 */
	private void uxProcessAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxProcessAllBtnActionPerformed
		if (root.getImageQueue() == null || root.getImageQueue().size() == 0) {
			JOptionPane.showMessageDialog(this, "Please select the image file generated by the scanner.", "No scanned image selected", JOptionPane.ERROR_MESSAGE);
		}//end if last scanned file is null
		else {
			root.handleMessage(InterfaceMessage.ProcessQueue, null);
			processQueue();
			JOptionPane.showMessageDialog(this, "Your images will now be processed.\nWhen they are done, they will show up in the output table on the bottom right.");
		}//end else we should probably be able to process the file
	}//GEN-LAST:event_uxProcessAllBtnActionPerformed

	private void processQueue() {
		try {
			// tell user we're about to do processing
			
			List<File> tempImageQueue = new ArrayList<File>(root.getImageQueue());
			IJTask ijTask = new IJTask(tempImageQueue, root.getIJProcess(), this);
			root.getIJProcess().th01 = thresholdDialog.thresholdToReturn;

			// roll over area flag stuff to the processing
			root.getIJProcess().lower_flag_thresh = areaFlagDialog.firstFlag;
			root.getIJProcess().upper_flag_thresh = areaFlagDialog.secondFlag;
			root.getIJProcess().shouldOutputKernImages = uxShouldOutputKernImages.isSelected();

			// handing for gui stuff
			// clear queue now that it's being processed
			for (File img : root.getImageQueue()) {
				root.getProcessedImages().add(img);
			}//end moving each processed image from queue to finished images
			root.getImageQueue().clear();
			updateQueueList();
			
			// SwingUtilities.invokeLater(doIjTask);
			ijTask.execute();
			// emptyQueue();
			System.out.println("Just invoked the task");
			// ijTask.doInBackground();
		} catch (Exception e) {
			e.printStackTrace();
			MainWindow.showGenericExceptionMessage(e);
		}//end catching URISyntaxException
	}//end processQueue()

	public void postProcessHandling(Result<String> outputData) {
		root.postProcessHandling(outputData);
		int prev_row_count = uxOutputTable.getRowCount();
		// group together SumResults which came from the same file path
		List<List<SumResult>> groupedResults = SumResult.groupResultsByFile(root.getIJProcess().lastProcResult);
		// process sumResults into string columns
		updateOutputTable(groupedResults);
		root.getImageQueue().removeAll(root.getProcessedImages());
		updateQueueList();
		// clear displayed image
		if (lastSelectedFrom == LastSelectedFrom.QueueList) {
			updateImageDisplay("%=empty");
			uxImagePropertiesTxt.setText("");
			lastSelectedFrom = LastSelectedFrom.NoSelection;
		}//end if we need to clear moved image
		// see about updating selections
		if (prev_row_count < uxOutputTable.getRowCount()) {
			uxOutputTable.changeSelection(prev_row_count, 0, false, false);
		}//end if we have a new row to select
		// make sure cursor is updated
		setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * Shows file chooser for adding files to processing queue.
	 * @param evt
	 */
	private void uxAddFilesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxAddFilesBtnActionPerformed
		int prev_list_count = uxQueueList.getModel().getSize();
		// adding selected files to queue should be handled by selectFIlesListener
		selectFilesChooser.showOpenDialog(this);
		updateQueueList();
		// make sure that new images in queue list show up to the right
		if (prev_list_count != uxQueueList.getModel().getSize()) {
			uxQueueList.setSelectedValue(uxQueueList.getModel().getElementAt(prev_list_count), true);
		}
	}//GEN-LAST:event_uxAddFilesBtnActionPerformed

	private void uxScanQueueBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxScanQueueBtnActionPerformed
		// ensure there is a valid filename to use
		if (!uxShouldOverwriteName.isSelected()) {
			if (uxOverwriteName.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "Please select a name for the scanned image.", "Image name left blank", JOptionPane.ERROR_MESSAGE);
				return;
			}//end if overwrite name is empty
		}//end if we aren't overwriting the name

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		@SuppressWarnings("unchecked")
		Result<File> scanResult = (Result<File>) root.handleMessage(InterfaceMessage.Scan,null);
		if (scanResult.isErr()) {showGenericExceptionMessage(scanResult.getError());}
		else if (scanResult.isOk()) {
			root.getImageQueue().add(scanResult.getValue());
			// allImages.add(scanResult.getValue());
			updateQueueList();
			// hopefully ensure that scanned image shows up immediately for user
			uxQueueList.setSelectedValue(scanResult.getValue(), true);
		}//end else if we can add something to the queue

		// ensure scan name is reset if we used it
		if (!uxShouldOverwriteName.isSelected()) {
			uxOverwriteName.setText("");
		}//end if we used the overwrite name

		setCursor(Cursor.getDefaultCursor());
	}//GEN-LAST:event_uxScanQueueBtnActionPerformed

	private void uxScanBigBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxScanBigBtnActionPerformed
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		@SuppressWarnings("unchecked")
		Result<File> scanResult = (Result<File>) root.handleMessage(InterfaceMessage.Scan,null);
		if (scanResult.isErr()) {showGenericExceptionMessage(scanResult.getError());}
		setCursor(Cursor.getDefaultCursor());
	}//GEN-LAST:event_uxScanBigBtnActionPerformed

	private void uxConnectToScannerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxConnectToScannerBtnActionPerformed
		// just trigger the top menu code
		uxConnectScannerBtnActionPerformed(evt);
	}//GEN-LAST:event_uxConnectToScannerBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton uxAddFilesBtn;
    private javax.swing.JButton uxClearOutputBtn;
    private javax.swing.JMenuItem uxConnectScannerBtn;
    private javax.swing.JButton uxConnectToScannerBtn;
    private javax.swing.JButton uxEmptyQueueBtn;
    private javax.swing.JMenuItem uxIjBtn;
    private javax.swing.JLabel uxImageLabel;
    private javax.swing.JLabel uxImageLabelE;
    private javax.swing.JLabel uxImageLabelK;
    public javax.swing.JTextArea uxImagePropertiesTxt;
    private javax.swing.JMenu uxInitMenu;
    private javax.swing.JButton uxNextImageBtn;
    private javax.swing.JButton uxOpenFileBtn;
    private javax.swing.JButton uxOpenOutputFile;
    public javax.swing.JTable uxOutputTable;
    public javax.swing.JTextField uxOverwriteName;
    private javax.swing.JButton uxPrevImageBtn;
    private javax.swing.JButton uxProcessAllBtn;
    private javax.swing.JList<String> uxQueueList;
    private javax.swing.JMenuItem uxResetScanner;
    private javax.swing.JMenu uxRunMenu;
    private javax.swing.JMenuItem uxScanAreaMenuBtn;
    private javax.swing.JButton uxScanBigBtn;
    private javax.swing.JMenuItem uxScanBtn;
    private javax.swing.JButton uxScanQueueBtn;
    private javax.swing.JMenuItem uxSetAreaFlagMenuBtn;
    private javax.swing.JMenuItem uxSetThresholdMenuBtn;
    private javax.swing.JMenuItem uxSetUnsharpMenuBtn;
    public javax.swing.JCheckBox uxShouldOutputKernImages;
    public javax.swing.JCheckBox uxShouldOverwriteName;
    private javax.swing.JTextArea uxStatusTxt;
    private javax.swing.JTextArea uxTitleBlockTxt;
    // End of variables declaration//GEN-END:variables
}
