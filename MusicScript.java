import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import org.openqa.selenium.JavascriptExecutor;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.net.URI;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicScript
{
  public static void main(String[] args)
  {
    String URL = "https://music.bugs.co.kr/";

    //======== INFO TO FILL OUT ========//
    // fill out info as necessary depending on the function to run

      //-------- Choose web browser --------//
      // comment out the one not used

      // Use Firefox Browser
      //System.setProperty("webdriver.gecko.driver","C:\\Path\\To\\geckodriver.exe");
      //WebDriver driver = new FirefoxDriver();

      // Use Chrome Browser
      System.setProperty("webdriver.chrome.driver","c:\\Path\\To\\chromedriver.exe");
      WebDriver driver = new ChromeDriver();

      //-------- Album Number --------//
      // used for generating BBCode and tagging songs
      int albumNumber = 20195596;

      //-------- Login Info --------//
      // used for Adding songs to playlists
      String email = "example@example.com";
      String password = "hunter2";

      //-------- Playlist Adding Info --------//
      // used for Adding songs to playlists
      String playlistToAdd = "Playlist 1";
      String playlistPageNumber = "243751616";// the unique ID associated with that playlist
      String pageNumber = "2";// which page on the newest releases page to start looking for songs from
      int dateStart = 20190201;
      int dateEnd = 20190205;

      //-------- Tagging Songs Info --------//
      // also make sure to fill out generate description info above
      String pathName = "C:/Path/To/Songs/";
      String fileType = ".flac";// usually .mp3 or .flac


    //======== FUNCTIONS TO RUN ========//

      //-------- Generate BBCode for this album --------//
      //LinkedList theList = getTrackList(driver, URL, albumNumber);
      //theList.print( albumNumber );

      //-------- Adding songs to playlists --------//
      //login(driver, URL, email, password);
      //addToPlaylists(driver, URL, playlistToAdd, dateStart, dateEnd, pageNumber);
      //removeInstrumetals(driver, URL, playlistPageNumber);

      //-------- Adding metadata to songs --------//
      LinkedList theList = getFullAlbumDetails(driver, URL, albumNumber);
      tagSongs(theList, pathName, fileType);
  }

  static void login(WebDriver driver, String URL, String email, String password)
  {
    driver.get( URL );

    WebElement loginDropdown = driver.findElement(By.className("login"));
    loginDropdown.click();

    WebElement bugsLogin = driver.findElement(By.id("to_bugs_login"));
    bugsLogin.click();

    WebElement usernameElement = driver.findElement(By.id("user_id"));
    usernameElement.sendKeys( email );

    WebElement passwordElement = driver.findElement(By.id("passwd"));
    passwordElement.sendKeys( password );

    WebElement submit = driver.findElement(By.className("submit"));
    submit.click();

    WebDriverWait wait = new WebDriverWait(driver,10);
    WebElement homeButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='logo hyrend']")));
  }

  static void addToPlaylists(WebDriver driver, String URL, String playlistToAdd, int dateStart, int dateEnd, String pageNumber)
  {
    System.out.println("Adding songs to playlist: " + playlistToAdd);

    // go to new music page
    driver.get( URL + "newest/album/totalpicked?page=" + pageNumber );

    // make an array of songs
    WebElement unorderedList = driver.findElement(By.xpath("//ul[@class='list tileView albumList ']"));
    List<WebElement> allSongs = unorderedList.findElements(By.tagName("li"));

    // add each song to the specified playlist
    for( WebElement currentSong: allSongs )
    {
      String theDate = currentSong.findElement(By.tagName("time")).getText();
      //System.out.println(theDate);

      if(validDate(dateStart, dateEnd, theDate))
      {
        String albumName = currentSong.findElement(By.className("albumTitle")).getText();
        //System.out.println(albumName);

        WebElement threeDotButton = currentSong.findElement(By.className("btnActions"));
        threeDotButton.click();

        WebDriverWait waitAfterThreeDotButton = new WebDriverWait(driver, 10);
        WebElement viewPlaylists = waitAfterThreeDotButton.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@atype='my_album']")));
        viewPlaylists.click();

        WebDriverWait waitAfterPlaylistButton = new WebDriverWait(driver, 10);
        String playlistName = "//a[@title='" + playlistToAdd + "']";
        WebElement addToPlaylist = waitAfterPlaylistButton.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(playlistName)));
        addToPlaylist.click();

        WebDriverWait popup = new WebDriverWait(driver, 10);
        WebElement popupScreen = popup.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//aside[@id='bugsAlert']")));
        WebElement btns = popupScreen.findElement(By.className("btns"));
        WebElement box = btns.findElement(By.className("btnNormal"));

        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", box);
      }
    }
  }

  static Boolean validDate( int start, int end, String givenDate )
  {
    Boolean toReturn = false;

    String newGivenDate = givenDate.replaceAll("\\.", "");
    int convertedGivenDate = Integer.parseInt( newGivenDate );

    if( start <= convertedGivenDate && convertedGivenDate <= end )
      toReturn = true;

    return toReturn;
  }

  static void removeInstrumetals(WebDriver driver, String URL, String playlistPageNumber )
  {
    System.out.println("Removing instrumentals from: " + playlistPageNumber );

    // go to new music page
    driver.get( URL + "user/library/myalbum/" + playlistPageNumber );

    // make an array of songs
    WebElement trackList = driver.findElement(By.xpath("//table[@class='list trackList ']"));
    List<WebElement> allSongs = trackList.findElements(By.tagName("tr"));

    for( WebElement currentSong: allSongs )
    {
      String songTitle = currentSong.findElement(By.className("title")).getText();
      //System.out.println(songTitle);

      if(songTitle.contains("Inst.") || songTitle.contains("inst.") || songTitle.contains("(MR)") ||
         songTitle.contains("Remix") || songTitle.contains("remix") || songTitle.contains("Radio Edit") ||
         songTitle.contains("Extended Mix"))
      {
        WebElement checkBox = currentSong.findElement(By.name("check"));
        checkBox.click();
      }
    }

    WebElement deleteTracks = driver.findElement(By.id("deleteLikeTrack"));
    deleteTracks.click();
  }

  static LinkedList getTrackList(WebDriver driver, String URL, int albumNumber)
  {
    LinkedList songList;

    // go to new the album's page
    driver.get(URL + "album/" + Integer.toString(albumNumber));

    //public LinkedList( String artist, String album, String year, String albumLength )

    //---------- Album Info ----------//
    WebElement albumInfo = driver.findElement(By.className("basicInfo"));
    List<WebElement> tableRows = albumInfo.findElements(By.tagName("tr"));

    // get album artist
    String albumArtist = tableRows.get(0).findElement(By.tagName("a")).getText();

    // sometimes there is some kind of checkmark thing beside the artists name
    // getText() also gets the text in the span, which contains "CONNECT 아티스트"
    // we remove it: "artist CONNECT 아티스트" to "artist"
    if( albumArtist.contains("CONNECT 아티스트") )
    {
      albumArtist = albumArtist.replaceAll("\\bCONNECT 아티스트\\b", "");
      albumArtist = albumArtist.substring(0, albumArtist.length() - 1);// remove trailing space
    }

    // "aaa(bbb)" to "aaa (bbb)"
    if( albumArtist.contains("(") )
    {
      if( !albumArtist.contains(" (") )
        albumArtist = albumArtist.replaceAll("[(]", " (");
    }

    // get album name
    WebElement article = driver.findElement(By.id("container"));
    List<WebElement> innerContainer = article.findElements(By.className("innerContainer"));
    String album = innerContainer.get(0).findElement(By.tagName("h1")).getText();

    // get album year
    List<WebElement> timeAttributes = albumInfo.findElements(By.tagName("time"));
    String year = timeAttributes.get(0).getText();
    year = year.replaceAll("[.]", "-");

    // get albumLength
    String albumLength = timeAttributes.get(1).getText();

    // make a new song list with all the innformation we just gathered
    songList = new LinkedList(albumArtist, album, year, albumLength);

    // make a List of songs to add to the LinkedList
    String name = "ALBUMTRACK" + Integer.toString(albumNumber);
    WebElement trackList = driver.findElement(By.id(name));//xpath("//table[@class='list trackList byAlbum']"));
    WebElement bodyOfTrackList = trackList.findElement(By.tagName("tbody"));
    List<WebElement> allSongs = bodyOfTrackList.findElements(By.tagName("tr"));

    int count = 1;

    // get songTitle and trackID and add it to a LinkedList
    for (WebElement currentSong : allSongs)
    {
      String songTitle = currentSong.findElement(By.className("title")).getText();

      if( songTitle.contains("feat.") )
        songTitle = songTitle.replaceAll("feat.", "Feat.");
      if( songTitle.contains("ver.") )
        songTitle = songTitle.replaceAll("ver.", "Ver.");
      if( songTitle.contains("inst.") )
        songTitle = songTitle.replaceAll("inst.", "Inst.");

      String trackID = currentSong.getAttribute("trackid");

      Node newSong = new Node(songTitle, Integer.toString(count), trackID);
      songList.addNode(newSong);

      count++;
    }

    // get trackLength of each song
    // while you're here, get track artists as well
    Node currNode = songList.getTopNode();

    while (currNode != null)
    {
      // get trackLength
      String trackID = currNode.getTrackID();

      // go to this track's description page to ge the track length
      driver.get(URL + "track/" + trackID);

      WebElement basicInfo = driver.findElement(By.className("basicInfo"));
      String trackLength = basicInfo.findElement(By.tagName("time")).getText();

      String firstThreeChars = trackLength.substring(0, 3);

      // remove leading zeroes
      // if it's under a minute like 00:26, don't remove all zeroes, change 00:26 to 0:26
      // else change 03:26 to 3:26
      if( firstThreeChars.equals("00:"))
        trackLength = trackLength.replaceFirst("00", "0");
      else
        trackLength = trackLength.replaceFirst("^0+(?!$)", "");

      currNode.setTrackLength(trackLength);

      // get track artist(s)
      List<WebElement> tr = basicInfo.findElements(By.tagName("tr"));
      WebElement firstTableRow = tr.get(0);
      List<WebElement> a = firstTableRow.findElements(By.tagName("a"));

      String trackArtists = "";

      for (WebElement currentA : a)
      {
        String newArtist = currentA.getText();

        // sometimes there is some kind of checkmark thing beside the artists name
        // getText() also gets the text in the span, which contains "CONNECT 아티스트"
        // we remove it: "artist CONNECT 아티스트" to "artist"
        if( newArtist.contains("CONNECT 아티스트") )
        {
          newArtist = newArtist.replaceAll("\\bCONNECT 아티스트\\b", "");
          newArtist = newArtist.substring(0, newArtist.length() - 1);// remove trailing space
        }

        // "aaa(bbb)" to "aaa (bbb)"
        if( newArtist.contains("(") )
        {
          if( !newArtist.contains(" (") )
            newArtist = newArtist.replaceAll("[(]", " (");
        }

        trackArtists = trackArtists + newArtist + ", ";
      }

      // the last track will have ", " at the end like "pen (zoo), boo (see), "
      // we want to move that last ", " part
      trackArtists = trackArtists.substring(0, trackArtists.length() - 2);

      currNode.setTrackArtists(trackArtists);

      currNode = currNode.getNext();
    }
    return songList;
  }


  static LinkedList getFullAlbumDetails(WebDriver driver, String URL, int albumNumber)
  {
    LinkedList songList = getTrackList(driver, URL, albumNumber);

    String albumArtist = songList.getAlbumArtist();
    String album = songList.getAlbum();

    // get genre

    // search up this album on MelOn to get better genre tags than Bugs!

    String toSearch = "https://www.melon.com/search/total/index.htm?q=" + albumArtist + "%20" + album + "&section=&linkOrText=T&ipath=srch_form";
    toSearch = toSearch.replaceAll("\\s+", "+");// replace all spaces with + for MelOn searches
    driver.get(toSearch);

    // try going to this album's page
    // if we can't, maybe our search terms weren't right
    // try searching up just the hangul of the artist name

    // searching "아이유 (IU) Palette" doesn't work on MelOn, but "아이유 Palette" does.
    // So search up "아이유 (IU) Palette" first, it didn't work so try "아이유 Palette"
    // sometimes still doesn't work, try "IU Palette"
    try
    {
      WebDriverWait wait = new WebDriverWait(driver,10);
      WebElement albumListElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='frm']")));
      List<WebElement> listOfAlbums = albumListElement.findElements(By.tagName("li"));
      WebElement firstAlbum = listOfAlbums.get(0);
      WebElement albumLink = firstAlbum.findElement(By.className("ellipsis"));

      albumLink.click();
    }
    catch( Exception e )
    {
      try
      {
        String notInBrackets = albumArtist.replaceAll("\\(.*?\\)", "");
        notInBrackets = notInBrackets.trim();

        toSearch = "https://www.melon.com/search/total/index.htm?q=" + notInBrackets + "+" + album + "&section=&linkOrText=T&ipath=srch_form";
        toSearch = toSearch.replaceAll("\\s+", "+");// replace all spaces with + for MelOn searches

        driver.get(toSearch);

        // make a wait thing here
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement albumListElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='frm']")));
        List<WebElement> listOfAlbums = albumListElement.findElements(By.tagName("li"));
        WebElement firstAlbum = listOfAlbums.get(0);
        WebElement albumLink = firstAlbum.findElement(By.className("ellipsis"));

        albumLink.click();
      }
      catch( Exception e2 )
      {
        String betweenBrackets;
        Pattern pattern = Pattern.compile(".*\\(([^']*)\\).*");

        Matcher matcher = pattern.matcher(albumArtist);
        if(matcher.matches())
        {
          betweenBrackets = matcher.group(1);
        }
        else
        {
          betweenBrackets = albumArtist.replaceAll("[^a-zA-Z\\s]", "");
        }

        toSearch = "https://www.melon.com/search/total/index.htm?q=" + betweenBrackets + "+" + album + "&section=&linkOrText=T&ipath=srch_form";
        toSearch = toSearch.replaceAll("\\s+", "+");// replace all spaces with + for MelOn searches

        driver.get(toSearch);

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement albumListElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='frm']")));
        List<WebElement> listOfAlbums = albumListElement.findElements(By.tagName("li"));
        WebElement firstAlbum = listOfAlbums.get(0);
        WebElement albumLink = firstAlbum.findElement(By.className("ellipsis"));

        albumLink.click();
      }
    }

    // store album page URL
    String albumPageURL = driver.getCurrentUrl();

    // get album genre(s)
    // get genre for this song
    WebDriverWait wait = new WebDriverWait(driver,20);
    WebElement wrapInfo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='wrap_info']")));

    List<WebElement> dd = wrapInfo.findElements(By.tagName("dd"));
    String albumGenre = dd.get(1).getText();

    // if there are multiple genres in this album, go to each song page and get its genre
    if (albumGenre.contains(","))
    {
      int count = 0;

      Node currNode = songList.getTopNode();

      while (currNode != null)
      {
        String trackArtists = currNode.getTrackArtists();

        WebDriverWait wait3 = new WebDriverWait(driver,20);
        WebElement trackList = wait3.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='service_list_song d_song_list']")));

        WebElement bodyOfTrackList = trackList.findElement(By.tagName("tbody"));
        List<WebElement> allSongs = bodyOfTrackList.findElements(By.tagName("tr"));
        WebElement desiredSong = allSongs.get(count);

        // song info button
        List<WebElement> tableData = desiredSong.findElements(By.tagName("td"));
        WebElement songInfoButton = tableData.get(2);
        WebElement theButton = songInfoButton.findElement(By.cssSelector("a[class='btn button_icons type03 song_info']"));
        theButton.click();

        // get genre for this song
        WebDriverWait wait2 = new WebDriverWait(driver,20);
        WebElement downloadfrm = wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("downloadfrm")));

        dd = downloadfrm.findElements(By.tagName("dd"));
        String songGenre = dd.get(2).getText();

        // set the genre for this song
        currNode.setGenre(songGenre);

        // iterate stuff
        currNode = currNode.getNext();
        count++;

        // go back to album page
        driver.get(albumPageURL);
      }
    }
    // else there is only one genre, so tag all songs with this genre
    else
    {
      Node currNode = songList.getTopNode();

      while (currNode != null)
      {
        // set the genre for this song
        currNode.setGenre(albumGenre);

        currNode = currNode.getNext();
      }
    }

    return songList;
  }

  static void tagSongs(LinkedList songList, String pathName, String fileType)
  {
    Node currNode = songList.getTopNode();

    int numFailed = 0;

    while( currNode != null )
    {
      try
      {
        String pathNamePercentEncoding = percentEncoding(pathName);

        String forURI = "file:///" + pathNamePercentEncoding + currNode.getTrackID() + fileType;
        URI uri = new URI(forURI);

        AudioFile f = AudioFileIO.read(new File(uri));
        Tag tag = f.getTag();


        tag.setField(FieldKey.TITLE, currNode.getTitle() );
        tag.setField(FieldKey.ARTIST, currNode.getTrackArtists() );
        tag.setField(FieldKey.ALBUM, songList.getAlbum() );
        tag.setField(FieldKey.ALBUM_ARTIST, songList.getAlbumArtist() );

        tag.setField(FieldKey.YEAR, songList.getYear() );
        tag.setField(FieldKey.TRACK, currNode.getTrackNumber() );
        tag.setField(FieldKey.GENRE, currNode.getGenre() );

        tag.setField(FieldKey.COMMENT, "" );
        tag.setField(FieldKey.DISC_NO, "1");
        AudioFileIO.write(f);

        // rename files now
        File oldFile = new File(pathName + currNode.getTrackID() + fileType);

        String trackNumber = currNode.getTrackNumber();
        String songTitle = currNode.getTitle();

        if( songTitle.contains("?") )
        {
          songTitle = songTitle.replaceAll("?", "？");
        }

        String fullPath;

        if( trackNumber.length() == 1 )
          fullPath =  pathName + "0" + trackNumber + ". " + songTitle + fileType;
        else
          fullPath = pathName + trackNumber + ". " + songTitle + fileType;

        File newFile = new File(fullPath);

        if(!oldFile.renameTo(newFile))
          numFailed++;
      }
      catch(Exception e)
      {
        System.out.println("Error when tagging");
        e.printStackTrace();
      }

      currNode = currNode.getNext();
    }

    if( numFailed != 0 )
      System.out.println("Failed to rename " + numFailed + " files");
    else
      System.out.println("Successfully renamed all files");

  }

  static String percentEncoding(String folderName)
  {
    String newFolderName = folderName.replaceAll("\\s+", "%20");
    newFolderName = newFolderName.replaceAll("[\\[]", "%5B");
    newFolderName = newFolderName.replaceAll("[\\]]", "%5D");

    return newFolderName;
  }
}
