package org.tsob.MCLang.API;

/**
 * API 主類基底接口
 * 用於讓 API 模組能夠在啟用時註冊自己的功能
 */
public abstract class ApiMainBase {

  /**
   * 當插件啟用時被調用
   * API 模組可以在此方法中註冊指令、事件監聽器等
   */
  public abstract void onEnable();

  /**
   * 當插件停用時被調用
   * API 模組可以在此方法中清理資源
   */
  public void onDisable() {
    // 默認實現為空，子類可以選擇性重寫
  }

  public void onReload() {
    // 默認實現為空，子類可以選擇性重寫
  }
}