
package com.sp.boilerplate.commons.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "uuid", "email", "roles" })
@EqualsAndHashCode(of="uuid")
@Data
@NoArgsConstructor
public class UserDetailsDto {

    private Long id;

    private UUID uuid;

    private String email;

    private List<AuthRoleDto> roles;

    private String userFullName;
}
