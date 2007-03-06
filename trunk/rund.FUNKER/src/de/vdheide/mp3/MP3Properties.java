package de.vdheide.mp3;

public interface MP3Properties {

	/**
	 * Mode: Stereo
	 */
	public final static int MODE_STEREO = 0;

	/**
	 * Mode: Joint stereo
	 */
	public final static int MODE_JOINT_STEREO = 1;

	/**
	 * Mode: Dual channel
	 */
	public final static int MODE_DUAL_CHANNEL = 2;

	/**
	 * Mode: Mono
	 */
	public final static int MODE_MONO = 3;

	/**
	 * Emphasis: Illegal value
	 */
	public final static int EMPHASIS_ILLEGAL = 0;

	/**
	 * Emphasis: None
	 */
	public final static int EMPHASIS_NONE = 1;

	/**
	 * Emphasis: 5012ms
	 */
	public final static int EMPHASIS_5015MS = 2;

	/**
	 * Emphasis: CCITT
	 */
	public final static int EMPHASIS_CCITT = 3;

	/**
	 * @return MPEG level
	 */
	public abstract int getMPEGLevel();

	/**
	 * @return Layer, 0 for illegal entries
	 */
	public abstract int getLayer();

	/**
	 * @return bitrate, 0 for illegal entries
	 */
	public abstract int getBitrate();

	/**
	 * @return samplerate, 0 for illegal entries
	 */
	public abstract int getSamplerate();

	/**
	 * Returns mode (mono, stereo) used in MP3 file. Please use the constants
	 * MODE_XXX.
	 * 
	 * @return Mode
	 */
	public abstract int getMode();

	/**
	 * Returns emphasis used in MP3 file. There are constants...
	 * 
	 * @return emphasis
	 */
	public abstract int getEmphasis();

	/**
	 * @return Protection set?
	 */
	public abstract boolean getProtection();

	/**
	 * @return Padding set?
	 */
	public abstract boolean getPadding();

	/**
	 * @return Private bit set?
	 */
	public abstract boolean getPrivate();

	/**
	 * @return Copyright bit set?
	 */
	public abstract boolean getCopyright();

	/**
	 * @return Original?
	 */
	public abstract boolean getOriginal();

	/**
	 * @return Length in seconds
	 */
	public abstract long getLength();

}