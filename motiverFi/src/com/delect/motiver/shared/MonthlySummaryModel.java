/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class MonthlySummaryModel extends BaseModelData implements IsSerializable {
	
	
  /**
   * 
   */
  private static final long serialVersionUID = 4028315133690990192L;
  public MonthlySummaryModel()  {
		
	}
	
	public MonthlySummaryModel(Date date) {
		setDate(date);
	}

  public long getId() {
    if(get("id") != null) {
      return get("id");
    }
    else {
      return 0L;
    }
  }

  public String getUid() {
    if(get("uid") != null) {
      return get("uid");
    }
    else {
      return "";
    }
  }

	public List<MonthlySummaryExerciseModel> getExercises() {
		return get("e");
  }

	public Date getDate() {
		return get("d");
  }

  public void setId(long id) {
    set("id", id);
  }
  public void setUid(String uid) {
    set("uid", uid);
  }
	public void setExercises(List<MonthlySummaryExerciseModel> e) {
		set("e", e);
	}
	public void setDate(Date date) {
		set("d", date);
	}
}
