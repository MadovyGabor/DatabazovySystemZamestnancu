package org.example.employee.domain.model.employeeType.securitySpecialist;

import org.example.employee.domain.EmployeeRepository;
import org.example.employee.domain.model.*;

import java.util.ArrayList;
import java.util.List;


public class SecuritySpecialist extends Employee {

    public SecuritySpecialist(Long id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public EmployeeTaskResults executeSkill(EmployeeRepository repository) {

        if (getCoworkers().isEmpty()) {
            return new SecurityResult(0.0, List.of());
        }

        List<ConnectionRisk> risks = new ArrayList<>();
        double totalSystemRisk = 0;


        for (Long targetId : getCoworkers().keySet()) {
            Employee targetEmployee = repository.getEmployeeById(targetId);
            if (targetEmployee == null) continue;


            var targetConnections = targetEmployee.getCoworkers();
            double targetRiskPercent;


            if (targetConnections.isEmpty()) {

                targetRiskPercent = 100.0;
            } else {
                double targetTotalScore = 0;

                for (CollaborationLevel level : targetConnections.values()) {
                    targetTotalScore += switch (level) {
                        case GOOD -> 0.0;
                        case AVERAGE -> 50.0;
                        case BAD -> 100.0;
                    };
                }

                targetRiskPercent = targetTotalScore / targetConnections.size();


                if (targetConnections.size() < 3) {
                    targetRiskPercent += 20.0;
                    targetRiskPercent = Math.min(100.0, targetRiskPercent);
                }
            }


            String name = targetEmployee.getFirstName() + " " + targetEmployee.getLastName();
            int connectionCount = targetConnections.size();

            risks.add(new ConnectionRisk(targetId, name, targetRiskPercent, connectionCount));
            totalSystemRisk += targetRiskPercent;
        }


        risks.sort((a, b) -> Double.compare(b.score(), a.score()));


        double finalAvgRisk = risks.isEmpty() ? 0 : totalSystemRisk / risks.size();


        return new SecurityResult(finalAvgRisk, risks);
    }

    @Override
    public String getGroupName() {
        return "Bezpecnostni Specialista";
    }

    @Override
    public String getGroupId() {
        return "SS";
    }
}
