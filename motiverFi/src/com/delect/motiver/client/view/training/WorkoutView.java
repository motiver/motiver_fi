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

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.StringConstants;
import com.delect.motiver.client.presenter.training.ExercisePresenter.ExerciseDisplay;
import com.delect.motiver.client.presenter.training.WorkoutPresenter;
import com.delect.motiver.client.presenter.training.WorkoutPresenter.WorkoutHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.EnterNamePanel;
import com.delect.motiver.client.view.EnterNamePanel.EnterNamePanelHandler;
import com.delect.motiver.client.view.RatingPanel;
import com.delect.motiver.client.view.RatingPanel.RatingPanelHandler;
import com.delect.motiver.client.view.SmallNotePanel;
import com.delect.motiver.client.view.SmallNotePanelDisplay;
import com.delect.motiver.client.view.TimeSelectFieldView;
import com.delect.motiver.client.view.TimeSelectFieldView.TimeSelectFieldHandler;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;
import com.delect.motiver.shared.WorkoutModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;


public class WorkoutView extends WorkoutPresenter.WorkoutDisplay {

	private MessageBox box = null;
	//widgets
	private MyButton btnAdd = new MyButton();
	private ImageButton btnMoveWorkout;
	private WorkoutHandler handler;
	private Image imgDone = new Image(MyResources.INSTANCE.done());

	private Image imgDoneNot = new Image(MyResources.INSTANCE.notDone());	
	//panels
	private SmallNotePanelDisplay panelBase = (SmallNotePanelDisplay)GWT.create(SmallNotePanel.class);
	private LayoutContainer panelComments = new LayoutContainer();
	private LayoutContainer panelExercises = new LayoutContainer();
	private RatingPanel panelRating;
	private LayoutContainer panelUser = new LayoutContainer();
	private LayoutContainer panelWorkoutInfo = new LayoutContainer();	//times, rating

	private Text textDuration = new Text();
	private WorkoutModel workout = null;
	
