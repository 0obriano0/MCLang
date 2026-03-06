package org.tsob.MCLang.Web;

/**
 * Web 主類基底接口
 * 用於讓 Web 模組能夠在啟用時註冊自己的功能
 */
public abstract class WebMainBase {

  public abstract void onEnable();

  public void onDisable() {
    // 默認實現為空，子類可以選擇性重寫
  }

  public void onReload() {
    // 默認實現為空，子類可以選擇性重寫
  }
}