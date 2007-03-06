package de.vdheide.mp3;

public interface MP3File {

	/**
	 * Commit information to file
	 * 
	 * @exception ID3Exception
	 *                If an error occurs when writing the ID3 tag
	 * @exception ID3v2Exception
	 *                If an error ocurrs when writing the ID3v2 tag
	 */
	public abstract void update() throws ID3Exception, ID3v2Exception;

	/**
	 * Write ID3 tag?
	 * 
	 * @param write_id3
	 *            True: Write ID3 tag on update
	 */
	public abstract void setWriteID3(boolean write_id3);

	/**
	 * Should an ID3 tag be written
	 * 
	 * @return true if ID3 tag will be written on update
	 */
	public abstract boolean getWriteID3();

	/**
	 * Write ID3v2 tag?
	 * 
	 * @param write_id3v2
	 *            True: Write ID3v2 tag on update
	 */
	public abstract void setWriteID3v2(boolean write_id3v2);

	/**
	 * Should an ID3v2 tag be written?
	 * 
	 * @return true if ID3v2 tag will be written on update
	 */
	public abstract boolean getWriteID3v2();

	/**
	 * Use compression in ID3v2 tag? Frames are compressed only when the
	 * compressed content is smaller than the uncompressed content.
	 * 
	 * @param use_compression
	 *            True: Use compression
	 */
	public abstract void setUseCompression(boolean use_compression);

	/**
	 * @return true if compression will be used in ID3v2 tag
	 */
	public abstract boolean getUseCompression();

	/**
	 * Use CRC in ID3v2 tag?
	 * 
	 * @param use_crc
	 *            True: Use CRC
	 */
	public abstract void setUseCRC(boolean use_crc);

	/**
	 * @return true if CRC will be used in ID3v2 tag
	 */
	public abstract boolean getUseCRC();

	/**
	 * Use padding in ID3v2 tag?
	 * 
	 * @param use_padding
	 *            True: Use padding
	 */
	public abstract void setUsePadding(boolean use_padding);

	/**
	 * @return true if padding will be used in ID3v2 tag
	 */
	public abstract boolean getUsePadding();

	/**
	 * Use unsynchronization in ID3v2 tag?
	 * 
	 * @param use_unsynch
	 *            True: Use unsynchronization
	 */
	public abstract void setUseUnsynchronization(boolean use_unsynch);

	/**
	 * @return true if unsynchronization will be used in ID3v2 tag
	 */
	public abstract boolean getUseUnsynchronization();

	/**
	 * @return MPEG level (1 or 2)
	 */
	public abstract int getMPEGLevel();

	/**
	 * @return Layer (1..3)
	 */
	public abstract int getLayer();

	/**
	 * @return Bitrate
	 */
	public abstract int getBitrate();

	/**
	 * @return Samplerate
	 */
	public abstract int getSamplerate();

	/**
	 * Returns mode (mono, stereo, etc.) used in MP3 file Better use constants
	 * from MP3Properties.
	 * 
	 * @return Mode
	 */
	public abstract int getMode();

	/**
	 * Returns emphasis used in MP3 file Better use constants from
	 * MP3Properties.
	 * 
	 * @return Emphasis
	 */
	public abstract int getEmphasis();

	/**
	 * @return Protection (CRC) set
	 */
	public abstract boolean getProtection();

	/**
	 * @return Private bit set?
	 */
	public abstract boolean getPrivate();

	/**
	 * @return Padding set?
	 */
	public abstract boolean getPadding();

