package com.u2d.mytunes;

import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.ImgEO;
import com.u2d.type.atom.PhotoIconAssistant;
import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.list.RelationalList;
import com.u2d.persist.Persist;

import javax.swing.*;

@Persist
public class Album extends AbstractComplexEObject
{
   private final StringEO _name = new StringEO();
   
   private final RelationalList _songs = new RelationalList(Song.class);
   public static final Class songsType = Song.class;
   public static String songsInverseFieldName = "album";
   
   private final ImgEO _cover = new ImgEO();
   
   public static final String[] fieldOrder = {"name", "cover", "songs"};

   public Album() {}

   public StringEO getName() { return _name; }
   public RelationalList getSongs() { return _songs; }
   public ImgEO getCover() { return _cover; }

   private transient PhotoIconAssistant assistant = new PhotoIconAssistant(this, _cover);
   public Icon iconLg() { return assistant.iconLg(); }
   public Icon iconSm() { return assistant.iconSm(); }

   public Title title() { return _name.title(); }
}
