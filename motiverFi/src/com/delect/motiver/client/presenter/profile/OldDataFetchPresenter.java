/*******************************************************************************
 * Copyright 2011 Antti Havanko
 * 
 * This file is part of Motiver.fi.
 * Motiver.fi is licensed under one open source license and one commercial license.
 * 
 * Commercial license: This is the appropriate option if you want to use Motiver.fi in 
 * commercial purposes. Contact license@motiver.fi for licensing options.
 * 
 * Open source license: This is the appropriate option if you are creating an open source 
 * application with a license compatible with the GNU GPL license v3. Although the GPLv3 has 
 * many terms, the most important is that you must provide the source code of your application 
 * to your users so they can be free to modify your application for their own needs.
 ******************************************************************************/
package com.delect.motiver.client.presenter.profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Timer;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.ServerConnection;
import com.delect.motiver.client.ServerConnection.ResponseHandler;
import com.delect.motiver.client.event.ConnectionErrorEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.CardioModel;
import com.delect.motiver.shared.CardioValueModel;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.ExerciseNameModel;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.FoodNameModel;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.GuideValueModel;
import com.delect.motiver.shared.MealModel;
import com.delect.motiver.shared.MeasurementModel;
import com.delect.motiver.shared.MeasurementValueModel;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.RunModel;
import com.delect.motiver.shared.RunValueModel;
import com.delect.motiver.shared.TimeModel;
import com.delect.motiver.shared.WorkoutModel;

/**
 * Interface for fetching old data from xlGain.com
 * @author Antti
 *
 */
public class OldDataFetchPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class OldDataFetchDisplay extends Display {

