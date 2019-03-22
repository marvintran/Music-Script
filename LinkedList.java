public class LinkedList
{
  private Node topNode;

  private String albumLength;
  private String albumArtist;
  private String album;
  private String year;


  public LinkedList( String albumArtist, String album, String year, String albumLength )
  {
    topNode = null;

    this.albumLength = albumLength;
    this.albumArtist = albumArtist;
    this.album = album;
    this.year = year;
  }

  public Node getTopNode(){ return topNode; }

  public String getAlbumLength(){ return albumLength; }
  public String getAlbumArtist(){ return albumArtist; }
  public String getAlbum(){ return album; }
  public String getYear(){ return year; }


  public void addNode( Node toAdd )
  {
    // if this is the first Node being added, make it the top
    if (topNode == null)
      topNode = toAdd;

    // else add this node to the end of the list
    else
    {
      Node currNode = topNode;

      while( currNode.getNext() != null )
      {
        currNode = currNode.getNext();
      }

      currNode.setNext(toAdd);
    }
  }

  // print out BBCode
  public void print(int albumNumber)
  {
    Node currNode = topNode;

    System.out.println("[size=4][b]Tracklist[/b][/size]");

    while( currNode != null )
    {
      System.out.println( "[b]" + currNode.getTrackNumber() + ".[/b] " + currNode.getTitle() +
                         " [i](" + currNode.getTrackLength() + ")[/i]");
      currNode = currNode.getNext();
    }

    System.out.println();
    System.out.println("[b]Total length:[/b] " + albumLength);
    System.out.println();
    System.out.println("More information: https://music.bugs.co.kr/album/" + Integer.toString(albumNumber));
  }
}
