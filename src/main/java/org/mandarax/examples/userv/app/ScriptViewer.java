
package org.mandarax.examples.userv.app;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Script viewer. Contains some basic filtering and syntax highlighting.
 * @author jens dietrich
 */

public class ScriptViewer extends JFrame {
	
	static String[] ruleSets = new String[] {
		"AdditionalDriverPremium.rel",
		"AdditionalPremium.rel",
		"AutoEligibility.rel",
		"BasePremium.rel",
		"DriverCategory.rel",
		"HasTrainingCertification.rel",
		"InsuranceEligibility.rel",
		"IsEligible.rel",
		"IsHighRiskDriver.rel",
		"IsLongTermClient.rel",
		"IsNew.rel",
		"IsSpecialLocation.rel",
		"PolicyEligibilityScore.rel",
		"PotentialOccupantInjuryRating.rel",
		"PotentialTheftRating.rel",
		"PremiumDiscount.rel"
	};

	static String[] KEYWORDS = {"package","import","static","&","->","queries","rel","extends","min","max","count","avg","sum"};
	static SimpleAttributeSet PLAIN = new SimpleAttributeSet();
	static SimpleAttributeSet KEYWORD = new SimpleAttributeSet();
	static SimpleAttributeSet ID = new SimpleAttributeSet();
	static SimpleAttributeSet ANNOTATION = new SimpleAttributeSet();
	static SimpleAttributeSet COMMENT = new SimpleAttributeSet();
	  
  	static {
		    StyleConstants.setForeground(PLAIN, Color.black);
		    StyleConstants.setFontFamily(PLAIN, "Helvetica");
		    StyleConstants.setFontSize(PLAIN, 14);
		    
		    StyleConstants.setForeground(ANNOTATION, new Color(60,160,90));
		    StyleConstants.setItalic(ANNOTATION, true);
		    StyleConstants.setFontFamily(ANNOTATION, "Helvetica");
		    StyleConstants.setFontSize(ANNOTATION, 14);
		    
		    StyleConstants.setForeground(COMMENT,new Color(160,90,60));
		    StyleConstants.setItalic(COMMENT, true);
		    StyleConstants.setFontFamily(COMMENT, "Helvetica");
		    StyleConstants.setFontSize(COMMENT, 14);
		    
		    StyleConstants.setForeground(KEYWORD, new Color(90,60,160));
		    StyleConstants.setFontFamily(KEYWORD, "Helvetica");
		    StyleConstants.setFontSize(KEYWORD, 14);
		    StyleConstants.setBold(KEYWORD, true);
		    
    }
	  
	  
	// components
	private JTextPane textPane = new JTextPane();;
	
	
	public static void showScript() {
		ScriptViewer viewer = new ScriptViewer();
		int W=900,H=700;
		viewer.setSize(W,H);
		viewer.setTitle("UServ rules");
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		viewer.setLocation((screen.width-W)/2,(screen.height-H)/2);
		viewer.setVisible(true);
	}
	
	public ScriptViewer() throws HeadlessException {
		super();
		init();
	}

	public ScriptViewer(GraphicsConfiguration gc) {
		super(gc);
		init();
	}

	public ScriptViewer(String title, GraphicsConfiguration gc) {
		super(title, gc);
		init();
	}

	public ScriptViewer(String title) throws HeadlessException {
		super(title);
		init();
	}
	private void init() {
		JPanel pane = new JPanel(new BorderLayout());
		
		JPanel nPane =  new JPanel(new FlowLayout(FlowLayout.CENTER));
		final JComboBox cbx = new JComboBox(ruleSets);
		cbx.setSelectedIndex(0);
		cbx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				loadScript(ruleSets[cbx.getSelectedIndex()]);
			}});
		nPane.add(new JLabel("Rule set:"));
		nPane.add(cbx);
		pane.add(nPane,BorderLayout.NORTH);
		
		JPanel sPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		final JButton butExit = new JButton("close");
		butExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		sPane.add(butExit);
		pane.add(sPane,BorderLayout.SOUTH);
		
		pane.add(new JScrollPane(textPane),BorderLayout.CENTER);
		
		this.setContentPane(new JScrollPane(pane));
		loadScript(ruleSets[cbx.getSelectedIndex()]);
	}
	
	private void loadScript(String source) {
		try {
			InputStream in = this.getClass().getResourceAsStream("/org/mandarax/examples/userv/rules/"+source);
			java.io.LineNumberReader reader = new java.io.LineNumberReader(new java.io.InputStreamReader(in));
			String line = null;
			textPane.setText(""); // clear
			while ((line=reader.readLine())!=null) {
				if (doShow(line)) { 
					// line formatting
					
					if (this.isAnnotation(line)) 
						insertText(line+"\n",this.ANNOTATION);
					else if (this.isComment(line)) 
						insertText(line+"\n",this.COMMENT);
					else {
						// tokenize
						StringTokenizer tok = new StringTokenizer(line," ");
						while (tok.hasMoreTokens()) {
							String token=tok.nextToken();
							if (isKeyword(token)) 
								insertText(token,this.KEYWORD);
							else 
								insertText(token,this.PLAIN);
							insertText(" ",this.PLAIN);
						}
						insertText("\n",this.PLAIN);
					}
				}
			}
		}
		catch (IOException x) {
			this.textPane.setText("Error - cannot read script. Check console for details");
			x.printStackTrace();
		}
	}
	private boolean isKeyword(String token) {
		String s = token.trim();
		for (String k:KEYWORDS) {
			if (k.equals(s))
				return true;
		}
		return false;
	}

	// whether to show a line
	private boolean doShow(String line) {
		return true;
	}
	private boolean isAnnotation(String line) {
		return line.trim().startsWith("@");
	}
	private boolean isComment(String line) {
		return line.trim().startsWith("//");
	}
	
	private void insertText(String text, AttributeSet set) {
		try {
			textPane.getDocument().insertString(textPane.getDocument().getLength(), text, set);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
