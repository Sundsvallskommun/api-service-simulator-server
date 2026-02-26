package se.sundsvall.simulatorserver.api;

import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;

public class StatusToEnumConverter implements Converter<String, HttpStatus> {

	private static final int CODE_STRING_LENGTH = 3;

	@Override
	public HttpStatus convert(@Nonnull String s) {
		s = s.trim().substring(0, CODE_STRING_LENGTH);

		return HttpStatus.valueOf(Integer.parseInt(s));
	}
}
