package se.sundsvall.simulatorserver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.zalando.problem.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {

	private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	@Autowired
	MockMvc mockMvc;

	@ParameterizedTest
	@EnumSource(Status.class)
	void testGetForAllStatuses(Status status) throws Exception {

		String detail = "Some example detail.";
		String type = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.1";
		String instance = "test.example";

		mockMvc.perform(
			get("/simulations/response")
				.queryParam("status", status.toString())
				.queryParam("detail", detail)
				.queryParam("type", type)
				.queryParam("instance", instance))
			.andExpect(status().is(status.getStatusCode()))
			.andExpect(jsonPath("$.title").value(status.getReasonPhrase()))
			.andExpect(jsonPath("$.status").value(status.getStatusCode()))
			.andExpect(jsonPath("$.detail").value(detail))
			.andExpect(jsonPath("$.type").value(type))
			.andExpect(jsonPath("$.instance").value(instance));
	}

	@ParameterizedTest
	@EnumSource(Status.class)
	void testPostForAllStatuses(Status status) throws Exception {
		TestClass testClassRequest = new TestClass();
		testClassRequest.setField1("Test string");
		testClassRequest.setField2(123);
		testClassRequest.setField3(true);

		String responseBody = mockMvc.perform(
			post("/simulations/response")
				.queryParam("status", status.toString())
				.content(mapper.writeValueAsString(testClassRequest)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(status.getStatusCode()))
			.andReturn().getResponse().getContentAsString();

		TestClass testClassResponse = new ObjectMapper().readValue(responseBody, TestClass.class);

		assertThat(testClassRequest).isEqualTo(testClassResponse);
	}

	@Test
	void testGetDelay() throws Exception {

		Status status = Status.INTERNAL_SERVER_ERROR;

		mockMvc.perform(
			get("/simulations/response")
				.queryParam("delay", "1000")
				.queryParam("status", status.toString()))
			.andExpect(status().is(status.getStatusCode()))
			.andExpect(jsonPath("$.title").value(status.getReasonPhrase()))
			.andExpect(jsonPath("$.status").value(status.getStatusCode()));
	}

	@Test
	void testPostDelay() throws Exception {

		Status status = Status.BAD_REQUEST;

		TestClass testClassRequest = new TestClass();
		testClassRequest.setField1("Test string");
		testClassRequest.setField2(123);
		testClassRequest.setField3(true);

		String responseBody = mockMvc.perform(
			post("/simulations/response")
				.queryParam("delay", "2000")
				.queryParam("status", status.toString())
				.content(mapper.writeValueAsString(testClassRequest)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(status.getStatusCode()))
			.andReturn().getResponse().getContentAsString();

		TestClass testClassResponse = new ObjectMapper().readValue(responseBody, TestClass.class);

		assertThat(testClassRequest).isEqualTo(testClassResponse);
	}

	@Test
	void testPostIllegalArgumentException() throws Exception {

		TestClass testClassRequest = new TestClass();
		testClassRequest.setField1("Test string");
		testClassRequest.setField2(123);
		testClassRequest.setField3(true);

		mockMvc.perform(post("/simulations/response")
			.queryParam("status", "12345")
			.content(mapper.writeValueAsString(testClassRequest)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(Status.BAD_REQUEST.getStatusCode()))
			.andExpect(jsonPath("$.detail").value(Matchers.containsString("Failed to convert value of type 'java.lang.String' to required type 'org.zalando.problem.Status'")));
	}

	@Test
	void testSort() throws Exception {

		Status status = Status.OK;

		mockMvc.perform(
				get("/simulations/response")
					.queryParam("sortSize", "2")
					.queryParam("status", status.toString()))
			.andExpect(status().is(status.getStatusCode()));

	}

}
