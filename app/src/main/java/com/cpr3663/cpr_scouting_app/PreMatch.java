package com.cpr3663.cpr_scouting_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cpr3663.cpr_scouting_app.data.Matches;
import com.cpr3663.cpr_scouting_app.databinding.PreMatchBinding;

import java.io.IOException;

public class PreMatch extends AppCompatActivity {
    // =============================================================================================
    // Global variables
    // =============================================================================================
    private PreMatchBinding preMatchBinding;
    // To store the inputted name
    protected static String ScouterName;
    protected static CheckBox checkbox_StartNote; // This needs to be global so that Match.java can access it
    private static String[] Start_Positions = Globals.StartPositionList.getDescriptionList();

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        preMatchBinding = PreMatchBinding.inflate(getLayoutInflater());
        setContentView(preMatchBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(preMatchBinding.preMatch, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Now that we are starting to scout data, set the Global values
        if (Globals.sp == null) Globals.sp = this.getSharedPreferences(getString(R.string.preference_setting_file_key), Context.MODE_PRIVATE);
        Globals.CurrentScoutingTeam = Globals.sp.getInt(Constants.SP_SCOUTING_TEAM, 0);
        Globals.CurrentCompetitionId = Globals.sp.getInt(Constants.SP_COMPETITION_ID, 0);
        Globals.CurrentDeviceId = Globals.sp.getInt(Constants.SP_DEVICE_ID, 0);
        Globals.CurrentColorId = Globals.sp.getInt(Constants.SP_COLOR_CONTEXT_MENU, 1);

        // Create components
        EditText edit_Match = preMatchBinding.editMatch;
        Spinner spinner_Team = preMatchBinding.spinnerTeamToScout;
        Spinner spinner_StartPos = preMatchBinding.spinnerStartingPosition;
        TextView text_TeamName = preMatchBinding.textTeamToScoutName;

        // adds the items from the starting positions array to the list
        ArrayAdapter<String> adp_StartPos = new ArrayAdapter<String>(this, R.layout.cpr_spinner, Start_Positions);
        adp_StartPos.setDropDownViewResource(R.layout.cpr_spinner_item);
        spinner_StartPos.setAdapter(adp_StartPos);
        // Search through the list of Start Positions till you find the one that is correct then get its position in the list
        //  and set that one as selected
        int start_Pos_DropId = 0;
        for (int i = 0; i < Start_Positions.length; i++) {
            if (Start_Positions[i] == Globals.StartPositionList.getStartPositionDescription(Globals.CurrentStartPosition)) {
                start_Pos_DropId = i;
                break;
            }
        }
        spinner_StartPos.setSelection(start_Pos_DropId);

        // adds teams in match to the spinner
        // Create an ArrayAdapter using the string array and a default spinner layout.
        int[] teams = Globals.MatchList.getMatchInfoRow(Globals.CurrentMatchNumber).getListOfTeams();
        String[] teams_Str = new String[teams.length];
        for (int i = 0; i < teams.length; i++) {
            teams_Str[i] = String.valueOf(teams[i]);
        }
        if (teams_Str[0].equals("0") || teams_Str[0].equals(null)) {
            teams_Str = new String[]{getString(R.string.dropdown_no_items)};
        }
        ArrayAdapter<String> adp_Team = new ArrayAdapter<String>(this, R.layout.cpr_spinner, teams_Str);
        adp_Team.setDropDownViewResource(R.layout.cpr_spinner_item);
        spinner_Team.setAdapter(adp_Team);
        int team_DropId = 0;
        if (!teams_Str[0].equals(getString(R.string.dropdown_no_items))) {
            for (int i = 0; i < teams.length; i++) {
                if (teams[i] == Globals.CurrentTeamToScout) {
                    team_DropId = i;
                    break;
                }
            }
        }
        // Run it from a handler because it doesn't like to work and it will just do absolutely nothing if you don't
        // Create a new variable so it wont change before the handler is called cause that will mess it up (Even though its only 1 millisecond)
        int finalTeam_DropId = team_DropId;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                spinner_Team.setSelection(finalTeam_DropId);
            }
        }, 1);

        Button but_AddOverrideTeamNum = preMatchBinding.butAddOverrideTeamNum;
        CheckBox checkbox_DidPlay = preMatchBinding.checkboxDidPlay;
        checkbox_StartNote = preMatchBinding.checkboxStartNote;
        CheckBox checkbox_Override = preMatchBinding.checkboxOverride;
        CheckBox checkbox_ReSubmit = preMatchBinding.checkboxResubmit;
        EditText edit_OverrideTeamNum = preMatchBinding.editOverrideTeamNum;
        EditText edit_Name = preMatchBinding.editScouterName;
        TextView text_Override = preMatchBinding.textOverride;

        // Since we are putting the checkbox on the RIGHT side of the text, the checkbox doesn't honor padding.
        // So we need to use 7 spaces, but you can't when using a string resource (it ignores the trailing spaces)
        // So add it in now.
        checkbox_DidPlay.setText(checkbox_DidPlay.getText() + Globals.CheckBoxTextPadding);
        checkbox_StartNote.setText(checkbox_StartNote.getText() + Globals.CheckBoxTextPadding);
        checkbox_ReSubmit.setText(checkbox_ReSubmit.getText() + Globals.CheckBoxTextPadding);

        // Create a text box to input the scouters name
        edit_Name.setText(ScouterName);

        if (Globals.CurrentMatchNumber > 0) {
            // MUST CONVERT TO STRING or it crashes with out warning
            edit_Match.setText(String.valueOf(Globals.CurrentMatchNumber));
            if (Globals.CurrentMatchNumber < Globals.MatchList.size()) {
                Matches.MatchRow Match = Globals.MatchList.getMatchInfoRow(Globals.CurrentMatchNumber);
                if (Match != null) {
                    int[] Teams = Match.getListOfTeams();
                    String[] teamsInMatch = new String[Teams.length];
                    for (int i = 0; i < Teams.length; i++) {
                        teamsInMatch[i] = String.valueOf(Teams[i]);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teamsInMatch);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner.
                    spinner_Team.setAdapter(adapter);
                }
            }
        } else edit_Match.setText("");

        // Default checkboxes
        checkbox_DidPlay.setChecked(true);
        checkbox_StartNote.setChecked(true);
        checkbox_ReSubmit.setChecked(false);

        // Hide override components initially
        text_Override.setVisibility(View.INVISIBLE);
        edit_OverrideTeamNum.setVisibility(View.INVISIBLE);
        but_AddOverrideTeamNum.setVisibility(View.INVISIBLE);

        checkbox_Override.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = View.VISIBLE;
                if (!checkbox_Override.isChecked()) state = View.INVISIBLE;
                text_Override.setVisibility(state);
                edit_OverrideTeamNum.setVisibility(state);
                but_AddOverrideTeamNum.setVisibility(state);
            }
        });

        but_AddOverrideTeamNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamNum = String.valueOf(preMatchBinding.editOverrideTeamNum.getText());
                if (!teamNum.isEmpty()) {
                    // TODO make it add teamNum to the options of single select dropdown after converting to int and
                    //      then have it auto select that one
                }
                checkbox_Override.setChecked(false);
                text_Override.setVisibility(View.INVISIBLE);
                edit_OverrideTeamNum.setVisibility(View.INVISIBLE);
                but_AddOverrideTeamNum.setVisibility(View.INVISIBLE);
            }
        });

        // Create a button for when you are done inputting info
        Button but_Next = preMatchBinding.butNext;
        but_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If we should re-submit data, go to the submit page immediately
                if (checkbox_ReSubmit.isChecked()) {
                    Intent GoToSubmitData = new Intent(PreMatch.this, SubmitData.class);
                    startActivity(GoToSubmitData);
                } else {
                    // Check we have all the fields entered that are needed.  Otherwise, pop a TOAST message instead
                    if (String.valueOf(edit_Match.getText()).isEmpty() || spinner_Team.getSelectedItemPosition() < 0 || String.valueOf(edit_Name.getText()).isEmpty()
                            //PREVIOUS CODE - if (String.valueOf(edit_Match.getText()).isEmpty() || String.valueOf(spinner_Team.getText()).isEmpty() || String.valueOf(edit_Name.getText()).isEmpty()
                            || (spinner_StartPos.getSelectedItem().toString() == Globals.StartPositionList.getStartPositionDescription(Constants.DATA_ID_START_POS_DEFAULT) && checkbox_DidPlay.isChecked())) {
                        Toast.makeText(PreMatch.this, R.string.missing_data, Toast.LENGTH_SHORT).show();
                    } else {
                        // Save off the current match number (Logger needs this)
                        Globals.CurrentMatchNumber = Integer.parseInt(preMatchBinding.editMatch.getText().toString());
                        Globals.NumberMatchFilesKept = Globals.sp.getInt(Constants.SP_NUM_MATCHES, 5);

                        // Set up the Logger - if it fails, we better stop now, or we won't capture any data!
                        try {
                            Globals.EventLogger = new Logger(getApplicationContext());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // Log all of the data from this page
                        Globals.EventLogger.LogData(Constants.LOGKEY_TEAM_TO_SCOUT, preMatchBinding.spinnerTeamToScout.getSelectedItem().toString());
                        Globals.EventLogger.LogData(Constants.LOGKEY_SCOUTER, preMatchBinding.editScouterName.getText().toString().toUpperCase().trim());
                        Globals.EventLogger.LogData(Constants.LOGKEY_DID_PLAY, String.valueOf(preMatchBinding.checkboxDidPlay.isChecked()));
                        Globals.EventLogger.LogData(Constants.LOGKEY_TEAM_SCOUTING, String.valueOf(Globals.CurrentScoutingTeam));
                        if (checkbox_DidPlay.isChecked()) {
                            int startPos = Globals.StartPositionList.getStartPositionId(spinner_StartPos.getSelectedItem().toString());
                            Globals.EventLogger.LogData(Constants.LOGKEY_START_POSITION, String.valueOf(startPos));
                            Globals.CurrentStartPosition = startPos;
                        }

                        // Save off some fields for next time or later usage
                        ScouterName = String.valueOf(edit_Name.getText());

                        // If they didn't play skip everything else
                        if (preMatchBinding.checkboxDidPlay.isChecked()) {
                            Intent GoToMatch = new Intent(PreMatch.this, Match.class);
                            startActivity(GoToMatch);
                        } else {
                            // Since we're jumping to the Submit page, we need to close the Logger first.
                            Globals.EventLogger.close();
                            Globals.EventLogger = null;

                            // Increases the team number so that it auto fills for the next match correctly
                            //  and do it after the logger is closed so that this can't mess the logger up
                            Globals.CurrentMatchNumber++;

                            // Reset the Saved Start position so that you have to choose it again
                            Globals.CurrentStartPosition = 0;

                            Intent GoToSubmitData = new Intent(PreMatch.this, SubmitData.class);
                            startActivity(GoToSubmitData);
                        }
                    }
                }
            }
        });

        edit_Match.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (!focus) {
                    String MatchNumStr = String.valueOf(edit_Match.getText());
                    if (!MatchNumStr.isEmpty()) {
                        int MatchNum = Integer.parseInt(MatchNumStr);
                        if (MatchNum > 0 && MatchNum < Globals.MatchList.size()) {
                            Matches.MatchRow Match = Globals.MatchList.getMatchInfoRow(MatchNum);
                            if (Match != null) {
                                int[] teams = Match.getListOfTeams();
                                String[] teams_Str = new String[teams.length];
                                for (int i = 0; i < teams.length; i++) {
                                    teams_Str[i] = String.valueOf(teams[i]);
                                }
                                if (teams_Str[0].equals("0") || teams_Str[0].equals(null)) {
                                    teams_Str = new String[]{getString(R.string.dropdown_no_items)};
                                }
                                ArrayAdapter<String> adp_Team = new ArrayAdapter<String>(view.getContext(), R.layout.cpr_spinner, teams_Str);
                                adp_Team.setDropDownViewResource(R.layout.cpr_spinner_item);
                                spinner_Team.setAdapter(adp_Team);
                            }
                        }
                    }
                }
            }
        });

        spinner_Team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String TeamToScoutStr = String.valueOf(spinner_Team.getSelectedItem());
                if (!TeamToScoutStr.isEmpty() && !TeamToScoutStr.equals(getString(R.string.dropdown_no_items))) {
                    int TeamToScout = Integer.parseInt(TeamToScoutStr);
                    if (TeamToScout > 0 && TeamToScout < Globals.TeamList.size()) {
                        // This will crash the app instead of returning null if you pass it an invalid num
                        String ScoutingTeamName = Globals.TeamList.get(TeamToScout);
                        text_TeamName.setText(ScoutingTeamName);
                    } else {
                        text_TeamName.setText("");
                    }

                    // Save off what you selected for if you go to the match and then back
                    Globals.CurrentTeamToScout = TeamToScout;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
}