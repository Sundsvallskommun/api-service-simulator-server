package se.sundsvall.simulatorserver.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/simulations")
@Tag(name = "Simulations")
public class SimulatorServerController {

	@PostMapping(path = "/response", consumes = APPLICATION_JSON_VALUE, produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Post request and response with the specified status and body.")
	public ResponseEntity<Object> getSuccessfulResponse(@Parameter(description = "Delay in milliseconds") @RequestParam(required = false) final Integer delay,
		@RequestParam(required = true) final Status status,
		@RequestBody(required = false) final Object object) throws InterruptedException {
		sleep(delay);

		return ResponseEntity.status(HttpStatus.valueOf(status.getStatusCode())).body(object);
	}

	@GetMapping(path = "/response", produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Get response with the specified fields in Problem-object.")
	public ResponseEntity<Problem> getErrorResponse(@Parameter(description = "Delay in milliseconds") @RequestParam(required = false) final Integer delay,
		@RequestParam(required = true) final Status status,
		@RequestParam(required = false) final String title,
		@RequestParam(required = false) final String detail,
		@RequestParam(required = false) final String type,
		@RequestParam(required = false) final String instance,
		@Parameter(description = "Sort a list of sortSize * 10000 UUIDs") @RequestParam(required = false) @Max(100) final Integer sortSize) throws InterruptedException {
		sleep(delay);

		if(sortSize != null) {
			Collection<String> uuids = new ArrayList<>();
			for (int i = 0; i < sortSize * 10000; i++) {
				uuids.add(UUID.randomUUID().toString());
				Thread.yield();
			}
			uuids.stream().sorted().toList();
		}


		final var problem = Problem.builder()
			.withStatus(status)
			.withTitle(title == null ? status.getReasonPhrase() : title)
			.withDetail(detail)
			.withType(type == null ? null : URI.create(type.replace(" ", "_")))
			.withInstance(instance == null ? null : URI.create(instance.replace(" ", "_")))
			.build();

		return ResponseEntity.status(HttpStatus.valueOf(status.getStatusCode())).body(problem);
	}

	private void sleep(final Integer delay) throws InterruptedException {
		if (delay != null) {
			Thread.sleep(delay);
		}
	}
}
