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
package com.delect.motiver.client.view.nutrition;

import java.util.List;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.Templates;
import com.delect.motiver.client.presenter.nutrition.FoodPresenter;
import com.delect.motiver.client.presenter.nutrition.FoodPresenter.FoodHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.CustomListener;
import com.delect.motiver.client.view.MySpinnerField;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.FoodNameModel;
import com.delect.motiver.shared.util.CommonUtils;
import com.delect.motiver.shared.util.CommonUtils.MessageBoxHandler;

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
import com.extjs.gxt.ui.client.widget.Popup;
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
	protected ComboBox<FoodNameModel> comboName;
	
	protected LayoutContainer containerName = new LayoutContainer();
	protected FoodModel food;
	private FoodHandler handler;
	protected Text labelPortions = new Text();
	public LayoutContainer panelButtons = new LayoutContainer();
	//widgets
	private MySpinnerField spinAmount = new MySpinnerField();
	
	private ListStore<FoodNameModel> store;
	//template for combobox
	private XTemplate template = XTemplate.create(Templates.getFoodNameTemplate());
	//panels
	private LayoutContainer thisContent = new LayoutContainer();
	private Popup popup = new Popup();
	
	private Timer timerUpdate;	
	
	/**
	 * Food view
	 */
	public FoodView() {
		this.setStyleName("panel-food");
        
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    thisContent.setLayout(layout);
    thisContent.setAutoHeight(true);
    thisContent.setAutoWidth(true);
    this.add(thisContent);
		
		//show hide header buttons based on mouse position
		this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				addStyleName("panel-food-active");
				panelButtons.setVisible(true);
				thisContent.layout(true);

        if(!popup.isVisible()) {
          popup.showAt(getAbsoluteLeft()+getWidth()+35, getAbsoluteTop()+5);
        }
			}
		});
		this.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				removeStyleName("panel-food-active");
				panelButtons.setVisible(false);
				thisContent.layout(true);
				
        if(popup.isVisible()) {
          popup.hide();
        }
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
				thisContent.add(containerName);
			}
			else {
				Text textName = new Text();
				if(food.getName() != null) {
					textName.setText( food.getName().getName() );
        }
				textName.addStyleName("field-readonly");
				textName.setStyleAttribute("text-align", "left");
				textName.setWidth(325);
				thisContent.add(textName);
			}

			//amount
      thisContent.add(getSpinAmount(), new HBoxLayoutData(new Margins(0, 0, 0, 10)));

			//portion
			labelPortions.setText("-");
			labelPortions.setStyleName("label-portions");
			thisContent.add(labelPortions, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
			updatePortions(food.getAmount());
	        
			if(food.getUid().equals(AppController.User.getUid())) {
				thisContent.add(getPanelButtons());
			}

	    popup.setSize(150, 25);
	    popup.setConstrainViewport(false);
	    popup.setAutoHide(false);
      updatePopup();
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		panelButtons.setVisible(false);
		
		return this;
	}
	
	/**
	 * Updates popup showing total calories
	 */
	private void updatePopup() {

    double e = 0D;
    double p = 0;
    double c = 0;
    double f = 0;
    
    if(food.getName() != null && food.getName().getEnergy() > 0) {
      final double amount = food.getAmount();
      e = (food.getName().getEnergy() / 100) * amount;
      p = (food.getName().getProtein() / 100) * amount;
      c = (food.getName().getCarb() / 100) * amount;
      f = (food.getName().getFet() / 100) * amount;
    }
	  
    popup.removeAll();
    if(e > 0) {
      popup.add(CommonUtils.getTotalPanelFlow(e, p, c, f));
    }
    popup.layout();
  }

  protected LayoutContainer getPanelButtons() {

    //spacer
    HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 0, 0, 5));
    flex.setFlex(1);  
    thisContent.add(new Text(), flex);  

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
        String html = CommonUtils.getDragPanel(AppController.Lang.CopyTargetTo(name, "..."));
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
        box = CommonUtils.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisFood().toLowerCase()), new MessageBoxHandler() {
          @Override
          public void okPressed(String text) {
            handler.foodRemoved();
          }
        });
        box.show();
      }
    });
    panelButtons.add(btnRemove, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
    
    return panelButtons;
  }

  protected Widget getSpinAmount() {

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
      spinAmount.addListener(Events.Valid, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          if(handler != null && spinAmount.getValue() != null && spinAmount.isValid() && Double.compare(spinAmount.getValue().doubleValue(), food.getAmount()) != 0) {
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
      return spinAmount;
    }
    else {
      final Text textValue = new Text();
      textValue.setText( NumberFormat.getFormat(NumberFormat.getDecimalFormat().getPattern() + " g").format(food.getAmount()) );
      textValue.addStyleName("field-readonly");
      textValue.setWidth(125);
      return textValue;
    }
    
  }

  @Override
	public void onStop() {
		if(timerUpdate != null) {
			timerUpdate.cancel();
    }
		if(box != null && box.isVisible()) {
      box.close();
    }
    
    if(popup != null && popup.isVisible()) {
      popup.hide();
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
        final FoodNameModel model = new FoodNameModel(-1L, AppController.Lang.AddNew(AppController.Lang.Food().toLowerCase()));
        list.getData().add(model);
      }
    });
        
    store = new ListStore<FoodNameModel>(loader);  
    combo.addStyleName("field-name");

    if(food.getName() != null) {
      if(food.getName().getId() != 0) {
        combo.setValue(food.getName());
      }
    }
    combo.setWidth(325);   
    combo.setValidationDelay(Constants.DELAY_SEARCH);
    combo.setForceSelection(false);
    combo.setMessageTarget("none");
    combo.setDisplayField("n");
    combo.setMinChars(Constants.LIMIT_MIN_QUERY_WORD);
    combo.setTemplate(template );  
    combo.setStore(store);
    combo.setEmptyText(AppController.Lang.EnterKeywordToSearchForFoods());
    combo.setHideTrigger(true);
    combo.setTriggerAction(TriggerAction.ALL);
    combo.setValidateOnBlur(false);
    //save typed value for when adding new name
    combo.addListener(Events.BeforeSelect, new Listener<FieldEvent>() {
      @Override
      public void handleEvent(FieldEvent be) {
        ComboBox<FoodNameModel> cb = ((ComboBox<FoodNameModel>)be.getSource());
        cb.setData("val", combo.getRawValue());
      }
    });
    
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
						  String val = combo.getData("val");
							handler.newNameEntered(val);
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
			
      updatePopup();
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}	
	
}
