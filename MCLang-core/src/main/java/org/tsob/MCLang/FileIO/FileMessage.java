package org.tsob.MCLang.FileIO;

import org.tsob.MCLang.AnsiColor;
import org.tsob.MCLang.DataBase.DataBase;

public class FileMessage extends FileIO{
  public FileMessage() {
    super("message", tools.getLang() + "/Base.yml");
  }
  
  @Override
  public boolean reloadcmd() {
    this.setFileName(tools.getLang() + "/Base.yml");
    return true;
  }
  
  /**
   * 錯誤訊息顯示
   * @param title 標題
   * @param name 名稱
   */
  public void errorMessage(String title,String name) {
    DataBase.Print(AnsiColor.RED + "[Loadlanguage] " + title + " -> " + name + " data load error，use default..." + AnsiColor.RESET);
  }
  
}
