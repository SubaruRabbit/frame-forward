package annotationprocessing;

import org.mapstruct.Mapper;

@Mapper
public interface SmokeConverter {

	SmokeTarget convert(SmokeSource source);

}
