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
package com.delect.motiver.client.view.css3.nutrition;

import java.util.List;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.Templates;
import com.delect.motiver.client.presenter.nutrition.FoodPresenter;
import com.delect.motiver.client.presenter.nutrition.FoodPresenter.FoodHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.CustomListener;
import com.delect.motiver.client.view.MySpinnerField;
import com.delect.motiver.client.view.css3.FlexBox;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.FoodNameModel;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;

import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;


public class FoodView extends FoodPresenter.FoodDisplay {

	private MessageBox box = null;
	private ImageButton btnEdit = new ImageButton(AppController.Lang.EditTarget(AppController.Lang.ThisFood().toLowerCase()), MyResources.INSTANCE.iconBtnRename());
	private ComboBox<FoodNameModel> comboName;
	private LayoutContainer containerName = new LayoutContainer();
	private FlexBox flexBox = new FlexBox();
	private FoodModel food;

	private FoodHandler handler;
	private Text labelPortions = new Text();
	private LayoutContainer panelButtons = new LayoutContainer();
	
	private MySpinnerField spinAmount = new MySpinnerField();
	private ListStore<FoodNameModel> store;
	private XTemplate template = XTemplate.create(Templates.getFoodNameTemplate());  //template for combobox
	private Timer timerUpdate;
	
	
	/**
	 * Meal view
	 */
	public FoodView() {
		this.setStyleName("panel-food");
		
		flexBox.setWidth("100%");
    this.add(flexBox);
		
		//show hide header buttons based on mouse position
		this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				addStyleName("panel-food-active");
				panelButtons.setVisible(true);
			}
		});
		this.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				removeStyleName("panel-food-active");
				panelButtons.setVisible(false);
			}
		});
	}
	
	@Override
	public Widget asWidget() {

		try {
			
			//food selection combo
			if(food.getUid().equals(AppController.User.getUid())) {
				comboName = addFoodCombo();
				containerName.add(comboName);
				flexBox.add(containerName, -1, 10);
			}
			else {
				Text textName = new Text();
				if(food.getName() != null) {
					textName.setText( food.getName().getName() );
        }
				textName.addStyleName("field-readonly");
				textName.setStyleAttribute("text-align", "left");
				textName.setWidth(325);
				flexBox.add(textName, -1, 10);
			}

			//amount
			//if "portion" set -> only increment 'even' values
			if(food.getUid().equals(AppController.User.getUid())) {
				spinAmount  = new MySpinnerField() {
					@Override
					protected void doSpin(boolean up) {
						
						try {
							final double minValue = this.getMinValue().doubleValue();
							final double maxValue = this.getMaxValue().doubleValue();
							double increment = (food.getName() != null)? food.getName().getPortion() / 2 : 100D;
							if(Double.compare(increment, 0) == 0) {
								increment = 100D;
		          }
							
							if (!readOnly) {
								Number value = this.getValue();
														
								double newValue = 0;
								double d = (value == null) ? 0d : getValue().doubleValue();
								if (up) {
									newValue = (Math.max(minValue, Math.min(d + increment, maxValue)));
								} else {
									newValue = (Math.max(minValue, Math.min(this.getAllowNegative() ? d - increment : Math.max(0, d - increment), maxValue)));
								}
								
								if(increment > 0) {
									double dRes = (int)(newValue / increment + 0.01);
									newValue = dRes * increment;
								}
								this.setValue(newValue);
								
								//update portions column
								updatePortions(this.getValue().doubleValue());
							}
						} catch (Exception e) {
				      Motiver.showException(e);
						}
					}
				};

				//save value when valid
				spinAmount.addListener(Events.Change, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						if(handler != null && spinAmount.getValue() != null&& spinAmount.isValid() && Double.compare(spinAmount.getValue().doubleValue(), food.getAmount()) != 0) {
							food.setAmount(spinAmount.getValue().doubleValue());
							saveData();
							
							//update portions text
							updatePortions(spinAmount.getValue().doubleValue());
						}
					}
				});
				spinAmount.addListener(Events.Invalid, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						if(spinAmount.getRawValue().contains(",")) {
							spinAmount.setValue( Double.parseDouble(spinAmount.getRawValue().replace(",", ".")) );
	          }
					}
				});
				spinAmount.addStyleName("field-amount");
				spinAmount.addListener(Events.OnClick, CustomListener.fieldOnClicked);
				spinAmount.setAllowNegative(false);
				spinAmount.setIncrement(100);
				spinAmount.setMinValue(0);
				spinAmount.setMaxValue(5000D);
				spinAmount.setEditable(true);
				spinAmount.setPropertyEditorType(Double.class);
				spinAmount.setFormat(NumberFormat.getFormat(NumberFormat.getDecimalFormat().getPattern() + " g")); 
        spinAmount.setValue(food.getAmount());
        spinAmount.setFireChangeEventOnSetValue(true);
        flexBox.add(spinAmount, -1, 10);
			}
			else {
				final Text textValue = new Text();
				textValue.setText( NumberFormat.getFormat(NumberFormat.getDecimalFormat().getPattern() + " g").format(food.getAmount()) );
				textValue.addStyleName("field-readonly");
				textValue.setWidth(125);
				flexBox.add(textValue, -1, 10);
			}

			//portion
			labelPortions.setText("-");
			labelPortions.setStyleName("label-portions");
			flexBox.add(labelPortions, -1, -1);
			updatePortions(food.getAmount());
	        
			if(food.getUid().equals(AppController.User.getUid())) {

				//spacer
			  HTML spacer = new HTML();
			  spacer.setWidth("50px");
        flexBox.add(spacer, 1, -1);  

				//buttons layout
				HBoxLayout layoutButtons = new HBoxLayout();
				layoutButtons.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
				layoutButtons.setPack(BoxLayoutPack.END);
        panelButtons.setLayout(layoutButtons);
        panelButtons.setHeight(16);
        panelButtons.setWidth(70);

        //edit food link
				btnEdit.addListener(Events.OnClick, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						handler.foodEdited();
					}
				});
				panelButtons.add(btnEdit, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
				btnEdit.setVisible(AppController.User.isAdmin() || (food.getName() != null && food.getName().getUid().equals(AppController.User.getUid())));

				//drag food
				ImageButton btnDrag = new ImageButton(AppController.Lang.DragToCopy(), MyResources.INSTANCE.iconBtnDrag());
				DragSource source = new DragSource(btnDrag) {  
          @Override  
          protected void onDragStart(DNDEvent event) { 
            super.onDragStart(event);
    
            event.setData(food);
    						
            //set drag panel
            String name = (food.getName() != null)? food.getName().getName() : "";
            String html = Functions.getDragPanel(AppController.Lang.CopyTargetTo(name, "..."));
            event.getStatus().update(html);    
          }
				};
				source.setGroup("mealfood");
				panelButtons.add(btnDrag, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
				
        //remove food link
				ImageButton btnRemove = new ImageButton(AppController.Lang.RemoveTarget(AppController.Lang.ThisFood().toLowerCase()), MyResources.INSTANCE.iconRemove());
				btnRemove.addListener(Events.OnClick, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						//ask for confirm
						box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisFood().toLowerCase()), new MessageBoxHandler() {
							@Override
							public void okPressed(String text) {
								handler.foodRemoved();
							}
						});
						box.show();
					}
				});
				panelButtons.add(btnRemove, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
				
				flexBox.add(panelButtons, -1, -1);
			}
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		panelButtons.setVisible(false);
		
		return this;
	}
	
	@Override
	public void onStop() {
		if(timerUpdate != null) {
			timerUpdate.cancel();
    }
		if(box != null && box.isVisible()) {
      box.close();
    }
	}

	@Override
	public void setHandler(FoodHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setModel(FoodModel food) {
		this.food = food;

		btnEdit.setVisible(AppController.User.isAdmin() || (food.getName() != null && food.getName().getUid().equals(AppController.User.getUid())));
		panelButtons.layout();
		
		if(comboName != null && food.getName() != null) {
			comboName.setValue(food.getName());
    }
		
		if(spinAmount.getValue() != null) {
			updatePortions(spinAmount.getValue().doubleValue());
    }
	}

	@Override
	public void setNameComboEnabled(boolean enabled) {
		comboName.setEnabled(enabled);
	}
	/**
	 * Adds food search combo
	 */
	protected ComboBox<FoodNameModel> addFoodCombo() {

		final ComboBox<FoodNameModel> combo = new ComboBox<FoodNameModel>() {
			@Override  
			protected boolean selectByValue(String value) {
				if(getStore().getCount() > 0) {
					select(0);
					return true;
				}
				else {
					return false;
        }
			}
		};	
	    
		 // proxy, reader and loader
		RpcProxy<List<FoodNameModel>> proxy = new RpcProxy<List<FoodNameModel>>() {
      @Override
      protected void load(Object loadConfig, AsyncCallback<List<FoodNameModel>> callback) {
        BasePagingLoadConfig config = (BasePagingLoadConfig)loadConfig;

        //trim
        String query = config.get("query").toString();
        query = query.trim();
       		
        handler.query(query, callback);
      }
    };
       
    ModelReader reader = new ModelReader();
    BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);

    loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {
      public void handleEvent(LoadEvent be) {
        be.<ModelData>getConfig().set("start", be.<ModelData>getConfig().get("offset"));
      }
    });
    loader.addListener(Loader.Load, new Listener<LoadEvent>() {
      @SuppressWarnings("unchecked")
      public void handleEvent(LoadEvent be) {
        //add "add new food" item on list
        BaseListLoadResult<FoodNameModel> list = (BaseListLoadResult<FoodNameModel>)be.getData();
        final FoodNameModel name = new FoodNameModel(-1L, AppController.Lang.AddNew(AppController.Lang.Food().toLowerCase()));
        list.getData().add(name);
      }
    });
        
    store = new ListStore<FoodNameModel>(loader);  
    combo.addStyleName("field-name");
    if(food.getName() != null && food.getName().getId() != 0) {
      combo.setValue(food.getName());
    }
    combo.setWidth(325);   
		combo.setValidationDelay(Constants.DELAY_SEARCH);
    combo.setForceSelection(false);
    combo.setMessageTarget("none");
    combo.setDisplayField("n");
    combo.setMinChars(Constants.LIMIT_MIN_QUERY_WORD);
    combo.setTemplate(template );  
    combo.setStore(store);
    if(AppController.IsSupportedBrowser) {
			combo.setEmptyText(AppController.Lang.EnterKeywordToSearchForFoods());
    }
    combo.setHideTrigger(true);
    combo.setTriggerAction(TriggerAction.ALL);
    combo.setValidateOnBlur(false);
    //update model when valid value
    combo.addListener(Events.Valid, new Listener<FieldEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleEvent(FieldEvent be) {
				try {
					ComboBox<FoodNameModel> cb = ((ComboBox<FoodNameModel>)be.getSource());
					
					//if selected something from the list
					if(cb.getValue() != null) {
						FoodNameModel mo = cb.getValue();
						
						//if user clicked "add new" value
						if(mo.getId() == -1) {
							final String str = combo.getRawValue();
							handler.nameChanged(str.substring(0, str.length()));
						}
						//value selected from list
						else if(handler != null) {
							//only if changed
							boolean changed = false;
							if(food.getName() != null) {
								if(food.getName().getId() != mo.getId()) {
									changed = true;
								}
							}
							else {
								changed = true;
		          }
							
							food.setName(mo);
							if(changed) {
								handler.saveData(food);
		          }
						}
					}
					
				} catch (Exception e) {
		      Motiver.showException(e);
				}			
			}				    	
    });
	    
    return combo;
	}

	/**
	 * Calls handler to save data
	 * <br>Uses timer so handler is called after 1 sec. (So if user changes value repeately, handler is called only once)
	 */
	protected void saveData() {
		if(timerUpdate != null) {
			timerUpdate.cancel();
    }

		timerUpdate = new Timer() {
			@Override
			public void run() {
				handler.saveData(food);
			}
		};
		timerUpdate.schedule(1000);
	}

	/**
	 * Updates portion label based on spinner field value
	 */
	protected void updatePortions(double value) {
		try {
			if(food.getName() == null) {
				return;
      }
			
			String text = "";
			double por = food.getName().getPortion();
			if(por == 0) {
				por = 100;
      }

			//if equal number
			double left = (((value / por) * 4) % 2);
			
			if(left == 0 && food.getName().getPortion() > 0) {
				double val = value / por;
				//one piece
				if(val == 1) {
					text = AppController.Lang.PortionsOne();
        }
				else if(val == 0) {
					text = "";
        }
				else {
					text = AppController.Lang.Portions(NumberFormat.getFormat("0.#").format((value / por)));
        }
				
			}

			labelPortions.setText(text);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}	
	
}
