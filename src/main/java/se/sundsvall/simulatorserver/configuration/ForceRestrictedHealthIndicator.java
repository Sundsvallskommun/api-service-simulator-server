package se.sundsvall.simulatorserver.configuration;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.util.jacoco.ExcludeFromJacocoGeneratedCoverageReport;

@Component
@ExcludeFromJacocoGeneratedCoverageReport
public class ForceRestrictedHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		return Health.status("RESTRICTED")
			.withDetail("reason", "Testing restricted status")
			.build();
	}
}
