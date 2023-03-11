package com.sp.boilerplate.commons.util;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.web.util.UriUtils;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class UrlUtil {

  private UrlUtil() {
    throw new AssertionError();
  }

  public static String encodeQueryString(String queryString) {
    String[] params = queryString.split("&");
    return
        Stream.of(params)
            .map(s -> s.split("="))
            .map(arr -> arr[0] + "=" + UriUtils.encodeQueryParam(arr[1], StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));
  }
}