	/**
	 * @return Copyright set?
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

	/**
	 * Read album/movie/show title. Album is stored as text content.
	 * 
	 * @return Album
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getAlbum() throws FrameDamagedException;

	/**
	 * Set album. Album is read from text content.
	 * 
	 * @param album
	 *            Album to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setAlbum(TagContent album) throws TagFormatException;

	/**
	 * Read BPM. BPM is stored as text content.
	 * 
	 * @return BPM
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getBPM() throws FrameDamagedException;

	/**
	 * Set BPM. BPM is read from text content.
	 * 
	 * @param bpm
	 *            BPM to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setBPM(TagContent bpm) throws TagFormatException;

	/**
	 * Read composer(s), stored as text content.
	 * 
	 * @return composer(s)
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getComposer() throws FrameDamagedException;

	/**
	 * Set composer(s), read from text content.
	 * 
	 * @param composer
	 *            Composer(s) to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setComposer(TagContent composer)
			throws TagFormatException;

	/**
	 * Read genre (type of music like "Soul", "Rock", etc.) stored as text
	 * content. ID3v1.1 content is denoted by putting round brackets around the
	 * number (like (4)), round brackets in text are escaped by ((.
	 * 
	 * @return Album
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getGenre() throws FrameDamagedException;

	/**
	 * Set genre, read from text content. ID3v1.1 genre is denoted by putting
	 * round brackets around the number (like (4)), round brackets in text are
	 * escaped by ((.
	 * 
	 * @param genre
	 *            Genre to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setGenre(TagContent genre) throws TagFormatException;

	/**
	 * Read copyright, store as text content. According to the ID3v2 informal
	 * standard, this has to be preceded when displayed with "Copyright (C)"
	 * where (C) is one character showing a C in a circle...
	 * 
	 * @return Copyright
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getCopyrightText() throws FrameDamagedException;

	/**
	 * Set copyright, read from text content.
	 * 
	 * @param copyright
	 *            Copyright to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setCopyrightText(TagContent copyright)
			throws TagFormatException;

	/**
	 * Read date (format DDMM), store as text content.
	 * 
	 * @return date
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getDateTag() throws FrameDamagedException;

	/**
	 * Set date (format DDMM), read from text content.
	 * 
	 * @param date
	 *            Date to set
	 * @exception TagFormatException
	 *                If input does not adhere to the format given above.
	 */
	public abstract void setDate(TagContent date) throws TagFormatException;

	/**
	 * Read playlist delay, store as text content.
	 * 
	 * @return Playlist delay
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getPlaylistDelay() throws FrameDamagedException;

	/**
	 * Set playlist delay, read from text content.
	 * 
	 * @param delay
	 *            Playlist delay to set
	 * @exception TagFormatException
	 *                If input is not a numeric string
	 */
	public abstract void setPlaylistDelay(TagContent delay)
			throws TagFormatException;

	/**
	 * Read encoded by, store as text content.
	 * 
	 * @return Encoded by
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getEncodedBy() throws FrameDamagedException;

	/**
	 * Set encoded by, read from text content.
	 * 
	 * @param encoder
	 *            Encoded by to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setEncodedBy(TagContent encoder)
			throws TagFormatException;

	/**
	 * Read lyricist, store as text content.
	 * 
	 * @return Lyricist
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getLyricist() throws FrameDamagedException;

	/**
	 * Set lyricist, read from text content.
	 * 
	 * @param lyricist
	 *            Lyricist to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setLyricist(TagContent lyricist)
			throws TagFormatException;

	/**
	 * Read file type, store as text content.
	 * <p>
	 * The following types are defined (other types may be used)
	 * <ul>
	 * <li><code>MPG    </code>: MPEG Audio
	 * <li><code>MPG/1  </code>: MPEG 1/2 layer I</li>
	 * <li><code>MPG/2  </code>: MPEG 1/2 layer II</li>
	 * <li><code>MPG/3  </code>: MPEG 1/2 layer III</li>
	 * <li><code>MPG/2.5</code>: MPEG 2.5</li>
	 * <li><code>MPG/AAC</code>: Advanced audio compression</li>
	 * <li><code>VQF    </code>: Transform-domain weighted interleace vector
	 * quantization</li>
	 * <li><code>PCM    </code>: Pulse code modulated audio</li>
	 * </ul>
	 * <p>
	 * 
	 * @return File type
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getFileType() throws FrameDamagedException;

	/**
	 * Set file type, read from text content.
	 * 
	 * @param type
	 *            File type to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setFileType(TagContent type) throws TagFormatException;

	/**
	 * Read time (format HHMM), store as text content.
	 * 
	 * @return Time
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getTime() throws FrameDamagedException;

	/**
	 * Set time (format HHMM), read from text content.
	 * 
	 * @param time
	 *            Time to set
	 * @exception TagFormatException
	 *                If input does not adhere to the format given above.
	 */
	public abstract void setTime(TagContent time) throws TagFormatException;

