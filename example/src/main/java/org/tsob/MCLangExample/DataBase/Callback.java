package org.tsob.MCLangExample.DataBase;

public interface Callback<T> {
  void onSuccess(T result);
  void onFailure(Exception e);
}
