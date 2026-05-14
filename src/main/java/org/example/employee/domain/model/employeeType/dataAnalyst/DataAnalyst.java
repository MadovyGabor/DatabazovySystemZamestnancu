package org.example.employee.domain.model.employeeType.dataAnalyst;

import org.example.employee.domain.EmployeeRepository;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.EmployeeTaskResults;

import java.util.Set;


/**
 * Represents a Data Analyst employee type.
 * A Data Analyst's special skill is finding the coworker with whom they share the most mutual connections.
 */
public class DataAnalyst extends Employee  {

    /**
     * Constructs a new DataAnalyst.
     *
     * @param id        The unique ID.
     * @param firstName The first name.
     * @param lastName  The last name.
     * @param birthYear The birth year.
     */
    public DataAnalyst(Long id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    /**
     * Executes the Data Analyst's skill: finds the direct coworker who shares the highest
     * number of mutual coworkers with this analyst.
     *
     * @param repository The repository to fetch coworker details.
     * @return An {@link AnalystResult} containing the best match details.
     */
    @Override
    public EmployeeTaskResults executeSkill(EmployeeRepository repository) {

        if (getCoworkers().isEmpty()) {
            return new AnalystResult(null, null,0);
        }

        Long bestMatchId = null;
        String bestMatchName = null;
        int maxCommonCount = -1;

        Set<Long> myCoworkers = getCoworkers().keySet();

        for (Long coworkerId : myCoworkers) {
            Employee coworkerObj = repository.getEmployeeById(coworkerId);
            if (coworkerObj == null) continue;

            int currentCommonCount = 0;
            Set<Long> hisCoworkers = coworkerObj.getCoworkers().keySet();


            for (Long hisFriendId : hisCoworkers) {


                if (myCoworkers.contains(hisFriendId) && !hisFriendId.equals(getId())) {
                    currentCommonCount++;
                }
            }


            if (currentCommonCount > maxCommonCount) {
                maxCommonCount = currentCommonCount;
                bestMatchId = coworkerId;
                bestMatchName = coworkerObj.getFirstName() + " " + coworkerObj.getLastName();
            }
        }

        return new AnalystResult(bestMatchId, bestMatchName, maxCommonCount);
    }

    /**
     * Returns the descriptive name for the Data Analyst group.
     *
     * @return "Datovy Analytik".
     */
    @Override
    public String getGroupName() {
        return "Datovy Analytik";
    }

    /**
     * Returns the short ID for the Data Analyst group.
     *
     * @return "DA".
     */
    @Override
    public String getGroupId() {
        return "DA";
    }
}
