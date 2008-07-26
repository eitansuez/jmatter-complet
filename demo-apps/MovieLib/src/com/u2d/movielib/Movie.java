package com.u2d.movielib;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.*;
import com.u2d.list.RelationalList;
import com.u2d.app.PersistenceMechanism;
import javax.persistence.Entity;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Nov 23, 2005
 * Time: 4:40:18 PM
 */
@Entity
public class Movie extends AbstractComplexEObject
{
   private final StringEO _title = new StringEO();
   private final ImgEO _poster = new ImgEO();
   private final TextEO _description = new TextEO();
   
   private final RelationalList _actors = new RelationalList(Actor.class);
   public static final Class actorsType = Actor.class;
   public static final int actorsRelationType = PersistenceMechanism.MANY_TO_MANY;
   public static final String actorsInverseFieldName = "movies";
   
   private Genre _genre;
   private final IntEO _year = new IntEO();
   private final StringEO _director = new StringEO();
   private final StringEO _producer = new StringEO();

   public static final String[] fieldOrder= {"title", "poster", "description", "actors", 
                                             "genre", "year", "director", "producer"};
   public static String[] tabViews = {"description"};
   
   public Movie() {}

   public StringEO getTitle() { return _title; }
   public ImgEO getPoster() { return _poster; }
   public TextEO getDescription() { return _description; }
   public RelationalList getActors() { return _actors; }

   public Genre getGenre() { return _genre; }
   public void setGenre(Genre genre)
   {
      Genre oldValue = _genre;
      _genre = genre;
      firePropertyChange("genre", oldValue, _genre);
   }

   public IntEO getYear() { return _year; }
   
   public StringEO getDirector() { return _director; }
   public StringEO getProducer() { return _producer; }



   public Title title()
   {
      return _title.title().appendParens(_year);
   }

}
