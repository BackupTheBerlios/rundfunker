// ID3.java
//
//$Id$
//
//de.vdheide.mp3: Access MP3 properties, ID3 and ID3v2 tags
//Copyright (C) 1999-2004 Jens Vonderheide <jens@vdheide.de>
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Library General Public
//License as published by the Free Software Foundation; either
//version 2 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Library General Public License for more details.
//
//You should have received a copy of the GNU Library General Public
//License along with this library; if not, write to the
//Free Software Foundation, Inc., 59 Temple Place - Suite 330,
//Boston, MA  02111-1307, USA.

package de.vdheide.mp3;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Class to read and modify ID3 tags on MP3 files.
 *
 * <p>
 * ID3 information is loaded
 * <ul>
 * <li>the first time any of these is requested
 * <li>after doing a readTag()
 * <li>after changing any of these (no real reload, it is just changed)
 * </ul>
 * <p>
 * ID3 information is written
 * <ul>
 * <li>after invoking a writeTag()
 * </ul>
 * <p>
 *
 * If a file does not contain an ID3 tag, each read access will throw a
 * NoID3TagException. A write access will create an ID3 tag if none is present.
 */
public class ID3Impl implements ID3 {

    // encoding to use when converting from Unicode (String) to bytes
    private final String encoding = "Cp437";

