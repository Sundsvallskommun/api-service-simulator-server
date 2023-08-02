package se.sundsvall.simulatorserver.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import jakarta.validation.constraints.Max;

@RestController
@Validated
@RequestMapping("/simulations")
@Tag(name = "Simulations")
public class SimulatorServerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorServerController.class);

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

		Optional.ofNullable(sortSize)
			.ifPresent(size -> {
				final List<String> result = IntStream.range(0, size * 10000)
					.mapToObj(i -> UUID.randomUUID().toString())
					.sorted()
					.toList();

				LOGGER.info("Sorted a list of size: {}", result.size());
			});

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
