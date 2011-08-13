/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.mobile.training;

import java.util.List;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.Templates;
import com.delect.motiver.client.presenter.training.ExercisePresenter;
import com.delect.motiver.client.presenter.training.ExercisePresenter.ExerciseHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.CustomListener;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.ExerciseNameModel;
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
import com.extjs.gxt.ui.client.data.ModelProcessor;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
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
import com.extjs.gxt.ui.client.widget.form.ListModelPropertyEditor;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;


public class ExerciseView extends ExercisePresenter.ExerciseDisplay {

	//template for combobox
	private static XTemplate nameTemplate = XTemplate.create(Templates.getExerciseNameTemplate());
	private MessageBox box = null;

	private ImageButton btnVideo;
	private ComboBox<ExerciseNameModel> comboName = null;
	private LayoutContainer containerName = new LayoutContainer();
	private ExerciseModel exercise;
	private ExerciseHandler handler;
	private LayoutContainer panelButtons = new LayoutContainer();
  private LayoutContainer panelVideo = new LayoutContainer();
	private SpinnerField spinSets = new SpinnerField();

	private ListStore<ExerciseNameModel> store;
	
	private final RowData rowData = new RowData(1, -1, new Margins(0, 0, 5, 0));
	
	/**
	 * Exercise view
	 */
	public ExerciseView() {
		
		this.setStyleName("panel-exercise");
		
		this.setLayout(new RowLayout());
		
	}
	
	@Override
	public Widget asWidget() {
		
		try {
						
			//food selection combo
			if(exercise.getUid().equals(AppController.User.getUid())) {
				comboName = addExerciseCombo();
				containerName.add(comboName);
				this.add(containerName, rowData);
			}
			else {
				Text textName = new Text();
				if(exercise.getName() != null) {
					textName.setText( exercise.getName().getName() );
        }
				textName.addStyleName("field-readonly field-name");
				textName.setWidth("100%");
				this.add(textName, rowData);
			}

			if(exercise.getUid().equals(AppController.User.getUid())) {
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
				spinSets.setWidth("100%");
				spinSets.setIncrement(1);
				spinSets.setMinValue(0);
				spinSets.setMaxValue(30);
				spinSets.setEditable(true);
				spinSets.setMessageTarget("none");
				spinSets.setPropertyEditorType(Integer.class);
				spinSets.setFormat(NumberFormat.getFormat("0"));
				if(AppController.IsSupportedBrowser) {
					spinSets.setEmptyText(AppController.Lang.Sets());
        }
				if(exercise.getSets() != 0) {
					spinSets.setValue(exercise.getSets());
        }
				this.add(spinSets, rowData);
			}
			else {
				final Text textSets = new Text();
				textSets.setText( String.valueOf(exercise.getSets()) );
				textSets.addStyleName("field-readonly");
				this.add(textSets, rowData);
			}
			
			//reps
			HBoxLayoutData flexReps = new HBoxLayoutData(new Margins(0, 0, 0, 10));
			flexReps.setFlex(1);  
			if(exercise.getUid().equals(AppController.User.getUid())) {
				final TextField<String> tfReps = new TextField<String>();
				tfReps.addStyleName("field-amount");
				tfReps.addListener(Events.OnClick, CustomListener.fieldOnClicked);
				tfReps.setValue(exercise.getReps());
				if(AppController.IsSupportedBrowser) {
					tfReps.setEmptyText(AppController.Lang.Reps());
        }
				tfReps.setWidth(100);
				tfReps.addListener(Events.Change, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						exercise.setReps(tfReps.getValue());
						handler.saveData(exercise, false);
					}
				});
        this.add(tfReps, rowData);
			}
			else {
				final Text textReps = new Text();
				textReps.setText( exercise.getReps() );
				textReps.addStyleName("field-readonly");
        this.add(textReps, rowData);
			}
			
			//weights
			if(exercise.getUid().equals(AppController.User.getUid())) {
				final TextField<String> tfWeights = new TextField<String>();
				tfWeights.addStyleName("field-amount");
				
				tfWeights.addListener(Events.OnClick, CustomListener.fieldOnClicked);
				tfWeights.setValue(exercise.getWeights());
				if(AppController.IsSupportedBrowser) {
					tfWeights.setEmptyText(AppController.Lang.Weights());
        }
				tfWeights.addListener(Events.Change, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						exercise.setWeights(tfWeights.getValue());
						handler.saveData(exercise, false);
					}
				});
				tfWeights.setWidth("100%");
        this.add(tfWeights, rowData);
			}
			else {
				final Text textWeights = new Text();
				textWeights.setText( exercise.getWeights() );
				textWeights.addStyleName("field-readonly");
				textWeights.setWidth(100);
        this.add(textWeights, rowData);
			}			
	        
			if(exercise.getUid().equals(AppController.User.getUid())) {
		        
				//buttons layout
				HBoxLayout layoutButtons = new HBoxLayout();
				layoutButtons.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
				layoutButtons.setPack(BoxLayoutPack.END);
        panelButtons.setLayout(layoutButtons);
        panelButtons.setHeight(16);
        panelButtons.setWidth(50);
        
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
				panelButtons.add(btnRemove, new HBoxLayoutData(new Margins(0, 10, 0, 0)));

        //video
        panelVideo.setWidth(16);
        panelButtons.add(panelVideo);
				
				this.add(panelButtons);
			}
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
	    
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
        String query = config.get("query").toString();
        for(int i=0; i < AppController.LangConstants.Targets().length; i++) {
          query = query.replaceAll(AppController.LangConstants.Targets()[i], "--" + i + "--");
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
    combo.setWidth("100%"); 
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