    /**
     * Create a new ID3 tag which is based on mp3_file
     *
     * @param mp3_file
     *            MP3 file to read ID3 tag to / write ID3 tag to
     */
    public ID3Impl(File mp3_file) {
        this.mp3_file = mp3_file;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#getTitle()
	 */
    public String getTitle() throws NoID3TagException {
        try {
            checkIfRead(title);
        } catch (IOException e) {
            throw new NoID3TagException();
        }
        return title;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#getArtist()
	 */
    public String getArtist() throws NoID3TagException {
        try {
            checkIfRead(artist);
        } catch (IOException e) {
            throw new NoID3TagException();
        }
        return artist;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#getAlbum()
	 */
    public String getAlbum() throws NoID3TagException {
        try {
            checkIfRead(album);
        } catch (IOException e) {
            throw new NoID3TagException();
        }
        return album;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#getYear()
	 */
    public String getYear() throws NoID3TagException {
        try {
            checkIfRead(year);
        } catch (IOException e) {
            throw new NoID3TagException();
        }
        return year;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#getGenre()
	 */
    public int getGenre() throws NoID3TagException {
        if (genre == null) {
            // ID3 tag not already read
            // read tag
            try {
                readTag();
            } catch (IOException e) {
                throw new NoID3TagException();
            }
        }
        return genre.byteValue();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#getComment()
	 */
    public String getComment() throws NoID3TagException {
        try {
            checkIfRead(comment);
            return comment;
        } catch (IOException e) {
            throw new NoID3TagException();
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#getTrack()
	 */
    public int getTrack() throws NoID3TagException {
        if (track == null) {
            try {
                readTag();
            } catch (IOException e) {
                throw new NoID3TagException();
            }
        }
        return track.byteValue();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#readTag()
	 */
    public void readTag() throws NoID3TagException, IOException {

        // get access to file
        RandomAccessFile in = new RandomAccessFile(mp3_file, "r");

        // file is now prepared
        // check for ID3 tag
        if (checkForTag() == false) {
            // No ID3 tag found
            in.close();
            throw new NoID3TagException();
        } else {
            // ID3 tag found, read it
            in.seek(in.length() - 125);
            byte[] buffer = new byte[125];
            if (in.read(buffer, 0, 125) != 125) {
                // tag too short
                // this cannot happen cause we found "TAG" at correct position
            }

            String tag = new String(buffer, 0, 125, encoding);
            // cut tag;
            title = tag.substring(0, 30).trim();
            artist = tag.substring(30, 60).trim();
            album = tag.substring(60, 90).trim();
            year = tag.substring(90, 94).trim();
            comment = tag.substring(94, 123).trim();
            // track is only present if byte at 122 is 0
            if (tag.charAt(122) == '\0') {
                track = new Byte((byte) tag.charAt(123));
            } else {
                track = new Byte((byte) 0);
            }
            // ouch, what a dirty cast...
            genre = new Byte((byte) tag.charAt(124));
        }

        in.close();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#setTitle(java.lang.String)
	 */
    public void setTitle(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#setArtist(java.lang.String)
	 */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#setAlbum(java.lang.String)
	 */
    public void setAlbum(String album) {
        this.album = album;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#setYear(java.lang.String)
	 */
    public void setYear(String year) {
        this.year = year;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#setComment(java.lang.String)
	 */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#setTrack(int)
	 */
    public void setTrack(int track) throws ID3IllegalFormatException {
        if (track < 0 || track > 255) {
            throw new ID3IllegalFormatException();
        } else {
            this.track = new Byte((byte) track);
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#setGenre(int)
	 */
    public void setGenre(int genre) throws ID3IllegalFormatException {
        this.genre = new Byte((byte) genre);
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#writeTag()
	 */
    public void writeTag() throws IOException {
        // get access to file
        RandomAccessFile in = new RandomAccessFile(mp3_file, "rw");

        // file is now prepared
        // check for ID3 tag
        if (checkForTag() == false) {
            // No ID3 tag found, create new
            // seek to end of file
            in.seek(in.length());
        } else {
            // jump to "TAG"
            in.seek(in.length() - 128);
        }

        // write new tag

        in.write(new String("TAG").getBytes(encoding));
        in.write(fillWithNills(title, 30).getBytes(encoding));
        in.write(fillWithNills(artist, 30).getBytes(encoding));
        in.write(fillWithNills(album, 30).getBytes(encoding));
        in.write(fillWithNills(year, 4).getBytes(encoding));
        in.write(fillWithNills(comment, 29).getBytes(encoding));
        if (track == null) {
            in.writeByte(0);
        } else {
            in.writeByte(track.byteValue());
        }
        if (genre == null) {
            in.writeByte(0);
        } else {
            in.writeByte(genre.byteValue());
        }

        in.close();
    }

    private File mp3_file = null; // file to access

    private String title = null; // id3 title

    private String artist = null; // id3 artist

    private String album = null; // id3 album

    private String year = null; // id3 year

    private Byte genre = null; // id3 genre, -1==not set

    private String comment = null; // id3 comment

    private Byte track = null; // id3 track number

    /**
     * Check if reading of ID3 tag if necessary. If so, reads tag.
     *
     * @param what
     *            Which information is requested?
     * @exception NoID3TagException
     *                If file does not contain an ID3 tag
     * @exception IOException
     *                If an I/O errors occurs
     */
    private void checkIfRead(String what) throws NoID3TagException,
            IOException {
        if (what == null) {
            readTag();
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.ID3#checkForTag()
	 */
    public boolean checkForTag() throws IOException {
        // Create random access file
        RandomAccessFile raf = new RandomAccessFile(mp3_file, "r");

        if (raf.length() < 129) {
            // file to short for an ID3 tag
            raf.close();
            return false;
        } else {
            // go to position where "TAG" must be
            long seekPos = raf.length() - 128;
            raf.seek(seekPos);

            byte buffer[] = new byte[3];

            if (raf.read(buffer, 0, 3) != 3) {
                // something terrible happened
                raf.close();
                throw new IOException("Read beyond end of file");
            }

            raf.close();

            String testTag = new String(buffer, 0, 3, encoding);
            if (!testTag.equals("TAG")) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Fill <tt>str</tt> with \0 until <tt>str</tt> has length <tt>len</tt>
     *
     * @param str
     *            String to work with
     * @param len
     *            Length of <tt>str</tt> after filling
     * @return Filled string
     */
    private String fillWithNills(String str, int len) {
        if (str == null) {
            // tag info not set!
            str = new String("");
        }
        StringBuffer tmp = new StringBuffer(str);
        for (int i = str.length() + 1; i <= len; i++) {
            tmp.append('\0');
        }
        return tmp.toString();
    }

}
