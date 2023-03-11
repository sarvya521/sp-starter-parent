package com.sp.boilerplate.commons.util;

import java.util.UUID;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class UUIDConverter {

  private static final String HEX_FORMAT = "%032x";
  private static final String MAX_HEX = formatHexString(String.format(HEX_FORMAT, Long.MAX_VALUE));

  private UUIDConverter() {
    throw new AssertionError();
  }

  public static Long uuidToLong(final UUID uuid) {
    return Long.parseLong(uuid.toString().replace("-", ""), 16);
  }

  public static UUID longToUuid(final Long n) {
    String paddedStr = String.format(HEX_FORMAT, n);
    return UUID.fromString(formatHexString(paddedStr));
  }

  private static String formatHexString(final String str) {
    return str.substring(0, 8)
        + "-" + str.substring(8, 12)
        + "-" + str.substring(12, 16)
        + "-" + str.substring(16, 20)
        + "-" + str.substring(20);
  }
}
