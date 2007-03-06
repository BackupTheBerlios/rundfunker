package de.vdheide.mp3;

import java.io.IOException;
import java.util.Vector;

public interface ID3v2 {

	/**
	 * ID3v2 version
	 */
	public final static byte VERSION = 3;

	/**
	 * ID3v2 revision
	 */
	public final static byte REVISION = 0;

	/**
	 * Enables or disables use of padding (enabled by default)
	 * 
	 * @param use_padding
	 *            True if padding should be used
	 */
	public abstract void setUsePadding(boolean use_padding);

	/**
	 * @return True if padding is used
	 */
	public abstract boolean getUsePadding();

	/**
	 * Enables / disables use of CRC
	 * 
	 * @param use_crc
	 *            True if CRC should be used
	 */
	public abstract void setUseCRC(boolean use_crc);

	/**
	 * @return True if CRC is used
	 */
	public abstract boolean getUseCRC();

	/**
	 * Enables / disables use of unsynchronization
	 * 
	 * @param use_unsynch
	 *            True if unsynchronization should be used
	 */
	public abstract void setUseUnsynchronization(boolean use_unsynch);

	/**
	 * @return True if unsynchronization should be used
	 */
	public abstract boolean getUseUnsynchronization();

	/**
	 * Test if file already has an ID3v2 tag
	 * 
	 * @return true if file has IDv2 tag
	 */
	public abstract boolean hasTag();

	/**
	 * Get all frames
	 * 
	 * @return <code>Vector</code> of all frames
	 * @exception NoID3v2TagException
	 *                If file does not contain ID3v2 tag
	 */
	public abstract Vector getFrames() throws NoID3v2TagException;

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
	public abstract Vector getFrame(String id) throws NoID3v2TagException,
			ID3v2NoSuchFrameException;

	/**
	 * Add a frame
	 * 
	 * @param frame
	 *            Frame to add
	 */
	public abstract void addFrame(ID3v2Frame frame);

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
	public abstract void removeFrame(ID3v2Frame frame)
			throws NoID3v2TagException, ID3v2NoSuchFrameException;

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
	public abstract void removeFrame(String id) throws NoID3v2TagException,
			ID3v2NoSuchFrameException;

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
	public abstract void removeFrame(String id, int number)
			throws NoID3v2TagException, ID3v2NoSuchFrameException;

	/**
	 * Remove all frames
	 */
	public abstract void removeFrames();

	/**
	 * Write changes to file
	 * 
	 * @exception IOException
	 *                If an I/O error occurs or the object was created from an
	 *                InputStream and an update should be executed
	 */
	public abstract void update() throws IOException;

}