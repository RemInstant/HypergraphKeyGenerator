package org.reminstant.junit.converter;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

class IntArrayConverter extends SimpleArgumentConverter {

  @Override
  protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
    if (source == null) {
      return new int[0];
    }

    if (!(source instanceof String)) {
      throw new ArgumentConversionException("Source must be a string");
    }

    String string = ((String) source).trim();

    if (!string.matches("\\[\\d*(,\\s*\\d+)*+]")) {
      throw new ArgumentConversionException("Invalid array string");
    }

    string = string.substring(1, string.length() - 1);
    if (string.isEmpty()) {
      return new int[0];
    }

    String[] parts = string.split("\\s*,\\s*");
    int[] result = new int[parts.length];

    try {
      for (int i = 0; i < parts.length; i++) {
        result[i] = Integer.parseInt(parts[i]);
      }
    } catch (NumberFormatException e) {
      throw new ArgumentConversionException(
          "Cannot convert '" + source + "' to int[]", e);
    }

    return result;
  }
}
