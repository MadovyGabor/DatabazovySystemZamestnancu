package org.example.employee.domain.model.employeeType.dataAnalyst;

import org.example.employee.domain.EmployeeRepository;
import org.example.employee.domain.model.Employee;
import org.example.employee.domain.model.EmployeeTaskResults;

import java.util.Set;


public class DataAnalyst extends Employee  {

    public DataAnalyst(Long id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

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

    @Override
    public String getGroupName() {
        return "Datovy Analytik";
    }

    @Override
    public String getGroupId() {
        return "DA";
    }
}
