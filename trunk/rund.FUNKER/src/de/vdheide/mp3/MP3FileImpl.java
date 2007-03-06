// MP3.java
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

/**
 * Instances of this class contain an MP3 file, giving access to its ID3 and
 * ID3v2 tags and other mp3 properties.
 * <p>
 * It provides a common interface to both tags, e.g.
 * <code>setTitle(title)</code> updates the title field in both tags. When
 * reading (e.g. <code>getTitle()</code>, it tries to provide as much
 * information as possible (this means returning the ID3v2 infos if tag and
 * requested field are present).
 * <p>
 * Information stored in tags is always returned as a <code>TagContent</code>,
 * the description of the respective get Methods state which fields are used.
 * The more complex frames are not parsed into fields, but rather returned as a
 * byte array. It is up to the user of this class to make sense of it. Usage of
 * a special decode class is recommended.
 * <p>
 * It is assumed that each ID3v2 frame is unique, as is the case for nearly all
 * frame types
 * 
 * @author Jens Vonderheide <jens@vdheide.de>
 */
public class MP3FileImpl extends java.io.File implements MP3File {

    /**
     * Creates a new instance. Tag information is completely read the first time
     * it is requested and written after <code>update()</code>.
     * 
     * @param filename
     *            File name
     * @exception IOException
     *                If I/O error occurs
     * @exception NoMP3FrameException
     *                If file does not contain at least one mp3 frame
     * @exception ID3v2WrongCRCException
     *                If the ID3v2 tag fails CRC
     * @exception ID3v2DecompressionException
     *                If the ID3v2 tag cannot be decompressed
     * @exception ID3v2IllegalVersionException
     *                If the ID3v2 tag has a wrong (too high) version
     */
    public MP3FileImpl(String filename) throws IOException,
            NoMP3FrameException, ID3v2WrongCRCException,
            ID3v2DecompressionException, ID3v2IllegalVersionException {
        super(filename);
        init();
    }

    /**
     * Creates a MP3File instance that represents the file with the specified
     * name in the specified directory. Tag information is completely read the
     * first time it is requested and written after <code>update()</code>.
     * 
     * @param dir
     *            Directory
     * @param filename
     *            File name
     * @exception IOException
     *                If I/O error occurs
     * @exception NoMP3FrameException
     *                If file does not contain at least one mp3 frame
     * @exception ID3v2WrongCRCException
     *                If the ID3v2 tag fails CRC
     * @exception ID3v2DecompressionException
     *                If the ID3v2 tag cannot be decompressed
     * @exception ID3v2IllegalVersionException
     *                If the ID3v2 tag has a wrong (too high) version
     */
    public MP3FileImpl(File dir, String filename) throws IOException,
            NoMP3FrameException, ID3v2WrongCRCException,
            ID3v2DecompressionException, ID3v2IllegalVersionException {
        super(dir, filename);
        init();
    }

