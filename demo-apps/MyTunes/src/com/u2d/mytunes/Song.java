package com.u2d.mytunes;

import com.u2d.model.AbstractComplexEObject;
import com.u2d.model.Title;
import com.u2d.type.atom.StringEO;
import com.u2d.type.atom.TimeEO;
import com.u2d.type.atom.FileEO;
import com.u2d.element.CommandInfo;
import com.u2d.persist.HBMSingleSession;
import com.u2d.reflection.Cmd;
import com.u2d.reflection.Arg;
import com.u2d.reflection.FieldAt;
import com.u2d.app.Context;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.io.FileFilter;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Song extends AbstractComplexEObject
{
   private final StringEO _title = new StringEO();
   private final TimeEO _duration = new TimeEO();
   private final FileEO _path = new FileEO();

   private Album _album;
   public static final String albumInverseFieldName = "songs";

   private final Genre _genre = new Genre();
   private Artist _artist;

   public static String[] fieldOrder = {"title", "duration", "artist", "album", "genre", "path"};

   public Song() {}
   public Song(File path)
   {
      _title.setValue(path.getName());
      _path.setValue(path);
   }

   public StringEO getTitle() { return _title; }

   @FieldAt(format="m:ss")
   public TimeEO getDuration() { return _duration; }

   public Album getAlbum() { return _album; }
   public void setAlbum(Album album)
   {
      Album oldAlbum = _album;
      _album = album;
      firePropertyChange("album", oldAlbum, _album);
   }

   public Genre getGenre() { return _genre; }

   public Artist getArtist() { return _artist; }
   public void setArtist(Artist artist)
   {
      Artist oldValue = _artist;
      _artist = artist;
      firePropertyChange("artist", oldValue, _artist);
   }

   public FileEO getPath() { return _path; }

   public Title title() { return _title.title().appendBracket(_duration); }

   private AudioClip _clip;

   @Cmd(mnemonic='p')
   public Object Play(CommandInfo cmdInfo)
   {
      try
      {
         _clip = Applet.newAudioClip(_path.fileValue().toURL());
         vmech().onMessage("Playing song.."+this);
         _clip.play();
      }
      catch (MalformedURLException ex)
      {
         System.err.println(ex);
      }
      return null;
   }

   @Cmd
   public void Pause(CommandInfo cmdInfo)
   {
      if (_clip != null) _clip.stop();
   }

   @Cmd
   public static Object ScanFromBasePath(CommandInfo cmdInfo, 
                                         @Arg("Base Path") FileEO basePath)
   {
      if (!basePath.fileValue().isDirectory())
         return "You must specify a directory as the base path";

      List songFiles = basePath.listRecursive(mp3Filter);
      Set songs = new HashSet();
      for (int i=0; i<songFiles.size(); i++)
      {
         
         songs.add(new Song((File) songFiles.get(i)));
      }

      HBMSingleSession pmech = (HBMSingleSession)
            Context.getInstance().getPersistenceMechanism();
      pmech.saveMany(songs);
      return "Finished importing MP3s";
   }

   private static FileFilter mp3Filter = new FileFilter()
   {
      public boolean accept(File file)
      {
         return file.getName().toLowerCase().endsWith(".mp3");
      }
   };

}