	/**
	 * Read content group description, store as text content.
	 * <p>
	 * Content group description is used if sound belongs to a larger category
	 * of sounds, e.g. "Piano Concerto", "Weather - Hurricane")
	 * 
	 * @return Content group
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getContentGroup() throws FrameDamagedException;

	/**
	 * Set content group description, read from text content.
	 * <p>
	 * Content group description is used if sound belongs to a larger category
	 * of sounds, e.g. "Piano Concerto", "Weather - Hurricane")
	 * 
	 * @param content
	 *            Content group description to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setContentGroup(TagContent content)
			throws TagFormatException;

	/**
	 * Read song title, store as text content.
	 * 
	 * @return Song title
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getTitle() throws FrameDamagedException;

	/**
	 * Set title, read from text content.
	 * 
	 * @param title
	 *            Title to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setTitle(TagContent title) throws TagFormatException;

	/**
	 * Read subtitle, store as text content.
	 * <p>
	 * Subtitle is used for information directly related to the contents title
	 * (e.g. "Op. 16" or "Performed live at Wembley")
	 * 
	 * @return Subtitle
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getSubtitle() throws FrameDamagedException;

	/**
	 * Set subtitle, read from text content.
	 * <p>
	 * Content group description is used if sound belongs to a larger category
	 * of sounds, e.g. "Piano Concerto", "Weather - Hurricane")
	 * 
	 * @param subtitle
	 *            Subtitle to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setSubtitle(TagContent subtitle)
			throws TagFormatException;

	/**
	 * Read initial key
	 * <p>
	 * Musical key in which sound starts. String with max 3 characters, ground
	 * keys: A, B, C, D, E, F, G, halfkeys b and #. Minor: m, Off key: o
	 * 
	 * @return Initial key
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getInitialKey() throws FrameDamagedException;

	/**
	 * Set initial key, read from text content.
	 * <p>
	 * Musical key in which sound starts. String with max 3 characters, ground
	 * keys: A, B, C, D, E, F, G, halfkeys b and #. Minor: m, Off key: o
	 * 
	 * @param key
	 *            Initial key to set
	 * @exception TagFormatException
	 *                If key is longer than three characters
	 */
	public abstract void setInitialKey(TagContent key)
			throws TagFormatException;

	/**
	 * Read language of lyrics
	 * <p>
	 * Language is represented with three characters according to ISO-639-2.
	 * 
	 * @return Language
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getLanguage() throws FrameDamagedException;

	/**
	 * Set language of lyrics, read from text content.
	 * <p>
	 * Language is represented with three characters according to ISO-639-2.
	 * 
	 * @param lang
	 *            Language to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setLanguage(TagContent lang) throws TagFormatException;

	/**
	 * Read length of audiofile in milliseconds, store as text content.
	 * <p>
	 * This returns the length stored in the ID3v2 tag, not the length
	 * calculated from file length.
	 * 
	 * @return Length
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getLengthInTag() throws FrameDamagedException;

	/**
	 * Set length of audiofile in milliseconds, read from text content.
	 * 
	 * @param length
	 *            Length to set
	 * @exception TagFormatException
	 *                If input is not a numeric string
	 */
	public abstract void setLengthInTag(TagContent length)
			throws TagFormatException;

