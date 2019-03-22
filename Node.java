public class Node
{
  private Node next;

  private String title;
  private String trackArtists;
  private String trackNumber;
  private String genre;

  private String trackID;
  private String trackLength;

  public Node()
  {
    next = null;

    title = null;
    trackArtists = null;
    trackNumber = null;
    genre = null;

    trackID = null;
    trackLength = null;
  }

  public Node( String songTitle, String trackNumber, String trackID )
  {
    next = null;

    this.title = songTitle;
    trackArtists = null;
    this.trackNumber = trackNumber;
    genre = null;

    this.trackID = trackID;
    trackLength = null;
  }

  public Node getNext(){ return next; }
  public void setNext(Node theNode){ next = theNode; }

  public String getTitle(){ return title; }
  public String getTrackArtists(){ return trackArtists; }
  public void setTrackArtists(String trackArtists){ this.trackArtists = trackArtists; }
  public String getTrackNumber(){ return trackNumber; }
  public String getGenre(){ return genre; }
  public void setGenre(String genre){ this.genre = genre; }

  public String getTrackID(){ return trackID; }
  public String getTrackLength(){ return trackLength; }
  public void setTrackLength(String trackLength){ this.trackLength = trackLength; }
}