    /**
     * Creates a File instance whose pathname is the pathname of the specified
     * directory, followed by the separator character, followed by the name
     * argument. Tag information is completely read the first time it is
     * requested and written after <code>update()</code>.
     * 
     * @param dir
     *            Name of directory
     * @param filename
     *            File name
     * @exception IOException
     *                If I/O error occurs
     * @exception NoMP3FrameException
     *                If file does not contain at least one mp3 frame
     * @exception ID3v2WrongCRCException
     *                If the ID3v2 tag fails CRC
     * @exception ID3v2DecompressionException
     *                If the ID3v2 tag cannot be decompressed
     * @exception ID3v2IllegalVersionException
     *                If the ID3v2 tag has a wrong (too high) version
     */
    public MP3FileImpl(String dir, String filename) throws IOException,
            NoMP3FrameException, ID3v2WrongCRCException,
            ID3v2DecompressionException, ID3v2IllegalVersionException {
        super(dir, filename);
        init();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#update()
	 */
    public void update() throws ID3Exception, ID3v2Exception {
        // write id3v1
        if (write_id3) {
            try {
                id3.writeTag();
            } catch (IOException e) {
                throw new ID3Exception();
            }
        }

        // write id3v2
        if (write_id3v2) {
            try {
                id3v2.update();
            } catch (IOException e) {
                throw new ID3v2Exception();
            }
        }

        // Properties are read only...
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setWriteID3(boolean)
	 */
    public void setWriteID3(boolean write_id3) {
        this.write_id3 = write_id3;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getWriteID3()
	 */
    public boolean getWriteID3() {
        return write_id3;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setWriteID3v2(boolean)
	 */
    public void setWriteID3v2(boolean write_id3v2) {
        this.write_id3v2 = write_id3v2;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getWriteID3v2()
	 */
    public boolean getWriteID3v2() {
        return write_id3v2;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setUseCompression(boolean)
	 */
    public void setUseCompression(boolean use_compression) {
        this.use_compression = use_compression;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getUseCompression()
	 */
    public boolean getUseCompression() {
        return use_compression;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setUseCRC(boolean)
	 */
    public void setUseCRC(boolean use_crc) {
        this.use_crc = use_crc;

        // inform id3v2 tag
        if (id3v2 != null) {
            id3v2.setUseCRC(use_crc);
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getUseCRC()
	 */
    public boolean getUseCRC() {
        return use_crc;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setUsePadding(boolean)
	 */
    public void setUsePadding(boolean use_padding) {
        this.use_padding = use_padding;

        // inform id3v2 tag
        if (id3v2 != null) {
            id3v2.setUsePadding(use_padding);
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getUsePadding()
	 */
    public boolean getUsePadding() {
        return use_padding;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setUseUnsynchronization(boolean)
	 */
    public void setUseUnsynchronization(boolean use_unsynch) {
        this.use_unsynchronization = use_unsynch;

        // inform id3v2 tag
        if (id3v2 != null) {
            id3v2.setUseUnsynchronization(use_unsynch);
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getUseUnsynchronization()
	 */
    public boolean getUseUnsynchronization() {
        return use_unsynchronization;
    }

    // Read MP3 properties

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getMPEGLevel()
	 */
    public int getMPEGLevel() {
        return prop.getMPEGLevel();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getLayer()
	 */
    public int getLayer() {
        return prop.getLayer();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getBitrate()
	 */
    public int getBitrate() {
        return prop.getBitrate();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getSamplerate()
	 */
    public int getSamplerate() {
        return prop.getSamplerate();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getMode()
	 */
    public int getMode() {
        return prop.getMode();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getEmphasis()
	 */
    public int getEmphasis() {
        return prop.getEmphasis();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getProtection()
	 */
    public boolean getProtection() {
        return prop.getProtection();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPrivate()
	 */
    public boolean getPrivate() {
        return prop.getPrivate();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPadding()
	 */
    public boolean getPadding() {
        return prop.getPadding();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getCopyright()
	 */
    public boolean getCopyright() {
        return prop.getCopyright();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getOriginal()
	 */
    public boolean getOriginal() {
        return prop.getOriginal();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getLength()
	 */
    public long getLength() {
        return prop.getLength();
    }

    // Tag information (for details see the ID3v2 informal standard)

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getAlbum()
	 */
    public TagContent getAlbum() throws FrameDamagedException {
        TagContent ret = TextFrameEncoding.read(id3v2, "TALB");
        if (ret.getTextContent() == null) {
            try {
                ret.setContent(id3.getAlbum());
            } catch (NoID3TagException e) {
                // do nothing, content just stays at null
            }
        }

        return ret;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setAlbum(de.vdheide.mp3.TagContent)
	 */
    public void setAlbum(TagContent album) throws TagFormatException {
        if (album.getTextContent() == null) {
            throw new TagFormatException();
        }

        // write v1
        id3.setAlbum(album.getTextContent());

        // write v2
        (new TextFrameEncoding(id3v2, "TALB", album, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getBPM()
	 */
    public TagContent getBPM() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TBPM");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setBPM(de.vdheide.mp3.TagContent)
	 */
    public void setBPM(TagContent bpm) throws TagFormatException {
        if (bpm.getTextContent() == null
                || checkNumeric(bpm.getTextContent()) == false) {
            throw new TagFormatException();
        }

        (new TextFrameEncoding(id3v2, "TBPM", bpm, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getComposer()
	 */
    public TagContent getComposer() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TCOM");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setComposer(de.vdheide.mp3.TagContent)
	 */
    public void setComposer(TagContent composer)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TCOM", composer, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getGenre()
	 */
    public TagContent getGenre() throws FrameDamagedException {
        TagContent ret = new TagContent();

        byte[] v2cont = null;

        try {
            v2cont = ((ID3v2Frame) (id3v2.getFrame("TCON").elementAt(0)))
                    .getContent();
        } catch (ID3v2Exception e) {
        }

        if (v2cont == null) {
            // try id3v1
            int v1cont;
            try {
                v1cont = id3.getGenre();

                // convert id3v1 info to new format
                ret.setContent("(" + v1cont + ")");
            } catch (ID3Exception e) {
                // no info
            }

        } else {
            // use v2
            Parser parse = new Parser(v2cont, true);
            try {
                ret.setContent(parse.parseText());
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }

        return ret;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setGenre(de.vdheide.mp3.TagContent)
	 */
    public void setGenre(TagContent genre) throws TagFormatException {
        if (genre.getTextContent() == null) {
            throw new TagFormatException();
        }

        // write v1

        // parse genre
        boolean found = false;
        int brackpos = 0;
        while ((brackpos != -1) && (found == false)) {
            brackpos = genre.getTextContent().indexOf('(', brackpos);
            if (brackpos != -1) {
                // check if next character is a number
                if (Character.isDigit(genre.getTextContent().charAt(
                        brackpos + 1)) == true) {
                    // found it
                    // search for )
                    int brackclose = genre.getTextContent().indexOf(
                            ')', brackpos);
                    if (brackclose == -1) {
                        // something went wrong...
                    } else {
                        // parse to int
                        try {
                            Integer par = new Integer(genre
                                    .getTextContent().substring(
                                            brackpos + 1, brackclose));
                            // write genre
                            try {
                                id3.setGenre(par.intValue());
                            } catch (ID3IllegalFormatException e2) {
                            }
                            found = true;
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }
        }

        if (found == false) {
            // could not parse a genre number
            try {
                id3.setGenre(12); // Genre: OTHER
            } catch (ID3IllegalFormatException e2) {
            }
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE);
        build.put(genre.getTextContent());
        byte[] v2cont = build.getBytes();

        //// store
        // remove frame
        try {
            id3v2.removeFrame("TCON");
        } catch (ID3v2Exception e) {
        }

        // store frame
        try {
            ID3v2Frame add = new ID3v2Frame("TCON", v2cont, false,
                    false, false,
                    (use_compression ? ID3v2Frame.DO_COMPRESS
                            : ID3v2Frame.NO_COMPRESSION), (byte) 0,
                    (byte) 0);
            id3v2.addFrame(add);
        } catch (ID3v2DecompressionException e) {
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getCopyrightText()
	 */
    public TagContent getCopyrightText() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TCOP");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setCopyrightText(de.vdheide.mp3.TagContent)
	 */
    public void setCopyrightText(TagContent copyright)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TCOP", copyright,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getDate()
	 */
    public TagContent getDateTag() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TDAT");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setDate(de.vdheide.mp3.TagContent)
	 */
    public void setDate(TagContent date) throws TagFormatException {
        // check format
        if (date.getTextContent() == null
                || checkExactLength(date.getTextContent(), 4) == false
                || checkNumeric(date.getTextContent()) == false) {
            throw new TagFormatException();
        }

        (new TextFrameEncoding(id3v2, "TDAT", date, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPlaylistDelay()
	 */
    public TagContent getPlaylistDelay() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TDLY");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setPlaylistDelay(de.vdheide.mp3.TagContent)
	 */
    public void setPlaylistDelay(TagContent delay)
            throws TagFormatException {
        // check format
        if (delay.getTextContent() == null
                || !checkNumeric(delay.getTextContent())) {
            throw new TagFormatException();
        }

        (new TextFrameEncoding(id3v2, "TDLY", delay, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getEncodedBy()
	 */
    public TagContent getEncodedBy() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TENC");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setEncodedBy(de.vdheide.mp3.TagContent)
	 */
    public void setEncodedBy(TagContent encoder)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TENC", encoder, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getLyricist()
	 */
    public TagContent getLyricist() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TEXT");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setLyricist(de.vdheide.mp3.TagContent)
	 */
    public void setLyricist(TagContent lyricist)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TEXT", lyricist, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getFileType()
	 */
    public TagContent getFileType() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TFLT");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setFileType(de.vdheide.mp3.TagContent)
	 */
    public void setFileType(TagContent type) throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TFLT", type, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getTime()
	 */
    public TagContent getTime() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TIME");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setTime(de.vdheide.mp3.TagContent)
	 */
    public void setTime(TagContent time) throws TagFormatException {
        // check format
        if (time.getTextContent() == null
                || !checkExactLength(time.getTextContent(), 4)
                || !checkNumeric(time.getTextContent())) {
            throw new TagFormatException();
        }

        (new TextFrameEncoding(id3v2, "TIME", time, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getContentGroup()
	 */
    public TagContent getContentGroup() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TIT1");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setContentGroup(de.vdheide.mp3.TagContent)
	 */
    public void setContentGroup(TagContent content)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TIT1", content, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getTitle()
	 */
    public TagContent getTitle() throws FrameDamagedException {
        TagContent ret = TextFrameEncoding.read(id3v2, "TIT2");
        if (ret.getTextContent() == null) {
            // try id3v1
            try {
                ret.setContent(id3.getTitle());
            } catch (NoID3TagException e) {
            }
        }

        return ret;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setTitle(de.vdheide.mp3.TagContent)
	 */
    public void setTitle(TagContent title) throws TagFormatException {
        if (title.getTextContent() == null) {
            throw new TagFormatException();
        }

        // write v1
        id3.setTitle(title.getTextContent());

        (new TextFrameEncoding(id3v2, "TIT2", title, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getSubtitle()
	 */
    public TagContent getSubtitle() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TIT3");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setSubtitle(de.vdheide.mp3.TagContent)
	 */
    public void setSubtitle(TagContent subtitle)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TIT3", subtitle, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getInitialKey()
	 */
    public TagContent getInitialKey() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TKEY");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setInitialKey(de.vdheide.mp3.TagContent)
	 */
    public void setInitialKey(TagContent key) throws TagFormatException {
        if (key.getTextContent() == null
                || !checkMaxLength(key.getTextContent(), 3)) {
            throw new TagFormatException();
        }

        (new TextFrameEncoding(id3v2, "TKEY", key, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getLanguage()
	 */
    public TagContent getLanguage() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TLAN");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setLanguage(de.vdheide.mp3.TagContent)
	 */
    public void setLanguage(TagContent lang) throws TagFormatException {
        if (lang.getTextContent() == null
                || !checkExactLength(lang.getTextContent(), 3)) {
            throw new TagFormatException();
        }

        (new TextFrameEncoding(id3v2, "TLAN", lang, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getLengthInTag()
	 */
    public TagContent getLengthInTag() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TLEN");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setLengthInTag(de.vdheide.mp3.TagContent)
	 */
    public void setLengthInTag(TagContent length)
            throws TagFormatException {
        // check format
        if (length.getTextContent() == null
                || !checkNumeric(length.getTextContent())) {
            throw new TagFormatException();
        }

        (new TextFrameEncoding(id3v2, "TLEN", length, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getMediaType()
	 */
    public TagContent getMediaType() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TMED");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setMediaType(de.vdheide.mp3.TagContent)
	 */
    public void setMediaType(TagContent type) throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TMED", type, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getOriginalTitle()
	 */
    public TagContent getOriginalTitle() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TOAL");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setOriginalTitle(de.vdheide.mp3.TagContent)
	 */
    public void setOriginalTitle(TagContent title)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TOAL", title, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getOriginalFilename()
	 */
    public TagContent getOriginalFilename()
            throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TOFN");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setOriginalFilename(de.vdheide.mp3.TagContent)
	 */
    public void setOriginalFilename(TagContent filename)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TOFN", filename, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getOriginalLyricist()
	 */
    public TagContent getOriginalLyricist()
            throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TOLY");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setOriginalLyricist(de.vdheide.mp3.TagContent)
	 */
    public void setOriginalLyricist(TagContent lyricist)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TOLY", lyricist, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getOriginalArtist()
	 */
    public TagContent getOriginalArtist() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TOPE");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setOriginalArtist(de.vdheide.mp3.TagContent)
	 */
    public void setOriginalArtist(TagContent artist)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TOPE", artist, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getOriginalYear()
	 */
    public TagContent getOriginalYear() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TORY");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setOriginalYear(de.vdheide.mp3.TagContent)
	 */
    public void setOriginalYear(TagContent year)
            throws TagFormatException {
        // check format
        if (year.getTextContent() == null
                || !checkExactLength(year.getTextContent(), 4)
                || !checkNumeric(year.getTextContent())) {
            throw new TagFormatException();
        }

        (new TextFrameEncoding(id3v2, "TORY", year, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getFileOwner()
	 */
    public TagContent getFileOwner() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TOWN");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setFileOwner(de.vdheide.mp3.TagContent)
	 */
    public void setFileOwner(TagContent owner)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TOWN", owner, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getArtist()
	 */
    public TagContent getArtist() throws FrameDamagedException {
        TagContent ret = TextFrameEncoding.read(id3v2, "TPE1");
        if (ret.getTextContent() == null) {
            try {
                ret.setContent(id3.getArtist());
            } catch (NoID3TagException e) {
            }
        }

        return ret;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setArtist(de.vdheide.mp3.TagContent)
	 */
    public void setArtist(TagContent artist) throws TagFormatException {
        // write v1
        id3.setArtist(artist.getTextContent());

        (new TextFrameEncoding(id3v2, "TPE1", artist, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getBand()
	 */
    public TagContent getBand() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TPE2");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setBand(de.vdheide.mp3.TagContent)
	 */
    public void setBand(TagContent band) throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TPE2", band, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getConductor()
	 */
    public TagContent getConductor() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TPE3");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setConductor(de.vdheide.mp3.TagContent)
	 */
    public void setConductor(TagContent conductor)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TPE3", conductor,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getRemixer()
	 */
    public TagContent getRemixer() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TPE4");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setRemixer(de.vdheide.mp3.TagContent)
	 */
    public void setRemixer(TagContent remixer)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TPE4", remixer, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPartOfSet()
	 */
    public TagContent getPartOfSet() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TPOS");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setPartOfSet(de.vdheide.mp3.TagContent)
	 */
    public void setPartOfSet(TagContent part) throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TPOS", part, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPublisher()
	 */
    public TagContent getPublisher() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TPUB");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setPublisher(de.vdheide.mp3.TagContent)
	 */
    public void setPublisher(TagContent publisher)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TPUB", publisher,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getTrack()
	 */
    public TagContent getTrack() throws FrameDamagedException {
        TagContent ret = new TagContent();

        byte[] v2cont = null;
        try {
            v2cont = ((ID3v2Frame) (id3v2.getFrame("TRCK").elementAt(0)))
                    .getContent();
        } catch (ID3v2Exception e) {
            // no info, wait for ID3
        }

        if (v2cont == null) {
            // try id3v1
            String v1cont;
            try {
                v1cont = String.valueOf(id3.getTrack());
                ret.setContent(v1cont);
            } catch (ID3Exception e) {
                // no info
            }
        } else {
            // use v2
            // Nils did comment out the following instructions
            // I don't know why, so I'm leaving it in for now
            Parser parse = new Parser(v2cont, true);
            try {
                ret.setContent(parse.parseText());
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }

        return ret;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setTrack(de.vdheide.mp3.TagContent)
	 */
    public void setTrack(TagContent track) throws TagFormatException {
        if (track.getTextContent() == null) {
            throw new TagFormatException();
        }

        // write v1

        // parse track
        boolean found = false;
        int slashpos = track.getTextContent().indexOf('/');
        String trackstring;
        if (slashpos != -1) {
            // using notation n/x
            trackstring = track.getTextContent().substring(0, slashpos);
        } else {
            trackstring = track.getTextContent();
        }

        try {
            Integer test = new Integer(trackstring);
            try {
                id3.setTrack(test.intValue());
            } catch (Exception e) {
            }
        } catch (NumberFormatException e) {
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE);
        build.put(track.getTextContent());
        byte[] v2cont = build.getBytes();

        //// store
        // remove frame
        try {
            id3v2.removeFrame("TRCK");
        } catch (ID3v2Exception e) {
        }

        // store frame
        try {
            ID3v2Frame add = new ID3v2Frame("TRCK", v2cont, false,
                    false, false,
                    (use_compression ? ID3v2Frame.DO_COMPRESS
                            : ID3v2Frame.NO_COMPRESSION), (byte) 0,
                    (byte) 0);
            id3v2.addFrame(add);
        } catch (ID3v2DecompressionException e) {
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getRecordingDates()
	 */
    public TagContent getRecordingDates() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TRDA");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setRecordingDate(de.vdheide.mp3.TagContent)
	 */
    public void setRecordingDate(TagContent date)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TRDA", date, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getInternetRadioStationName()
	 */
    public TagContent getInternetRadioStationName()
            throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TRSN");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setInternetRadioStationName(de.vdheide.mp3.TagContent)
	 */
    public void setInternetRadioStationName(TagContent name)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TRSN", name, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getInternetRadioStationOwner()
	 */
    public TagContent getInternetRadioStationOwner()
            throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TRSO");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setInternetRadioStationOwner(de.vdheide.mp3.TagContent)
	 */
    public void setInternetRadioStationOwner(TagContent owner)
            throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TRSO", owner, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getFilesize()
	 */
    public TagContent getFilesize() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TSIZ");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setFilesize(de.vdheide.mp3.TagContent)
	 */
    public void setFilesize(TagContent size) throws TagFormatException {
        if (size.getTextContent() == null
                || !checkNumeric(size.getTextContent())) {
            throw new TagFormatException();
        }

        (new TextFrameEncoding(id3v2, "TSIZ", size, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getISRC()
	 */
    public TagContent getISRC() throws FrameDamagedException {
        return TextFrameEncoding.read(id3v2, "TSRC");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setISRC(de.vdheide.mp3.TagContent)
	 */
    public void setISRC(TagContent isrc) throws TagFormatException {
        (new TextFrameEncoding(id3v2, "TSRC", isrc, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getYear()
	 */
    public TagContent getYear() throws FrameDamagedException {
        TagContent ret = TextFrameEncoding.read(id3v2, "TYER");
        if (ret.getTextContent() == null) {
            try {
                ret.setContent(id3.getYear());
            } catch (NoID3TagException e) {
            }

        }

        return ret;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setYear(de.vdheide.mp3.TagContent)
	 */
    public void setYear(TagContent year) throws TagFormatException {
        if (year.getTextContent() == null
                || !checkNumeric(year.getTextContent())
                || (!checkMaxLength(year.getTextContent(), 4) && !checkMaxLength(
                        year.getTextContent(), 5))) {
            throw new TagFormatException();
        }

        id3.setYear(year.getTextContent());

        (new TextFrameEncoding(id3v2, "TYER", year, use_compression))
                .write();
    }

    ////// URL link frames

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getCommercialInformation()
	 */
    public TagContent getCommercialInformation()
            throws FrameDamagedException {
        return TextFrameNoEncoding.read(id3v2, "WCOM");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setCommercialInformation(de.vdheide.mp3.TagContent)
	 */
    public void setCommercialInformation(TagContent info)
            throws TagFormatException {
        (new TextFrameNoEncoding(id3v2, "WCOM", info, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getCopyrightWebpage()
	 */
    public TagContent getCopyrightWebpage()
            throws FrameDamagedException {
        return TextFrameNoEncoding.read(id3v2, "WCOP");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setCopyrightWebpage(de.vdheide.mp3.TagContent)
	 */
    public void setCopyrightWebpage(TagContent copy)
            throws TagFormatException {
        (new TextFrameNoEncoding(id3v2, "WCOP", copy, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getAudioFileWebpage()
	 */
    public TagContent getAudioFileWebpage()
            throws FrameDamagedException {
        return TextFrameNoEncoding.read(id3v2, "WOAF");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setAudioFileWebpage(de.vdheide.mp3.TagContent)
	 */
    public void setAudioFileWebpage(TagContent page)
            throws TagFormatException {
        (new TextFrameNoEncoding(id3v2, "WOAF", page, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getArtistWebpage()
	 */
    public TagContent getArtistWebpage() throws FrameDamagedException {
        return TextFrameNoEncoding.read(id3v2, "WOAR");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setArtistWebpage(de.vdheide.mp3.TagContent)
	 */
    public void setArtistWebpage(TagContent page)
            throws TagFormatException {
        (new TextFrameNoEncoding(id3v2, "WOAR", page, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getAudioSourceWebpage()
	 */
    public TagContent getAudioSourceWebpage()
            throws FrameDamagedException {
        return TextFrameNoEncoding.read(id3v2, "WOAS");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setAudioSourceWebpage(de.vdheide.mp3.TagContent)
	 */
    public void setAudioSourceWebpage(TagContent page)
            throws TagFormatException {
        (new TextFrameNoEncoding(id3v2, "WOAS", page, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getInternetRadioStationWebpage()
	 */
    public TagContent getInternetRadioStationWebpage()
            throws FrameDamagedException {
        return TextFrameNoEncoding.read(id3v2, "WORS");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setInternetRadioStationWebpage(de.vdheide.mp3.TagContent)
	 */
    public void setInternetRadioStationWebpage(TagContent page)
            throws TagFormatException {
        (new TextFrameNoEncoding(id3v2, "WORS", page, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPaymentWebpage()
	 */
    public TagContent getPaymentWebpage() throws FrameDamagedException {
        return TextFrameNoEncoding.read(id3v2, "WPAY");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setPaymentWebpage(de.vdheide.mp3.TagContent)
	 */
    public void setPaymentWebpage(TagContent page)
            throws TagFormatException {
        (new TextFrameNoEncoding(id3v2, "WPAY", page, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPublishersWebpage()
	 */
    public TagContent getPublishersWebpage()
            throws FrameDamagedException {
        return TextFrameNoEncoding.read(id3v2, "WPUB");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setPublishersWebpage(de.vdheide.mp3.TagContent)
	 */
    public void setPublishersWebpage(TagContent page)
            throws TagFormatException {
        (new TextFrameNoEncoding(id3v2, "WPUB", page, use_compression))
                .write();
    }

    ////// Binary frames

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getEventTimingCodes()
	 */
    public TagContent getEventTimingCodes()
            throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "ETCO");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setEventTimingCodes(de.vdheide.mp3.TagContent)
	 */
    public void setEventTimingCodes(TagContent codes)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "ETCO", codes, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getLookupTable()
	 */
    public TagContent getLookupTable() throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "MLLT");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setLookupTable(de.vdheide.mp3.TagContent)
	 */
    public void setLookupTable(TagContent table)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "MLLT", table, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getSynchronizedTempoCodes()
	 */
    public TagContent getSynchronizedTempoCodes()
            throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "SYTC");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setSynchronizedTempoCodes(de.vdheide.mp3.TagContent)
	 */
    public void setSynchronizedTempoCodes(TagContent codes)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "SYTC", codes, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getSynchronizedLyrics()
	 */
    public TagContent getSynchronizedLyrics()
            throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "SYLT");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setSynchronizedLyrics(de.vdheide.mp3.TagContent)
	 */
    public void setSynchronizedLyrics(TagContent lyrics)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "SYLT", lyrics, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getRelativeVolumenAdjustment()
	 */
    public TagContent getRelativeVolumenAdjustment()
            throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "RVAD");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setRelativeVolumeAdjustment(de.vdheide.mp3.TagContent)
	 */
    public void setRelativeVolumeAdjustment(TagContent adjust)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "RVAD", adjust, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getEqualisation()
	 */
    public TagContent getEqualisation() throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "EQUA");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setEqualisation(de.vdheide.mp3.TagContent)
	 */
    public void setEqualisation(TagContent equal)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "EQUA", equal, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getReverb()
	 */
    public TagContent getReverb() throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "RVRB");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setReverb(de.vdheide.mp3.TagContent)
	 */
    public void setReverb(TagContent reverb) throws TagFormatException {
        (new BinaryFrame(id3v2, "RVRB", reverb, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPlayCounter()
	 */
    public TagContent getPlayCounter() throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "PCNT");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setPlayCounter(de.vdheide.mp3.TagContent)
	 */
    public void setPlayCounter(TagContent count)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "PCNT", count, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPopularimeter()
	 */
    public TagContent getPopularimeter() throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "POPM");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setPopularimeter(de.vdheide.mp3.TagContent)
	 */
    public void setPopularimeter(TagContent pop)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "POPM", pop, use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getRecommendedBufferSize()
	 */
    public TagContent getRecommendedBufferSize()
            throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "RBUF");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setRecommendedBufferSize(de.vdheide.mp3.TagContent)
	 */
    public void setRecommendedBufferSize(TagContent size)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "RBUF", size, use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPositionSynchronization()
	 */
    public TagContent getPositionSynchronization()
            throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "POSS");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setPositionSynchronization(de.vdheide.mp3.TagContent)
	 */
    public void setPositionSynchronization(TagContent synch)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "POSS", synch, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getOwnership()
	 */
    public TagContent getOwnership() throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "OWNE");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setOwnership(de.vdheide.mp3.TagContent)
	 */
    public void setOwnership(TagContent owner)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "OWNE", owner, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getCommercial()
	 */
    public TagContent getCommercial() throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "COMR");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setCommercial(de.vdheide.mp3.TagContent)
	 */
    public void setCommercial(TagContent commercial)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "COMR", commercial, use_compression))
                .write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getCDIdentifier()
	 */
    public TagContent getCDIdentifier() throws FrameDamagedException {
        return BinaryFrame.read(id3v2, "MCDI");
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setCDIdentifier(de.vdheide.mp3.TagContent)
	 */
    public void setCDIdentifier(TagContent ident)
            throws TagFormatException {
        (new BinaryFrame(id3v2, "MCDI", ident, use_compression))
                .write();
    }

    //////// Other frames, each is different to parse

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getUniqueFileIdentifier()
	 */
    public TagContent getUniqueFileIdentifier()
            throws FrameDamagedException {
        byte[] v2cont = Frame.read(id3v2, "UFID");
        if (v2cont == null) {
            return new TagContent();
        } else {
            TagContent ret = new TagContent();

            Parser parse = new Parser(v2cont, false);
            try {
                ret.setDescription(parse.parseText());
                ret.setContent(parse.parseBinary());

                return ret;
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setUniqueFileIdentifier(de.vdheide.mp3.TagContent)
	 */
    public void setUniqueFileIdentifier(TagContent ufi)
            throws TagFormatException {
        // check correct format
        if (ufi.getDescription() == null
                || ufi.getBinaryContent() == null
                || checkMaxLength(ufi.getBinaryContent(), 64) == false) {
            throw new TagFormatException();
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.NONE, ufi
                .getDescription().length()
                + 2 + ufi.getBinaryContent().length);

        build.put(ufi.getDescription());
        build.put((byte) 0);
        build.put(ufi.getBinaryContent());

        (new Frame(id3v2, "UFID", build.getBytes(), true, true,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getUserDefinedText()
	 */
    public TagContent getUserDefinedText() throws FrameDamagedException {
        byte[] v2cont = Frame.read(id3v2, "TXXX");
        if (v2cont == null) {
            return new TagContent();
        } else {
            TagContent ret = new TagContent();

            Parser parse = new Parser(v2cont, true);
            try {
                ret.setDescription(parse.parseText());
                ret.setContent(parse.parseText());
                return ret;
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setUserDefinedText(de.vdheide.mp3.TagContent)
	 */
    public void setUserDefinedText(TagContent info)
            throws TagFormatException {
        if (info.getDescription() == null
                || info.getTextContent() == null) {
            throw new TagFormatException();
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE, info
                .getDescription().length()
                * 2 + 3 + info.getTextContent().length() * 2);

        build.put(info.getDescription());
        build.put((byte) 0);
        build.put((byte) 0);
        build.put(info.getTextContent());

        (new Frame(id3v2, "TXXX", build.getBytes(), true, true,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getUserDefinedURL()
	 */
    public TagContent getUserDefinedURL() throws FrameDamagedException {
        byte[] v2cont = Frame.read(id3v2, "WXXX");
        if (v2cont == null) {
            return new TagContent();
        } else {
            TagContent ret = new TagContent();

            Parser parse = new Parser(v2cont, true);
            try {
                ret.setDescription(parse.parseText());
                ret.setContent(parse.parseText(Parser.ISO));
                return ret;
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setUserDefinedURL(de.vdheide.mp3.TagContent)
	 */
    public void setUserDefinedURL(TagContent link)
            throws TagFormatException {
        if (link.getDescription() == null
                || link.getTextContent() == null) {
            throw new TagFormatException();
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE, link
                .getDescription().length()
                * 2 + 3 + link.getTextContent().length());

        build.put(link.getDescription());
        build.put((byte) 0);
        build.put((byte) 0);
        try {
            build.put(link.getTextContent().getBytes("ISO8859_1"));
        } catch (java.io.UnsupportedEncodingException e) {
        }

        (new Frame(id3v2, "WXXX", build.getBytes(), true, true,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getUnsynchronizedLyrics()
	 */
    public TagContent getUnsynchronizedLyrics()
            throws FrameDamagedException {
        byte[] v2cont = Frame.read(id3v2, "USLT");
        if (v2cont == null) {
            return new TagContent();
        } else {
            TagContent ret = new TagContent();

            Parser parse = new Parser(v2cont, true);
            try {
                byte[] tmp = parse.parseBinary(3);
                try {
                    ret.setType(new String(tmp, "ISO8859_1"));
                } catch (java.io.UnsupportedEncodingException e) {
                }
                ret.setDescription(parse.parseText());
                ret.setContent(parse.parseText());
                return ret;
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setUnsynchronizedLyrics(de.vdheide.mp3.TagContent)
	 */
    public void setUnsynchronizedLyrics(TagContent lyric)
            throws TagFormatException {
        if (lyric.getType() == null || lyric.getDescription() == null
                || lyric.getTextContent() == null
                || !checkExactLength(lyric.getType(), 3)) {
            throw new TagFormatException();
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE, 6
                + lyric.getDescription().length() * 2
                + lyric.getTextContent().length() * 2);

        try {
            build.put(lyric.getType().getBytes("ISO8859_1"));
        } catch (java.io.UnsupportedEncodingException e) {
        }
        build.put(lyric.getDescription());
        build.put((byte) 0);
        build.put((byte) 0);
        build.put(lyric.getTextContent());

        (new Frame(id3v2, "USLT", build.getBytes(), true, true,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getComments()
	 */
    public TagContent getComments() throws FrameDamagedException {
        TagContent ret = new TagContent();

        byte[] v2cont = Frame.read(id3v2, "COMM");
        if (v2cont == null) {
            // use v1
            try {
                ret.setContent(id3.getComment());
            } catch (Exception e) {
            }
        } else {
            Parser parse = new Parser(v2cont, true);
            try {
                byte[] tmp = parse.parseBinary(3);
                try {
                    ret.setType(new String(tmp, "ISO8859_1"));
                } catch (java.io.UnsupportedEncodingException e) {
                }
                ret.setDescription(parse.parseText());
                ret.setContent(parse.parseText());
                return ret;
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }

        return ret;
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setComments(de.vdheide.mp3.TagContent)
	 */
    public void setComments(TagContent comm) throws TagFormatException {
        if (comm.getType() == null || comm.getDescription() == null
                || comm.getTextContent() == null
                || !checkExactLength(comm.getType(), 3)) {
            throw new TagFormatException();
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE, 4
                + comm.getDescription().length() * 2
                + comm.getTextContent().length() * 2);

        try {
            build.put(comm.getType().getBytes("ISO8859_1"));
        } catch (java.io.UnsupportedEncodingException e) {
        }
        build.put(comm.getDescription());
        build.put((byte) 0);
        build.put((byte) 0);
        build.put(comm.getTextContent());

        (new Frame(id3v2, "COMM", build.getBytes(), true, true,
                use_compression)).write();

        // write id3v1
        id3.setComment(comm.getTextContent());
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPicture()
	 */
    public TagContent getPicture() throws FrameDamagedException {
        byte[] v2cont = Frame.read(id3v2, "APIC");
        if (v2cont == null) {
            return new TagContent();
        } else {
            TagContent ret = new TagContent();

            Parser parse = new Parser(v2cont, true);
            try {
                ret.setType(parse.parseText(Parser.ISO));
                ret.setSubtype(parse.parseBinary(1));
                ret.setDescription(parse.parseText());
                ret.setContent(parse.parseBinary());
                return ret;
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setPicture(de.vdheide.mp3.TagContent)
	 */
    public void setPicture(TagContent pic) throws TagFormatException {
        if (pic.getType() == null || pic.getBinarySubtype() == null
                || pic.getDescription() == null
                || pic.getBinaryContent() == null) {
            throw new TagFormatException();
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE, 6
                + pic.getType().length() + 1
                + pic.getDescription().length() * 2
                + pic.getBinaryContent().length);

        try {
            build.put(pic.getType().getBytes("ISO8859_1"));
        } catch (java.io.UnsupportedEncodingException e) {
        }
        build.put((byte) 0);
        build.put(pic.getBinarySubtype()[0]);
        build.put(pic.getDescription());
        build.put((byte) 0);
        build.put((byte) 0);
        build.put(pic.getBinaryContent());

        (new Frame(id3v2, "APIC", build.getBytes(), true, true,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getEncapsulatedObject()
	 */
    public TagContent getEncapsulatedObject()
            throws FrameDamagedException {
        byte[] v2cont = Frame.read(id3v2, "GEOB");
        if (v2cont == null) {
            return new TagContent();
        } else {
            TagContent ret = new TagContent();

            Parser parse = new Parser(v2cont, true);
            try {
                ret.setType(parse.parseText(Parser.ISO));
                ret.setSubtype(parse.parseText());
                ret.setDescription(parse.parseText());
                ret.setContent(parse.parseBinary());
                return ret;
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setEncapsulatedObject(de.vdheide.mp3.TagContent)
	 */
    public void setEncapsulatedObject(TagContent obj)
            throws TagFormatException {
        if (obj.getType() == null || obj.getTextSubtype() == null
                || obj.getDescription() == null
                || obj.getBinaryContent() == null) {
            throw new TagFormatException();
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE, 6
                + obj.getType().length()
                + obj.getTextSubtype().length() * 2
                + obj.getDescription().length() * 2
                + obj.getBinaryContent().length);

        try {
            build.put(obj.getType().getBytes("ISO8859_1"));
        } catch (java.io.UnsupportedEncodingException e) {
        }
        build.put((byte) 0);
        build.put(obj.getTextSubtype());
        build.put((byte) 0);
        build.put((byte) 0);
        build.put(obj.getDescription());
        build.put((byte) 0);
        build.put((byte) 0);
        build.put(obj.getBinaryContent());

        (new Frame(id3v2, "GEOB", build.getBytes(), true, true,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getTermsOfUse()
	 */
    public TagContent getTermsOfUse() throws FrameDamagedException {
        byte[] v2cont = Frame.read(id3v2, "USER");
        if (v2cont == null) {
            return new TagContent();
        } else {
            TagContent ret = new TagContent();

            Parser parse = new Parser(v2cont, true);
            try {
                byte[] tmp = parse.parseBinary(3);
                try {
                    ret.setType(new String(tmp, "ISO8859_1"));
                } catch (java.io.UnsupportedEncodingException e) {
                }
                ret.setContent(parse.parseText());
                return ret;
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setTermsOfUse(de.vdheide.mp3.TagContent)
	 */
    public void setTermsOfUse(TagContent use) throws TagFormatException {
        if (use.getType() == null || use.getTextContent() == null
                || !checkExactLength(use.getType(), 3)) {
            throw new TagFormatException();
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE,
                3 + use.getTextContent().length() * 2);

        try {
            build.put(use.getType().getBytes("ISO8859_1"));
        } catch (java.io.UnsupportedEncodingException e) {
        }
        build.put(use.getTextContent());

        (new Frame(id3v2, "USER", build.getBytes(), true, true,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getEncryptionMethodRegistration()
	 */
    public TagContent getEncryptionMethodRegistration()
            throws FrameDamagedException {
        byte[] v2cont = Frame.read(id3v2, "ENCR");
        if (v2cont == null) {
            return new TagContent();
        } else {
            TagContent ret = new TagContent();

            Parser parse = new Parser(v2cont, false);
            try {
                ret.setType(parse.parseText());
                ret.setSubtype(parse.parseBinary(1));
                ret.setContent(parse.parseBinary());
                return ret;
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setEncryptionMethodRegistration(de.vdheide.mp3.TagContent)
	 */
    public void setEncryptionMethodRegistration(TagContent encr)
            throws TagFormatException {
        if (encr.getType() == null || encr.getBinarySubtype() == null
                || encr.getBinaryContent() == null
                || !checkExactLength(encr.getBinarySubtype(), 1)) {
            throw new TagFormatException();
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.NONE, 2
                + encr.getType().length()
                + encr.getBinaryContent().length);

        build.put(encr.getType());
        build.put((byte) 0);
        build.put(encr.getBinarySubtype()[0]);
        build.put(encr.getBinaryContent());

        (new Frame(id3v2, "ENCR", build.getBytes(), true, true,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getGroupIdentificationRegistration()
	 */
    public TagContent getGroupIdentificationRegistration()
            throws FrameDamagedException {
        byte[] v2cont = Frame.read(id3v2, "GRID");
        if (v2cont == null) {
            return new TagContent();
        } else {
            TagContent ret = new TagContent();

            Parser parse = new Parser(v2cont, false);
            try {
                ret.setType(parse.parseText());
                ret.setSubtype(parse.parseBinary(1));
                ret.setContent(parse.parseBinary());
                return ret;
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setGroupIdentificationRegistration(de.vdheide.mp3.TagContent)
	 */
    public void setGroupIdentificationRegistration(TagContent grp)
            throws TagFormatException {
        if (grp.getType() == null || grp.getBinarySubtype() == null
                || grp.getBinaryContent() == null
                || !checkExactLength(grp.getBinarySubtype(), 1)) {
            throw new TagFormatException();
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.NONE, 2
                + grp.getType().length()
                + grp.getBinaryContent().length);

        build.put(grp.getType());
        build.put((byte) 0);
        build.put(grp.getBinarySubtype()[0]);
        build.put(grp.getBinaryContent());

        (new Frame(id3v2, "GRID", build.getBytes(), true, true,
                use_compression)).write();
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#getPrivateData()
	 */
    public TagContent getPrivateData() throws FrameDamagedException {
        byte[] v2cont = Frame.read(id3v2, "PRIV");
        if (v2cont == null) {
            return new TagContent();
        } else {
            TagContent ret = new TagContent();

            Parser parse = new Parser(v2cont, false);
            try {
                ret.setType(parse.parseText());
                ret.setContent(parse.parseBinary());
                return ret;
            } catch (ParseException e) {
                throw new FrameDamagedException();
            }
        }
    }

    /* (non-Javadoc)
	 * @see de.vdheide.mp3.MP3File#setPrivateData(de.vdheide.mp3.TagContent)
	 */
    public void setPrivateData(TagContent data)
            throws TagFormatException {
        if (data.getType() == null || data.getBinaryContent() == null) {
            throw new TagFormatException();
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.NONE, 1
                + data.getType().length()
                + data.getBinaryContent().length);

        build.put(data.getType());
        build.put((byte) 0);
        build.put(data.getBinaryContent());

        (new Frame(id3v2, "PRIV", build.getBytes(), true, true,
                use_compression)).write();
    }

    /**
     * Write ID3 tag?
     */
    protected boolean write_id3 = true;

    /**
     * Write ID3v2 tag?
     */
    protected boolean write_id3v2 = true;

    /**
     * Use compression in ID3v2 tag?
     */
    protected boolean use_compression = true;

    /**
     * Use CRC in ID3v2 tag?
     */
    protected boolean use_crc = true;

    /**
     * Use padding in ID3v2 tag?
     */
    protected boolean use_padding = true;

    /**
     * Use unsynchronization in ID3v2 tag?
     */
    protected boolean use_unsynchronization = true;

    /**
     * ID3 tag
     */
    protected ID3 id3 = null;

    /**
     * ID3v3 tag
     */
    protected ID3v2 id3v2 = null;

    /**
     * MP3 properties
     */
    protected MP3Properties prop = null;

    /**
     * Checks if input string is of a given length
     * 
     * @param in
     *            string to check
     * @param length
     *            length to check against
     * @return true if the string is of the given length
     */
    protected boolean checkExactLength(String in, int length) {
        return (in.length() == length);
    }

    /**
     * Checks if input string has a maximum given length
     * 
     * @param in
     *            string to check
     * @param length
     *            length to check against
     * @return true if the string is of the given length or shorter
     */
    protected boolean checkMaxLength(String in, int length) {
        return (in.length() <= length);
    }

    /**
     * Checks if input byte array is of a given length
     * 
     * @param in
     *            byte array to check
     * @param length
     *            length to check against
     * @return true if the array is of the given length
     */
    protected boolean checkExactLength(byte[] in, int length) {
        return (in.length == length);
    }

    /**
     * Checks if input byte array has a maximum given length
     * 
     * @param in
     *            byte array to check
     * @param length
     *            length to check against
     * @return true if the array is of the given length or has less elements
     */
    protected boolean checkMaxLength(byte[] in, int length) {
        return (in.length <= length);
    }

    /**
     * Checks if input string is numeric
     * 
     * @param in
     *            string to check
     * @return true if the array is numeric
     */
    protected boolean checkNumeric(String in) {
        try {
            Integer test = new Integer(in);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Initialization code, called by all constructors
     * 
     * @exception IOException
     *                If I/O error occurs
     * @exception NoMP3FrameException
     *                If file does not contain at least one mp3 frame
     * @exception ID3v2WrongCRCException
     *                If the ID3v2 tag fails CRC
     * @exception ID3v2DecompressionException
     *                If the ID3v2 tag cannot be decompressed
     * @exception ID3v2IllegalVersionException
     *                If the ID3v2 tag has a wrong (too high) version
     */
    protected void init() throws IOException, NoMP3FrameException,
            ID3v2WrongCRCException, ID3v2DecompressionException,
            ID3v2IllegalVersionException {
        // read properties and tags
        prop = new MP3PropertiesImpl(this);
        id3 = new ID3Impl(this);
        id3v2 = new ID3v2Impl(this);
    }

} // public class MP3File
