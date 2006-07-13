/*
 * Created on Oct 6, 2004
 */
package com.u2d.list;

/**
 * @author Eitan Suez
 */
public interface Paginable
{
   public void firstPage();
   public boolean hasNextPage();
   public boolean hasPreviousPage();
   public void nextPage();
   public void previousPage();
   public String getPageTitleInfo();
   
   public void fetchPage(int pageNum);
   public int numPages();
   public int pageNum();
   public void lastPage();
}