	/**
	 * Read media type, store as text content.
	 * <p>
	 * See the ID3v2 informal standard for more information.
	 * 
	 * @return Media type
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getMediaType() throws FrameDamagedException;

	/**
	 * Set media type, read from text content.
	 * <p>
	 * See the ID3v2 informal standard for more information.
	 * 
	 * @param type
	 *            Media type to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setMediaType(TagContent type)
			throws TagFormatException;

	/**
	 * Read original title (for cover songs), store as text content
	 * 
	 * @return Original title
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getOriginalTitle() throws FrameDamagedException;

	/**
	 * Set original title, read from text content.
	 * 
	 * @param title
	 *            Original title to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setOriginalTitle(TagContent title)
			throws TagFormatException;

	/**
	 * Read original filename, store as text content
	 * <p>
	 * Original filename is used to store prefered filename on media which does
	 * have limitations to the filename. It is stored including suffix.
	 * 
	 * @return Original filename
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getOriginalFilename()
			throws FrameDamagedException;

	/**
	 * Set original filename, read from text content.
	 * <p>
	 * Original filename is used to store prefered filename on media which have
	 * limitations to the filename. It is stored including suffix.
	 * 
	 * @param filename
	 *            Original filename to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setOriginalFilename(TagContent filename)
			throws TagFormatException;

	/**
	 * Read original lyricist(s) (for cover songs), store as text content
	 * 
	 * @return Original lyricist(s)
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getOriginalLyricist()
			throws FrameDamagedException;

	/**
	 * Set original lyricist(s), read from text content.
	 * 
	 * @param lyricist
	 *            Original lyricist(s) to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setOriginalLyricist(TagContent lyricist)
			throws TagFormatException;

	/**
	 * Read original artist(s) (for cover songs), store as text content
	 * 
	 * @return Original artist(s)
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getOriginalArtist() throws FrameDamagedException;

	/**
	 * Set original artist(s), read from text content.
	 * 
	 * @param artist
	 *            Original artist(s) to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setOriginalArtist(TagContent artist)
			throws TagFormatException;

	/**
	 * Read original release year (format YYYY) (for cover songs), store as text
	 * content
	 * 
	 * @return Original release year
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getOriginalYear() throws FrameDamagedException;

	/**
	 * Set original release year (format YYYY), read from text content.
	 * 
	 * @param year
	 *            Original year to set
	 * @exception TagFormatException
	 *                If input is not in the format listed above
	 */
	public abstract void setOriginalYear(TagContent year)
			throws TagFormatException;

	/**
	 * Read file owner, store as text content
	 * 
	 * @return File owner
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getFileOwner() throws FrameDamagedException;

	/**
	 * Set file owner, read from text content.
	 * 
	 * @param owner
	 *            File owner to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setFileOwner(TagContent owner)
			throws TagFormatException;

	/**
	 * Read artist, store as text content.
	 * 
	 * @return Artist
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getArtist() throws FrameDamagedException;

	/**
	 * Set artist, read from text content.
	 * 
	 * @param artist
	 *            Artist to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setArtist(TagContent artist) throws TagFormatException;

	/**
	 * Read band (orchestra, accompaniment), store as text content
	 * 
	 * @return Band
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getBand() throws FrameDamagedException;

	/**
	 * Set band, read from text content.
	 * 
	 * @param band
	 *            Band to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setBand(TagContent band) throws TagFormatException;

	/**
	 * Read conductor, store as text content
	 * 
	 * @return Conductor
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getConductor() throws FrameDamagedException;

	/**
	 * Set conductor, read from text content.
	 * 
	 * @param conductor
	 *            Conductor to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setConductor(TagContent conductor)
			throws TagFormatException;

	/**
	 * Read remixer, store as text content
	 * 
	 * @return Remixer
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getRemixer() throws FrameDamagedException;

	/**
	 * Set remixer, read from text content.
	 * 
	 * @param remixer
	 *            Remixer to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setRemixer(TagContent remixer)
			throws TagFormatException;

	/**
	 * Read part of a set (e.g. 1/2 for a double CD), store as text content
	 * 
	 * @return Part of a set
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getPartOfSet() throws FrameDamagedException;

	/**
	 * Set part of a set (e.g. 1/2 for a double CD), read from text content.
	 * 
	 * @param part
	 *            Part of a set to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setPartOfSet(TagContent part)
			throws TagFormatException;

	/**
	 * Read publisher, store as text content
	 * 
	 * @return Publisher
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getPublisher() throws FrameDamagedException;

	/**
	 * Set publisher, read from text content.
	 * 
	 * @param publisher
	 *            Publisher to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setPublisher(TagContent publisher)
			throws TagFormatException;

	/**
	 * Read track number, store in text content.
	 * 
	 * @return Track number
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getTrack() throws FrameDamagedException;

	/**
	 * Set track number, read from text content.
	 * 
	 * @param track
	 *            Track number to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setTrack(TagContent track) throws TagFormatException;

	/**
	 * Get recording dates, store as text content
	 * 
	 * @return Recording dates
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getRecordingDates() throws FrameDamagedException;

	/**
	 * Set recording date, read from text content.
	 * 
	 * @param date
	 *            Recording date
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setRecordingDate(TagContent date)
			throws TagFormatException;

	/**
	 * Get Internet radio station name, store as text content
	 * 
	 * @return Internet radio station name
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getInternetRadioStationName()
			throws FrameDamagedException;

	/**
	 * Set Internet radio station name, read from text content.
	 * 
	 * @param name
	 *            Internet radio station name
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setInternetRadioStationName(TagContent name)
			throws TagFormatException;

	/**
	 * Get Internet radio station owner, store as text content
	 * 
	 * @return Internet radio station owner
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getInternetRadioStationOwner()
			throws FrameDamagedException;

	/**
	 * Set Internet radio station owner, read from text content.
	 * 
	 * @param owner
	 *            Station owner
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setInternetRadioStationOwner(TagContent owner)
			throws TagFormatException;

	/**
	 * Get size of file in bytes, excluding id3v2 tag, store as text content
	 * 
	 * @return Size of File
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getFilesize() throws FrameDamagedException;

	/**
	 * Set size of files in bytes, excluding id3v2 tag, read from text content.
	 * 
	 * @param size
	 *            Size of file
	 * @exception TagFormatException
	 *                If input is not numeric
	 */
	public abstract void setFilesize(TagContent size) throws TagFormatException;

