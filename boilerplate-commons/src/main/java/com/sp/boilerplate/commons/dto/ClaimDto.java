package com.sp.boilerplate.commons.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "method", "resource"})
@EqualsAndHashCode(of = {"name", "method", "resource"})
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimDto {

  private UUID uuid;

  private String name;

  private String method;

  private String resource;
}
