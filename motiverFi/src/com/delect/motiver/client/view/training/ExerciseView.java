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
package com.delect.motiver.client.view.training;

import java.util.List;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.Templates;
import com.delect.motiver.client.presenter.training.ExercisePresenter;
import com.delect.motiver.client.presenter.training.ExercisePresenter.ExerciseHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.CustomListener;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.client.view.widget.Widgets;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.ExerciseNameModel;
import com.delect.motiver.shared.util.CommonUtils;
import com.delect.motiver.shared.util.CommonUtils.MessageBoxHandler;
import com.delect.motiver.shared.WorkoutModel;

import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelProcessor;
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
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.ListModelPropertyEditor;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;


public class ExerciseView extends ExercisePresenter.ExerciseDisplay {

	//template for combobox
	private static XTemplate nameTemplate = XTemplate.create(Templates.getExerciseNameTemplate());
	
	protected MessageBox box = null;
	protected ImageButton btnVideo;
	protected ComboBox<ExerciseNameModel> comboName = null;
	protected LayoutContainer containerName = new LayoutContainer();
	protected ExerciseModel exercise;

	private final HBoxLayoutData flexSets = new HBoxLayoutData(new Margins(0, 0, 0, 10));
	private final HBoxLayoutData flexX = new HBoxLayoutData(new Margins(4, 0, 0, 10));
	protected ExerciseHandler handler;
	protected LayoutContainer panelButtons = new LayoutContainer();
	protected LayoutContainer panelVideo = new LayoutContainer();

	protected ListStore<ExerciseNameModel> store;
	private LayoutContainer thisContent = new LayoutContainer();
	
