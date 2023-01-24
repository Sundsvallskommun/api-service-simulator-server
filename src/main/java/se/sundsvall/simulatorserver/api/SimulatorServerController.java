package se.sundsvall.simulatorserver.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import java.net.URI;

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

	@PostMapping(path = "/response", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Post request and response with the specified status and body.")
	public ResponseEntity<Object> getSuccessfulResponse(@Parameter(description = "Delay in milliseconds") @RequestParam(required = false) Integer delay,
		@RequestParam(required = true) Status status,
		@RequestBody(required = false) Object object) throws InterruptedException {
		sleep(delay);

		return ResponseEntity.status(HttpStatus.valueOf(status.getStatusCode())).body(object);
	}

	@GetMapping(path = "/response", produces = APPLICATION_PROBLEM_JSON_VALUE)
	@Operation(summary = "Get response with the specified fields in Problem-object.")
	public ResponseEntity<Problem> getErrorResponse(@Parameter(description = "Delay in milliseconds") @RequestParam(required = false) Integer delay,
		@RequestParam(required = true) Status status,
		@RequestParam(required = false) String title,
		@RequestParam(required = false) String detail,
		@RequestParam(required = false) String type,
		@RequestParam(required = false) String instance) throws InterruptedException {
		sleep(delay);

		final var problem = Problem.builder()
			.withStatus(status)
			.withTitle(title == null ? status.getReasonPhrase() : title)
			.withDetail(detail)
			.withType(type == null ? null : URI.create(type.replace(" ", "_")))
			.withInstance(instance == null ? null : URI.create(instance.replace(" ", "_")))
			.build();

		return ResponseEntity.status(HttpStatus.valueOf(status.getStatusCode())).body(problem);
	}

	private void sleep(Integer delay) throws InterruptedException {
		if (delay != null) {
			Thread.sleep(delay);
		}
	}
}
