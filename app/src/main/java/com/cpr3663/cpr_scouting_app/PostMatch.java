package com.cpr3663.cpr_scouting_app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    TextView textView;
    TextView DNPTextView;
    boolean[] selectedComment;
    boolean[] selectedDNPReasons;
    //Creating an array list for the Comments
    ArrayList<Integer> CommentList = new ArrayList<>();
    String[] CommentArray = {"Robot became disabled or stopped moving", "Robot (or part of it) broke",
            "Robot didn't contribute much (no auto, low scoring, no defense)", "Poor human player (source)",
            "Poor human player (amp)", "Robot got note(s) stuck in it"};
    //Creating an array list for the DNP reasons
    ArrayList<Integer> DNPReasonsList = new ArrayList<>();
    String[] DNPReasonsArray = {"Fouled excessively", "Red/Yellow card", 
            "Never contributing to match", "no show", "e", "f"};



    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        postMatchBinding = PostMatchBinding.inflate(getLayoutInflater());
        View page_root_view = postMatchBinding.getRoot();
        setContentView(page_root_view);
        ViewCompat.setOnApplyWindowInsetsListener(postMatchBinding.postMatch, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setContentView(R.layout.post_match);

        //Creating the single select dropdown menu for the trap outcomes
        Spinner trapSpinner=findViewById(R.id.spinnerTrap);
        //accessing the array in strings.xml
        ArrayAdapter<CharSequence> trapAdapter= ArrayAdapter.createFromResource(this,R.array.trap_outcomes_array, android.R.layout.simple_spinner_item);
        trapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trapSpinner.setAdapter(trapAdapter);

        //Creating the single select dropdown menu for the climb positions
        Spinner climbPositionSpinner=findViewById(R.id.spinnerClimbPosition);
        //accessing the array in strings.xml
        ArrayAdapter<CharSequence> climbPositionAdapter= ArrayAdapter.createFromResource(this,R.array.climb_positions_array, android.R.layout.simple_spinner_item);
        climbPositionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        climbPositionSpinner.setAdapter(climbPositionAdapter);

        // assign variable
        textView = findViewById(R.id.textViewComments);
        DNPTextView = findViewById(R.id.textViewDNP);

        // initialize comment and dnp reasons arrays
        selectedComment = new boolean[CommentArray.length];
        selectedDNPReasons = new boolean[DNPReasonsArray.length];

        //code for how to open the dropdown menu when clicked and select items
        textView.setOnClickListener(new View.OnClickListener() {
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
                        // set text on textView
                        textView.setText(stringBuilder.toString());
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
                            textView.setText("");
                        }
                    }
                });
                // show dialog
                builder.show();
            }
        });

        // code for how to open the DNP dropdown and select items
        DNPTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PostMatch.this);

                // set title for the dropdown
                builder.setTitle("Select Reason(s)");


                // set dialog non cancelable
                builder.setCancelable(false);

                // adds the predetermined DNP reasons to the dropdown
                builder.setMultiChoiceItems(DNPReasonsArray, selectedDNPReasons, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in DNP Reasons list
                            DNPReasonsList.add(i);
                            // Sort array list
                            Collections.sort(DNPReasonsList);
                        } else {
                            // when checkbox unselected
                            // Remove position from DNP Reasons list
                            DNPReasonsList.remove(Integer.valueOf(i));
                        }
                    }
                });

                // adds the "ok" button to the dropdown menu, allowing you to exit the menu
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < DNPReasonsList.size(); j++) {
                            // concat array value
                            stringBuilder.append(DNPReasonsArray[DNPReasonsList.get(j)]);
                            // check condition
                            if (j != DNPReasonsList.size() - 1) {
                                // When j value  not equal
                                // to DNP Reasons list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        DNPTextView.setText(stringBuilder.toString());
                    }
                });

                // adds the "cancel" button to the dropdown, allowing you to exit the
                // menu without having any selected items
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });

                // adds the "clear all" button, allowing you to exit the menu after
                // clearing all previously selected items
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selectedDNPReasons.length; j++) {
                            // remove all selection
                            selectedDNPReasons[j] = false;
                            // clear DNP Reasons list
                            DNPReasonsList.clear();
                            // clear text view value
                            DNPTextView.setText("");
                        }
                    }
                });
                // show dialog
                builder.show();

            }
        });


        // Create a button for when you are done inputting info
        // finishes scouting the team and submits info
        Button but_Next = postMatchBinding.butNext;
        but_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GoToSubmitData = new Intent(PostMatch.this, SubmitData.class);
                startActivity(GoToSubmitData);
            }
        });
    }
}






