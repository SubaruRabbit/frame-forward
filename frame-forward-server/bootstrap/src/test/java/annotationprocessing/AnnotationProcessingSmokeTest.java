package annotationprocessing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import lombok.Builder;
import lombok.Value;

class AnnotationProcessingSmokeTest {

	@Test
	void lombokBuilderAndMapStructProcessorWorkTogether() {
		var source = SmokeSource.builder().value("ready").build();

		var target = Mappers.getMapper(SmokeConverter.class).convert(source);

		assertEquals("ready", target.getValue());
	}

}

@Value
@Builder
class SmokeSource {

	String value;

}

@Value
@Builder
class SmokeTarget {

	String value;

}
