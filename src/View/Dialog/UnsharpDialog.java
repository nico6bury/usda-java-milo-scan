/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package View.Dialog;

import javax.swing.JOptionPane;

import com.formdev.flatlaf.FlatLightLaf;

/**
 *
 * @author Nicholas.Sixbury
 */
public class UnsharpDialog extends javax.swing.JDialog {

	public double unsharp_sigma = 1.5;
	public double unsharp_weight = 0.5;
	public boolean unsharp_skip = false;
	public boolean unsharp_rename = true;

	/**
	 * Creates new form UnsharpDialog
	 */
	public UnsharpDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		FlatLightLaf.setup();
		initComponents();
	}

	/**
	 * Updates interface based on public vars
	 */
	public void updateInterface() {
		this.uxUnsharpSigmaSpnr.setValue(unsharp_sigma);
		this.uxUnsharpWeightSpnr.setValue(unsharp_weight);
		this.uxShouldSkipUnsharp.setSelected(unsharp_skip);
		this.uxShouldRenameUnsharpFile.setSelected(unsharp_rename);
	}//end updateInterface

	@Override
	public void setVisible(boolean visibility) {
		updateInterface();
		super.setVisible(visibility);
	}//end setVisible

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jLabel2 = new javax.swing.JLabel();
		uxParametersWhyBtn = new javax.swing.JButton();
		uxUnsharpSigmaSpnr = new javax.swing.JSpinner();
		jLabel1 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		uxUnsharpWeightSpnr = new javax.swing.JSpinner();
		uxParametersWhatBtn = new javax.swing.JButton();
		uxCancelBtn = new javax.swing.JButton();
		uxConfirmBtn = new javax.swing.JButton();
		uxShouldSkipUnsharp = new javax.swing.JCheckBox();
		uxShouldRenameUnsharpFile = new javax.swing.JCheckBox();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Unsharp Mask Configuration");

		jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getSize()+2f));
		jLabel2.setText("Please configure settings for unsharp mask applied to scanned image.");

		uxParametersWhyBtn.setFont(uxParametersWhyBtn.getFont().deriveFont(uxParametersWhyBtn.getFont().getSize()+2f));
		uxParametersWhyBtn.setText("Why is this needed?");
		uxParametersWhyBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				uxParametersWhyBtnActionPerformed(evt);
			}
		});

		uxUnsharpSigmaSpnr.setFont(uxUnsharpSigmaSpnr.getFont().deriveFont(uxUnsharpSigmaSpnr.getFont().getSize()+2f));
		uxUnsharpSigmaSpnr.setModel(new javax.swing.SpinnerNumberModel(1.5d, 0.5d, 3.0d, 0.1d));

		jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getSize()+2f));
		jLabel1.setText("Unsharp Mask Sigma");

		jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getSize()+2f));
		jLabel3.setText("Unsharp Mask Weight");

		uxUnsharpWeightSpnr.setFont(uxUnsharpWeightSpnr.getFont().deriveFont(uxUnsharpWeightSpnr.getFont().getSize()+2f));
		uxUnsharpWeightSpnr.setModel(new javax.swing.SpinnerNumberModel(0.5d, 0.1d, 0.9d, 0.1d));
		uxUnsharpWeightSpnr.setToolTipText("");

		uxParametersWhatBtn.setFont(uxParametersWhatBtn.getFont().deriveFont(uxParametersWhatBtn.getFont().getSize()+2f));
		uxParametersWhatBtn.setText("What do parameters mean?");
		uxParametersWhatBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				uxParametersWhatBtnActionPerformed(evt);
			}
		});

		uxCancelBtn.setFont(uxCancelBtn.getFont().deriveFont(uxCancelBtn.getFont().getSize()+2f));
		uxCancelBtn.setText("Cancel");
		uxCancelBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				uxCancelBtnActionPerformed(evt);
			}
		});

		uxConfirmBtn.setFont(uxConfirmBtn.getFont().deriveFont(uxConfirmBtn.getFont().getSize()+2f));
		uxConfirmBtn.setText("Confirm");
		uxConfirmBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				uxConfirmBtnActionPerformed(evt);
			}
		});

		uxShouldSkipUnsharp.setFont(uxShouldSkipUnsharp.getFont().deriveFont(uxShouldSkipUnsharp.getFont().getSize()+2f));
		uxShouldSkipUnsharp.setText("Skip Unsharp Mask");
		uxShouldSkipUnsharp.setToolTipText("If enabled, then the unsharp mask will not be performed for scanned files.");

		uxShouldRenameUnsharpFile.setFont(uxShouldRenameUnsharpFile.getFont().deriveFont(uxShouldRenameUnsharpFile.getFont().getSize()+2f));
		uxShouldRenameUnsharpFile.setText("Rename File for Unsharp Mask");
		uxShouldRenameUnsharpFile.setToolTipText("If off, the the unsharp masked image will overwrite the original.");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addComponent(uxParametersWhatBtn)
							.addComponent(uxParametersWhyBtn))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(uxCancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(uxConfirmBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addComponent(jLabel2)
					.addGroup(layout.createSequentialGroup()
						.addGap(6, 6, 6)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(layout.createSequentialGroup()
								.addComponent(jLabel1)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(uxUnsharpSigmaSpnr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addComponent(uxShouldSkipUnsharp, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(layout.createSequentialGroup()
								.addComponent(jLabel3)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(uxUnsharpWeightSpnr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addGroup(layout.createSequentialGroup()
								.addComponent(uxShouldRenameUnsharpFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGap(11, 11, 11)))))
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(jLabel2)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
					.addComponent(jLabel1)
					.addComponent(uxUnsharpSigmaSpnr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addComponent(jLabel3)
					.addComponent(uxUnsharpWeightSpnr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
					.addComponent(uxShouldSkipUnsharp)
					.addComponent(uxShouldRenameUnsharpFile))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
						.addComponent(uxParametersWhatBtn)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(uxParametersWhyBtn))
					.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(uxCancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(uxConfirmBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void uxConfirmBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxConfirmBtnActionPerformed
		this.unsharp_sigma = (double)this.uxUnsharpSigmaSpnr.getValue();
		this.unsharp_weight = (double)this.uxUnsharpWeightSpnr.getValue();
		this.unsharp_skip = uxShouldSkipUnsharp.isSelected();
		this.unsharp_rename = uxShouldRenameUnsharpFile.isSelected();
		this.setVisible(false);
	}//GEN-LAST:event_uxConfirmBtnActionPerformed

	private void uxCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxCancelBtnActionPerformed
		this.setVisible(false);
	}//GEN-LAST:event_uxCancelBtnActionPerformed

	private void uxParametersWhatBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxParametersWhatBtnActionPerformed
		String message = "Taken from https://imagej.net/ij/docs/menus/process.html#filters :\n\"Unsharp masking subtracts a blurred copy \nof the image and rescales the image to obtain the same contrast \nof large (low-frequency) structures as in the input image. This \nis equivalent to adding a high-pass filtered image and thus \nsharpens the image. (Sigma) Radius is the standard deviation \n(blur radius) of the Gaussian blur that is subtracted. Mask Weight \ndetermines the strength of filtering, whereby Mask Weight=1 would \nbe an infinite weight of the high-pass filtered image that is added.\"";
		JOptionPane.showMessageDialog(this, message);
	}//GEN-LAST:event_uxParametersWhatBtnActionPerformed

	private void uxParametersWhyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxParametersWhyBtnActionPerformed
		String message = "" + 
		"This program seeks to recreate certain scan settings from the \nEPSON Scan utility. The settings we seek to recreate used EPSON's \nunsharp mask, but because that's a post-processing effect, it's \nnot accessible through TWAIN or Morena, and we therefore have to \nrecreate that effect separately. EPSON does not provide the exact \nparameters used for their unsharp mask, so instead it is \npartially left to the user to find parameters that fit their needs.";
		JOptionPane.showMessageDialog(this, message);
	}//GEN-LAST:event_uxParametersWhyBtnActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JButton uxCancelBtn;
	private javax.swing.JButton uxConfirmBtn;
	private javax.swing.JButton uxParametersWhatBtn;
	private javax.swing.JButton uxParametersWhyBtn;
	private javax.swing.JCheckBox uxShouldRenameUnsharpFile;
	private javax.swing.JCheckBox uxShouldSkipUnsharp;
	private javax.swing.JSpinner uxUnsharpSigmaSpnr;
	private javax.swing.JSpinner uxUnsharpWeightSpnr;
	// End of variables declaration//GEN-END:variables
}