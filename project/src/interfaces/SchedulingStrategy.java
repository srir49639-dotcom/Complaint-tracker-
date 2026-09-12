package interfaces;

import model.Complaint;
import model.Staff;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

public interface SchedulingStrategy {
    /**
     * Produces an ordered schedule or assignment mapping complaints to staff.
     */
    CustomHashTable<String, CustomArrayList<Complaint>> generateSchedule(
            CustomArrayList<Complaint> complaints,
            CustomArrayList<Staff> staffList);

    String getStrategyName();
    String getQualityRating();
}
