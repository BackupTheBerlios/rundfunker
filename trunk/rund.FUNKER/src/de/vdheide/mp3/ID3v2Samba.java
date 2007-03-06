// ID3v2.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import jcifs.smb.SmbFile;

/**
 * Instances of this class contain an ID3v2 tag
 * <p>
 * Notes:
 * <ol>
 * <li>There are two ways of detecting the size of padding used:
 * <ol type="a">
 * <li>The "Size of padding" field in the extended header
 * <li>Detecting all frames and substracting the tag's actual length from its'
 * length in the header.
 * </ol>
 * Method a) is used in preference, so if a wrong padding size is stated in the
 * extended header, all bad things may happen.
 * <li>Although the ID3v2 informal standard does not state it, this class will
 * only detect an ID3v2 tag if is starts at the first byte of a file.
 * <li>There is no direct access to the header and extended header. Both are
 * read, created and written internally.
 */
public class ID3v2Samba implements ID3v2 {

    /** ******** Constructors ********* */

    /**
     * Provides access to ID3v2 tag. When used with an InputStream, no writes
     * are possible (<code>update</code> will fail with an
     * <code>IOException</code>, so make sure you just read.
     *
     * @param in
     *            Input stream to read from. Stream position must be set to
     *            beginning of file (i.e. position of ID3v2 tag).
     * @exception IOException
     *                If I/O errors occur
     * @exception ID3v2IllegalVersionException
     *                If file contains an IDv2 tag of higher version than
     *                <code>VERSION</code>.<code>REVISION</code>
     * @exception ID3v2WrongCRCException
     *                If file contains CRC and this differs from CRC calculated
     *                from the frames
     * @exception ID3v2DecompressionException
     *                If a decompression error occured while decompressing a
     *                compressed frame
     */
    public ID3v2Samba(InputStream in) throws IOException,
            ID3v2IllegalVersionException, ID3v2WrongCRCException,
            ID3v2DecompressionException {
        this.file = null;

        // open file and read tag (if present)
        try {
            readHeader(in);
        } catch (NoID3v2HeaderException e) {
            // no tag
            header = null;
            extended_header = null;
            frames = null;
            in.close();
            return;
        }

        // tag present
        if (header.hasExtendedHeader()) {
            readExtendedHeader(in);
        } else {
            extended_header = null;
        }

        readFrames(in);

        in.close();
        is_changed = false;
    }

    /**
     * Provides access to <code>file</code>'s ID3v2 tag
     *
     * @param file
     *            File to access
     * @exception IOException
     *                If I/O errors occur
     * @exception ID3v2IllegalVersionException
     *                If file contains an IDv2 tag of higher version than
     *                <code>VERSION</code>.<code>REVISION</code>
     * @exception ID3v2WrongCRCException
     *                If file contains CRC and this differs from CRC calculated
     *                from the frames
     * @exception ID3v2DecompressionException
     *                If a decompression error occured while decompressing a
     *                compressed frame
     */
    public ID3v2Samba(SmbFile file) throws IOException,
            ID3v2IllegalVersionException, ID3v2WrongCRCException,
            ID3v2DecompressionException {
    	this(file.getInputStream());
        this.file = file;
    }

    /** ******** Public variables ********* */

    /**
     * ID3v2 version
     */
    public final static byte VERSION = 3;

    /**
     * ID3v2 revision
     */
    public final static byte REVISION = 0;

    /** ******** Public methods ********* */

    /**
     * This method undoes the effect of the unsynchronization scheme by
     * replacing $FF $00 by $FF
     *
     * @param in
     *            Array of bytes to be "synchronized"
     * @return Changed array or null if no "synchronization" was necessary
     */
    public static byte[] synchronize(byte[] in) {
        boolean did_synch = false;
        byte out[] = new byte[in.length];
        int outpos = 0; // next position to write to

        for (int i = 0; i < in.length; i++) {
            out[outpos++] = in[i];

            // Skip next byte if this byte is 0xff and
            // next byte is 0x00
            if (in[i] == (byte) 255 && i < in.length - 1
                    && in[i + 1] == 0) {
                did_synch = true;
                i++;
            }
        }

        // make out smaller if necessary
        if (outpos != in.length) {
            // removed one or more bytes
            byte[] tmp = new byte[outpos];
            System.arraycopy(out, 0, tmp, 0, outpos);
            out = tmp;
        }

        if (did_synch == true) {
            return out;
        } else {
            return null;
        }
    }

