package com.chozoi.product.domain.factories.notification;

public abstract class AbstractFactory {
  public abstract EventChozoi getEvent(String type);
}