	/**
	 * Get International Standard Recording Code, store as text content
	 * 
	 * @return ISRC
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getISRC() throws FrameDamagedException;

	/**
	 * Set International Standard Recording Code, read from text content.
	 * 
	 * @param isrc
	 *            ISRC
	 * @exception TagFormatException
	 *                If input is not of 12 character's length
	 */
	public abstract void setISRC(TagContent isrc) throws TagFormatException;

	/**
	 * Get year of recording, store as text content
	 * 
	 * @return Recording dates
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getYear() throws FrameDamagedException;

	/**
	 * Set year of recording, read from text content.
	 * 
	 * @param year
	 *            Year of recording
	 * @exception TagFormatException
	 *                If input is not numeric or not 4 or 5 characters
	 */
	public abstract void setYear(TagContent year) throws TagFormatException;

	/**
	 * Read Commercial information webpage, store as text content
	 * 
	 * @return Commercial information
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getCommercialInformation()
			throws FrameDamagedException;

	/**
	 * Set Commercial information webpage, read from text content.
	 * 
	 * @param info
	 *            Commercial information to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setCommercialInformation(TagContent info)
			throws TagFormatException;

	/**
	 * Read Coypright / legal information webpage, store as text content
	 * 
	 * @return Copyright webpage
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getCopyrightWebpage()
			throws FrameDamagedException;

	/**
	 * Set Copyright / legal information webpage, read from text content.
	 * 
	 * @param copy
	 *            Copyright webpage to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setCopyrightWebpage(TagContent copy)
			throws TagFormatException;

	/**
	 * Read official audio file webpage, store as text content
	 * 
	 * @return Audio file webpage
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getAudioFileWebpage()
			throws FrameDamagedException;

	/**
	 * Set official audio file webpage, read from text content.
	 * 
	 * @param page
	 *            Official audio file webpage to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setAudioFileWebpage(TagContent page)
			throws TagFormatException;

	/**
	 * Read official artist / performer webpage, store as text content
	 * 
	 * @return Artist webpage
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getArtistWebpage() throws FrameDamagedException;

	/**
	 * Set official artist / performer webpage, read from text content.
	 * 
	 * @param page
	 *            Artist webpage webpage to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setArtistWebpage(TagContent page)
			throws TagFormatException;

	/**
	 * Read official audio source webpage, store as text content Used e.g. for
	 * movie soundtracks, then points to the movie
	 * 
	 * @return Audio source webpage
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getAudioSourceWebpage()
			throws FrameDamagedException;

	/**
	 * Set official audio source webpage, read from text content.
	 * 
	 * @param page
	 *            Official audio source webpage to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setAudioSourceWebpage(TagContent page)
			throws TagFormatException;

	/**
	 * Read official internet radio station webpage, store as text content
	 * 
	 * @return Internet radio station webpage
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getInternetRadioStationWebpage()
			throws FrameDamagedException;

	/**
	 * Set official internet radio station webpage, read from text content.
	 * 
	 * @param page
	 *            Official internet radio station webpage to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setInternetRadioStationWebpage(TagContent page)
			throws TagFormatException;

	/**
	 * Read payment webpage, store as text content
	 * 
	 * @return Payment webpage
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getPaymentWebpage() throws FrameDamagedException;

	/**
	 * Set payment webpage, read from text content.
	 * 
	 * @param page
	 *            Payment webpage to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setPaymentWebpage(TagContent page)
			throws TagFormatException;

	/**
	 * Read official publishers webpage, store as text content
	 * 
	 * @return Publishers webpage
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getPublishersWebpage()
			throws FrameDamagedException;

	/**
	 * Set official publishers webpage, read from text content.
	 * 
	 * @param page
	 *            Official publishers webpage to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setPublishersWebpage(TagContent page)
			throws TagFormatException;

	/**
	 * Read event timing codes, store as binary content See the ID3v2 informal
	 * standard for details on the format of this field.
	 * 
	 * @return Event timing codes
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getEventTimingCodes()
			throws FrameDamagedException;

	/**
	 * Set event timing codes, read from binary content.
	 * 
	 * @param codes
	 *            Timing codes to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setEventTimingCodes(TagContent codes)
			throws TagFormatException;

	/**
	 * Read MPEG location lookup table, store as binary content See the ID3v2
	 * informal standard for details on the format of this field.
	 * 
	 * @return Lookup table
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getLookupTable() throws FrameDamagedException;

	/**
	 * Set MPEG location lookup table, read from binary content. See the ID3v2
	 * informal standard for details on the format of this field.
	 * 
	 * @param table
	 *            Location lookup table to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setLookupTable(TagContent table)
			throws TagFormatException;

	/**
	 * Read synchronized tempo codes, store as binary content See the ID3v2
	 * informal standard for details on the format of this field.
	 * 
	 * @return Synchronized tempo codes
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getSynchronizedTempoCodes()
			throws FrameDamagedException;

	/**
	 * Set synchronized tempo codes, read from binary content. See the ID3v2
	 * informal standard for details on the format of this field.
	 * 
	 * @param codes
	 *            Synchronized tempo codes to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setSynchronizedTempoCodes(TagContent codes)
			throws TagFormatException;

	/**
	 * Read synchronized lyrics, store as binary content See the ID3v2 informal
	 * standard for details on the format of this field.
	 * 
	 * @return Synchronized lyrics
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getSynchronizedLyrics()
			throws FrameDamagedException;

	/**
	 * Set synchronized lyrics, read from binary content. See the ID3v2 informal
	 * standard for details on the format of this field.
	 * 
	 * @param lyrics
	 *            Synchronized lyrics
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setSynchronizedLyrics(TagContent lyrics)
			throws TagFormatException;

	/**
	 * Read relative volume adjustment, store as binary content See the ID3v2
	 * informal standard for details on the format of this field.
	 * 
	 * @return Relative volume adjustment
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getRelativeVolumenAdjustment()
			throws FrameDamagedException;

	/**
	 * Set relative volume adjustment, read from binary content. See the ID3v2
	 * informal standard for details on the format of this field.
	 * 
	 * @param adjust
	 *            Relative volume adjustment to set
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setRelativeVolumeAdjustment(TagContent adjust)
			throws TagFormatException;

	/**
	 * Read equalisation, store as binary content See the ID3v2 informal
	 * standard for details on the format of this field.
	 * 
	 * @return Equalisation
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getEqualisation() throws FrameDamagedException;

	/**
	 * Set equalisation, read from binary content. See the ID3v2 informal
	 * standard for details on the format of this field.
	 * 
	 * @param equal
	 *            Equalisation
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setEqualisation(TagContent equal)
			throws TagFormatException;

	/**
	 * Read reverb, store as binary content See the ID3v2 informal standard for
	 * details on the format of this field.
	 * 
	 * @return Reverb
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getReverb() throws FrameDamagedException;

	/**
	 * Set reverb, read from binary content. See the ID3v2 informal standard for
	 * details on the format of this field.
	 * 
	 * @param reverb
	 *            Reverb
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setReverb(TagContent reverb) throws TagFormatException;

	/**
	 * Read play counter, store as binary content See the ID3v2 informal
	 * standard for details on the format of this field.
	 * 
	 * @return Play counter
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getPlayCounter() throws FrameDamagedException;

	/**
	 * Set play counter, read from binary content. See the ID3v2 informal
	 * standard for details on the format of this field.
	 * 
	 * @param count
	 *            Play counter
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setPlayCounter(TagContent count)
			throws TagFormatException;

	/**
	 * Read popularimeter, store as binary content See the ID3v2 informal
	 * standard for details on the format of this field.
	 * 
	 * @return Popularimeter
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getPopularimeter() throws FrameDamagedException;

	/**
	 * Set popularimeter, read from binary content. See the ID3v2 informal
	 * standard for details on the format of this field.
	 * 
	 * @param pop
	 *            Popularimeter
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setPopularimeter(TagContent pop)
			throws TagFormatException;

	/**
	 * Read recommended buffer size, store as binary content See the ID3v2
	 * informal standard for details on the format of this field.
	 * 
	 * @return Recommended buffer size
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getRecommendedBufferSize()
			throws FrameDamagedException;

	/**
	 * Set recommended buffer size, read from binary content. See the ID3v2
	 * informal standard for details on the format of this field.
	 * 
	 * @param size
	 *            Recommended buffer size
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setRecommendedBufferSize(TagContent size)
			throws TagFormatException;

	/**
	 * Read position synchronization, store as binary content See the ID3v2
	 * informal standard for details on the format of this field.
	 * 
	 * @return Position synchronization
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getPositionSynchronization()
			throws FrameDamagedException;

	/**
	 * Set position synchronization, read from binary content. See the ID3v2
	 * informal standard for details on the format of this field.
	 * 
	 * @param synch
	 *            Position synchronization
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setPositionSynchronization(TagContent synch)
			throws TagFormatException;

	/**
	 * Read ownership, store as binary content See the ID3v2 informal standard
	 * for details on the format of this field.
	 * 
	 * @return Ownership
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getOwnership() throws FrameDamagedException;

	/**
	 * Set ownership, read from binary content. See the ID3v2 informal standard
	 * for details on the format of this field.
	 * 
	 * @param owner
	 *            Ownership
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setOwnership(TagContent owner)
			throws TagFormatException;

	/**
	 * Read commercial frame, store as binary content See the ID3v2 informal
	 * standard for details on the format of this field.
	 * 
	 * @return Commercial frame
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getCommercial() throws FrameDamagedException;

	/**
	 * Set commercial frame, read from binary content. See the ID3v2 informal
	 * standard for details on the format of this field.
	 * 
	 * @param commercial
	 *            Commercial frame
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setCommercial(TagContent commercial)
			throws TagFormatException;

	/**
	 * Read Music CD identifier, store as binary content
	 * 
	 * @return Music CD identifier
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getCDIdentifier() throws FrameDamagedException;

	/**
	 * Set music CD identifier, read from binary content.
	 * 
	 * @param ident
	 *            CD identifier
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setCDIdentifier(TagContent ident)
			throws TagFormatException;

	/**
	 * Read unique file identifier. Owner identifier is stored as description,
	 * identifier as binary content.
	 * 
	 * @return Unique file identifier
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getUniqueFileIdentifier()
			throws FrameDamagedException;

	/**
	 * Set unique file identifier. Owner identifier is read from description,
	 * identifier from binary content.
	 * 
	 * @param ufi
	 *            Unique file identifier to set.
	 * @exception TagFormatException
	 *                If File identifier is longer than 64 characters
	 */
	public abstract void setUniqueFileIdentifier(TagContent ufi)
			throws TagFormatException;

