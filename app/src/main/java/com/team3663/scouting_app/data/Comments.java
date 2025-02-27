package com.team3663.scouting_app.data;

import java.util.ArrayList;

// =============================================================================================
// Class:       Comments
// Description: Defines a structure/class to hold the information for all Comments
// Methods:     addCommentRow()
// =============================================================================================
public class Comments {
    private final ArrayList<CommentRow> comment_list;

    // Constructor
    public Comments() {
        comment_list = new ArrayList<>();
    }

    // Member Function: Add a row of Comment info into the list giving the data individually
    public void addCommentRow(String in_id, String in_description) {
        comment_list.add(new CommentRow(in_id, in_description));
    }

    // Member Function: return the size of the list
    public int size() {
        return comment_list.size();
    }

    // Member Function: Get back the Id for a given DNP entry (needed for logging)
    public int getCommentId(String in_description) {
        int ret = 0;

        // Loop through the DNP list to find a matching description and return the id
        for (CommentRow cr : comment_list) {
            if (cr.description.equals(in_description)) {
                ret = cr.id;
                break;
            }
        }

        return ret;
    }

    // Member Function: Return a string list of all records
    public ArrayList<String> getDescriptionList() {
        ArrayList<String> descriptions = new ArrayList<>();

        for (int i = 0; i < comment_list.size(); i++) {
            descriptions.add(comment_list.get(i).description);
        }
        return descriptions;
    }

    // Member Function: Empties out the list
    public void clear() {
        comment_list.clear();
    }

    // =============================================================================================
    // Class:       CommentRow
    // Description: Defines a structure/class to hold the information for each Comment
    // =============================================================================================
    private static class CommentRow {
        private final int id;
        private final String description;

        // Constructor with individual data
        public CommentRow(String in_id, String in_description) {
            id = Integer.parseInt(in_id);
            description = in_description;
        }
    }
}
