/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View.Dialog;

import javax.swing.DefaultListModel;
import com.formdev.flatlaf.FlatLightLaf;

/**
 *
 * @author Nicholas.Sixbury
 */
public class ScanDpiDialog extends javax.swing.JDialog {

	DefaultListModel<Double> model = new DefaultListModel<>();

	/**
	 * Creates new form ScanDpiDialog
	 */
	public ScanDpiDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		FlatLightLaf.setup();
		initComponents();
		this.uxSupportedDpiList.setModel(model);
	}

	public int dpi = 300;
	public double[] supported_dpis = new double[0];

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        uxDpiSpnr = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        uxSupportedDpiList = new javax.swing.JList<>();
        uxCancelBtn = new javax.swing.JButton();
        uxConfirmBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Image DPI");

        uxDpiSpnr.setFont(uxDpiSpnr.getFont().deriveFont(uxDpiSpnr.getFont().getSize()+2f));

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getSize()+2f));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Image DPI:");

        uxSupportedDpiList.setFont(uxSupportedDpiList.getFont().deriveFont(uxSupportedDpiList.getFont().getSize()+2f));
        uxSupportedDpiList.setModel(model);
        jScrollPane1.setViewportView(uxSupportedDpiList);

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

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getSize()+2f));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Supported DPI Settings:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(uxCancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxConfirmBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uxDpiSpnr)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uxDpiSpnr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(uxCancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(uxConfirmBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * Updates interface based on public vars
	 */
	public void updateInterface() {
		this.uxDpiSpnr.setValue(dpi);
		model.clear();
		for (double d : supported_dpis) {
			model.addElement(d);
		}
		
	}//end updateInterface()

	@Override
	public void setVisible(boolean visibility) {
		updateInterface();
		super.setVisible(visibility);
	}//ebd setVisible(visibility)

    private void uxCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxCancelBtnActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_uxCancelBtnActionPerformed

    private void uxConfirmBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uxConfirmBtnActionPerformed
        this.dpi = (int) this.uxDpiSpnr.getValue();
		this.setVisible(false);
    }//GEN-LAST:event_uxConfirmBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton uxCancelBtn;
    private javax.swing.JButton uxConfirmBtn;
    private javax.swing.JSpinner uxDpiSpnr;
    private javax.swing.JList<Double> uxSupportedDpiList;
    // End of variables declaration//GEN-END:variables
}
