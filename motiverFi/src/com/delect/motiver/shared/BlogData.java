/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BlogData implements IsSerializable {

	private List<CardioValueModel> cardios = new ArrayList<CardioValueModel>();
	/**
	 * Date which this blog type is assigned
	 */
	private Date date = null;
	private List<MeasurementValueModel> measurements = new ArrayList<MeasurementValueModel>();
	private NutritionDayModel nutritionDayModel = null;
	private List<RunValueModel> runs = new ArrayList<RunValueModel>();
	private UserModel user;
	private List<WorkoutModel> workouts = new ArrayList<WorkoutModel>();

	
	/**
	 * Cardios for current day
	 */
	public List<CardioValueModel> getCardios() {
		return cardios;
	}
	/**
	 * Date which this blog type is assigned
	 * @return
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * Measurements for current day
	 */
	public List<MeasurementValueModel> getMeasurements() {
		return measurements;
	}
	/**
	 * if nutrition found
	 */
	public NutritionDayModel getNutrition() {
		return nutritionDayModel;
	}
	/**
	 * Runs for current day
	 */
	public List<RunValueModel> getRuns() {
		return runs;
	}
	/**
	 * Returns who's data this is
	 * @return
	 */
	public UserModel getUser() {
		return user;
	}
	
	/**
	 * Workouts for current day
	 */
	public List<WorkoutModel> getWorkouts() {
		return workouts;
	}
	public void setCardios(List<CardioValueModel> cardios) {
		this.cardios = cardios;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setMeasurements(List<MeasurementValueModel> measurements) {
		this.measurements = measurements;
	}
	public void setNutrition(NutritionDayModel nutritionDayModel) {
		this.nutritionDayModel = nutritionDayModel;
	}
	public void setRuns(List<RunValueModel> runs) {
		this.runs = runs;
	}
	public void setUser(UserModel user) {
		this.user = user;
	}
	
	public void setWorkouts(List<WorkoutModel> workouts) {
		this.workouts = workouts;
	}

}
