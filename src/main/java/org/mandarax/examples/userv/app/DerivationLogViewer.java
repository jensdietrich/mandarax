
package org.mandarax.examples.userv.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.mandarax.rt.DerivationLogEntry;

/**
 * Utility to show the derivation log in a pop up window.
 * @author jens dietrich
 */

public class DerivationLogViewer {
	
	@SuppressWarnings("serial")
	private static TreeCellRenderer renderer= new DefaultTreeCellRenderer () {

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object object, boolean arg2, boolean arg3, boolean arg4, int arg5, boolean arg6) {
			
			JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, object, arg2, arg3, arg4, arg5, arg6);
			if (object instanceof DerivationLogEntry) {
				DerivationLogEntry e = (DerivationLogEntry)object;
				String description = e.getAnnotation("description");
				label.setText(e.getName() + (e==null?"":(": "+description)));
			}
			return label;
		}
		
	};

	public static void displayUsedRules(List<DerivationLogEntry> log,JComponent parentComponent) {
		
		if (log.size()==0) 
			JOptionPane.showMessageDialog(parentComponent, "There are no applicable rules", "", JOptionPane.WARNING_MESSAGE);
		else {
			DerivationModel treeModel = new DerivationModel(log);
			JTree tree = new JTree(treeModel);
			tree.setShowsRootHandles(true);
			tree.setRootVisible(false);
			tree.setPreferredSize(new Dimension(700,400));
			tree.setCellRenderer(renderer);
			final JDialog win = new JDialog();
			win.setResizable(true);
			JPanel pane = new JPanel();
			pane.setLayout(new BorderLayout(5,5));
			win.setContentPane(pane);
			pane.add(new JScrollPane(tree),BorderLayout.CENTER);
			JPanel bPane = new JPanel(new FlowLayout());
			JButton cButton = new JButton("close");
			cButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					win.dispose();
				}});
			bPane.add(cButton);
			pane.add(bPane,BorderLayout.SOUTH);
			win.setTitle("rules used");
			win.setSize(700,300);
			win.setLocation(100,100);
			win.setVisible(true);
		}
	}
	
}
