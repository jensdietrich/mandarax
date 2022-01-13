
package org.mandarax.examples.userv.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * Executable UServ application. Starts a user interface to interact with the rules.
 * Static compilation is used, i.e. the rules are compiled at design time.
 * This could be done at runtime as well, see take test cases for an example.
 * http://www.businessrulesforum.com/2005_Product_Derby.pdf 
 * @author jens dietrich
 */

public class Main extends JFrame {

	public static void main(String[] args) {
		Main app = new Main();
		app.setTitle("UServ Sample Application implemented using Mandarax");
		app.setVisible(true);
	}

	public Main() throws HeadlessException {
		super();
		init();
	}

	public Main(GraphicsConfiguration gc) {
		super(gc);
		init();
	}

	public Main(String title, GraphicsConfiguration gc) {
		super(title, gc);
		init();
	}

	public Main(String title) throws HeadlessException {
		super(title);
		init();
	}
	
	private void init() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("UServ");
		JPanel pane = new JPanel(new BorderLayout(5,5));
		JToolBar toolbar = new JToolBar();
		pane.add(toolbar,BorderLayout.NORTH);
		toolbar.setFloatable(false);
		toolbar.add(new AbstractAction("exit"){
			public void actionPerformed(ActionEvent e) {
				Main.this.dispose();
			}});
		toolbar.addSeparator();
		/**
		toolbar.add(new AbstractAction("validate rules"){
			public void actionPerformed(ActionEvent e) {
				validateRules();
			}});
		**/
		toolbar.add(new AbstractAction("show rules"){
			public void actionPerformed(ActionEvent e) {
				ScriptViewer.showScript();
			}});
		toolbar.addSeparator();
		toolbar.add(new AbstractAction("about"){
			public void actionPerformed(ActionEvent e) {
				about();
			}});
		
		
		JPanel butPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel lab = new JLabel("UServ Product Derby Case Study is Copyright Business Rules Forum (http://www.businessrulesforum.com/)");
		lab.setHorizontalAlignment(JLabel.RIGHT);
		butPane.add(lab);
		pane.add(butPane,BorderLayout.SOUTH);
		
		pane.add(new UServPanel(),BorderLayout.CENTER);
		
		setContentPane(pane);
		int W=800,H=700;
		setSize(W,H);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width-W)/2,(screen.height-H)/2);
	}

	
	private void about() {
		String about = 	"<html>"+
						"UServ Implementation based on mandarax." +
						"</html>";
		JOptionPane.showMessageDialog(this,about,"About UServ",JOptionPane.INFORMATION_MESSAGE);
	}
	
}