		public abstract void hideProgress();
		public abstract void setHandler(OldDataFetchHandler oldDataFetchHandler);
		public abstract void showCompleted(boolean successful, String msg);
		public abstract void showProgress(String string, int c, int i);
	}
	public interface OldDataFetchHandler {
		void go(String user, String pass, Boolean showTraining, Boolean showCardio, Boolean showNutrition, Boolean showMeasurements);
	}

	private OldDataFetchDisplay display;

	private List<ExerciseNameModel> exercises;
	private Boolean fetchCardio = false;
	
	private Boolean fetchMeasurements = false;
	private Boolean fetchNutrition = false;
	private Boolean fetchTraining = false;
	private List<FoodNameModel> foods;
	private String msg ="";
	private String pass = "";
	private String user = "";

	protected int measFetchCount;
	  
	int c = 0;
	
	final int limit = 50;
	public OldDataFetchPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, OldDataFetchDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setHandler(new OldDataFetchHandler() {

			@Override
			public void go(String userParam, String passParam, Boolean showTrainingParam, Boolean showCardioParam, Boolean showNutritionParam, Boolean showMeasurementsParam) {
				user = userParam;
				pass = passParam;
				fetchTraining = showTrainingParam;
				fetchCardio = showCardioParam;
				fetchNutrition = showNutritionParam;
				fetchMeasurements = showMeasurementsParam;
				
				startFetch();
			}
			
		});
	}

	/**
	 * Fetchs cardio
	 * @param index
	 */
	private void fetchCardio(int index) {
		
		//if cardios are not fetched jump directly to measurements
		if(!fetchCardio) {
			fetchMeasurements(0);
			return;
		}

		if(index == 0) {
			display.showProgress("Fetching cardio", 0, 0);
    }
		
		//TEMP fetch data
    ServerConnection con = new ServerConnection();
    //workouts
    try {
			con.connect("http://www.xlgain.com/feed_jsonp/get_all.php?t=6&i=" + index + "&user=" + user + "&pass=" + Functions.md5(pass), new ResponseHandler() {

				@Override
				public void loadError(Throwable throwable) {
					ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
					eventBus.fireEvent(event);
				}
				@SuppressWarnings("deprecation")
				@Override
				public void loadOk(JSONObject json) {

					JSONArray measurements;
					try {
						measurements = json.get("cardio").isArray();
						final int indexNew = (int)json.get("index").isNumber().doubleValue(); 
						final int total = (int)json.get("total").isNumber().doubleValue();
						
						List<CardioModel> arrCardios = new ArrayList<CardioModel>();
						List<List<CardioValueModel>> arrValues = new ArrayList<List<CardioValueModel>>();	//values for each measurement
						for(int i=0; i < measurements.size(); i++) {
							try {
								JSONObject obj = measurements.get(i).isObject(); 
								CardioModel cardio = new CardioModel();
								cardio.setName(obj.get("n").isString().stringValue());
								
								//values
								List<CardioValueModel> val = new ArrayList<CardioValueModel>();
								JSONArray values = obj.get("v").isArray();
								for(int j=0; j < values.size(); j++) {
									try {
										JSONObject objVal = values.get(j).isObject(); 
										CardioValueModel ex = new CardioValueModel();
										ex.setDate(new Date((long) (objVal.get("d").isNumber().doubleValue() * 1000)));
										String t1 = objVal.get("t").isString().stringValue();
										if(t1.length() == 8) {
											t1 = t1.substring(0, 5);
							      }
										final double time = Functions.getTimeToSeconds(t1);
										ex.getDate().setHours((int) (time / 3600));
										ex.getDate().setMinutes((int) ((time % 3600) / 60));
										ex.setDuration((long) objVal.get("du").isNumber().doubleValue());
										ex.setPulse((int) objVal.get("p").isNumber().doubleValue());
										ex.setCalories((int) objVal.get("c").isNumber().doubleValue());
										ex.setInfo(objVal.get("i").isString().stringValue());
										val.add(ex);
									} catch (Exception e) {
							      Motiver.showException(e);
									}	
								}	
								arrValues.add(val);	
								arrCardios.add(cardio);					
								
							} catch (Exception e) {
					      Motiver.showException(e);
							}
						}

						display.showProgress("Fetching cardio", (indexNew != 0)? indexNew : total, total);
						
						//add to server
						rpcService.fetchSaveCardios(arrCardios, arrValues, new MyAsyncCallback<Boolean>() {
							@Override
							public void onSuccess(Boolean result) {
								if(indexNew != 0) {
									fetchCardio(indexNew);
								}
								else {
									fetchRuns(0);
					      }
							}
						});
						
					} catch (Exception e) {
						Motiver.showException(e);
						showCompleted(false);
					}
				}
				
			});
		} catch (Exception e) {
			ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
			eventBus.fireEvent(event);

      Motiver.showException(e);
      
			showCompleted(false);
		}
	}
	
	/**
	 * Fetchs foods
	 * @param index
	 */
	private void fetchFoods(int index) {
		
		//if nutrition are not fetched end fetch
		if(!fetchNutrition) {
			showCompleted(true);
			return;
		}

		if(index == 0) {
			display.showProgress("Fetching foods", 0, 0);
    }
		
		//TEMP fetch data
    ServerConnection con = new ServerConnection();
    //workouts
    try {
			con.connect("http://www.xlgain.com/feed_jsonp/get_all.php?t=3&m=1&i=" + index + "&user=" + user + "&pass=" + Functions.md5(pass), new ResponseHandler() {

				@Override
				public void loadError(Throwable throwable) {
					ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
					eventBus.fireEvent(event);
				}
				@Override
				public void loadOk(JSONObject json) {

					JSONArray foods;
					try {
						foods = json.get("foods").isArray();
						final int indexNew = (int)json.get("index").isNumber().doubleValue(); 
						final int total = (int)json.get("total").isNumber().doubleValue();
						
						List<FoodNameModel> arrFoods = new ArrayList<FoodNameModel>();
						for(int i=0; i < foods.size(); i++) {
							try {
								JSONObject obj = foods.get(i).isObject(); 
								FoodNameModel food = new FoodNameModel();
								food.setName(obj.get("n").isString().stringValue());
								food.setEnergy(obj.get("e").isNumber().doubleValue());
								food.setProtein(obj.get("p").isNumber().doubleValue());
								food.setCarb(obj.get("c").isNumber().doubleValue());
								food.setFet(obj.get("f").isNumber().doubleValue());
								food.setPortion(obj.get("po").isNumber().doubleValue());
								food.setLocale(AppController.User.getLocale());
								
								arrFoods.add(food);					
								
							} catch (Exception e) {
					      Motiver.showException(e);
							}
						}
						
						display.showProgress("Fetching foods", (indexNew != 0)? indexNew : total, total);
						
						//add to server
						rpcService.fetchSaveFoodNames(arrFoods, new MyAsyncCallback<Boolean>() {
							@Override
							public void onSuccess(Boolean result) {
								if(indexNew != 0) {
									fetchFoods(indexNew);
								}
								else {
									fetchMeals(0);
					      }
							}
						});
						
					} catch (Exception e) {
			      Motiver.showException(e);
						showCompleted(false);
					}
				}
				
			});
		} catch (Exception e) {
			ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
			eventBus.fireEvent(event);

      Motiver.showException(e);
			showCompleted(false);
		}
	}
	/**
	 * Fetchs guide values
	 * @param index
	 */
	private void fetchGuideValues(int index) {

		if(index == 0) {
			display.showProgress("Fetching guide values", 0, 0);
    }
		
		//TEMP fetch data
    ServerConnection con = new ServerConnection();
    //workouts
    try {
			con.connect("http://www.xlgain.com/feed_jsonp/get_all.php?t=8&i=" + index + "&user=" + user + "&pass=" + Functions.md5(pass), new ResponseHandler() {

				@Override
				public void loadError(Throwable throwable) {
					ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
					eventBus.fireEvent(event);
				}
				@Override
				public void loadOk(JSONObject json) {

					JSONArray guides;
					try {
						guides = json.get("guides").isArray();
						final int indexNew = (int)json.get("index").isNumber().doubleValue(); 
						final int total = (int)json.get("total").isNumber().doubleValue();
						
						//each values
						List<GuideValueModel> listValues = new ArrayList<GuideValueModel>();
						for(int i=0; i < guides.size(); i++) {
							JSONObject obj = guides.get(i).isObject();
							
							final String name = obj.get("n").isString().stringValue();
							final Date dateStart = new Date((long) obj.get("d1").isNumber().doubleValue() * 1000);
							final Date dateEnd = new Date((long) obj.get("d2").isNumber().doubleValue() * 1000);
							final JSONObject objTraining = obj.get("t").isObject();
							final double eT = objTraining.get("e").isNumber().doubleValue();
							final double pT = objTraining.get("p").isNumber().doubleValue();
							final double cT = objTraining.get("c").isNumber().doubleValue();
							final double fT = objTraining.get("f").isNumber().doubleValue();
							final JSONObject objRest = obj.get("t").isObject();
							final double eR = objRest.get("e").isNumber().doubleValue();
							final double pR = objRest.get("p").isNumber().doubleValue();
							final double cR = objRest.get("c").isNumber().doubleValue();
							final double fR = objRest.get("f").isNumber().doubleValue();
							final boolean isPercent = !obj.get("g").isBoolean().booleanValue();
							
							GuideValueModel value = new GuideValueModel(0L, name);
							value.setDates(dateStart, dateEnd);
							value.setEnergy(true, eT);
							value.setProtein(true, pT);
							value.setCarb(true, cT);
							value.setFet(true, fT);
							value.setEnergy(false, eR);
							value.setProtein(false, pR);
							value.setCarb(false, cR);
							value.setFet(false, fR);
							value.setPercent(isPercent);
							
							listValues.add(value);
						}
						
						display.showProgress("Fetching guide values", (indexNew != 0)? indexNew : total, total);
						
						//add to server
						rpcService.fetchSaveGuideValues(listValues, new MyAsyncCallback<Boolean>() {
							@Override
							public void onSuccess(Boolean result) {
								if(indexNew != 0) {
									fetchGuideValues(indexNew);
								}
								else {
									fetchTimes(0);
					      }
							}
						});
						
					} catch (Exception e) {
			      Motiver.showException(e);
						showCompleted(false);
					}
				}
				
			});
		} catch (Exception e) {
			ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
			eventBus.fireEvent(event);

      Motiver.showException(e);
			showCompleted(false);
		}
	}

	/**
	 * Fetchs meals
	 * @param index
	 */
	private void fetchMeals(int index) {

		if(index == 0) {
			display.showProgress("Fetching meals", 0, 0);
    }
		
		//TEMP fetch data
    ServerConnection con = new ServerConnection();
    //workouts
    try {
			con.connect("http://www.xlgain.com/feed_jsonp/get_all.php?t=5&i=" + index + "&user=" + user + "&pass=" + Functions.md5(pass), new ResponseHandler() {

				@Override
				public void loadError(Throwable throwable) {
					ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
					eventBus.fireEvent(event);
				}
				@Override
				public void loadOk(JSONObject json) {

					JSONArray meals;
					try {
						meals = json.get("meals").isArray();
						final int indexNew = (int)json.get("index").isNumber().doubleValue(); 
						final int total = (int)json.get("total").isNumber().doubleValue();
						
						List<MealModel> arrMeals = new ArrayList<MealModel>();
						for(int i=0; i < meals.size(); i++) {
							try {
								JSONObject obj = meals.get(i).isObject(); 
								MealModel mMeal = new MealModel();
								mMeal.setName(obj.get("n").isString().stringValue());
								
								//foods
								List<FoodModel> foods = new ArrayList<FoodModel>();
								JSONArray f = obj.get("f").isArray();
								for(int j=0; j < f.size(); j++) {
									try {
										JSONObject objF = f.get(j).isObject(); 
										FoodModel food = new FoodModel();
										food.setAmount(objF.get("a").isNumber().doubleValue());
										//get food name
										food.setName(getFoodName(objF.get("n").isString().stringValue(), objF.get("e").isNumber().doubleValue()));
										foods.add(food);
									} catch (Exception e) {
										Motiver.showException(e);
									}
								}
								mMeal.setFoods(foods);
								arrMeals.add(mMeal);					
								
							} catch (Exception e) {
					      Motiver.showException(e);
							}
						}

						display.showProgress("Fetching meals", (indexNew != 0)? indexNew : total, total);
						
						//add to server
						rpcService.fetchSaveMeals(arrMeals, new MyAsyncCallback<Boolean>() {
							@Override
							public void onSuccess(Boolean result) {
								if(indexNew != 0) {
									fetchMeals(indexNew);
								}
								else {
									//fetch food names to reduce server load
									rpcService.searchFoodNames("", 500, new MyAsyncCallback<List<FoodNameModel>>() {
										@Override
										public void onSuccess(List<FoodNameModel> result) {
											foods = result;
											fetchGuideValues(0);
										}
									});
								}
							}
						});
						
					} catch (Exception e) {
			      Motiver.showException(e);
						showCompleted(false);
					}
				}
				
			});
		} catch (Exception e) {
			ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
			eventBus.fireEvent(event);

      Motiver.showException(e);
			showCompleted(false);
		}
	}


	/**
	 * Fetchs measurements
	 * @param index
	 */
	private void fetchMeasurements(int index) {
		
		//if measurements are not fetched jump directly to food
		if(!fetchMeasurements) {
			fetchFoods(0);
			return;
		}

		if(index == 0) {
			display.showProgress("Fetching measurements", 0, 0);
    }
		
		//TEMP fetch data
    ServerConnection con = new ServerConnection();
    //workouts
    try {
			con.connect("http://www.xlgain.com/feed_jsonp/get_all.php?t=2&i=" + index + "&user=" + user + "&pass=" + Functions.md5(pass), new ResponseHandler() {

				@Override
				public void loadError(Throwable throwable) {
					ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
					eventBus.fireEvent(event);
				}
				@Override
				public void loadOk(JSONObject json) {
					JSONArray measurements;
					try {
						measurements = json.get("measurements").isArray(); 
						final int total = (int)json.get("total").isNumber().doubleValue();
						
						List<MeasurementModel> arrMeasurements = new ArrayList<MeasurementModel>();
						List<List<MeasurementValueModel>> arrValues = new ArrayList<List<MeasurementValueModel>>();	//values for each measurement
						for(int i=0; i < measurements.size(); i++) {
							try {
								JSONObject obj = measurements.get(i).isObject(); 
								MeasurementModel measurement = new MeasurementModel();
								measurement.setName(obj.get("t").isString().stringValue());
								measurement.setUnit(obj.get("u").isString().stringValue());
								
								//values
								List<MeasurementValueModel> val = new ArrayList<MeasurementValueModel>();
								JSONArray values = obj.get("v").isArray();
								for(int j=0; j < values.size(); j++) {
									try {
										JSONObject objVal = values.get(j).isObject(); 
										MeasurementValueModel ex = new MeasurementValueModel();
										ex.setDate(new Date((long) (objVal.get("d").isNumber().doubleValue() * 1000)));
										ex.setValue(objVal.get("v").isNumber().doubleValue());
										val.add(ex);
										
									} catch (Exception e) {
							      Motiver.showException(e);
									}	
								}	
								arrValues.add(val);	
								arrMeasurements.add(measurement);					
								
							} catch (Exception e) {
					      Motiver.showException(e);
							}
						}

						display.showProgress("Fetching measurements", 0, total);
						
						sendMeasurements(0, 0, arrMeasurements, arrValues);
						
					} catch (Exception e) {
						Motiver.showException(e);
						measFetchCount = 100;
						showCompleted(false);
					}
				}
				
			});
		} catch (Exception e) {
			ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
			eventBus.fireEvent(event);

      Motiver.showException(e);
			showCompleted(false);
		}
	}


	/**
	 * Fetchs routines
	 * @param index
	 */
	private void fetchRoutines(int index) {

		if(index == 0) {
			display.showProgress("Fetching routines", 0, 0);
    }
		
		//TEMP fetch data
    ServerConnection con = new ServerConnection();
    //workouts
    try {
			con.connect("http://www.xlgain.com/feed_jsonp/get_all.php?t=1&i=" + index + "&user=" + user + "&pass=" + Functions.md5(pass), new ResponseHandler() {

				@Override
				public void loadError(Throwable throwable) {
					ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
					eventBus.fireEvent(event);
				}
				@Override
				public void loadOk(JSONObject json) {
					try {
						JSONArray routines = json.get("routines").isArray();
						final int indexNew = (int)json.get("index").isNumber().doubleValue(); 
						final int total = (int)json.get("total").isNumber().doubleValue();
						
						List<RoutineModel> arrRoutines = new ArrayList<RoutineModel>();
						List<List<WorkoutModel>> arrWorkouts = new ArrayList<List<WorkoutModel>>();	//workouts for each routine
						for(int i=0; i < routines.size(); i++) {
							try {
								JSONObject obj = routines.get(i).isObject(); 
								RoutineModel routine = new RoutineModel();
								try {
									routine.setDate(new Date((long) (obj.get("s").isNumber().doubleValue() * 1000)));
								} catch (Exception e) {
								}
								routine.setDays((int) obj.get("d").isNumber().doubleValue());
								routine.setName(obj.get("n").isString().stringValue());
								routine.setInfo(obj.get("i").isString().stringValue());
								
								//workouts
								JSONArray workouts = obj.get("w").isArray();
								arrWorkouts.add(parseWorkouts(workouts));
								arrRoutines.add(routine);
							} catch (Exception e) {
					      Motiver.showException(e);
							}
						}

						display.showProgress("Fetching routines", (indexNew != 0)? indexNew : total, total);
						
						//add to server
						rpcService.fetchSaveRoutines(arrRoutines, arrWorkouts, new MyAsyncCallback<Boolean>() {
							@Override
							public void onSuccess(Boolean result) {
								if(indexNew != 0) {
									fetchRoutines(indexNew);
								}
								else {
									fetchCardio(0);
								}
							}
						});
					} catch (Exception e) {
			      Motiver.showException(e);
						showCompleted(false);
					}
				}
			});
		} catch (Exception e) {
			ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
			eventBus.fireEvent(event);

      Motiver.showException(e);
			showCompleted(false);
		}
	}

	/**
	 * Fetchs run
	 * @param index
	 */
	private void fetchRuns(int index) {

		if(index == 0) {
			display.showProgress("Fetching runs", 0, 0);
    }
		
		//TEMP fetch data
    ServerConnection con = new ServerConnection();
    //workouts
    try {
			con.connect("http://www.xlgain.com/feed_jsonp/get_all.php?t=7&i=" + index + "&user=" + user + "&pass=" + Functions.md5(pass), new ResponseHandler() {

				@Override
				public void loadError(Throwable throwable) {
					ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
					eventBus.fireEvent(event);
				}
				@SuppressWarnings("deprecation")
				@Override
				public void loadOk(JSONObject json) {
				  
					JSONArray measurements;
					try {
						measurements = json.get("run").isArray();
						final int indexNew = (int)json.get("index").isNumber().doubleValue(); 
						final int total = (int)json.get("total").isNumber().doubleValue();
						
						List<RunModel> arrRuns = new ArrayList<RunModel>();
						List<List<RunValueModel>> arrValues = new ArrayList<List<RunValueModel>>();	//values for each measurement
						for(int i=0; i < measurements.size(); i++) {
							try {
								JSONObject obj = measurements.get(i).isObject(); 
								RunModel run = new RunModel();
								run.setName(obj.get("n").isString().stringValue());
								run.setDistance(obj.get("d").isNumber().doubleValue());
								run.setTargetTime((long) obj.get("t").isNumber().doubleValue());
								
								//values
								List<RunValueModel> val = new ArrayList<RunValueModel>();
								JSONArray values = obj.get("v").isArray();
								for(int j=0; j < values.size(); j++) {
									try {
										JSONObject objVal = values.get(j).isObject(); 
										RunValueModel ex = new RunValueModel();
										ex.setDate(new Date((long) (objVal.get("d").isNumber().doubleValue() * 1000)));
										String t1 = objVal.get("t").isString().stringValue();
										if(t1.length() == 8) {
											t1 = t1.substring(0, 5);
							      }
										final double time = Functions.getTimeToSeconds(t1);
										ex.getDate().setHours((int) (time / 3600));
										ex.getDate().setMinutes((int) ((time % 3600) / 60));
										ex.setDuration((long) objVal.get("du").isNumber().doubleValue());
										ex.setPulse((int) objVal.get("p").isNumber().doubleValue());
										ex.setCalories((int) objVal.get("c").isNumber().doubleValue());
										ex.setInfo(objVal.get("i").isString().stringValue());
										val.add(ex);
									} catch (Exception e) {
							      Motiver.showException(e);
									}	
								}	
								arrValues.add(val);	
								arrRuns.add(run);					
								
							} catch (Exception e) {
					      Motiver.showException(e);
							}
						}

						display.showProgress("Fetching runs", (indexNew != 0)? indexNew : total, total);
						
						//add to server
						rpcService.fetchSaveRuns(arrRuns, arrValues, new MyAsyncCallback<Boolean>() {
							@Override
							public void onSuccess(Boolean result) {
								if(indexNew != 0) {
									fetchRuns(indexNew);
								}
								else {
									fetchMeasurements(0);
					      }
							}
						});
						
					} catch (Exception e) {
			      Motiver.showException(e);
						showCompleted(false);
					}
				}
				
			});
		} catch (Exception e) {
			ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
			eventBus.fireEvent(event);

      Motiver.showException(e);
			showCompleted(false);
		}
	}
	
	/**
	 * Fetchs times
	 * @param index
	 */
	private void fetchTimes(int index) {

		if(index == 0) {
			display.showProgress("Fetching nutrition", 0, 0);
    }
		
		//TEMP fetch data
    ServerConnection con = new ServerConnection();
    //workouts
    try {
			con.connect("http://www.xlgain.com/feed_jsonp/get_all.php?t=4&i=" + index + "&user=" + user + "&pass=" + Functions.md5(pass), new ResponseHandler() {

				@Override
				public void loadError(Throwable throwable) {
					ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
					eventBus.fireEvent(event);
				}
				@Override
				public void loadOk(JSONObject json) {

					JSONArray times;
					try {
						times = json.get("times").isArray();
						final int indexNew = (int)json.get("index").isNumber().doubleValue(); 
						final int total = (int)json.get("total").isNumber().doubleValue();
						
						List<TimeModel> arrTimes = new ArrayList<TimeModel>();
						for(int i=0; i < times.size(); i++) {
							try {
								JSONObject obj = times.get(i).isObject(); 
								TimeModel mTime = new TimeModel();
								mTime.setDate(new Date((long) (obj.get("d").isNumber().doubleValue() * 1000)));
								String t1 = obj.get("t").isString().stringValue();
								if(t1.length() == 8) {
									t1 = t1.substring(0, 5);
					      }
								mTime.setTime(Functions.getTimeToSeconds(t1));
								
								//foods
								List<FoodModel> foods = new ArrayList<FoodModel>();
								JSONArray f = obj.get("f").isArray();
								for(int j=0; j < f.size(); j++) {
									try {
										JSONObject objF = f.get(j).isObject(); 
										FoodModel food = new FoodModel();
										food.setAmount(objF.get("a").isNumber().doubleValue());
										//get food name
										food.setName(getFoodName(objF.get("n").isString().stringValue(), objF.get("e").isNumber().doubleValue()));
										foods.add(food);
									} catch (Exception e) {
										Motiver.showException(e);
									}
								}
								mTime.setFoods(foods);

								List<MealModel> meals = new ArrayList<MealModel>();
								JSONArray array = obj.get("m").isArray();
								for(int j=0; j < array.size(); j++) {
									try {
										JSONObject objM = array.get(j).isObject(); 
										MealModel meal = new MealModel();
										meal.setName(objM.get("n").isString().stringValue());
										
										//foods in this meal
										List<FoodModel> foodsInMeal = new ArrayList<FoodModel>();
										JSONArray fMeal = objM.get("f").isArray();
										for(int k=0; k < fMeal.size(); k++) {
											try {
												JSONObject objF = fMeal.get(k).isObject(); 
												FoodModel food = new FoodModel();
												food.setAmount(objF.get("a").isNumber().doubleValue());
												//get food name
												food.setName(getFoodName(objF.get("n").isString().stringValue(), objF.get("e").isNumber().doubleValue()));
												foodsInMeal.add(food);
											} catch (Exception e) {
												Motiver.showException(e);
											}
										}
										meal.setFoods(foodsInMeal);
										meals.add(meal);
									} catch (Exception e) {
							      Motiver.showException(e);
									}
								}
								mTime.setMeals(meals);
								
								arrTimes.add(mTime);					
								
							} catch (Exception e) {
					      Motiver.showException(e);
							}
						}

						display.showProgress("Fetching nutrition", (indexNew != 0)? indexNew : total, total);
						
						//add to server
						if(arrTimes.size() > 0) {

							rpcService.fetchSaveTimes(arrTimes, new MyAsyncCallback<Boolean>() {
								@Override
								public void onSuccess(Boolean result) {
									if(indexNew != 0) {
										//small delay before new fetch
										Timer timer = new Timer() {
											@Override
											public void run() {
												fetchTimes(indexNew);
											}										
										};
										timer.schedule(Constants.FETCH_DELAY);
									}
									else {
										showCompleted(true);
									}
								}
							});
						}
						else {
							if(indexNew != 0) {
								fetchTimes(indexNew);
							}
							else {
								showCompleted(true);
							}
						}

					} catch (Exception e) {
			      Motiver.showException(e);
						showCompleted(false);
					}
				}
				
			});
		} catch (Exception e) {
			ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
			eventBus.fireEvent(event);

      Motiver.showException(e);
			showCompleted(false);
		}
	}

	/**
	 * Fetchs workotus
	 * @param index
	 */
	private void fetchWorkouts(int index) {
		
		//if training are not fetched jump directly to cardio
		if(!fetchTraining) {
			fetchCardio(0);
			return;
		}

		if(index == 0) {
			display.showProgress("Fetching workouts", 0, 0);
    }
		
		//TEMP fetch data
    ServerConnection con = new ServerConnection();
    //workouts
    try {
			con.connect("http://www.xlgain.com/feed_jsonp/get_all.php?t=0&i=" + index + "&user=" + user + "&pass=" + Functions.md5(pass), new ResponseHandler() {

				@Override
				public void loadError(Throwable throwable) {
					ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
					eventBus.fireEvent(event);
				}
				@Override
				public void loadOk(JSONObject json) {

					JSONArray workouts;
					try {
						workouts = json.get("workouts").isArray();
						final int indexNew = (int)json.get("index").isNumber().doubleValue(); 
						final int total = (int)json.get("total").isNumber().doubleValue();
						
						List<WorkoutModel> arrWorkouts = parseWorkouts(workouts);
						display.showProgress("Fetching workouts", (indexNew != 0)? indexNew : total, total);

						//add to server
						rpcService.fetchSaveWorkouts(arrWorkouts, new MyAsyncCallback<Boolean>() {
							@Override
							public void onSuccess(Boolean result) {
								if(indexNew != 0) {
									fetchWorkouts(indexNew);
								}
								else {
									//fetch food names to reduce server load
									rpcService.searchExerciseNames("", 500, new MyAsyncCallback<List<ExerciseNameModel>>() {
										@Override
										public void onSuccess(List<ExerciseNameModel> result) {
											exercises = result;
											fetchRoutines(0);
										}
									});
								}
							}
						});
						
					} catch (Exception e) {
			      Motiver.showException(e);
						showCompleted(false);
					}
				}
				
			});
		} catch (Exception e) {
			ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
			eventBus.fireEvent(event);

      Motiver.showException(e);
			showCompleted(false);
		}
	}


	/**
	 * Returns correct exercise name based on name & energy
	 * @param name
	 * @param target
	 * @return
	 */
	protected ExerciseNameModel getExerciseName(String name, int target) {

		//set base model (used if nothing founds)
		ExerciseNameModel model = new ExerciseNameModel(0L, name, target);
		//set locale
		model.setLocale(AppController.User.getLocale());
		
		if(exercises == null) {
			return model;
    }
		
		try {
			for(ExerciseNameModel m : exercises) {
				if(m.getName().equals(name) && m.getTarget() == target) {
					model = m;
					break;
				}
			}
		} catch (Exception e) {
			Motiver.showException(e);
		}
		return model;
	}
	
	/**
	 * Returns correct food name based on name & energy
	 * @param name
	 * @param energy
	 * @return
	 */
	protected FoodNameModel getFoodName(String name, double energy) {

		//set base model (used if nothing founds)
		FoodNameModel model = new FoodNameModel(0L, name);
		model.setEnergy(energy);
		//set locale
		model.setLocale(AppController.User.getLocale());
		
		if(foods == null) {
			return model;
    }
		
		try {
			for(FoodNameModel m : foods) {
				if(m.getName().equals(name) && Double.compare(m.getEnergy(), energy) == 0) {
					model = m;
					break;
				}
			}
		} catch (Exception e) {
			Motiver.showException(e);
		}
		return model;
	}

	/**
	 * Parses workouts json array
	 * @param workouts
	 * @return
	 */
	protected List<WorkoutModel> parseWorkouts(JSONArray workouts) {

		ArrayList<WorkoutModel> arrWorkouts = new ArrayList<WorkoutModel>();
		for(int i=0; i < workouts.size(); i++) {
			try {
				JSONObject obj = workouts.get(i).isObject(); 
				WorkoutModel workout = new WorkoutModel();
				try {
					workout.setDate(new Date((long) (obj.get("d").isNumber().doubleValue() * 1000)));
				} catch (Exception e) {
		      Motiver.showException(e);
				}
				workout.setDone(obj.get("done").isBoolean().booleanValue());
				workout.setName(obj.get("n").isString().stringValue());
				workout.setRating((int) obj.get("r").isNumber().doubleValue());
				workout.setInfo(obj.get("i").isString().stringValue());
				//day in routine
				try {
					workout.setDayInRoutine((int) obj.get("day").isNumber().doubleValue());
				} catch (Exception e) {
		      Motiver.showException(e);
				}
				//times
				try {
					String t1 = obj.get("t1").isString().stringValue();
					if(t1.length() == 8) {
						t1 = t1.substring(0, 5);
		      }
					workout.setTimeStart(Functions.getTimeToSeconds(t1));
					String t2 = obj.get("t2").isString().stringValue();
					if(t2.length() == 8) {
						t2 = t2.substring(0, 5);
		      }
					workout.setTimeEnd(Functions.getTimeToSeconds(t2));
				} catch (Exception e) {
					Motiver.showException(e);
				}
				
				//exercises
				List<ExerciseModel> arrExercises = new ArrayList<ExerciseModel>();
				JSONArray exercises = obj.get("ex").isArray();
				for(int j=0; j < exercises.size(); j++) {
					JSONObject objEx = exercises.get(j).isObject(); 
					//get name
					ExerciseNameModel name = getExerciseName(objEx.get("n").isString().stringValue(), (int)(objEx.get("t").isNumber().doubleValue()));
					ExerciseModel ex = new ExerciseModel();
					ex.setName(name);
					ex.setInfo(objEx.get("i").isString().stringValue());
					ex.setReps(objEx.get("r").isString().stringValue());
					ex.setSets((int)objEx.get("s").isNumber().doubleValue());
					ex.setTempo(objEx.get("te").isString().stringValue());
					ex.setOrder((int)objEx.get("o").isNumber().doubleValue());
					ex.setWeights(objEx.get("w").isString().stringValue());
					arrExercises.add(ex);
				}
				workout.setExercises(arrExercises);
				
				arrWorkouts.add(workout);
				
			} catch (Exception e) {
				Motiver.showException(e);
			}
		}
		
		return arrWorkouts;
	}


	protected void removeAll() {

		display.showProgress("Removing all data", c, 10);
		c++;

		//first remove all data
		rpcService.fetchRemoveAll(fetchTraining, fetchCardio, fetchNutrition, fetchMeasurements, new MyAsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {

				//if all removed
				if(!result) {
					//fetch food names to reduce server load
					rpcService.searchExerciseNames("", 500, new MyAsyncCallback<List<ExerciseNameModel>>() {
						@Override
						public void onSuccess(List<ExerciseNameModel> result) {
							exercises = result;
							fetchWorkouts(0);
						}
					});
				}
				//more entities left
				else {
					removeAll();
				}
			}
		});
	    
	}


	protected void sendMeasurements(final int meas, final int val, final List<MeasurementModel> measurements, final List<List<MeasurementValueModel>> arrValues) {

		try {
			final List<MeasurementValueModel> listValues = arrValues.get(meas);
			
			//send 100 values at once
			List<MeasurementValueModel> list = new ArrayList<MeasurementValueModel>();
			list.addAll(listValues.subList(val, (val + limit >= listValues.size())? listValues.size() : val + limit));
			
			//add to server
			rpcService.fetchSaveMeasurements(measurements.get(meas), list, new MyAsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					
					//next measurement/value
					//if values left
					if(val + limit < listValues.size()) {
						sendMeasurements(meas, val + limit, measurements, arrValues);
					}
					//if measurements left
					else if(meas + 1 < measurements.size()) {
						sendMeasurements(meas + 1, 0, measurements, arrValues);
					}
					//next fetch
					else {
						fetchFoods(0);
					}
				}
			});
		} catch (Exception e) {
			Motiver.showException(e);
		}
	}

	/**
	 * Starts fetching data
	 */
	protected void startFetch() {
		
		removeAll();
		
	}

	/**
	 * Shows if data is fetched correctly or not
	 * @param successful
	 */
	void showCompleted(boolean successful) {
		display.showCompleted(successful, msg );
	}

}
