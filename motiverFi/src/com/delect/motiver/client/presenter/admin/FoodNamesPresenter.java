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
package com.delect.motiver.client.presenter.admin;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.ServerConnection;
import com.delect.motiver.client.ServerConnection.ResponseHandler;
import com.delect.motiver.client.event.ConnectionErrorEvent;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.FoodNameModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 
 * Shows foods names in list where admins can edit/delete those
 */
public class FoodNamesPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class FoodNamesDisplay extends Display {

		public abstract void clearQuickAdd();
		public abstract LayoutContainer getBodyContainer();
		public abstract void hideProgress();
		public abstract void setFoods(List<FoodNameModel> foods);
		public abstract void setHandler(FoodNamesHandler handler);
		public abstract void showCompleted(boolean successful, String string);
		public abstract void showProgress(String text, int count, int total);
	}
	public interface FoodNamesHandler {
		/**
		 * Combines names together
		 * @param firstId : where other IDs are combined
		 * @param ids : other IDs
		 */
		void combineNames(Long firstId, Long[] ids);
		void fetchFoods();
		void quickAdd(String value);
		void saveName(FoodNameModel model);
		void search(String query);
		void updateModel(FoodNameModel model);
	}
	private FoodNamesDisplay display;

	//child presenters
	private EmptyPresenter emptyPresenter;

	private String lastQuery = "";

	/**
	 * Shows foods names in list where admins can edit/delete those 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public FoodNamesPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, FoodNamesDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		display.setHandler(new FoodNamesHandler() {
			@Override
			public void combineNames(Long firstId, Long[] ids) {
				final Request req = rpcService.combineFoodNames(firstId, ids, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						//refresh
						onRun();
					}
				});
				addRequest(req);
			}
			@Override
			public void fetchFoods() {
				fetchFoodsFromServer(0);
			}
			@Override
			public void quickAdd(String value) {
				addMultipleNames(value);
			}
			@SuppressWarnings("unchecked")
      @Override
			public void saveName(FoodNameModel model) {
				final Request req = rpcService.updateFoodName(model, MyAsyncCallback.EmptyCallback);
				addRequest(req);
			}
			@Override
			public void search(String query) {
				searchFoods(query);
			}
			@SuppressWarnings("unchecked")
      @Override
			public void updateModel(FoodNameModel model) {
				final Request req = rpcService.updateFoodName(model, MyAsyncCallback.EmptyCallback);
				addRequest(req);
			}
		});
	}


	@Override
	public void onRun() {

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBodyContainer());
	}


	@Override
	public void onStop() {
		
		if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
	}


	/**
	 * Fetchs foods
	 * @param index
	 */
	private void fetchFoodsFromServer(int index) {

		if(index == 0) {
      display.showProgress("Fetching foods", 0, 0);
    }
		
		//TEMP fetch data
    final ServerConnection con = new ServerConnection();
    //workouts
    try {
			con.connect("http://www.xlgain.com/feed_jsonp/get_all_foods.php?t=3&m=1&i=" + index + "&user=justerr&pass=c8e04ea27e565adbdebdb4f84574474a", new ResponseHandler() {

				@Override
				public void loadError(Throwable throwable) {
					final ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
					eventBus.fireEvent(event);
				}
				@Override
				public void loadOk(JSONObject json) {

					final JSONArray foods;
					try {
						foods = json.get("foods").isArray();
						final int indexNew = (int)json.get("index").isNumber().doubleValue(); 
						final int total = (int)json.get("total").isNumber().doubleValue();
						
						final List<FoodNameModel> arrFoods = new ArrayList<FoodNameModel>();
						for(int i=0; i < foods.size(); i++) {
							try {
								JSONObject obj = foods.get(i).isObject(); 
								FoodNameModel model = new FoodNameModel();
								model.setName(obj.get("n").isString().stringValue());
								model.setEnergy(obj.get("e").isNumber().doubleValue());
								model.setProtein(obj.get("p").isNumber().doubleValue());
								model.setCarb(obj.get("c").isNumber().doubleValue());
								model.setFet(obj.get("f").isNumber().doubleValue());
								model.setPortion(obj.get("po").isNumber().doubleValue());
								
								arrFoods.add(model);
								
							} catch (Exception e) {
					      Motiver.showException(e);
							}
						}
						
						display.showProgress("Fetching foods", (indexNew != 0)? indexNew : total, total);
						
						//add to server
						final Request req = rpcService.fetchSaveFoodNames(arrFoods, new MyAsyncCallback<Boolean>() {
							@Override
							public void onSuccess(Boolean result) {
								if(indexNew != 0) {
									fetchFoodsFromServer(indexNew);
								}
								else {
                  showCompleted(true);
				        }
							}
						});
						addRequest(req);
						
					} catch (Exception e) {
			      Motiver.showException(e);
					}
				}
				
			});
		} catch (Exception e) {
			final ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_XLGAIN);
			eventBus.fireEvent(event);
		}
	}


	/**
	 * Adds multiple names from excel "string"
	 * <br>name -tab- protein -tab- carb -tab- fet -tab- portion
	 * @param value
	 */
	protected void addMultipleNames(String value) {
		
		display.setContentEnabled(false);
		
		//split
		final String[] rows = value.split("\\r?\\n");
		
		List<FoodNameModel> list = new ArrayList<FoodNameModel>();
		
		for(String row : rows) {
			try {
				String[] data = (row + " ").split("\\t");
				
				//if all data
				if(data.length == 6) {
					String name = data[0];
					double e=0, p=0, c=0, f=0, po=0;
					
					if(data[1].replace(",", ".").trim().length() > 0) {
						//if format joule/kcal
						if(data[1].contains("/")) {
							String[] arr = data[1].replace(",", ".").trim().split("/");
							e = Double.parseDouble(arr[1]);
						}
						else {
              e = Double.parseDouble(data[1].replace(",", ".").trim());
		        }
					}
					if(data[2].replace(",", ".").trim().length() > 0) {
            p = Double.parseDouble(data[2].replace(",", ".").trim());
	        }
					if(data[3].replace(",", ".").trim().length() > 0) {
            c = Double.parseDouble(data[3].replace(",", ".").trim());
	        }
					if(data[4].replace(",", ".").trim().length() > 0) {
            f = Double.parseDouble(data[4].replace(",", ".").trim());
	        }
					if(data[5].replace(",", ".").trim().length() > 0) {
            po = Double.parseDouble(data[5].replace(",", ".").trim());
	        }
					
					FoodNameModel model = new FoodNameModel(0L, name);
					model.setEnergy(e);
					model.setProtein(p);
					model.setCarb(c);
					model.setFet(f);
					model.setPortion(po);

					//if admin -> set trusted
					if(AppController.User.isAdmin()) {
            model.setTrusted(100);
	        }
					
					list.add(model);
				}
			} catch (Exception e) {
	      Motiver.showException(e);
			}
		}
		
		//send to server
		if(list.size() > 0) {
			final Request req = rpcService.addFoodnames(list, new MyAsyncCallback<List<FoodNameModel>>() {
				@Override
				public void onSuccess(List<FoodNameModel> result) {
					display.clearQuickAdd();
					display.setContentEnabled(true);
				}
			});
			addRequest(req);
		}
		else {
      display.setContentEnabled(true);
    }
		
	}

	protected void searchFoods(String query) {

    if(query == null) {
      query = "";
    }
    
    if(lastQuery.equals(query)) {
      return;
    }
		
		lastQuery = query;
		
		//fetch foods
		final Request req = rpcService.searchFoodNames(query, 200, new MyAsyncCallback<List<FoodNameModel>>() {
			@Override
			public void onSuccess(List<FoodNameModel> result) {
				if(emptyPresenter != null) {
          emptyPresenter.stop();
        }
				
				display.setFoods(result);
			}
		});
		addRequest(req);
	}

	/**
	 * Shows if data is fetched correctly or not
	 * @param successful
	 */
	void showCompleted(boolean successful) {
		display.showCompleted(successful, "" );
	}

}