    /**
     * Unsynchronizes an array of bytes by replacing $FF 00 with $FF 00 00 and
     * %11111111 111xxxxx with %11111111 00000000 111xxxxx.
     *
     * @param in
     *            Array of bytes to be "unsynchronized"
     * @return Changed array or null if no change was necessary
     */
    public static byte[] unsynchronize(byte[] in) {
        byte[] out = new byte[in.length];
        int outpos = 0; // next position to write to
        boolean did_unsync = false;

        for (int i = 0; i < in.length; i++) {
            // Check every byte in in if it is $FF
            if (in[i] == -1) {
                // yes, perhaps we must unsynchronize
                if (i < in.length - 1
                        && ((in[i + 1] & 0xff) >= 0xe0 || in[i + 1] == 0)) {
                    // next byte is %111xxxxx or %00000000,
                    // we must unsynchronize

                    // first, enlarge out by one element
                    byte[] tmp = new byte[out.length + 1];
                    System.arraycopy(out, 0, tmp, 0, outpos);
                    out = tmp;
                    tmp = null;
                    out[outpos++] = -1;
                    out[outpos++] = 0;
                    out[outpos++] = in[i + 1];

                    // skip next byte, we have already written it
                    i++;

                    did_unsync = true;
                } else {
                    // no unsynchronization necessary
                    out[outpos++] = in[i];
                }
            } else {
                // no unsynchronization necessary
                out[outpos++] = in[i];
            }

        }

        if (did_unsync == true) {
            // we did some unsynchronization
            return out;
        } else {
            return null;
        }

    }

    /**
     * Enables or disables use of padding (enabled by default)
     *
     * @param use_padding
     *            True if padding should be used
     */
    public void setUsePadding(boolean use_padding) {
        if (this.use_padding != use_padding) {
            is_changed = true;
            this.use_padding = use_padding;
        }
    }

    /**
     * @return True if padding is used
     */
    public boolean getUsePadding() {
        return use_padding;
    }

    /**
     * Enables / disables use of CRC
     *
     * @param use_crc
     *            True if CRC should be used
     */
    public void setUseCRC(boolean use_crc) {
        if (this.use_crc != use_crc) {
            is_changed = true;
            this.use_crc = use_crc;
        }
    }

    /**
     * @return True if CRC is used
     */
    public boolean getUseCRC() {
        return use_crc;
    }

    /**
     * Enables / disables use of unsynchronization
     *
     * @param use_unsynch
     *            True if unsynchronization should be used
     */
    public void setUseUnsynchronization(boolean use_unsynch) {
        if (this.use_unsynchronization != use_unsynch) {
            is_changed = true;
            this.use_unsynchronization = use_unsynch;
        }
    }

    /**
     * @return True if unsynchronization should be used
     */
    public boolean getUseUnsynchronization() {
        return use_unsynchronization;
    }