	/**
	 * Workout view
	 * @param showOnlyTitle : show title (TRUE, open workout when clicked) or show just workout (FALSE)
	 */
	public WorkoutView() {
		
		try {
			panelBase.setStylePrefix("panel-workout");
			panelBase.setCollapsible(false);
			
			this.add(panelBase);

			//listener for shift + key
			panelBase.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					panelBase.setTabIndex(0);
				}
			});
			new KeyNav<ComponentEvent>(panelBase) { 
				@Override
				public void onKeyPress(ComponentEvent ce) {

					//if valid key comco
					if(Functions.isValidKeyCombo(ce)) {
            switch(ce.getKeyCode()) {
            //shift + E
			        		case 69:
			        			handler.newExercise();
			        			ce.cancelBubble();
			        			break;
            }
          }
				}
			};
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	@Override
	public Widget asWidget() {

		if(workout.getRoutineId() == 0 || workout.getDate() != null) {
			panelBase.setStyleName("panel-workout");
    }
		else {
			panelBase.setStyleName("panel-workout-inroutine");
    }
		
		panelBase.getPanelData().removeAll();

		try {
			//if no model -> ask for name
			if(workout.getId() == 0) {
				
				//add panel where user can type name
				EnterNamePanel panelNameInput = new EnterNamePanel(new EnterNamePanelHandler() {
					@Override
					public void newName(String name) {
						//if cancelled
						if(name == null) {
							handler.saveData(null);
            }
						else {
							workout.setName(name);
							handler.saveData(workout);
						}
					}
				});
				panelBase.getPanelData().add(panelNameInput);
				
			}
			//model set
			else {
		        
				//userview
				panelUser.setStyleAttribute("float", "right");
				panelUser.setStyleAttribute("margin", "20px 20px 0 20px");
				panelUser.setVisible(false);
				this.insert(panelUser, 0);
				
				//workout info
				HBoxLayout layout3 = new HBoxLayout();
				layout3.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);  
        panelWorkoutInfo.setLayout(layout3);
        panelWorkoutInfo.setHeight(45);
				panelWorkoutInfo.setStyleName("panel-workout-info");
				panelBase.getPanelData().add(panelWorkoutInfo, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
				
				//exercises
				panelExercises.setStyleAttribute("min-height", "125px");
				panelBase.getPanelData().add(panelExercises, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
				
				panelBase.getPanelData().add(panelComments, new RowData(-1, -1, new Margins(10)));
				
				panelExercises.setLayout(new RowLayout()); 

				initTitlePanel();
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		panelBase.getPanelData().layout();
		
		return this;
	}
	

	@Override
	public LayoutContainer getBodyContainer() {
		return panelExercises;
	}
	
	@Override
	public LayoutContainer getCommentsContainer() {
		return panelComments;
	}

	@Override
	public LayoutContainer getUserContainer() {
		panelUser.setVisible(true);
		return panelUser;
	}
	@Override
	public void onStop() {
		if(box != null && box.isVisible()) {
      box.close();
    }
	}

	@Override
	public void setAddButtonVisible(boolean visible) {
		if(btnAdd != null) {
			btnAdd.setVisible(visible);
    }
	}

	@Override
	public void setDropTarget(String target) {
		if(target != null) {
			DropTarget dt = new DropTarget(panelBase.getPanelData()) {    
				@Override
				protected void onDragCancelled(DNDEvent event) {
					setRowStyles(event, false);
					super.onDragCancelled(event);
				}
				@Override  
				protected void onDragDrop(DNDEvent event) {  
					super.onDragDrop(event);  

					try {
						int thisY = event.getXY().y - (panelExercises.getAbsoluteTop() - Window.getScrollTop());
						int childs = panelExercises.getItemCount();
						ExerciseDisplay draggedE = ((ExerciseDisplay)event.getData());
						long draggedId = draggedE.getExercise().getId();
						
						//check where this is dragged
						if(childs > 0) {
							int newPos = 1;
							int totalY = 0;
							for(newPos=1; newPos <= childs; newPos++) {
								LayoutContainer firstChild = (LayoutContainer)panelExercises.getItem(newPos - 1);
								final int childHeight = firstChild.getHeight();
								
								//before current
								if(thisY < totalY + childHeight / 2) {
									break;
								}
								//after current
								else if(thisY < totalY + childHeight) {
									newPos++;
									break;
								}
								
								totalY += childHeight;
							}
							
							//if new pos after old position -> remove on position
							if(draggedE.getExercise().getOrder() < newPos) {
								newPos--;
              }
							
							handler.dragged(draggedId, newPos);
							
							//reset styles
							for(int i=1; i <= childs; i++) {
								final LayoutContainer firstChild = (LayoutContainer)panelExercises.getItem(i - 1);

								//remove old styles
								firstChild.removeStyleName("row-drag-after");
								firstChild.removeStyleName("row-drag-before");
							}
						}
						
					} catch (Exception e) {
			      Motiver.showException(e);
					}
				} 
				@Override
				protected void onDragFail(DNDEvent event) {
					setRowStyles(event, false);
					super.onDragFail(event);
				}
				@Override
				protected void onDragLeave(DNDEvent event) {
					setRowStyles(event, false);
					super.onDragLeave(event);
				}
				@Override
				protected void onDragMove(DNDEvent event) {
					setRowStyles(event, true);
					super.onDragMove(event);
				}  
      };  
      dt.setGroup(target);  
      dt.setOverStyle("drag-ok");
		}
	}

	@Override
	public void setHandler(WorkoutHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setModel(WorkoutModel workout) {
		this.workout = workout;
	}

	/**
	 * Inits panel which contains the title
	 */
	private void initTitlePanel() {

		try {
			String name = "- " + AppController.Lang.NoName() + " -";
			if(workout.getName().length() > 0) {
				name = workout.getName();
      }
			panelBase.setTitleText(name);
			
			//buttons
			if(workout.getId() != 0) {
				
				if(workout.getUser().equals(AppController.User)) {
					
					//add food
					panelBase.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Exercise().toLowerCase()), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              handler.newExercise();
            }
          });
					
					//move workout (only if in calendar)
					if(workout.getDate() != null) {
						btnMoveWorkout = panelBase.addHeaderImageButton(AppController.Lang.Move(), MyResources.INSTANCE.iconBtnCalendar(), 
            new Listener<BaseEvent>() {
              @Override
              public void handleEvent(BaseEvent be) {
	
                //show popup containing date field and button
                final Popup popupMove = new Popup() {
                  //disable autohide when "autohide" variable is false
                  @Override
                  protected boolean onAutoHide(Event event) {
                    return Boolean.parseBoolean(this.getData("autohide").toString());
                  }
                };
                popupMove.setData("autohide", true);
                FormData formData = new FormData("0");
                FormPanel simple = new FormPanel();
                simple.setFrame(true); 
                simple.setWidth(250);
                simple.setHeading(AppController.Lang.MoveTarget(AppController.Lang.ThisWorkout().toLowerCase()));
                final DateField tfDate = new DateField(); 
                final DateTimeFormat fmt = DateTimeFormat.getFormat(StringConstants.DATEFORMATS[AppController.User.getDateFormat()]);
                DateTimePropertyEditor pr = new DateTimePropertyEditor(fmt.getPattern());
                tfDate.setPropertyEditor(pr);
                tfDate.setAllowBlank(false);
                if(workout.getDate() != null) {
                  tfDate.setValue(workout.getDate());
                }
                tfDate.setFieldLabel(AppController.Lang.Date());
                //disable autohide so popup won't close when text field is clicked
                tfDate.addListener(Events.OnClick, new Listener<BaseEvent>() {
                  @Override
                  public void handleEvent(BaseEvent be) {
                    popupMove.setData("autohide", false);
                  }
                });
                tfDate.addListener(Events.Valid, new Listener<BaseEvent>() {
                  @Override
                  public void handleEvent(BaseEvent be) {
                    popupMove.setData("autohide", true);
                  }
                });
                simple.add(tfDate, formData);  
                //move
                final Button b = new Button(AppController.Lang.Move());  
                simple.addButton(b);  
                simple.setButtonAlign(HorizontalAlignment.LEFT);
                FormButtonBinding binding = new FormButtonBinding(simple);  
                binding.addButton(b);
                b.addListener(Events.OnClick, new Listener<BaseEvent>() {
                  @Override
                  public void handleEvent(BaseEvent be) {
	
                    //if valid date
                    if(tfDate.isValid()) {
                      //hide popup
                      if(popupMove != null) {
                        popupMove.hide();
                      }
												
                      Date date = tfDate.getValue();
												
                      handler.workoutMoved(date);
                    }
											
                  }
                });
								   
                popupMove.add(simple);
                popupMove.show(btnMoveWorkout.getElement(), "", new int[] {-4, -4} );
								    
              }
            });
					}
					
					//rename workout
					panelBase.addHeaderImageButton(AppController.Lang.Rename(), MyResources.INSTANCE.iconBtnRename(), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              if(box != null && box.isVisible()) {
                box.close();
              }
              //ask for confirm
              box = Functions.getMessageBoxPrompt(workout.getName(), new MessageBoxHandler() {
                @Override
                public void okPressed(String text) {
                  if(!workout.getName().equals( text )) {
                    workout.setName(text);
                    panelBase.setTitleText(workout.getName());
							        		  
                    handler.saveData(workout);
                  }
                }
              });
              box.setTitle(AppController.Lang.Name());
              box.setMessage(AppController.Lang.EnterName() + ":");
              box.show(); 
            }
          });
					
					//remove workout
					panelBase.addHeaderImageButton(AppController.Lang.RemoveTarget(AppController.Lang.Workout().toLowerCase()), MyResources.INSTANCE.iconRemove(), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              //ask for confirm
              box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisWorkout().toLowerCase()), new MessageBoxHandler() {
                @Override
                public void okPressed(String text) {
                  handler.workoutRemoved();
                }
              });
              box.show();
            }
          });
				}

				//init times, rating, etc...
				if(workout.getDate() != null) {
		        
					//times
					panelWorkoutInfo.add(new Text(AppController.Lang.Time() + ": "), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
					
					if(workout.getUser().equals(AppController.User)) {
						TimeSelectFieldView tfStart = new TimeSelectFieldView((int) workout.getTimeStart(), new TimeSelectFieldHandler() {
							@Override
							public void timeChanged(int time) {
								workout.setTimeStart(time);
								
								setDuration();
								
								handler.saveData(workout);
							}
						});
						tfStart.setAllowBlank(true);
            panelWorkoutInfo.add(tfStart, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
            panelWorkoutInfo.add(new Text("-"), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
						TimeSelectFieldView tfEnd = new TimeSelectFieldView((int) workout.getTimeEnd(), new TimeSelectFieldHandler() {
							@Override
							public void timeChanged(int time) {
								workout.setTimeEnd(time);

								setDuration();
								
								handler.saveData(workout);
							}
						});
						tfEnd.setAllowBlank(true);
            panelWorkoutInfo.add(tfEnd, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
					}
					//show just text
					else {
            panelWorkoutInfo.add(new Text(Functions.getTimeToString((int) workout.getTimeStart())), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
            panelWorkoutInfo.add(new Text("-"), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
            panelWorkoutInfo.add(new Text(Functions.getTimeToString((int) workout.getTimeEnd())), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
					}
					
					//duration
					textDuration.setStyleName("label-duration");
					panelWorkoutInfo.add(textDuration, new HBoxLayoutData(new Margins(0, 5, 0, 5)));
					setDuration();
					
          //spacer
          HBoxLayoutData flex2 = new HBoxLayoutData(new Margins(0, 5, 0, 0));  
          flex2.setFlex(1);  
          panelWorkoutInfo.add(new Text(), flex2);
			        
          //rating
          panelRating = new RatingPanel(workout.getRating(), new RatingPanelHandler() {
						@Override
						public void ratingChanged(int rating) {
							workout.setRating(rating);
							
							handler.saveData(workout);
						}
					});
          panelRating.setEnabled(workout.getUser().equals(AppController.User));
          panelWorkoutInfo.add(panelRating, new HBoxLayoutData(new Margins(0, 10, 0, 0)));
					
          //done
					imgDone.setVisible(workout.getDone());
					imgDoneNot.setVisible(!workout.getDone());
					LayoutContainer lcDone = new LayoutContainer();
					if(workout.getUser().equals(AppController.User)) {
						//tooltip and click listener
						imgDoneNot.setTitle(AppController.Lang.MarkAsDone());
						final ClickHandler handlerClick = new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {	
								workout.setDone( !workout.getDone() );
								
								//change images
								imgDone.setVisible(workout.getDone());
								imgDoneNot.setVisible(!workout.getDone());
								
								handler.saveData(workout);
							}
						};
						imgDone.addClickHandler(handlerClick);
						imgDoneNot.addClickHandler(handlerClick);
						lcDone.setStyleAttribute("cursor", "pointer");
					}
					lcDone.add(imgDone);
					lcDone.add(imgDoneNot);
					panelWorkoutInfo.add(lcDone, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			        
          panelWorkoutInfo.layout();
				}
				else {
					panelWorkoutInfo.setVisible(false);
        }

			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Set duration text based on workout times
	 */
	protected void setDuration() {
		try {
			//set duration
			if(workout.getTimeStart() > 0 && workout.getTimeEnd() > 0 && workout.getTimeEnd() > workout.getTimeStart()) {
				textDuration.setText( Functions.getDurationString(workout.getTimeEnd() - workout.getTimeStart()) );
      }
			else {
				textDuration.setText("");
      }
			
		} catch (Exception e) {
			textDuration.setText("");
		}
	}

	/**
	 * Sets correct styles for each exercise (if not mousemove -> clears all styles)
	 * @param event
	 * @param onMouseMove : called from mousemove event
	 */
	protected void setRowStyles(DNDEvent event, boolean onMouseMove) {
		
		try {
			int thisY = event.getXY().y - (panelExercises.getAbsoluteTop() - Window.getScrollTop());
			int childs = panelExercises.getItemCount();
			
			//check where this is dragged
			if(childs > 0) {
				boolean found = false;
				int totalY = 0;
				for(int i=1; i <= childs; i++) {
					final LayoutContainer firstChild = (LayoutContainer)panelExercises.getItem(i - 1);
					final int childHeight = firstChild.getHeight();

					//remove old styles
					firstChild.removeStyleName("row-drag-after");
					firstChild.removeStyleName("row-drag-before");
					
					if(thisY < totalY + childHeight / 2 && !found && onMouseMove) {
						found = true;
						firstChild.addStyleName("row-drag-before");
					}
					else if(thisY < totalY + childHeight && !found && onMouseMove) {
						found = true;
						firstChild.addStyleName("row-drag-after");
					}
					totalY += childHeight;
				}
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
}
