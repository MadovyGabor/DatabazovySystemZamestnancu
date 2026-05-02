package org.example.employee.domain.model.employeeType;

import org.example.employee.domain.EmployeeRepository;
import org.example.employee.domain.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Employee subtype representing a Security Specialist.
 * Prints a sample action when executeSkill() is invoked.
 */
public class SecuritySpecialist extends Employee {

    public SecuritySpecialist(Long id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public EmployeeTaskResults executeSkill(EmployeeRepository repository) {
        // 1. Early return: If the auditor has no connections, there is no one to audit.
        if (getCoworkers().isEmpty()) {
            return new SecurityResult(0.0, List.of());
        }

        List<ConnectionRisk> risks = new ArrayList<>();
        double totalSystemRisk = 0;

        // 2. Iterate through our direct connections (the audit targets).
        for (Long targetId : getCoworkers().keySet()) {
            Employee targetEmployee = repository.getEmployeeById(targetId);
            if (targetEmployee == null) continue;

            // 3. Fetch the full connection network of the current target.
            var targetConnections = targetEmployee.getCoworkers();
            double targetRiskPercent;

            // 4. Calculate the target's risk score based on THEIR connections.
            if (targetConnections.isEmpty()) {
                // Target is completely isolated -> Maximum security risk!
                targetRiskPercent = 100.0;
            } else {
                double targetTotalScore = 0;
                // Evaluate the quality of the target's connections.
                for (CollaborationLevel level : targetConnections.values()) {
                    targetTotalScore += switch (level) {
                        case GOOD -> 0.0;
                        case AVERAGE -> 50.0;
                        case BAD -> 100.0;
                    };
                }
                // Calculate base risk percentage.
                targetRiskPercent = targetTotalScore / targetConnections.size();

                // Isolation penalty: Targets with fewer than 3 connections pose a higher risk.
                if (targetConnections.size() < 3) {
                    targetRiskPercent += 20.0;
                    targetRiskPercent = Math.min(100.0, targetRiskPercent); // Cap at 100% to maintain scale limits.
                }
            }

            // 5. Construct the individual connection risk record (DTO).
            String name = targetEmployee.getFirstName() + " " + targetEmployee.getLastName();
            int connectionCount = targetConnections.size();

            risks.add(new ConnectionRisk(targetId, name, targetRiskPercent, connectionCount));
            totalSystemRisk += targetRiskPercent;
        }

        // 6. Sort results descending (highest risk targets at the top).
        risks.sort((a, b) -> Double.compare(b.score(), a.score()));

        // 7. Calculate the overall average risk of the audited group.
        double finalAvgRisk = risks.isEmpty() ? 0 : totalSystemRisk / risks.size();

        // 8. Return the aggregated security audit payload.
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