    /**
     * Test if file already has an ID3v2 tag
     *
     * @return true if file has IDv2 tag
     */
    public boolean hasTag() {
        if (header == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Get all frames
     *
     * @return <code>Vector</code> of all frames
     * @exception NoID3v2TagException
     *                If file does not contain ID3v2 tag
     */
    public Vector getFrames() throws NoID3v2TagException {
        if (frames == null) {
            throw new NoID3v2TagException();
        }

        return frames;
    }

    /**
     * Return all frame with ID <code>id</code>
     *
     * @param id
     *            Frame ID
     * @return Requested frames
     * @exception NoID3v2TagException
     *                If file does not contain ID3v2Tag
     * @exception ID3v2NoSuchFrameException
     *                If file does not contain requested ID3v2 frame
     */
    public Vector getFrame(String id) throws NoID3v2TagException,
            ID3v2NoSuchFrameException {
        if (frames == null) {
            throw new NoID3v2TagException();
        }

        Vector res = new Vector();
        ID3v2Frame tmp;
        for (Enumeration e = frames.elements(); e.hasMoreElements();) {
            tmp = (ID3v2Frame) e.nextElement();
            if (tmp.getID().equals(id)) {
                res.addElement(tmp);
            }
        }

        if (res.size() == 0) {
            // no frame found
            throw new ID3v2NoSuchFrameException();
        } else {
            return res;
        }
    }

    /**
     * Add a frame
     *
     * @param frame
     *            Frame to add
     */
    public void addFrame(ID3v2Frame frame) {
        if (frames == null) {
            frames = new Vector();
        }

        frames.addElement(frame);
        is_changed = true;
    }

    /**
     * Remove a frame.
     *
     * @param frame
     *            Frame to remove
     * @exception NoID3v2TagException
     *                If file does not contain ID3v2Tag
     * @exception ID3v2NoSuchFrameException
     *                If file does not contain requested ID3v2 frame
     */
    public void removeFrame(ID3v2Frame frame)
            throws NoID3v2TagException, ID3v2NoSuchFrameException {
        if (frames == null) {
            throw new NoID3v2TagException();
        }

        if (frames.removeElement(frame) == false) {
            throw new ID3v2NoSuchFrameException();
        }
        is_changed = true;
    }

    /**
     * Remove all frames with a given id.
     *
     * @param id
     *            ID of frames to remove
     * @exception NoID3v2TagException
     *                If file does not contain ID3v2Tag
     * @exception ID3v2NoSuchFrameException
     *                If file does not contain requested ID3v2 frame
     */
    public void removeFrame(String id) throws NoID3v2TagException,
            ID3v2NoSuchFrameException {
        if (frames == null) {
            throw new NoID3v2TagException();
        }

        ID3v2Frame tmp;
        boolean found = false; // will be true if at least one frame was found
        for (Enumeration e = frames.elements(); e.hasMoreElements();) {
            tmp = (ID3v2Frame) e.nextElement();
            if (tmp.getID().equals(id)) {
                frames.removeElement(tmp);
                found = true;
            }
        }

        if (found == false) {
            throw new ID3v2NoSuchFrameException();
        }
        is_changed = true;
    }

    /**
     * Remove a spefic frames with a given id. A number is given to identify the
     * frame if more than one frame exists
     *
     * @param id
     *            ID of frames to remove
     * @param number
     *            Number of frame to remove (the first frame gets number 0)
     * @exception NoID3v2TagException
     *                If file does not contain ID3v2Tag
     * @exception ID3v2NoSuchFrameException
     *                If file does not contain requested ID3v2 frame
     */
    public void removeFrame(String id, int number)
            throws NoID3v2TagException, ID3v2NoSuchFrameException {
        if (frames == null) {
            throw new NoID3v2TagException();
        }

        ID3v2Frame tmp;
        int count = 0; // Number of frames with id found so far
        boolean removed = false; // will be true if at least frame was removed
        for (Enumeration e = frames.elements(); e.hasMoreElements();) {
            tmp = (ID3v2Frame) e.nextElement();
            if (tmp.getID().equals(id)) {
                if (count == number) {
                    frames.removeElement(tmp);
                    removed = true;
                } else {
                    count++;
                }
            }
        }

        if (removed == false) {
            throw new ID3v2NoSuchFrameException();
        }
        is_changed = true;
    }

    /**
     * Remove all frames
     */
    public void removeFrames() {
        if (frames != null) {
            frames = new Vector();
        }
    }

    /**
     * Write changes to file
     *
     * @exception IOException
     *                If an I/O error occurs or the object was created from an
     *                InputStream and an update should be executed
     */
    public void update() throws IOException {
        throw new java.lang.RuntimeException("Update-Methode nicht implementiert");
    }

    /** ******** Private variables ********* */

    private SmbFile file;

    private ID3v2Header header;

    private ID3v2ExtendedHeader extended_header;

    private Vector frames;

    private boolean is_changed = false;

    private boolean use_padding = true;

    private boolean use_crc = true;

    private boolean use_unsynchronization = true;

    /** ******** Private methods ********* */

    /**
     * Read ID3v2 header from file <code>in</code>
     *
     * @param in
     *            InputStream to read from
     * @throws NoID3v2HeaderException
     * @throws ID3v2IllegalVersionException
     * @throws IOException
     */
    private void readHeader(InputStream in)
            throws NoID3v2HeaderException,
            ID3v2IllegalVersionException, IOException {
        header = new ID3v2Header(in);
    }

    /**
     * Read extended ID3v2 header from input stream <tt>in</tt>
     *
     * @param in
     *            Input stream to read from
     * @throws IOException
     */
    private void readExtendedHeader(InputStream in) throws IOException {
        // in file pointer must be at correct position (header
        // has just been read)
        extended_header = new ID3v2ExtendedHeader(in);
    }

    /**
     * Read ID3v2 frames from stream <tt>in</tt> Stream position must be set
     * to beginning of frames
     *
     * @param in
     *            Stream to read from
     * @throws IOException
     * @throws ID3v2WrongCRCException
     * @throws ID3v2DecompressionException
     */
    private void readFrames(InputStream in) throws IOException,
            ID3v2WrongCRCException, ID3v2DecompressionException {
        // steps to read frames:
        // 1) Read all frames as bytes (don't include padding if size of padding
        // is
        //                              known, i.e. ext. header exists)
        // 2) If CRC is present, make CRC check on frames
        // 3) Convert bytes to ID3v2Frames

        //// read all frames as bytes
        // calculate number of bytes to be read
        int bytes_to_read;
        if (extended_header != null) {
            // ext. header exists
            bytes_to_read = header.getTagSize()
                    - (extended_header.getSize() + 4)
                    - extended_header.getPaddingSize();
        } else {
            // no ext. header, include padding
            bytes_to_read = header.getTagSize();
        }

        // read bytes
        byte[] unsynch_frames_as_byte = new byte[bytes_to_read];
        in.read(unsynch_frames_as_byte);

        byte[] frames_as_byte;
        if (header.getUnsynchronization()) {
            // undo effects of unsynchronization
            frames_as_byte = synchronize(unsynch_frames_as_byte);
            if (frames_as_byte == null) {
                frames_as_byte = unsynch_frames_as_byte;
            }
        } else {
            frames_as_byte = unsynch_frames_as_byte;
        }

        //// CRC check
        if (extended_header != null && extended_header.hasCRC() == true) {
            // make CRC check
            // calculate crc of read frames (because extended header exists,
            // they contain no padding)

            java.util.zip.CRC32 crc_calculator = new java.util.zip.CRC32();
            crc_calculator.update(frames_as_byte);
            int crc = (int) crc_calculator.getValue();

            if ((int) crc != (int) extended_header.getCRC()) {
                // crc mismatch
                //throw new ID3v2WrongCRCException();
            }
        }

        //// Convert bytes to ID3v2Frames
        frames = new Vector();

        ByteArrayInputStream bis = new ByteArrayInputStream(
                frames_as_byte);
        // read frames as long as there are bytes and we are not reading from
        // padding
        // (indicated by invalid frame id)
        ID3v2Frame frame = null;
        boolean cont = true;
        while ((bis.available() > 0) && (cont == true)) {
            frame = new ID3v2Frame(bis);

            if (frame.getID() == ID3v2Frame.ID_INVALID) {
                // reached end of frames
                cont = false;
            } else {
                frames.addElement(frame);
            }
        }
        bis.close();
    }

    /**
     * Convert all frames to an array of bytes
     *
     * @return all frames as bytes
     */
    private byte[] convertFramesToArrayOfBytes() {
        ID3v2Frame tmp = null;

        ByteArrayOutputStream out = new ByteArrayOutputStream(500);

        for (Enumeration e = frames.elements(); e.hasMoreElements();) {
            tmp = (ID3v2Frame) e.nextElement();
            byte frame_in_bytes[] = tmp.getBytes();
            out.write(frame_in_bytes, 0, frame_in_bytes.length);
        }

        return out.toByteArray();
    }
}
