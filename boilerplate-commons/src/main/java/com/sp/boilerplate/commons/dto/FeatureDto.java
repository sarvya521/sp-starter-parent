
package com.sp.boilerplate.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sp.boilerplate.commons.constant.FeatureAction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "featureActions"})
@EqualsAndHashCode
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureDto {
    private UUID uuid;

    private String name;

    private List<FeatureAction> featureActions = new ArrayList<>();

    public FeatureDto(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}
