package com.cpr3663.cpr_scouting_app;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cpr3663.cpr_scouting_app.databinding.PostMatchBinding;

import java.util.ArrayList;
import java.util.Collections;

public class PostMatch extends AppCompatActivity {
    // =============================================================================================
    // Constants
    // =============================================================================================


    // =============================================================================================
    // Global variables
    // =============================================================================================
    private PostMatchBinding postMatchBinding;
    TextView drop_Comments;
    boolean[] selectedComment;
    //Creating an array list for the Comments
    ArrayList<Integer> CommentList = new ArrayList<>();
    String[] CommentArray = Globals.CommentList.getDescriptionList();

    // Doesn't appear to be needed on Tablet but helps on Virtual Devices.
    @SuppressLint({"DiscouragedApi", "SetTextI18n", "ClickableViewAccessibility", "ResourceAsColor"})
    @Override
    protected void onResume() {
        super.onResume();

        // Hide the status and action bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) actionBar.hide();
    }

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        postMatchBinding = PostMatchBinding.inflate(getLayoutInflater());
        setContentView(postMatchBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(postMatchBinding.postMatch, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Default them to leaving
        postMatchBinding.checkboxDidLeave.setChecked(true);

        //Creating the single select dropdown menu for the trap outcomes
        Spinner trapSpinner = findViewById(R.id.spinnerTrap);
        //accessing the array in strings.xml
        // TODO make this not use the string resource
        ArrayAdapter<String> trapAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, Globals.TrapResultsList.getDescriptionList());
        trapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trapSpinner.setAdapter(trapAdapter);

        trapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.cpr_bkgnd));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Creating the single select dropdown menu for the climb positions
        Spinner climbPositionSpinner = findViewById(R.id.spinnerClimbPosition);
        //accessing the array in strings.xml
        // TODO make this not use the string resource
        ArrayAdapter<String> climbPositionAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, Globals.ClimbPositionList.getDescriptionList());
        climbPositionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        climbPositionSpinner.setAdapter(climbPositionAdapter);

        climbPositionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.cpr_bkgnd));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // assign variable
        drop_Comments = postMatchBinding.dropComments;

        // initialize comment reasons arrays
        selectedComment = new boolean[CommentArray.length];

        drop_Comments.setText("0 " + getResources().getString(R.string.dropdown_items_selected));
        //code for how to open the dropdown menu when clicked and select items
        drop_Comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostMatch.this);

                // set title for the dropdown menu
                builder.setTitle("Select All That Apply");


                // set dialog non cancelable
                builder.setCancelable(false);

                // Puts to comments from the array into the dropdown menu
                builder.setMultiChoiceItems(CommentArray, selectedComment, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in comment list
                            CommentList.add(i);
                            // Sort array list
                            Collections.sort(CommentList);
                        } else {
                            // when checkbox unselected
                            // Remove position from comment list
                            CommentList.remove(Integer.valueOf(i));
                        }
                    }
                });

                //adds the "ok" button to the dropdown menu
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < CommentList.size(); j++) {
                            // concat array value
                            stringBuilder.append(CommentArray[CommentList.get(j)]);
                            // check condition
                            if (j != CommentList.size() - 1) {
                                // When j value  not equal
                                // to comment list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set number of selected on CommentsTextView
                        drop_Comments.setText(CommentList.size() + " " + getResources().getString(R.string.dropdown_items_selected));
                    }
                });

                //adds the "cancel" button to the dropdown menu
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });

                //adds the "clear all" button to the dropdown menu
                // to clear all previously selected items
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selectedComment.length; j++) {
                            // remove all selection
                            selectedComment[j] = false;
                            // clear comment list
                            CommentList.clear();
                            // clear text view value
                            drop_Comments.setText("0 " + getResources().getString(R.string.dropdown_items_selected));
                        }
                    }
                });
                // show dialog
                builder.show();
            }
        });

        // Create Components
        // TODO: Change type for drop downs once we have the right XML and Java for it.
        CheckBox check_DidLeave = postMatchBinding.checkboxDidLeave;
        Spinner drop_ClimbPosition = postMatchBinding.spinnerClimbPosition;
        Spinner drop_Trap = postMatchBinding.spinnerTrap;
        TextView drop_Comments = postMatchBinding.dropComments;

        // Since we are putting the checkbox on the RIGHT side of the text, the checkbox doesn't honor padding.
        // So we need to use 7 spaces, but you can't when using a string resource (it ignores the trailing spaces)
        // So add it in now.
        postMatchBinding.checkboxDidLeave.setText(postMatchBinding.checkboxDidLeave.getText() + Globals.CheckBoxTextPadding);

        // Create a button for when you are done inputting info
        // finishes scouting the team and submits info
        Button but_Next = postMatchBinding.butNext;
        but_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String multi_values = "";

                // Log all of the data from this page
                Globals.EventLogger.LogData(Constants.LOGKEY_DID_LEAVE_START, String.valueOf(postMatchBinding.checkboxDidLeave.isChecked()));
//                Globals.EventLogger.LogData(Constants.LOGKEY_CLIMB_POSITION, postMatchBinding.spinnerClimbPosition.getSelectedItem().toString());
//                Globals.EventLogger.LogData(Constants.LOGKEY_TRAP, postMatchBinding.spinnerTrap.getSelectedItem().toString());
                // TODO : need to know how to build a multi-selected list of IDs (delimiter will be ":")
//                Globals.EventLogger.LogData(Constants.LOGKEY_COMMENTS, postMatchBinding.dropComments.toString());

                // We're done with the logger
                Globals.EventLogger.close();
                Globals.EventLogger = null;

                Intent GoToSubmitData = new Intent(PostMatch.this, SubmitData.class);
                startActivity(GoToSubmitData);
            }
        });
    }
}