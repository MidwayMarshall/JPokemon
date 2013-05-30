package com.jpokemon.mapeditor.widget.selector;

import java.awt.Component;

import javax.swing.JLabel;

import org.jpokemon.map.gps.Area;
import org.jpokemon.map.gps.Border;

public class BorderSelector extends JPokemonSelector<Border> {
  public BorderSelector(int a) {
    area = a;
  }

  public void setArea(int a) {
    if (a != area) {
      area = a;
      setSelectedIndex(-1);
    }
  }

  protected void renderElement(Component c, Border border) {
    Area nextArea = Area.get(border.getNext());
    ((JLabel) c).setText(nextArea.toString());
  }

  @Override
  protected void reloadItems() {
    removeAllItems();

    for (Border border : Border.get(area)) {
      addElementToModel(border);
    }
  }

  private int area;

  private static final long serialVersionUID = 1L;
}