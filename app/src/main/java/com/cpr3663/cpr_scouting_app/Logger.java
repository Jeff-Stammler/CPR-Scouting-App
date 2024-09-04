package com.cpr3663.cpr_scouting_app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Pair;

// =============================================================================================
// Class:       Logger
// Description: Sets up how all of the scouting data will be logged to disk.
// Methods:     LogEvent()
//                  used for logging time-based events.  Keep track of previous events, with
//                  special handling of Defense and Defended events.
//              LogData()
//                  used for logging a specific non-time-based scouting piece of data
//              close()
//                  finish logging any/all events, and flush/close the log files
// =============================================================================================
public class Logger {
    private static File file_data;
    private static File file_event;
    private static String filename_data;
    private static String filename_event;
    private static FileOutputStream fos_data;
    private static FileOutputStream fos_event;
    private static int seq_number = 0; // Track the current sequence number for events
    private static int seq_number_prev_common = 0; // Track previous sequence number for all common events
    private static int seq_number_prev_defended = 0; // Track previous sequence number for just defended toggle
    private static int seq_number_prev_defense = 0; // Track previous sequence number for just defense toggle
    private static final ArrayList<Pair<String, String>> match_data = new ArrayList<Pair<String, String>>();

    // Constructor: create the new files
    public Logger(Context in_context) throws IOException {
        String path = in_context.getResources().getString(R.string.logger_path);

        // Ensure the path (if it's not blank) has a trailing delimiter
        if (!path.isEmpty()) {
            if (!path.endsWith("/")) path = path + "/";
        }

        // Define the filenames/files to be used for this logger
        filename_data =  path + Globals.CurrentCompetitionId + "_" + Globals.CurrentMatchNumber + "_" + Globals.CurrentDeviceId + "_d.csv";
        filename_event = path + Globals.CurrentCompetitionId + "_" + Globals.CurrentMatchNumber + "_" + Globals.CurrentDeviceId + "_e.csv";

        File file_data = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), filename_data);
        File file_event = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), filename_event);

        // Ensure the directory structure exists first - only need to do one
        file_data.getParentFile().mkdirs();

        // TODO: read in the list of files and delete the excess file(s)

        // If the output file doesn't exist, output a stream to it and copy contents over
        if (!file_data.exists()) file_data.createNewFile();
        if (!file_event.exists()) file_event.createNewFile();

        try {
            fos_data = new FileOutputStream(file_data);
            fos_event = new FileOutputStream(file_event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Member Function: Close out the logger.  Write out all of the non-time based match data and close the files.
    public void close(){
        boolean found = false;

        try {
            // Start the csv line with the event key
            String csv_line = Globals.CurrentCompetitionId + ":" + Globals.CurrentMatchNumber + ":" + Globals.CurrentDeviceId;

            // Append to the csv line the values in the correct order
            csv_line += FindValueInPair(Constants.LOGKEY_TEAM_TO_SCOUT);
            csv_line += FindValueInPair(Constants.LOGKEY_TEAM_SCOUTING);
            csv_line += FindValueInPair(Constants.LOGKEY_SCOUTER);
            csv_line += FindValueInPair(Constants.LOGKEY_DID_PLAY);
            csv_line += FindValueInPair(Constants.LOGKEY_START_POSITION);
            csv_line += FindValueInPair(Constants.LOGKEY_DID_LEAVE_START);
            csv_line += FindValueInPair(Constants.LOGKEY_CLIMB_POSITION);
            csv_line += FindValueInPair(Constants.LOGKEY_TRAP);
            csv_line += FindValueInPair(Constants.LOGKEY_DNPS);
            csv_line += FindValueInPair(Constants.LOGKEY_COMMENTS);
            csv_line += FindValueInPair(Constants.LOGKEY_START_TIME_OFFSET);

            // Write out the data
            fos_data.write(csv_line.getBytes(StandardCharsets.UTF_8));
            fos_data.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));

            fos_event.flush();
            fos_event.close();
            fos_data.flush();
            fos_data.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Member Function: Find the correct data in the Key/Value Pair variable
    private String FindValueInPair(String in_Key) {
        String ret = ",";

        // loop through the pairs and stop if you find a key match.  Append the value if found.
        for(Pair<String, String> p : match_data) {
            if (p.first.equals(in_Key)) {
                ret += p.second;
                break;
            }
        }

        return ret;
    }

    // Member Function: Log a time-based event
    public void LogEvent(int in_EventId, float in_X, float in_Y, boolean in_NewSequence, double in_time) {
        int seq_number_prev = 0;

        // We need to special case the toggle switches.  We must preserve their own "previous" eventID but still
        // keep the sequence numbers going.
        switch (in_EventId) {
            case Constants.EVENT_ID_DEFENDED_START:
                seq_number_prev_defended = ++seq_number;
                break;
            case Constants.EVENT_ID_DEFENDED_END:
                seq_number_prev = seq_number_prev_defended;
                seq_number++;
                break;
            case Constants.EVENT_ID_DEFENSE_START:
                seq_number_prev_defense = ++seq_number;
                break;
            case Constants.EVENT_ID_DEFENSE_END:
                seq_number_prev = seq_number_prev_defense;
                seq_number++;
                break;
            default:
                seq_number_prev = seq_number_prev_common;
                seq_number_prev_common = ++seq_number;
        }

        String prev="";
        String seq = Globals.CurrentCompetitionId + ":" + Globals.CurrentMatchNumber + ":" + Globals.CurrentDeviceId + ":" + seq_number;
        String csv_line;

        // If this is NOT a new sequence, we need to write out the previous event id that goes with this one
        if (!in_NewSequence) prev = String.valueOf(seq_number_prev);

        // Form the output line that goes in the csv file.  Round X,Y to 2 decimal places.
        csv_line = seq + "," + in_EventId + "," + String.valueOf((float)(Math.round((in_time - Match.startTime) / 100.0)) / 100.0) + "," + String.valueOf((float)(Math.round(in_X * 100.0)) / 100.0) + "," + String.valueOf((float)(Math.round(in_Y * 100.0)) / 100.0) + "," + prev;
        try {
            fos_event.write(csv_line.getBytes(StandardCharsets.UTF_8));
            fos_event.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Member Function: Log a time-based event (with no time passed in)
    public void LogEvent(int in_EventId, float in_X, float in_Y, boolean in_NewSequence){
        LogEvent(in_EventId, in_X, in_Y, in_NewSequence, System.currentTimeMillis());
    }

    // Member Function: Log a non-time based event - just store this for later.
    public void LogData(String in_Key, String in_Value) {
        match_data.add(new Pair<String, String>(in_Key, in_Value));
    }
}