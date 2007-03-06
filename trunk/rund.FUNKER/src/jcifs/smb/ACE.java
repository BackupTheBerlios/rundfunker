package jcifs.smb;

import jcifs.util.Hexdump;

public class ACE {

    public static final int FILE_READ_DATA        = 0x00000001; // 1
    public static final int FILE_WRITE_DATA       = 0x00000002; // 2
    public static final int FILE_APPEND_DATA      = 0x00000004; // 3
    public static final int FILE_READ_EA          = 0x00000008; // 4
    public static final int FILE_WRITE_EA         = 0x00000010; // 5
    public static final int FILE_EXECUTE          = 0x00000020; // 6
    public static final int FILE_DELETE           = 0x00000040; // 7
    public static final int FILE_READ_ATTRIBUTES  = 0x00000080; // 8
    public static final int FILE_WRITE_ATTRIBUTES = 0x00000100; // 9
    public static final int DELETE                = 0x00010000; // 16
    public static final int READ_CONTROL          = 0x00020000; // 17
    public static final int WRITE_DAC             = 0x00040000; // 18
    public static final int WRITE_OWNER           = 0x00080000; // 19
    public static final int SYNCHRONIZE           = 0x00100000; // 20
    public static final int GENERIC_ALL           = 0x10000000; // 28
    public static final int GENERIC_EXECUTE       = 0x20000000; // 29
    public static final int GENERIC_WRITE         = 0x40000000; // 30
    public static final int GENERIC_READ          = 0x80000000; // 31

    boolean allow;
    int flags;
    int access;
    SID sid;

    public boolean isAllow() {
        return allow;
    }
    public boolean isInherited() {
        return (flags & 0x10) != 0;
    }
    public int getAccessMask() {
        return access;
    }
    public SID getSID() {
        return sid;
    }

    int decode( byte[] buf, int bi ) {
        allow = buf[bi++] == (byte)0x00;
        flags = buf[bi++] & 0xFF;
        int size = ServerMessageBlock.readInt2(buf, bi);
        bi += 2;
        access = ServerMessageBlock.readInt4(buf, bi);
        bi += 4;
        sid = new SID(buf, bi);
        return size;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( isInherited() ? "inherited " : "direct    " );
        sb.append( isAllow() ? "allow " : "deny  " );
        sb.append( "0x" ).append( Hexdump.toHexString( access, 8 ));
        sb.append( " " ).append( sid.toString() );
        return sb.toString();
    }
}
