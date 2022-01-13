
package org.mandarax.examples.userv.app;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mandarax.examples.userv.domain.*;
import org.mandarax.rt.DerivationLogEntry;
import org.mandarax.rt.ResultSet;
// the code generated from rules is in here:
import org.mandarax.examples.userv.rules.generated.*;

/**
 * The main interface of the UServ application, used within UServApp.
 * @author <a href="http://www-ist.massey.ac.nz/JBDietrich/">Jens Dietrich</a>
 */

public class UServPanel extends JPanel {
	// models
	private Driver driver = new Driver();
	private Car car = new Car();
	private Policy policy = new Policy();
	private Map<String,List<DerivationLogEntry>> derivationLogs = new HashMap<String,List<DerivationLogEntry>> ();
	
	// constants
	private final static String[] STATES = {
		"unknown","CA","FL","NY"
	};
	private final static String[] CAR_CATEGORIES = {
		"compact","sedan","luxury","svu","other"
	};
	
	// parts	
	JPanel panel = this;
	JPanel carPanel = new JPanel(new GridBagLayout());
	JPanel driverPanel = new JPanel(new GridBagLayout());
	JPanel resultPanel = new JPanel(new GridBagLayout());
	JPanel policyPanel = new JPanel(new GridBagLayout());
	JPanel driverEligibilityPanel = new JPanel(new GridBagLayout());
	JPanel policyPremiumPanel = new JPanel(new GridBagLayout());
	JPanel discountPanel = new JPanel(new GridBagLayout());
	
	int[] rowCounts = {-1,-1,-1,-1,-1,-1,-1,-1,-1};
	
	// output components
	JTextField txtDriverCategory = createOutputTextField();
	JTextField txtDriverEligibility = createOutputTextField();
	JTextField txtHasTrainingCertification = createOutputTextField();
	JTextField txtHighRiskDriver = createOutputTextField();
	MultiValueResultView multiOutPolicyEligibilityScore = new MultiValueResultView() {
		public int extractValue(Object object) {
			PolicyEligibilityScoreRel pes = (PolicyEligibilityScoreRel)object;
			return (int)pes.score;
		}
	};
	JTextField txtInsuranceEligibility = createOutputTextField();
	JTextField txtLongTermClient = createOutputTextField();
	JTextField txtBasePremium = createOutputTextField();
	MultiValueResultView multiAdditionalPremium = new MultiValueResultView() {
		public int extractValue(Object object) {
			AdditionalPremiumRel pes = (AdditionalPremiumRel)object;
			return (int)pes.premium;
		}
	};
	MultiValueResultView multiAdditionalDriverPremium = new MultiValueResultView() {
		public int extractValue(Object object) {
			AdditionalDriverPremiumRel pes = (AdditionalDriverPremiumRel)object;
			return (int)pes.premium;
		}
	};
	MultiValueResultView multiPremiumDiscount = new MultiValueResultView() {
		public int extractValue(Object object) {
			PremiumDiscountRel pes = (PremiumDiscountRel)object;
			return (int)pes.discount;
		}
	};
	public UServPanel() {
		super();
		init();
	}
	
	private JTextField createOutputTextField() {
		JTextField txtField = new JTextField();
		txtField.setEditable(false);
		return txtField;
	}
	


