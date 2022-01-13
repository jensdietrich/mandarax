package org.mandarax.examples.userv.domain;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Bean class that is part of the example domain model.
 * http://www.businessrulesforum.com/2005_Product_Derby.pdf 
 * @author jens dietrich
 */

public class Car {
	private boolean isConvertible = false;
	private int price = 30000;
	private boolean  hasDriversAirbag = false;
	private boolean  hasFrontPassengerAirbag = false;
	private boolean  hasSidePanelAirbags = false;
	private boolean  hasRollBar = true;
	private int age = 0;
	private String category = "compact";
	private int modelYear = new GregorianCalendar().get(Calendar.YEAR);
	private boolean hasAlarm = false;
	
	private String  type = "unknown";
	
	public boolean hasDriversAirbag() {
		return hasDriversAirbag;
	}
	public void setHasDriversAirbag(boolean hasDriversAirbag) {
		this.hasDriversAirbag = hasDriversAirbag;
	}
	public boolean hasFrontPassengerAirbag() {
		return hasFrontPassengerAirbag;
	}
	public void setHasFrontPassengerAirbag(boolean hasFrontPassengerAirbag) {
		this.hasFrontPassengerAirbag = hasFrontPassengerAirbag;
	}
	public boolean hasRollBar() {
		return hasRollBar;
	}
	public void setHasRollBar(boolean hasRollBar) {
		this.hasRollBar = hasRollBar;
	}
	public boolean hasSidePanelAirbags() {
		return hasSidePanelAirbags;
	}
	public boolean hasAirbags() {
		return this.hasDriversAirbag || this.hasFrontPassengerAirbag || this.hasSidePanelAirbags;
	}
	public void setHasSidePanelAirbags(boolean hasSidePanelAirbags) {
		this.hasSidePanelAirbags = hasSidePanelAirbags;
	}
	public boolean isConvertible() {
		return isConvertible;
	}
	public void setConvertible(boolean isConvertible) {
		this.isConvertible = isConvertible;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isCompact() {
		return "compact".equals(this.category);
	}

	public boolean isSedan() {
		return "sedan".equals(this.category);
	}

	public boolean isLuxury() {
		return "luxury".equals(this.category);
	}

	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getModelYear() {
		return modelYear;
	}
	public void setModelYear(int modelYear) {
		this.modelYear = modelYear;
	}
	public boolean hasAlarm() {
		return hasAlarm;
	}
	public void setHasAlarm(boolean hasAlarm) {
		this.hasAlarm = hasAlarm;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
}
