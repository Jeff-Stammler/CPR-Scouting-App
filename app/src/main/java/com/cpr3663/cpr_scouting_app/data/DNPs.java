package com.cpr3663.cpr_scouting_app.data;

import java.util.ArrayList;

// =============================================================================================
// Class:       DNPs
// Description: Defines a structure/class to hold the information for all DNP reasons
// Methods:     addDNPRow()
//                  add a row of DNP info
//              size()
//                  return the number of DNPs we have
//              getDNPRow()
//                  return a DNPInfoRow item for the given DNP id
//              getDescriptionList()
//                  return a String Array of all of the descriptions of the DNP Reasons
// =============================================================================================
public class DNPs {
    private final ArrayList<DNPRow> dnp_list;

    // Constructor
    public DNPs() {
        dnp_list = new ArrayList<DNPRow>();
    }

    // Member Function: Add a row of DNP info into the list giving the data individually
    public void addDNPRow(String in_id, String in_description) {
        dnp_list.add(new DNPRow(in_id, in_description));
    }

    // Member Function: return the size of the list
    public int size() {
        return dnp_list.size();
    }

    // Member Function: Get back a row of data for a given DNP entry
    public DNPRow getDNPRow(int in_index) {
        return dnp_list.get(in_index);
    }

    // Member Function: Get back the Id for a given DNP entry (needed for logging)
    public int getDNPId(String in_description) {
        int ret = 0;

        // Loop through the DNP list to find a matching description and return the id
        for (DNPRow dr : dnp_list) {
            if (dr.getDescription().equals(in_description)) {
                ret = dr.id;
                break;
            }
        }

        return ret;
    }

    // TODO See if we can make it return a ArrayList<String> for consistency
    public String[] getDescriptionList() {
        String[] descriptions = new String[dnp_list.size()];

        for (int i = 0; i < descriptions.length; i++) {
            descriptions[i] = dnp_list.get(i).getDescription();
        }
        return descriptions;
    }

    // =============================================================================================
    // Class:       DNPRow
    // Description: Defines a structure/class to hold the information for each DNP reason
    // Methods:     getId()
    //                  returns the (int) DNP number for this row to use for logging
    //              getDescription()
    //                  returns the (String) description for this row
    // =============================================================================================
    public static class DNPRow {
        // Class Members
        private final int id;
        private final String description;

        // Constructor with individual data
        public DNPRow(String in_id, String in_description) {
            id = Integer.parseInt(in_id);
            description = in_description;
        }

        // Getter
        public int getId() {
            return id;
        }

        // Getter
        public String getDescription() {
            return description;
        }
    }
}

