
package org.mandarax.examples.userv.app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mandarax.rt.DerivationLogEntry;
import org.mandarax.rt.ResultSet;

/**
 * Component to visualise a result set with multiple results.
 * @author jens dietrich
 */

public abstract class MultiValueResultView extends JPanel {
	private JList list = new JList();
	private JButton logButton = new JButton(" ? ");
	private List<Integer> values = new ArrayList<Integer>();
	private List<List<DerivationLogEntry>> logs = new ArrayList<List<DerivationLogEntry>>();
	
	public MultiValueResultView() {
		super();
		setLayout(new BorderLayout(5,5));
		add(new JScrollPane(list),BorderLayout.CENTER);
		JPanel bPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		logButton.setBorder(null);
		bPane.add(logButton);
		add(bPane,BorderLayout.SOUTH);
		logButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int pos = list.getSelectedIndex();
						if (pos<logs.size()) {
							DerivationLogViewer.displayUsedRules(logs.get(pos),MultiValueResultView.this);
						}
					}
				}
		);
		logButton.setEnabled(false);
		list.addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						int pos = list.getSelectedIndex();
						logButton.setEnabled(pos<logs.size());
					}
				}
		);
		list.addMouseListener(
				new MouseAdapter() {
					// to open derivation log on double click
					public void mouseClicked(MouseEvent e) {
						int pos = list.getSelectedIndex();
						if (e.getClickCount()>1 && pos<logs.size()) {
							DerivationLogViewer.displayUsedRules(logs.get(pos),MultiValueResultView.this);
						}
					}
				}
		);
		
	}
	
	public abstract int extractValue(Object rs);
	
	public void setResultSet(ResultSet<?> rs) {
		int total = 0;
		DefaultListModel model = new DefaultListModel();
		values.clear();
		logs.clear();
		while (rs.hasNext()) {
			int next = extractValue(rs.next());
			total = total+next;
			model.addElement(next);
			values.add(next);
			logs.add(rs.getDerivationLog());
		}
		rs.close();
		model.addElement("total: " + total);
		list.setModel(model);
		logButton.setEnabled(false);
	}
	
	
}
