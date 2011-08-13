/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.statistics;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.statistics.MonthlyReportPresenter.MonthlyReportDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.statistics.MonthlyReportView;
import com.delect.motiver.shared.MonthlySummaryModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class MonthlyReportListPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MonthlyReportListDisplay extends Display {
	  public abstract void setMonthlySummaries(List<MonthlySummaryModel> summaries);
	  public abstract void setHandler(MonthlyReportListHandler handler);
	  public abstract LayoutContainer getBodyContainer();
	}
	
	public interface MonthlyReportListHandler {
	  void summarySelected(long summaryId);
	}
	
	private MonthlyReportListDisplay display;
  protected MonthlyReportPresenter monthlySummaryPresenter;
  protected EmptyPresenter emptyPresenter;

	/**
	 * History for single exercise
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public MonthlyReportListPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MonthlyReportListDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}
	

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
	  display.setHandler(new MonthlyReportListHandler() {
      @Override
      public void summarySelected(long summaryId) {
        if(monthlySummaryPresenter != null) {
          monthlySummaryPresenter.stop();
        }

        //show loading
        emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
        emptyPresenter.run(display.getBodyContainer());
        
        final Request req = rpcService.getMonthlySummary(summaryId, new MyAsyncCallback<MonthlySummaryModel>() {
          @Override
          public void onSuccess(MonthlySummaryModel result) {
            if(emptyPresenter != null) {
              emptyPresenter.stop();
            }
            
            monthlySummaryPresenter = new MonthlyReportPresenter(rpcService, eventBus, (MonthlyReportDisplay)GWT.create(MonthlyReportView.class), result); 
            monthlySummaryPresenter.run(display.getBodyContainer());
          }
        }); 
        addRequest(req);
      }
	  });
	}
	
	@Override
	public void onRun() {
	  
    //load monthly summaries
    final Request req = rpcService.getMonthlySummaries(new MyAsyncCallback<List<MonthlySummaryModel>>() {
      @Override
      public void onSuccess(List<MonthlySummaryModel> result) {
        display.setMonthlySummaries(result);
      }
    }); 
    addRequest(req);
	}

	@Override
	public void onStop() {
    if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
    if(monthlySummaryPresenter != null) {
      monthlySummaryPresenter.stop();
    }
	}
}
