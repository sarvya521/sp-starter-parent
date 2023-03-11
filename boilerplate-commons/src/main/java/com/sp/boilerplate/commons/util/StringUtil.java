package com.sp.boilerplate.commons.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class StringUtil {

  private StringUtil() {
    throw new AssertionError();
  }

  public static boolean isEmpty(final String[] array) {
    return null == array || array.length < 1;
  }

  public static boolean nonEmpty(final String[] array) {
    return null != array && array.length >= 1;
  }

  public static String[] crossJoinArrays(final String[]... arrays) {
    String[] finalArray = arrays[0];
    for (int index = 1; index < arrays.length; index++) {
      finalArray = crossJoinArrays(finalArray, arrays[index]);
    }
    return finalArray;
  }

  private static String[] crossJoinArrays(final String[] arr1, final String[] arr2) {
    if (isEmpty(arr1) && isEmpty(arr2)) {
      return new String[0];
    } else if (nonEmpty(arr1) && isEmpty(arr2)) {
      return arr1;
    } else if (isEmpty(arr1) && nonEmpty(arr2)) {
      return arr2;
    }

    return Arrays.stream(arr1).flatMap(firstArrElement -> Arrays.stream(arr2)
        .map(secondArrElement -> firstArrElement + secondArrElement)).toArray(String[]::new);
  }

  public static String[] union(String[] arr1, String[] arr2) {
    if (Objects.isNull(arr1)) {
      return arr2;
    }
    if (Objects.isNull(arr2)) {
      return arr1;
    }
    final Stream<String> stream1 = Stream.of(arr1);
    final Stream<String> stream2 = Stream.of(arr2);
    return
        Stream.concat(stream1, stream2)
            .distinct()
            .toArray(String[]::new);
  }

  /**
   * <p>
   * Get empty string if input text is null else get the same value.
   * </p>
   *
   * @param inputText
   * @return
   */
  public static String getEmptyIfNull(String inputText) {
    return Objects.isNull(inputText) ? "" : inputText;
  }
}
