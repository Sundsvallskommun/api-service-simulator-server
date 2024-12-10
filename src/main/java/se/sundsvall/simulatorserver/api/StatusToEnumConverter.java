package se.sundsvall.simulatorserver.api;

import javax.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.zalando.problem.Status;

public class StatusToEnumConverter implements Converter<String, Status> {

	private static final int CODE_STRING_LENGTH = 3;

	@Override
	public Status convert(@Nonnull String s) {
		s = s.trim().substring(0, CODE_STRING_LENGTH);

		return Status.valueOf(Integer.parseInt(s));
	}
}
