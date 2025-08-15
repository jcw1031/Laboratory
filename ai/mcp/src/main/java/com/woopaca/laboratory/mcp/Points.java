
package com.woopaca.laboratory.mcp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Points(@JsonProperty("properties") Props properties) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Props(@JsonProperty("forecast") String forecast) {
    }
}