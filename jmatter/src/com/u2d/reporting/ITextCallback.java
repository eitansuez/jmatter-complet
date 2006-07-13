package com.u2d.reporting;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Mar 11, 2006
 * Time: 11:52:10 AM
 */
public interface ITextCallback
{
   public void doInIText(Document doc) throws DocumentException;
}