	private void init() {
				
		// application can set objects referenced in rules
		PotentialTheftRatingRelInstances.highTheftProbabilityAutoList = HighTheftProbabilityAutoList.getList();
		IsNewRelInstances.CurrentYear = new GregorianCalendar().get(Calendar.YEAR);
		
		this.setLayout(new GridLayout(2,1));
		
		// input pane
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(3,3,3,3), 
						BorderFactory.createTitledBorder(" input "))
		);
		this.add(tabbedPane);
		
		// driver editor
		activatePanel(tabbedPane,driverPanel, "driver details");
		
		add(createIntEditor(driver,"getAge","setAge",0,100),"age",0,40);
		add(createSelectStringEditor(driver,"getLocation","setLocation",STATES),"location",0,100);
		add(createBooleanEditor(driver,"isMarried","setMarried"),"is married",0);
		add(createBooleanEditor(driver,"isMale","setMale"),"is male",0);
		
		add(createBooleanEditor(driver,"isElite","setElite"),"is elite",1);
		add(createBooleanEditor(driver,"isPreferred","setPreferred"),"is preferred",1);		
		add(createBooleanEditor(driver,"hasDriversTrainingFromSchool","setHasDriversTrainingFromSchool"),"has drivers training from school",1);
		add(createBooleanEditor(driver,"hasDriversTrainingFromLicensedDriverTrainingCompany","setHasDriversTrainingFromLicensedDriverTrainingCompany"),"<html><body align=\"right\">has drivers training from licensed<br> driver training company</body></html>",1);
		add(createBooleanEditor(driver,"hasTakenASeniorCitizenDriversRefresherCourse","setHasTakenASeniorCitizenDriversRefresherCourse"),"<html><body align=\"right\">has taken a senior citizen<br>drivers refresher course</body></html>",1);
		
		add(createBooleanEditor(driver,"hasBeenConvictedOfaDUI","setHasBeenConvictedOfaDUI"),"has been convicted of a DUI",2);	
		add(createIntEditor(driver,"getNumberOfAccidentsInvolvedIn","setNumberOfAccidentsInvolvedIn",0,30),"number of accidents involved in",2,40);
		add(createIntEditor(driver,"getNumberOfMovingViolationsInLastTwoYears","setNumberOfMovingViolationsInLastTwoYears",0,100),"<html><body align=\"right\">number of moving violations<br>in last two years</body></html>",2,40);
		add(createIntEditor(driver,"getNumberOfYearsWithUServ","setNumberOfYearsWithUServ",0,50),"number of years with UServ",2,40);
		
		// car editor
		activatePanel(tabbedPane,carPanel, "car details");
		add(createBooleanEditor(car,"isConvertible","setConvertible"),"is convertible",0);
		add(createSelectStringEditor(car,"getCategory","setCategory",CAR_CATEGORIES),"category",0);
		add(createIntEditor(car,"getAge","setAge",0,50),"age",0,40);
		add(createIntEditor(car,"getModelYear","setModelYear",1950,new GregorianCalendar().get(Calendar.YEAR)+1),"model year",0,80);
		add(createIntEditor(car,"getPrice","setPrice",1000,100000,100),"price",0,80);
		
		add(createBooleanEditor(car,"hasRollBar","setHasRollBar"),"has rollbar",1);
		add(createBooleanEditor(car,"hasDriversAirbag","setHasDriversAirbag"),"has drivers airbag",1);
		add(createBooleanEditor(car,"hasFrontPassengerAirbag","setHasFrontPassengerAirbag"),"has front passenger airbag",1);
		add(createBooleanEditor(car,"hasSidePanelAirbags","setHasSidePanelAirbags"),"has side panel airbags",1);
		add(createBooleanEditor(car,"hasAlarm","setHasAlarm"),"has alarm",1);

		// policy editor
		activatePanel(tabbedPane,policyPanel, "policy details");	
		add(createBooleanEditor(policy,"includesMedicalCoverage","setIncludesMedicalCoverage"),"includes medical coverage",0);
		add(createBooleanEditor(policy,"includesUninsuredMotoristCoverage","setIncludesUninsuredMotoristCoverage"),"includes uninsured motorist coverage",1);
		
		// output		
		tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(3,3,3,3), 
						BorderFactory.createTitledBorder(" output "))
		);
		this.add(tabbedPane);
		
		// driver eligibility
		activatePanel(tabbedPane,driverEligibilityPanel, "driver eligibility");	
		
		resultPanel.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(3,3,3,3), 
						BorderFactory.createTitledBorder(" results "))
		);
		addOut(this.txtDriverCategory,"driver category",0,100);
		addOut(this.txtDriverEligibility,"is eligible",0,40);
		addOut(this.txtHasTrainingCertification,"has training certification",0,40);
		addOut(this.txtHighRiskDriver,"is high risk driver",0,40);
		addOut(this.txtLongTermClient,"is long term client",0,40);
		addOut(this.multiOutPolicyEligibilityScore,"policy eligibility score",1,80);
		addOut(this.txtInsuranceEligibility,"insurance eligibility",1,200);
		
		// premium
		activatePanel(tabbedPane,policyPremiumPanel, "policy premium");		
		addOut(this.multiAdditionalPremium,"<html><body align=\"right\">additional<br/>premium</body></html>",0,80);
		addOut(this.txtBasePremium,"base premium",0,100); 
		addOut(this.multiAdditionalDriverPremium,"<html><body align=\"right\">additional<br/>driver<br/>premium</body></html>",1,80);
		addOut(this.multiPremiumDiscount,"<html><body align=\"right\">premium<br/>discounts</body></html>",2,80);
		
		// apply rules to initial settings
		this.applyRules();
	}
	
	private void activatePanel(JTabbedPane tabbedPane,JPanel panel,String name) {
		
		JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER));
		tabbedPane.add(new JScrollPane(container), name);
		container.add(panel);
		this.setPanel(panel);
	} 
	
	private void makeEqual() {
		int max = 0;
		for (int i:rowCounts) 
			max = Math.max(max,i);
		for (int i=0;i<rowCounts.length;i++)
			rowCounts[i]=max;
		
	}

	private void addSep(String label, int... cols) {
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.ipady = 5;
		c.gridx = cols[0]*3;
		c.gridwidth = cols.length*3;
		c.anchor = c.WEST;
		c.fill = c.HORIZONTAL;
		JLabel jlabel = new JLabel();
		jlabel.setText(label);
		jlabel.setHorizontalAlignment(jlabel.CENTER);
		c.insets = new Insets(1,3,1,3);

		c.insets = new Insets(3,3,3,3);
		for (int col:cols)
			c.gridy = ++rowCounts[col];
		panel.add(new JLabel(),c);
		panel.add(jlabel,c);
		for (int col:cols)
			c.gridy = ++rowCounts[col];
		c.insets = new Insets(1,3,1,3);
		panel.add(new JSeparator(),c);
	
	}
	private void add(JComponent component,String label, int col) {
		add(component,label,col,-1);
	} 
	private void addOut(JComponent component,String label, int col) {
		addOut(component,label,col,-1);
	} 
	private void add(JComponent component,String label, int col, int width) {
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.ipady = 5;
		c.insets = new Insets(3,3,3,3);
		c.gridx = 3*col;
		c.gridy = ++rowCounts[col];
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = c.EAST;
		c.fill = c.HORIZONTAL;
		JLabel jlabel = new JLabel();
		jlabel.setText(label);
		jlabel.setHorizontalAlignment(jlabel.RIGHT);
		panel.add(jlabel,c);
		c.anchor = c.WEST;
		c.gridx = 3*col+1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = c.NONE;
		if (width!=-1) {
			Dimension size = component.getPreferredSize();
			size.width=width;
			component.setPreferredSize(size);
			// component.setSize(size);
		}
		panel.add(component,c);
	} 
	private void addOut(JComponent component,final String label, int col, int width) {
		component.setName(label);
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.ipady = 5;
		c.insets = new Insets(3,3,3,3);
		c.gridx = 3*col;
		c.gridy = ++rowCounts[col];
		c.gridwidth = 1;
		c.anchor = c.EAST;
		c.fill = c.NONE;
		JLabel jlabel = new JLabel();
		jlabel.setText(label);
		jlabel.setHorizontalAlignment(jlabel.RIGHT);
		panel.add(jlabel,c);
		c.anchor = c.WEST;
		c.gridx = 3*col+1;
		c.gridwidth = 1;
		c.weightx = 2;
		//c.fill = c.HORIZONTAL;
		if (width!=-1) {
			Dimension size = component.getPreferredSize();
			size.width=width;
			component.setPreferredSize(size);
			// component.setSize(size);
		}
		JPanel p = new JPanel(new BorderLayout(5,5));
		p.add(component,BorderLayout.CENTER);
		
		JButton butDetails = new JButton(" ? ");
		butDetails.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						List<DerivationLogEntry> logs = UServPanel.this.derivationLogs.get(label);
						displayUsedRules(logs);
					}
				}
		);
		butDetails.setBorder(null);
		p.add(butDetails,BorderLayout.EAST);
		panel.add(p,c);
	} 
	
	private void addOut(MultiValueResultView component,final String label, int col, int width) {
		component.setBorder(BorderFactory.createEtchedBorder());
		component.setName(label);
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.ipady = 5;
		c.insets = new Insets(3,3,3,3);
		c.gridx = 3*col;
		c.gridy = ++rowCounts[col];
		c.gridwidth = 1;
		c.gridheight = 5;
		c.anchor = c.NORTHEAST;
		c.fill = c.NONE;
		JLabel jlabel = new JLabel();
		jlabel.setText(label);
		jlabel.setHorizontalAlignment(jlabel.RIGHT);
		panel.add(jlabel,c);
		c.anchor = c.WEST;
		c.gridx = 3*col+1;
		c.gridwidth = 2;
		c.weightx = 2;
		c.fill = c.VERTICAL;
		Dimension size = component.getPreferredSize();
		if (width!=-1) {
			size.width=width;
		}
		size.height=150;
		component.setPreferredSize(size);
		panel.add(component,c);
		
		for (int i=0;i<c.gridheight-1;i++) {
			++rowCounts[col];
		}

	}
	
	private void displayUsedRules(List<DerivationLogEntry> logs) {
		DerivationLogViewer.displayUsedRules(logs,this);	
	}

	private JCheckBox createBooleanEditor(final Object bean, final String getter, final String setter) {
		final JCheckBox box = new JCheckBox();
		// set value
		try {
			Boolean value = (Boolean)readValue(bean,getter);
			box.setSelected(value);
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		// listener
		box.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				writeValue(bean,setter,Boolean.TYPE,box.isSelected());
			}});
		
		return box;
	}
	
	private JComboBox createSelectStringEditor(final Object bean, final String getter, final String setter,String[] list) {
		final JComboBox box = new JComboBox(list);
		// set value
		try {
			String value = (String)readValue(bean,getter);
			box.setSelectedItem(value);
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		// listener
		box.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				writeValue(bean,setter,String.class,box.getSelectedItem());
			}});
		
		return box;
	}
	
	private JSpinner createIntEditor(final Object bean, final String getter, final String setter, int min, int max) {
		return createIntEditor(bean,getter,setter,min,max,1);
	}
	
	private JSpinner createIntEditor(final Object bean, final String getter, final String setter, int min, int max,int step) {
		final JSpinner spinner = new JSpinner();

		// set value
		try {
			int value = (Integer)readValue(bean,getter);
			SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step); 
			spinner.setModel(model);
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		// listener
		spinner.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				writeValue(bean,setter,Integer.TYPE,spinner.getValue());
			}});
		
		return spinner;
	}
	
	private Object readValue(Object bean,String getter) {
		try {
			return bean.getClass().getMethod(getter, new Class[]{}).invoke(bean,new Object[]{});
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		return null;
	}
	private void writeValue(Object bean, String setter, Class type, Object value) {
		try {
			Method m = bean.getClass().getMethod(setter, new Class[]{type});
			m.invoke(bean,new Object[]{value});
		}
		catch (Exception x) {
			x.printStackTrace();
		}	
		// System.out.println("Setting " + bean + "#" + setter + " -> " + value);
		applyRules();
	}
	
	private void applyRules() {
		String driverCategory = "?";
		ResultSet<DriverCategoryRel> rs1 = DriverCategoryRelInstances.getCategory(driver);
		if (rs1.hasNext()) {
			driverCategory = rs1.next().category;
			this.derivationLogs.put(txtDriverCategory.getName(),rs1.getDerivationLog());
		}
		else {
			this.derivationLogs.put(txtDriverCategory.getName(),new ArrayList());
		}
		this.txtDriverCategory.setText(driverCategory);
		
		
		String driverEligibility = "no";
		ResultSet<IsEligibleRel> rs2 = IsEligibleRelInstances.isEligible(driver);
		if (rs2.hasNext()) {
			driverEligibility = "yes";
			this.derivationLogs.put(txtDriverEligibility.getName(),rs2.getDerivationLog());
		}
		else {
			this.derivationLogs.put(txtDriverEligibility.getName(),new ArrayList());
		}
		this.txtDriverEligibility.setText(driverEligibility);
		
		
		String trainingCertification = "no";
		ResultSet<HasTrainingCertificationRel> rs3 = HasTrainingCertificationRelInstances.hasDertification(driver);
		if (rs3.hasNext()) {
			trainingCertification = "yes";
			this.derivationLogs.put(txtHasTrainingCertification.getName(),rs3.getDerivationLog());
		}
		else {
			this.derivationLogs.put(txtHasTrainingCertification.getName(),new ArrayList());
		}
		this.txtHasTrainingCertification.setText(trainingCertification);
		
			
		String highRiskDriver = "no";
		ResultSet<IsHighRiskDriverRel> rs4 = IsHighRiskDriverRelInstances.isHighRiskDriver(driver);
		if (rs4.hasNext()) {
			highRiskDriver = "yes";
			this.derivationLogs.put(txtHighRiskDriver.getName(),rs4.getDerivationLog());
		}
		else {
			this.derivationLogs.put(txtHighRiskDriver.getName(),new ArrayList());
		};
		this.txtHighRiskDriver.setText(highRiskDriver);

		ResultSet<PolicyEligibilityScoreRel> rs5 = PolicyEligibilityScoreRelInstances.getScore(car, driver);
		this.multiOutPolicyEligibilityScore.setResultSet(rs5);
		
		String insuranceEligibility = "?";
		ResultSet<InsuranceEligibilityRel> rs6 = InsuranceEligibilityRelInstances.getEligibility(car, driver);
		if (rs6.hasNext())
			insuranceEligibility = rs6.next().eligibility;
		this.txtInsuranceEligibility.setText(insuranceEligibility);
		this.derivationLogs.put(txtInsuranceEligibility.getName(),rs6.getDerivationLog());
		
		String basePremium = "?";
		ResultSet<BasePremiumRel> rs7 = BasePremiumRelInstances.getPremium(car);
		if (rs7.hasNext()) {
			basePremium = ""+rs7.next().premium;
			this.derivationLogs.put(txtBasePremium.getName(),rs7.getDerivationLog());
		}
		else {
			this.derivationLogs.put(txtBasePremium.getName(),new ArrayList());
		}
		this.txtBasePremium.setText(basePremium);
		
		ResultSet<AdditionalDriverPremiumRel> rs8 = AdditionalDriverPremiumRelInstances.getPremium(driver);
		this.multiAdditionalDriverPremium.setResultSet(rs8);
		
		ResultSet<AdditionalPremiumRel> rs9 = AdditionalPremiumRelInstances.getPremium(policy, car);
		this.multiAdditionalPremium.setResultSet(rs9);
		
		ResultSet<PremiumDiscountRel> rs10 = PremiumDiscountRelInstances.getDiscount(car);
		this.multiPremiumDiscount.setResultSet(rs10);
		
		String longTermClient = "no";
		ResultSet<IsLongTermClientRel> rs11 = IsLongTermClientRelInstances.isLongTermClient(driver);
		if (rs11.hasNext()) {
			longTermClient = "yes";
			this.derivationLogs.put(this.txtLongTermClient.getName(),rs11.getDerivationLog());
		}
		else {
			this.derivationLogs.put(txtLongTermClient.getName(),new ArrayList());
		}
		this.txtLongTermClient.setText(longTermClient);
	}
	
	private void setPanel(JPanel pane) {
		this.panel = pane;
		rowCounts = new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1};
	}

}
