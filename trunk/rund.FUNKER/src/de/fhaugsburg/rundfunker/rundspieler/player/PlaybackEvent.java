package de.fhaugsburg.rundfunker.rundspieler.player;

public class PlaybackEvent
{
        public static int STOPPED = 1;
        public static int STARTED = 2;
        public static int ERROROCURRED=3;

        private Object source;
        private int frame;
        private int id;

        public PlaybackEvent(Object source, int id, int frame)
        {
                this.id = id;
                this.source = source;
                this.frame = frame;
        }

        public int getId(){return id;}
        public void setId(int id){this.id = id;}

        public int getFrame(){return frame;}
        public void setFrame(int frame){this.frame = frame;}

        public Object getSource(){return source;}
        public void setSource(Object source){this.source = source;}

}
