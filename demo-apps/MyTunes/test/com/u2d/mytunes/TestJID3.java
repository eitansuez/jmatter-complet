package com.u2d.mytunes;

import junit.framework.TestCase;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.v1.ID3V1Tag;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Jun 27, 2008
 * Time: 11:20:16 AM
 */
public class TestJID3 extends TestCase
{
   public void testExtractNameAndDuration() throws ID3Exception
   {
      MP3File mp3 = new MP3File(new File("/home/eitan/Music/hadag-nahash-sticker.mp3"));
      ID3V1Tag tag = mp3.getID3V1Tag();
      System.out.printf("Song Info: Title: %s, Artist: %s, Album: %s, Genre: %s, Year: %s",
            tag.getTitle(), tag.getArtist(), tag.getAlbum(), tag.getGenre(), tag.getYear());
   }
}
