package com.u2d.movielib;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;
import com.u2d.list.RelationalList;
import com.u2d.app.PersistenceMechanism;
import com.u2d.persist.Persist;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 23, 2005
 * Time: 4:40:22 PM
 */
@Persist
public class Actor extends AbstractComplexEObject
{
   private final StringEO _name = new StringEO();
   private final TextEO _bio = new TextEO();
   private final ImgEO _photo = new ImgEO();

   private final RelationalList _movies = new RelationalList(Movie.class);
   public static final Class moviesType = Movie.class;
   public static final int moviesRelationType = PersistenceMechanism.MANY_TO_MANY;
   public static final String moviesInverseFieldName = "actors";
   public static final boolean moviesRelationIsInverse = true;

   public static final String[] fieldOrder = {"name", "bio", "photo", "movies"};

   public Actor() {}

   public StringEO getName() { return _name; }
   public ImgEO getPhoto() { return _photo; }
   public TextEO getBio() { return _bio; }

   /* *** custom icon code:  use photo as icon if possible  *** */
   private transient PhotoIconAssistant _assistant = new PhotoIconAssistant(this, _photo);
   public Icon iconLg() { return _assistant.iconLg(); }
   public Icon iconSm() { return _assistant.iconSm(); }

   public RelationalList getMovies() { return _movies; }


   public Title title() { return _name.title(); }
}