	/**
	 * Read user defined text, store description as description and value as
	 * text content
	 * 
	 * @return User defined text
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getUserDefinedText()
			throws FrameDamagedException;

	/**
	 * Set user defined text information. Description is read from description,
	 * value from text content.
	 * 
	 * @param info
	 *            User defined text information
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setUserDefinedText(TagContent info)
			throws TagFormatException;

	/**
	 * Read user defined URL, store description as description and URL as text
	 * content
	 * 
	 * @return User defined URL link
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getUserDefinedURL() throws FrameDamagedException;

	/**
	 * Set user defined URL link. Description is read from description, URL from
	 * text content.
	 * 
	 * @param link
	 *            User defined URL link
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setUserDefinedURL(TagContent link)
			throws TagFormatException;

	/**
	 * Read unsynchronized lyrics, store language as type, description as
	 * description and lyrics as text content
	 * 
	 * @return Unsynchronized lyrics
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getUnsynchronizedLyrics()
			throws FrameDamagedException;

	/**
	 * Set unsynchronized lyrics. Language is read from type, Description from
	 * description, lyrics from text content.
	 * 
	 * @param lyric
	 *            Unsynchronized lyrics
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setUnsynchronizedLyrics(TagContent lyric)
			throws TagFormatException;

	/**
	 * Read comments, store language as type, description as description and
	 * comments as text content
	 * 
	 * @return Comments
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getComments() throws FrameDamagedException;

	/**
	 * Set comments. Language is read from type, Description from description,
	 * comments from text content.
	 * 
	 * @param comm
	 *            Comments
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setComments(TagContent comm) throws TagFormatException;

	/**
	 * Read attached picture, store MIME type as type, picture type as binary
	 * subtype, description as description and picture data as binary content.
	 * 
	 * @return Picture
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getPicture() throws FrameDamagedException;

	/**
	 * Set attached picture. MIME type is read from type, picture type from
	 * binary subtype, description from description, picture data from binary
	 * content.
	 * 
	 * @param pic
	 *            Picture
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setPicture(TagContent pic) throws TagFormatException;

	/**
	 * Read general encapsulated object, store MIME type as type, filename as
	 * text subtype, description as description and object as binary content.
	 * 
	 * @return Object
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getEncapsulatedObject()
			throws FrameDamagedException;

	/**
	 * Set general encapsulated object. MIME type is read from type, filename
	 * from subtype, description from description, object from binary content.
	 * 
	 * @param obj
	 *            Object
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setEncapsulatedObject(TagContent obj)
			throws TagFormatException;

	/**
	 * Read terms of use, store language as type and terms of use as text
	 * content
	 * 
	 * @return Terms of use
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getTermsOfUse() throws FrameDamagedException;

	/**
	 * Set terms of use. Language is read from type, terms of use from text
	 * content.
	 * 
	 * @param use
	 *            Terms of use
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setTermsOfUse(TagContent use)
			throws TagFormatException;

	/**
	 * Read encryption method registration, store owner identifier as type,
	 * method symbol as binary subtype and encryption data as binary content.
	 * 
	 * @return Encryption method registration
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getEncryptionMethodRegistration()
			throws FrameDamagedException;

	/**
	 * Set encryption method registration. Owner identifier is read from type,
	 * method symbol from binary subtype and encryption data from binary
	 * content.
	 * 
	 * @param encr
	 *            Encryption method
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setEncryptionMethodRegistration(TagContent encr)
			throws TagFormatException;

	/**
	 * Read group identification registration, store owner identifier as type,
	 * group symbol as binary subtype and group dependent data as binary
	 * content.
	 * 
	 * @return Group identification registration
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getGroupIdentificationRegistration()
			throws FrameDamagedException;

	/**
	 * Set group identification registration. Owner identifier is read from
	 * type, group symbol from binary subtype and group dependent data from
	 * binary content.
	 * 
	 * @param grp
	 *            Group identification
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setGroupIdentificationRegistration(TagContent grp)
			throws TagFormatException;

	/**
	 * Read private data, store owner identifier as type, private data as binary
	 * content.
	 * 
	 * @return Private data
	 * @exception FrameDamagedException
	 *                If frame is damaged (e.g. too short)
	 */
	public abstract TagContent getPrivateData() throws FrameDamagedException;

	/**
	 * Set private data. Owner identifier is read from type, private data from
	 * binary content.
	 * 
	 * @param data
	 *            Private data
	 * @exception TagFormatException
	 *                If information to set is not correctly formatted
	 */
	public abstract void setPrivateData(TagContent data)
			throws TagFormatException;

}