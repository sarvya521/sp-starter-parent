package com.sp.boilerplate.commons.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.boilerplate.commons.dto.ClaimDto;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;

import static com.sp.boilerplate.commons.constant.AuthorityPrefix.GH_CLAIM_;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class ClaimUtil {
    private ClaimUtil() {
        throw new AssertionError();
    }

    public static ClaimDto parseClaimDto(final GrantedAuthority grantedAuthority) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        return
                objectMapper
                        .readValue(
                                grantedAuthority
                                        .getAuthority()
                                        .replace(String.valueOf(GH_CLAIM_), "")
                                , ClaimDto.class
                        );
    }
}