	/**
	 * Exercise view
	 */
	public ExerciseView() {
		
		this.setStyleName("panel-exercise");
		
		HBoxLayout layout = new HBoxLayout();
		layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    thisContent.setLayout(layout);
		
		//show hide header buttons based on mouse position
		this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				
				addStyleName("panel-exercise-active");
				panelButtons.setVisible(true);
				if(btnVideo != null) {
					btnVideo.setVisible(true);
        }
				thisContent.layout(true);
			}
		});
		this.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				removeStyleName("panel-exercise-active");
				panelButtons.setVisible(false);
				if(btnVideo != null) {
					btnVideo.setVisible(false);
        }
				thisContent.layout(true);
			}
		});
	}
	
	@Override
	public Widget asWidget() {
		
		try {
			
		  WorkoutModel w = exercise.getWorkout();
		  //if our exercise
			if(exercise.getWorkout().getUser().equals(AppController.User)) {

	      //change order "link" (=drag source)
        HBoxLayoutData flexDrag = new HBoxLayoutData(new Margins(0, 5, 0, 0));
				thisContent.add(getDragButton(), flexDrag);		

	      //name combo
	      comboName = addExerciseCombo();
	      containerName.add(comboName);
	      thisContent.add(containerName);
	      
	      //video
	      panelVideo.setWidth(16);
	      thisContent.add(panelVideo, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
	      
	      //sets
        thisContent.add(getSpinnerField(), flexSets);

        HtmlContainer labelX1 = new HtmlContainer("x");
        labelX1.setStyleName("label-x");
        thisContent.add(labelX1, flexX);

        //reps
        HBoxLayoutData flexReps = new HBoxLayoutData(new Margins(0, 0, 0, 10));
        flexReps.setFlex(1);  
        final TextField<String> tfReps = new TextField<String>();
        tfReps.addStyleName("field-amount");
        tfReps.addListener(Events.OnClick, CustomListener.fieldOnClicked);
        tfReps.setValue(exercise.getReps());
        tfReps.setEmptyText(AppController.Lang.Reps());
        tfReps.setWidth(100);
        tfReps.addListener(Events.Valid, new Listener<BaseEvent>() {
          @Override
          public void handleEvent(BaseEvent be) {
            exercise.setReps(tfReps.getValue());
            handler.saveData(exercise, false);
          }
        });
        tfReps.setMaxLength(50);
        CommonUtils.setWarningMessages(tfReps);
        thisContent.add(tfReps, flexReps);

        HtmlContainer labelX2 = new HtmlContainer("x");
        labelX2.setStyleName("label-x");
        thisContent.add(labelX2, flexX);

        //weights
        final TextField<String> tfWeights = new TextField<String>();
        tfWeights.addStyleName("field-amount");
        tfWeights.addListener(Events.OnClick, CustomListener.fieldOnClicked);
        tfWeights.setValue(exercise.getWeights());
        tfWeights.setEmptyText(AppController.Lang.Weights());
        tfWeights.addListener(Events.Valid, new Listener<BaseEvent>() {
          @Override
          public void handleEvent(BaseEvent be) {
            exercise.setWeights(tfWeights.getValue());
            handler.saveData(exercise, false);
          }
        });
        tfWeights.setWidth(100);
        tfWeights.setMaxLength(50);
        CommonUtils.setWarningMessages(tfWeights);
        thisContent.add(tfWeights, flexReps);
            
        //buttons layout        
        thisContent.add(getPanelButtons(), new HBoxLayoutData(new Margins(0, 0, 0, 10)));
			}
      //not our exercise
      else {        
        HorizontalPanel panel = Widgets.getReadOnlyExercise(exercise);        
        thisContent.add(panel);
      }
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		panelButtons.setVisible(false);

    this.add(thisContent);
	    
		return this;
	}

	protected LayoutContainer getPanelButtons() {

    HBoxLayout layoutButtons = new HBoxLayout();
    layoutButtons.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    layoutButtons.setPack(BoxLayoutPack.END);
    panelButtons.setLayout(layoutButtons);
    panelButtons.setHeight(16);
    panelButtons.setWidth(50);
    panelButtons.setHideMode(HideMode.VISIBILITY);

    //last weights
    ImageButton btnLastWeights = new ImageButton(AppController.Lang.LastWeightsForThisExercise(), MyResources.INSTANCE.iconLastWeights());
    btnLastWeights.addListener(Events.OnClick, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        handler.showLastWeights();
      }
    });
    panelButtons.add(btnLastWeights, new HBoxLayoutData(new Margins(0, 10, 0, 0)));

    //remove exercise link
    ImageButton btnRemove = new ImageButton(AppController.Lang.RemoveTarget(AppController.Lang.ThisExercise().toLowerCase()), MyResources.INSTANCE.iconRemove());
    btnRemove.addListener(Events.OnClick, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        //ask for confirm
        box = CommonUtils.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisExercise().toLowerCase()), new MessageBoxHandler() {
          @Override
          public void okPressed(String text) {
            handler.exerciseRemoved();
          }
        });
        box.show();
      }
    });
    panelButtons.add(btnRemove);
    
    return panelButtons;
  }

  protected SpinnerField getSpinnerField() {

    final SpinnerField spinSets = new SpinnerField();
    //save value when valid
    spinSets.addListener(Events.Valid, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        if(spinSets.getValue() != null) {
          exercise.setSets(spinSets.getValue().intValue());
          handler.saveData(exercise, false);
        }
      }
    });
    spinSets.addStyleName("field-amount");
    spinSets.addListener(Events.OnClick, CustomListener.fieldOnClicked);
    spinSets.setAllowNegative(false);
    spinSets.setWidth(50);
    spinSets.setIncrement(1);
    spinSets.setMinValue(0);
    spinSets.setMaxValue(30);
    CommonUtils.setWarningMessages(spinSets);
    spinSets.setEditable(true);
    spinSets.setPropertyEditorType(Integer.class);
    spinSets.setFormat(NumberFormat.getFormat("0"));
    spinSets.setEmptyText(AppController.Lang.Sets());
    if(exercise.getSets() != 0) {
      spinSets.setValue(exercise.getSets());
    }
    
    return spinSets;
  }

  protected ImageButton getDragButton() {

    ImageButton btnDrag = new ImageButton(AppController.Lang.ChangeExerciseOrder(), MyResources.INSTANCE.iconBtnDrag());
    btnDrag.setTabIndex(-1);
    btnDrag.setData("view", this);
    //drag source
    DragSource source = new DragSource(btnDrag) {  
      @Override  
      protected void onDragStart(DNDEvent event) { 
        try {
          super.onDragStart(event);
              
          final ExerciseView view = (ExerciseView)event.getComponent().getData("view");
              
          //set exercise name as label and ExerciseView as data
          HTML html = new HTML();
          html.setHTML(AppController.Lang.MoveTarget(CommonUtils.getExerciseName(view.exercise.getName())));
          event.setData(view);  
          event.getStatus().update(El.fly(html.getElement()).cloneNode(true));
        } catch (Exception e) {
          Motiver.showException(e);
        }  
      }  
    };
    source.setGroup(Constants.DRAG_GROUP_WORKOUT + exercise.getWorkoutId());
    
    return btnDrag;
  }

  @Override
	public ExerciseModel getExercise() {
		return exercise;
	}

	@Override
	public void onStop() {
		if(box != null && box.isVisible()) {
      box.close();
    }
	}
	@Override
	public void setHandler(ExerciseHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setModel(ExerciseModel exercise) {
		this.exercise = exercise;
		
		//update combo
		if(comboName != null) {
			comboName = addExerciseCombo();
			containerName.removeAll();
			containerName.add(comboName);
			containerName.layout();
		}
		
		try {
			//show/hide video link
			boolean videoFound = false;
			if(exercise.getName() != null && exercise.getName().getVideo().length() > 0) {
        videoFound = true;
      }
			if(videoFound) {
				btnVideo = new ImageButton(AppController.Lang.ShowTarget(AppController.Lang.Video().toLowerCase()), MyResources.INSTANCE.iconBtnVideo());
				btnVideo.addListener(Events.OnClick, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						handler.showVideo();
					}
				});
				btnVideo.setVisible(false);
				panelVideo.add(btnVideo);
			}
			else {
				panelVideo.removeAll();
				btnVideo = null;
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	@Override
	public void setNameComboEnabled(boolean enabled) {
		comboName.setEnabled(enabled);
	}

	/**
	 * Adds exercise search combo
	 */
	protected ComboBox<ExerciseNameModel> addExerciseCombo() {

		final ComboBox<ExerciseNameModel> combo = new ComboBox<ExerciseNameModel>();
		
		//custom editors so we see also target correctly
		combo.setPropertyEditor(new ListModelPropertyEditor<ExerciseNameModel>() {
			@Override
			public ExerciseNameModel convertStringValue(String value) {
				return store.findModel("fn", value);
			}

			@Override
			public String getStringValue(ExerciseNameModel value) {
				return CommonUtils.getExerciseName(value);
			}
		});
		//set fullname to "fn" so we see target correctly
    combo.getView().setModelProcessor(new ModelProcessor<ExerciseNameModel>() {
			@Override
			public ExerciseNameModel prepareData(ExerciseNameModel model) {
				model.set("fn", CommonUtils.getExerciseName(model));
				return model;
			}
    });

		 // proxy, reader and loader
		RpcProxy<List<ExerciseNameModel>> proxy = new RpcProxy<List<ExerciseNameModel>>() {
      @Override
      protected void load(Object loadConfig, AsyncCallback<List<ExerciseNameModel>> callback) {
        BasePagingLoadConfig config = (BasePagingLoadConfig)loadConfig;

        String query = config.get("query").toString();
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

        try {
          //add "new exercise" to end of the list
          BaseListLoadResult<ExerciseNameModel> list = (BaseListLoadResult<ExerciseNameModel>)be.getData();
          final ExerciseNameModel model = new ExerciseNameModel(-1L, AppController.Lang.AddNew(AppController.Lang.Exercise().toLowerCase()), 0);
          list.getData().add(model);
				   
        } catch (Exception e) {
          Motiver.showException(e);
        }  
      }
    });
        
    store = new ListStore<ExerciseNameModel>(loader);

    combo.addStyleName("field-name");
    if(exercise.getName() != null && exercise.getName().getId() != 0) {
      combo.setValue(exercise.getName());
    }
    combo.setWidth(280); 
    combo.setValidationDelay(Constants.DELAY_SEARCH);
    combo.setForceSelection(true);
    combo.setMessageTarget("none");
    combo.setDisplayField("fn");
    combo.setMinChars(Constants.LIMIT_MIN_QUERY_WORD);
    combo.setTemplate(nameTemplate);  
    combo.setStore(store);
		combo.setEmptyText(AppController.Lang.EnterKeywordToSearchForExercises());
		combo.setLoadingText(AppController.Lang.Loading());
    combo.setHideTrigger(true);
    combo.setTriggerAction(TriggerAction.ALL);
    combo.setValidateOnBlur(false);
    //save typed value for when adding new name
    combo.addListener(Events.BeforeSelect, new Listener<FieldEvent>() {
      @Override
      public void handleEvent(FieldEvent be) {
        ComboBox<ExerciseNameModel> cb = ((ComboBox<ExerciseNameModel>)be.getSource());
        cb.setData("val", combo.getRawValue());
      }
    });
    
    //update model when valid value
    combo.addListener(Events.Valid, new Listener<FieldEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleEvent(FieldEvent be) {
				try {
					ComboBox<ExerciseNameModel> cb = ((ComboBox<ExerciseNameModel>)be.getSource());
					
					//if selected something from the list
					if(cb.getValue() != null) {
						
						ExerciseNameModel mo = cb.getValue();
						
						//if user clicked "add new" value
						if(mo.getId() == -1) {
              String val = combo.getData("val");
              handler.newNameEntered(val);
						}
						//value selected from list
						else if(handler != null) {
							//only if changed
							boolean changed = false;
							if(exercise.getName() != null) {
								if(exercise.getName().getId() != mo.getId()) {
									changed = true;
								}
							}
							else {
								changed = true;
              }
							
							exercise.setName(mo);
							if(changed) {
							  handler.saveData(exercise, true);
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
}
