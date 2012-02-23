package com.delect.motiver.client.view.widget;

public class PopupSize {
  public int w;
  public int h;
  public int wMax;
  public int hMax;
  public int wMin;
  public int hMin;

  public PopupSize() {
    this(600, 400);
  }
  
  public PopupSize(int w, int h) {
    this.w = w;
    this.h = h;
    this.wMax = w+200;
    this.hMax = w+200;
    this.wMin = w-150;
    this.hMin = h-150;
  }
}
