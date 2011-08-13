/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.History;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.CommentRemovedEvent;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.CommentModel;

/**
 * Shows single comment
 * - user
 * - where comment is
 * - text
 * @author Antti
 *
 */
public class CommentPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class CommentDisplay extends Display {
		/**
		 * Sets if comment is clikable
		 * @param isClickable boolean
		 */
		public abstract void setClickable(boolean isClickable);
		/**
		 * Sets handler to view
		 * @param handler CommentHandler
		 */
		public abstract void setHandler(CommentHandler handler);
		/**
		 * Set comment
		 * @param comment CommentModel
		 */
		public abstract void setModel(CommentModel comment);
	}
	/** Handler for this presenter
	 */
	public interface CommentHandler {
		/**
		 * Called when comment is removed
		 */
		void commentRemoved();
		/**
		 * Called when comment is clicked
		 */
		void onClick();
	}
	private final CommentModel comment;
	
	private CommentDisplay display = null;
	private final boolean isClickable;
		
	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param comment CommentModel
	 * @param isClickable boolean
	 */
	public CommentPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CommentDisplay display, CommentModel comment, boolean isClickable) {
		super(rpcService, eventBus);
    this.display = display;
    this.comment = comment;
    this.isClickable = isClickable;
		
    if(comment == null) {
      return;
    }
	}
	  
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		display.setModel(comment);
		display.setClickable(isClickable);
		
		//set handler when clicked
		display.setHandler(new CommentHandler() {
			@Override
			public void commentRemoved() {
				display.setContentEnabled(false);
				
				final List<CommentModel> list = new ArrayList<CommentModel>();
				list.add(comment);
				final Request req = rpcService.removeComments(list, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						stop();
						
						if(result) {
							fireEvent(new CommentRemovedEvent(comment));
							
						}
					}
				});
				addRequest(req);
			}
			@Override
			public void onClick() {
				showCommentsTarget();
			}
		});
	}

	@Override
	public void onRun() {
	}

	/**
	 * Shows comment's target
	 */
	protected void showCommentsTarget() {

		String token = "";
		
		try {
			//if workout
			if(comment.getWorkout() != null) {
				token = "user/training/";
				//date (just open correct day
				if(comment.getWorkout().getDate() != null) {
					token += comment.getWorkout().getDate().getTime() / 1000;
				}
				//open also workout
				else {
					token += (new Date()).getTime() / 1000 + "/";
					token += "w" + comment.getWorkout().getId();
				}
			}
			//if routine
			else if(comment.getRoutine() != null) {
				token = "user/training/";
				//date (just open correct day
				if(comment.getRoutine().getDate() != null) {
					token += comment.getRoutine().getDate().getTime() / 1000;
				}
				//open also workout
				else {
					token += (new Date()).getTime() / 1000 + "/";
					token += "r" + comment.getRoutine().getId();
				}
			}
			//if meal
			else if(comment.getMeal() != null) {
				token = "user/nutrition/";
				//date (just open correct day
				if(comment.getMeal().getDate() != null) {
					token += comment.getMeal().getDate().getTime() / 1000;
				}
				//open also workout
				else {
					token += (new Date()).getTime() / 1000 + "/";
					token += "m" + comment.getMeal().getId();
				}
			}
			//if measurement
			else if(comment.getMeasurement() != null) {
				token = "user/profile/";
				token += "m" + comment.getMeasurement().getId();
			}
			//if cardio
			else if(comment.getCardio() != null) {
				token = "user/cardio/";
				token += "c" + comment.getCardio().getId();
			}
			//if run
			else if(comment.getRun() != null) {
				token = "user/cardio/";
				token += "r" + comment.getRun().getId();
			}
			//if nutrition
			else if(comment.getNutritionDate() != null) {
				token = "user/nutrition/";
				token += comment.getNutritionDate().getTime() / 1000;
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		if(token.length() > 0) {
      History.newItem(token);
    }
	}

}
