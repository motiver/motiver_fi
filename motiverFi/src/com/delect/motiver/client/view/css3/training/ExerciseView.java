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
package com.delect.motiver.client.view.css3.training;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.delect.motiver.client.view.css3.ExtendedTextBox;
import com.delect.motiver.client.view.css3.FlexBox;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.client.view.widget.Widgets;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.ExerciseNameModel;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;

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
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;


public class ExerciseView extends ExercisePresenter.ExerciseDisplay {

	private static XTemplate nameTemplate = XTemplate.create(Templates.getExerciseNameTemplate());
	
	private MessageBox box = null;
	private ImageButton btnVideo;
	private ComboBox<ExerciseNameModel> comboName = null;
	
	private LayoutContainer containerName = new LayoutContainer();
	private ExerciseModel exercise;
	private FlexBox flexBox = new FlexBox();
	private ExerciseHandler handler;

	private LayoutContainer panelButtons = new LayoutContainer();
	private LayoutContainer panelVideo = new LayoutContainer();

	private SpinnerField spinSets = new SpinnerField();
	private ListStore<ExerciseNameModel> store;
	private ExtendedTextBox tfReps = new ExtendedTextBox();
	private ExtendedTextBox tfWeights = new ExtendedTextBox();
	
	/**
	 * Meal view
	 */
	public ExerciseView() {
		
		this.setStyleName("panel-exercise");

    flexBox.setWidth("100%");
    this.add(flexBox);
		
		//show hide header buttons based on mouse position
		this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {				
				addStyleName("panel-exercise-active");
				panelButtons.setVisible(true);
				if(btnVideo != null) {
					btnVideo.setVisible(true);
        }
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
			}
		});
	}
	
	@Override
	public Widget asWidget() {
		
		try {
		  
		  //if our exercise
			if(exercise.getWorkout().getUid().equals(AppController.User.getUid())) {
			  
	      //change order "link" (=drag source)
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
              html.setHTML(AppController.Lang.MoveTarget(Functions.getExerciseName(view.exercise.getName())));
              event.setData(view);  
              event.getStatus().update(El.fly(html.getElement()).cloneNode(true));
            } catch (Exception e) {
              Motiver.showException(e);
            }  
          }  
				};
				source.setGroup("wp" + exercise.getWorkoutId());
				flexBox.add(btnDrag, -1, 5);	

	      //food selection combo
	      comboName = addExerciseCombo();
	      containerName.add(comboName);
	      flexBox.add(containerName, -1, 10);
        
	      //video
	      panelVideo.setWidth(16);
	      flexBox.add(panelVideo, -1, 10);

	      //sets
        spinSets  = new SpinnerField();
        //save value when valid
        spinSets.addListener(Events.Change, new Listener<BaseEvent>() {
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
        Functions.setWarningMessages(spinSets);
        spinSets.setEditable(true);
        spinSets.setPropertyEditorType(Integer.class);
        spinSets.setFormat(NumberFormat.getFormat("0"));
        if(AppController.IsSupportedBrowser) {
          spinSets.setEmptyText(AppController.Lang.Sets());
        }
        if(exercise.getSets() != 0) {
          spinSets.setValue(exercise.getSets());
        }
        flexBox.add(spinSets, -1, 10);

        HtmlContainer labelX1 = new HtmlContainer("x");
        labelX1.setStyleName("label-x");
        flexBox.add(labelX1, -1, 10);

        //reps
        tfReps.addStyleName("field-amount");
        tfReps.setValue(exercise.getReps());
        tfReps.setEmptyText(AppController.Lang.Reps());
        tfReps.setMaxLength(100);
        tfReps.setWidth("96%");
        tfReps.addChangeHandler(new ChangeHandler() {
          @Override
          public void onChange(ChangeEvent event) {
            exercise.setReps(tfReps.getValue());
            handler.saveData(exercise, false);
          }
        });
        flexBox.add(tfReps, 1, 10);

        HtmlContainer labelX2 = new HtmlContainer("x");
        labelX2.setStyleName("label-x");
        flexBox.add(labelX2, -1, 10);

        //weights
        tfWeights.addStyleName("field-amount");
        tfWeights.setValue(exercise.getWeights());
        tfWeights.setEmptyText(AppController.Lang.Weights());
        tfWeights.addChangeHandler(new ChangeHandler() {
          @Override
          public void onChange(ChangeEvent event) {
            exercise.setWeights(tfWeights.getValue());
            handler.saveData(exercise, false);
          }
        });
        tfWeights.setMaxLength(100);
        tfWeights.setWidth("96%");
        flexBox.add(tfWeights, 1, 10);

        //buttons layout
        HBoxLayout layoutButtons = new HBoxLayout();
        layoutButtons.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
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
            box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisExercise().toLowerCase()), new MessageBoxHandler() {
              @Override
              public void okPressed(String text) {
                handler.exerciseRemoved();
              }
            });
            box.show();
          }
        });
        panelButtons.add(btnRemove);
        
        flexBox.add(panelButtons, -1, -1);
			}
			//not our exercise
			else {			  
			  HorizontalPanel panel = Widgets.getReadOnlyExercise(exercise);        
        flexBox.add(panel, -1, 0);
			}
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		panelButtons.setVisible(false);
	    
		return this;
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
		
		//update combo (if changed)
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
				return Functions.getExerciseName(value);
			}
		});
		//set fullname to "fn" so we see target correctly
    combo.getView().setModelProcessor(new ModelProcessor<ExerciseNameModel>() {
			@Override
			public ExerciseNameModel prepareData(ExerciseNameModel model) {
				model.set("fn", Functions.getExerciseName(model));
				return model;
			}
    });

		 // proxy, reader and loader
		RpcProxy<List<ExerciseNameModel>> proxy = new RpcProxy<List<ExerciseNameModel>>() {
      @Override
      protected void load(Object loadConfig, AsyncCallback<List<ExerciseNameModel>> callback) {
        BasePagingLoadConfig config = (BasePagingLoadConfig)loadConfig;
       		
        //parse query name (transfer equipment's name to index)
        String query = config.get("query").toString().toLowerCase();
        for(int i=0; i < AppController.LangConstants.Targets().length; i++) {
          query = query.replaceAll(AppController.LangConstants.Targets()[i].toLowerCase(), "--" + i + "--");
        }
       		
        //trim
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
        //add "add new exercise" item on list
        BaseListLoadResult<ExerciseNameModel> list = (BaseListLoadResult<ExerciseNameModel>)be.getData();
        final ExerciseNameModel model = new ExerciseNameModel(-1L, AppController.Lang.AddNew(AppController.Lang.Exercise().toLowerCase()), 0);
        list.getData().add(model);
      }
    });
        
    store = new ListStore<ExerciseNameModel>(loader);

    combo.addStyleName("field-name");
    if(exercise.getName() != null) {
      if(exercise.getName().getId() != 0) {
        combo.setValue(exercise.getName());
      }
    }
    combo.setWidth(280); 
    combo.setValidationDelay(Constants.DELAY_SEARCH);
    combo.setForceSelection(true);
    combo.setMessageTarget("none");
    combo.setDisplayField("fn");
    combo.setMinChars(Constants.LIMIT_MIN_QUERY_WORD);
    combo.setTemplate(nameTemplate);  
    combo.setStore(store);
    if(AppController.IsSupportedBrowser) {
      combo.setEmptyText(AppController.Lang.EnterKeywordToSearchForExercises());
    }
    combo.setLoadingText(AppController.Lang.Loading());
    combo.setHideTrigger(true);
    combo.setTriggerAction(TriggerAction.ALL);
    combo.setValidateOnBlur(false);
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
							final String str = combo.getRawValue();
							handler.nameChanged(str.substring(0, str.length()));
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
