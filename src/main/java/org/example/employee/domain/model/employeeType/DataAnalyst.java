package org.example.employee.domain.model.employeeType;

import org.example.employee.domain.EmployeeRepository;
import org.example.employee.domain.model.AnalystResult;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.EmployeeTaskResults;

import java.util.Set;

/**
 * Employee subtype representing a Data Analyst.
 * Prints a sample action when executeSkill() is invoked.
 */
public class DataAnalyst extends Employee  {

    public DataAnalyst(Long id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public EmployeeTaskResults executeSkill(EmployeeRepository repository) {

        //TODO: iF REPOSITORY IS NULL, THROW EXCEPTION
        if (getCoworkers().isEmpty()) {
            return new AnalystResult(null, 0);
        }

        Long bestMatchId = null;
        int maxCommonCount = -1;

        Set<Long> myCoworkers = getCoworkers().keySet();

        for (Long coworkerId : myCoworkers) {
            Employee coworkerObj = repository.getEmployeeById(coworkerId);
            if (coworkerObj == null) continue;

            int currentCommonCount = 0;
            Set<Long> hisCoworkers = coworkerObj.getCoworkers().keySet();

            // 3. Compute the intersection of coworker sets
            for (Long hisFriendId : hisCoworkers) {
                // Count a mutual contact only if it is present in my list
                // and it is not myself (filter self-reference)
                if (myCoworkers.contains(hisFriendId) && !hisFriendId.equals(getId())) {
                    currentCommonCount++;
                }
            }

            // 4. Keep the maximum
            if (currentCommonCount > maxCommonCount) {
                maxCommonCount = currentCommonCount;
                bestMatchId = coworkerId;
            }
        }
        return new AnalystResult(bestMatchId, maxCommonCount);
    }

    @Override
    public String getGroupName() {
        return "Datovy Analytik";
    }

    @Override
    public String getGroupId() {
        return "DA";
    }
}
