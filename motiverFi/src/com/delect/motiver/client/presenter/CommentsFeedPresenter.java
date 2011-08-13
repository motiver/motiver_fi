/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.presenter.CommentPresenter.CommentDisplay;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.CommentView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.shared.CommentModel;
import com.delect.motiver.shared.Constants;

/**
 * Shows comment's from other users
 * @author Antti
 *
 */
public class CommentsFeedPresenter extends Presenter {

	/** Handler for this presenter
	 */
	public interface CommentFeedHandler {
	}
	/**
	* Abstract class for view to extend
	*/
	public abstract static class CommentsFeedDisplay extends Display {
		/**
		 * Sets handler for view to call
		 * @param handler CommentFeedHandler
		 */
		public abstract void setHandler(CommentFeedHandler handler);
	}

	private List<Presenter> commentPresenters = new ArrayList<Presenter>();
	private CommentsFeedDisplay display;
	private EmptyPresenter emptyPresenter;
	private ShowMorePresenter showMorePresenter;

	/**
  * Shows comment's from other users
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public CommentsFeedPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CommentsFeedDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
		
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		display.setHandler(new CommentFeedHandler() {
			
		});
	}
	
	@Override
	public void onRun() {
	    
    loadComments(0);
	}


	@Override
	public void onStop() {
		
		unbindPresenters();
	}


	/**
	 * Unbinds all the meal/time presenters
	 */
	private void unbindPresenters() {
		if(commentPresenters != null) {
			for(int i=0; i < commentPresenters.size(); i++) {
				final Presenter presenter = commentPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			commentPresenters.clear();
		}
	}


	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(Presenter presenter) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		commentPresenters.add(presenter);
		presenter.run(display.getBaseContainer());
	}

	/**
	 * Loads comments from facebook
	 * @param index int
	 */
	protected void loadComments(final int index) {
    		
		if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
		//stop show more
		if(showMorePresenter != null) {
      showMorePresenter.stop();
    }
    
    //add empty presenter
    emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING_SMALL);
    emptyPresenter.run(display.getBaseContainer());
    
		//stop presenters if first items
		if(index == 0) {
      unbindPresenters();
      Motiver.setNextCallCacheable(true);
    }
		
		//fetch all comments
		final Request req = rpcService.getComments(index, Constants.LIMIT_COMMENTS_FEED, null, AppController.User.getUid(), true, new MyAsyncCallback<List<CommentModel>>() {
			@Override
			public void onSuccess(List<CommentModel> result) {
				showComments(index, result);
			}
		});
		addRequest(req);
	}

	/**
	 * Parses facebooks groups
	 * @param index int
	 * @param result List<CommentModel>
	 */
	protected void showComments(final int index, List<CommentModel> result) {

		try {

			if(emptyPresenter != null) {
        emptyPresenter.stop();
	    }
			//stop show more
			if(showMorePresenter != null) {
        showMorePresenter.stop();
	    }
			//stop presenters if first items
			if(index == 0) {
        unbindPresenters();
	    }
			
			//if no comments
			if(result.size() == 0 && index == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoComments(), EmptyPresenter.OPTION_SMALLER_LEFT_ALIGN);
				emptyPresenter.run(display.getBaseContainer());
			}
			else {
				//add comments
				for(CommentModel m : result) {
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadComments(index + Constants.LIMIT_COMMENTS);
							}
						});
						showMorePresenter.run(display.getBaseContainer());
					}
					else {
						final CommentPresenter fp = new CommentPresenter(rpcService, eventBus, (CommentDisplay)GWT.create(CommentView.class), m, true);
						addNewPresenter(fp);
					}
				}
				
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

}
