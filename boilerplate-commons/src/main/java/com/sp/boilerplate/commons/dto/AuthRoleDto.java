package com.sp.boilerplate.commons.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"uuid", "name", "claims", "features"})
@EqualsAndHashCode(of="uuid")
@Data
@NoArgsConstructor
public class AuthRoleDto {

    private UUID uuid;

    private String name;

    private Set<ClaimDto> claims = new LinkedHashSet<>();

    private Set<FeatureDto> features = new LinkedHashSet<>();

}
