package com.someone.someone_v2;

import android.content.Context;
import android.media.MediaPlayer;

class AudioPlayer {

    private MediaPlayer mMediaPlayer;

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(Context c, int rid, final AudioPlayerEvent audioPlayerEvent) {
        stop();

        mMediaPlayer = MediaPlayer.create(c, rid);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
                if (audioPlayerEvent != null)
                    audioPlayerEvent.onCompleted();
            }
        });

        mMediaPlayer.start();
    }

    public int getAudioSessionId() {
        if (mMediaPlayer == null)
            return -1;
        return mMediaPlayer.getAudioSessionId();
    }

    public interface AudioPlayerEvent {
        void onCompleted();
    }
}
