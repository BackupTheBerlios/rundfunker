package de.fhaugsburg.rundfunker.rundspieler.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.fhaugsburg.rundfunker.rundspieler.DataException;
import de.fhaugsburg.rundfunker.rundspieler.EndOfPlaylistException;
import de.fhaugsburg.rundfunker.rundspieler.Playlist;
import de.fhaugsburg.rundfunker.rundspieler.SearchFilter;
import de.fhaugsburg.rundfunker.rundspieler.SearchFilterImpl;
import de.fhaugsburg.rundfunker.rundsucher.Config;
import de.fhaugsburg.rundfunker.rundsucher.Song;

public class PlaylistDBImpl implements Playlist {

    public static final String ORDERBY = "artist, album, tracknumber, title";
	private Connection conn;

	private SearchFilter filter;

	private ResultSet allSongs;

	private int currentSongIndex = -1;

	private PreparedStatement allSongsPS;

	public PlaylistDBImpl() throws SQLException {

		conn = Config.getInstance().createCon();
		Statement s = conn.createStatement();
		ResultSet rs = s
				.executeQuery("SELECT filterType, filterString, songIndex FROM rf_player_playlist WHERE id=1");
		rs.next();
		String filterType = rs.getString("filterType");
		currentSongIndex = rs.getInt("songIndex");
		if (rs.wasNull()) {
			filterType = "artist";
		}
		String filterString = rs.getString("filterString");
		if (rs.wasNull()) {
			filterString = "";
		}
		filter = new SearchFilterImpl();
		filter.setFilter(filterType);
		filter.setSearchString(filterString);

		rs.close();
		s.close();

		initPlaylist();
	}

	/**
	 * Playlist initialisieren
	 *
	 * @throws SQLException
	 */
	private void initPlaylist() throws SQLException {

		allSongsPS = conn.prepareStatement("SELECT IFNULL("
				+ filter.getFilter() + ",' ') as ?"
				+ getAllesAusser(filter.getFilter())
				+ " FROM rf_song ORDER BY ?,"+ORDERBY);
		allSongsPS.setString(1, filter.getFilter());
		allSongsPS.setString(2, filter.getFilter());

		allSongs = allSongsPS.executeQuery();
		if (currentSongIndex < 0) {
			currentSongIndex = 0;
			// allSongs.next();
			while (allSongs.next()
					&& !(allSongs.getString(filter.getFilter()).toLowerCase()
							.startsWith(filter.getSearchString().toLowerCase()))) {
				currentSongIndex++;
			}
			setCurrentSongIndex(currentSongIndex);

		} else {
			for (int i = 0; i <= currentSongIndex; i++) {
				allSongs.next();
			}
		}
	}

	private String getAllesAusser(String type) {
		String s = ",artist,genre,title,album,filename,songSeconds,filenameHash,";
		int index = s.indexOf(type);
		s = s.substring(0, index)
				+ s.substring(index + type.length() + 1, s.length() - 1);
		return s;
	}

	// public void skipSongs(int delta) throws EndOfPlaylistException {
	// setCurrentSongIndex(getCurrentSongIndex() + delta);
	// // Song song = getSong(getCurrentSongIndex());
	// // return song;
	// }

	public void play(SearchFilter sf) throws DataException {
		try {
			PreparedStatement ps = conn
					.prepareStatement("UPDATE rf_player_playlist "
							+ "SET filterType= ?, filterString=? WHERE Id=1;");
			ps.setString(1, sf.getFilter());
			ps.setString(2, sf.getSearchString());
			ps.executeUpdate();

			ps.close();
			filter = sf;
			currentSongIndex = -1;
			initPlaylist();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataException(
					"Datenbank Fehler. Prüfen Sie die Dantenbankverbindung");
		}

	}

	public Song getCurrentSong() throws DataException {
		try {
			if (allSongs == null || allSongs.isAfterLast()) {
				throw new DataException("Song kann nicht gefunden werden.");
			}
			Song song = new Song();
			song.setTitle(allSongs.getString("title"));
			song.setArtist(allSongs.getString("artist"));
			song.setFilename(allSongs.getString("filename"));
			song.setAlbum(allSongs.getString("album"));
			song.setLengthSeconds(allSongs.getInt("songSeconds"));
			song.setFilenameHash(allSongs.getLong("filenameHash"));

			return song;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataException("Datenbankfehler.");
		}
	}

	private void setCurrentSongIndex(int index) throws SQLException{
		Statement s= conn.createStatement();
		s.executeUpdate("UPDATE rf_player_playlist " + "SET songIndex= "
				+ index + " WHERE Id=1;");
		s.close();
	}

	public SearchFilter getFilter() {
		return filter;
	}

	public void disconnet() throws SQLException {
		allSongs.close();
		allSongsPS.close();
		conn.close();
	}

	public void nextSong() throws EndOfPlaylistException, DataException {
		try {
			if (!allSongs.next()) {
				allSongs.previous();
				throw new EndOfPlaylistException();
			}
			setCurrentSongIndex(currentSongIndex+1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void prevSong() throws EndOfPlaylistException, DataException {
		try {
			if (!allSongs.previous()) {
				allSongs.next();
				throw new EndOfPlaylistException();
			}
			setCurrentSongIndex(currentSongIndex-1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
