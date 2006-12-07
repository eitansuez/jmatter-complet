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
import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.decoder.JavaLayerException;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.v2.ID3V2Tag;
import org.blinkenlights.jid3.v1.ID3V1Tag;
import org.hibernate.Session;
import org.hibernate.Query;

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
   public Song(File file)
   {
      _path.setValue(file);

      MP3File mp3File = new MP3File(file);
      SimpleCommonTag tag = tag(mp3File);
      if (tag == null || StringEO.isEmpty(tag.getTitle()))
      {
         _title.setValue(file.getName());
         return;
      }

      // how do i get the song duration??
      
      _title.setValue(tag.getTitle());
      
      lookupOrCreateArtist(tag.getArtist());
      lookupOrCreateAlbum(tag.getAlbum());
      lookupOrCreateGenre(tag.getGenre());
   }

   private void lookupOrCreateArtist(String name)
   {
      Session session = hbmPersistor().getSession();
      String hql = "from Artist a where a.name=:name";
      Query query = session.createQuery(hql);
      query.setParameter("name", name);
      Artist artist = (Artist) query.uniqueResult();
      if (artist == null)
      {
         artist = new Artist();
         artist.getName().setValue(name);
         session.save(artist);
      }
      setArtist(artist);
   }
   private void lookupOrCreateAlbum(String name)
   {
      Session session = hbmPersistor().getSession();
      String hql = "from Album a where a.name=:name";
      Query query = session.createQuery(hql);
      query.setParameter("name", name);
      Album album = (Album) query.uniqueResult();
      if (album == null)
      {
         album = new Album();
         album.getName().setValue(name);
         session.save(album);
      }
      setAlbum(album);
   }
   private void lookupOrCreateGenre(String name)
   {
      Session session = hbmPersistor().getSession();
      String hql = "from Genre g where g.code=:name";
      Query query = session.createQuery(hql);
      query.setParameter("name", name);
      Genre genre = (Genre) query.uniqueResult();
      if (genre == null)
      {
         genre = new Genre(name, name);
         session.save(genre);
      }
      getGenre().setValue(genre);
   }

   private SimpleCommonTag tag(MP3File file)
   {
      try
      {
         ID3V1Tag tag1 = file.getID3V1Tag();
         if (tag1 != null)
         {
            return new ID3V1TagAdapter(tag1);
         }
         ID3V2Tag tag2 = file.getID3V2Tag();
         if (tag2 != null)
         {
            return new ID3V2TagAdapter(tag2);
         }
      }
      catch (ID3Exception ex) {}
      return null;
   }
   
   // go figure why the jid3 library doesn't provide such
   // a common interface..
   interface SimpleCommonTag
   {
      String getTitle();
      String getArtist();
      String getGenre();
      String getAlbum();
   }
   class ID3V1TagAdapter implements SimpleCommonTag
   {
      ID3V1Tag _tag;
      ID3V1TagAdapter(ID3V1Tag tag) { _tag = tag; }

      public String getTitle() { return _tag.getTitle(); }
      public String getArtist() { return _tag.getAlbum(); }
      public String getGenre() { return _tag.getGenre().toString(); }
      public String getAlbum() { return _tag.getAlbum(); }
   }
   class ID3V2TagAdapter implements SimpleCommonTag
   {
      ID3V2Tag _tag;
      ID3V2TagAdapter(ID3V2Tag tag) { _tag = tag; }
      
      public String getTitle() { return _tag.getTitle(); }
      public String getAlbum() { return _tag.getAlbum(); }
      public String getGenre() { return _tag.getGenre(); }
      public String getArtist() { return _tag.getArtist(); }
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

   
   AdvancedPlayer player;
   AudioDevice device;
   int position = 0;
   
   /**
    * JLayer Streams!
    */
   @Cmd(mnemonic='p')
   public void Play(CommandInfo cmdInfo)
   {
      try
      {
         InputStream is = new BufferedInputStream(new FileInputStream(_path.fileValue()));

         FactoryRegistry r = FactoryRegistry.systemRegistry();
         device = r.createAudioDevice();
         player = new AdvancedPlayer(is, device);
         player.setPlayBackListener(new PlaybackListener()
         {
            public void playbackStarted(PlaybackEvent playbackEvent)
            {
               vmech().onMessage("Playback started..");
            }

            public void playbackFinished(PlaybackEvent playbackEvent)
            {
               vmech().onMessage("Playback finished..");
            }
         });
         
         player.play(position, Integer.MAX_VALUE);
         vmech().onMessage("Playing song.."+this);
      }
      catch (JavaLayerException e)
      {
         e.printStackTrace();
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
   }

   @Cmd
   public void Pause(CommandInfo cmdInfo)
   {
      int positionInMillis = device.getPosition();
      position = positionInMillis / 26;  // a frame is roughly 26 ms;  this is not very precise.
      player.stop();
      
      // why don't they just support pause/play directly in the api??
   }
   
   @Cmd
   public void ResetPosition(CommandInfo cmdInfo)
   {
      position = 0;
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
         File file = (File) songFiles.get(i);
         songs.add(new Song(file));
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
