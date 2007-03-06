package de.vdheide.mp3;

import java.io.IOException;

public interface ID3 {

	/**
	 * Read title from ID3 tag
	 *
	 * @return Title
	 * @exception NoID3TagException
	 *                If file does not contain an ID3 tag
	 */
	public abstract String getTitle() throws NoID3TagException;

	/**
	 * Read artist from ID3 tag
	 *
	 * @return Artist
	 * @exception NoID3TagException
	 *                If file does not contain an ID3 tag
	 */
	public abstract String getArtist() throws NoID3TagException;

	/**
	 * Read album from ID3 tag
	 *
	 * @return album
	 * @exception NoID3TagException
	 *                If file does not contain an ID3 tag
	 */
	public abstract String getAlbum() throws NoID3TagException;

	/**
	 * Read year from ID3 tag
	 *
	 * @return Year
	 * @exception NoID3TagException
	 *                If file does not contain an ID3 tag
	 */
	public abstract String getYear() throws NoID3TagException;

	/**
	 * Read genre from ID3 tag
	 *
	 * @return Genre
	 * @exception NoID3TagException
	 *                If file does not contain an ID3 tag
	 */
	public abstract int getGenre() throws NoID3TagException;

	/**
	 * Read comment from ID3 tag
	 *
	 * @return comment
	 * @exception NoID3TagException
	 *                If file does not contain an ID3 tag
	 */
	public abstract String getComment() throws NoID3TagException;

	/**
	 * Read track number from ID3 tag
	 *
	 * @return Track number
	 * @exception NoID3TagException
	 *                If file does not contain an ID3 tag
	 */
	public abstract int getTrack() throws NoID3TagException;

	/**
	 * Read ID3 tag and prepare for retrieval with getXXX.
	 *
	 * Use this method to reread tag if changed externally
	 *
	 * @exception NoID3TagException
	 *                If file does not contain an ID3 tag
	 * @exception IOException
	 *                If I/O error occurs
	 */
	public abstract void readTag() throws NoID3TagException, IOException;

	/**
	 * Set title
	 *
	 * @param title
	 *            Title
	 */
	public abstract void setTitle(String title);

	/**
	 * Set artist
	 *
	 * @param artist
	 *            Artist
	 */
	public abstract void setArtist(String artist);

	/**
	 * Set album
	 *
	 * @param album
	 *            Album
	 */
	public abstract void setAlbum(String album);

	/**
	 * Set year
	 *
	 * @param year
	 *            Year
	 */
	public abstract void setYear(String year);

	/**
	 * Set comment
	 *
	 * @param comment
	 *            Comment
	 */
	public abstract void setComment(String comment);

	/**
	 * Set track number
	 *
	 * @param track
	 *            Track number
	 * @exception ID3IllegalFormatException
	 *                if track is negative or larger than 255
	 */
	public abstract void setTrack(int track) throws ID3IllegalFormatException;

	/**
	 * Set genre
	 *
	 * @param genre
	 *            Genre
	 * @exception ID3IllegalFormatException
	 *                if genre is negative or larger than 255
	 */
	public abstract void setGenre(int genre) throws ID3IllegalFormatException;

	/**
	 * Write information provided with setXXX to ID3 tag
	 *
	 * @throws IOException
	 */
	public abstract void writeTag() throws IOException;

	/**
	 * Check if ID3 tag is present
	 *
	 * @return true if tag present
	 * @throws IOException
	 */
	public abstract boolean checkForTag() throws IOException;

}