package graphql.kickstart.autoconfigure.editor.playground.properties.settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaygroundPrettierSettings {

  @Min(1)
  private Integer printWidth;

  @Min(1)
  private Integer tabWidth;

  private Boolean useTabs;
}
