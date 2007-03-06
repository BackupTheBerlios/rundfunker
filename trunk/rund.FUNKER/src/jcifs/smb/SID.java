package jcifs.smb;

import java.util.*;
import jcifs.util.Hexdump;

public class SID {

    int revision;
    int sub_authority_count;
    byte[] identifier_authority = new byte[6];
    int[] sub_authority;

    public SID( byte[] src, int si ) {
        revision = src[si++];
        sub_authority_count = src[si++];
        System.arraycopy(src, si, identifier_authority, 0, 6);
        si += 6;
        if (sub_authority_count > 100)
            throw new RuntimeException( "Invalid SID" );
        sub_authority = new int[sub_authority_count];
        for (int i = 0; i < sub_authority_count; i++) {
            sub_authority[i] = ServerMessageBlock.readInt4( src, si );
            si += 4;
        }
    }

    public String toString() {
        String ret = "S-" + revision + "-";

        if (identifier_authority[0] != (byte)0 || identifier_authority[1] != (byte)0) {
            ret += "0x";
            ret += Hexdump.toHexString(identifier_authority, 0, 6);
        } else {
            long shift = 0;
            long id = 0;
            for (int i = 5; i > 1; i--) {
                id += (identifier_authority[i] & 0xFFL) << shift;
                shift += 8;
            }
            ret += id;
        }

        for (int i = 0; i < sub_authority_count ; i++)
            ret += "-" + (sub_authority[i] & 0xFFFFFFFFL);

        return ret;
    }
}

