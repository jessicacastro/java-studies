package dev.jessicacastro.todolist.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {
  public static void copyNonNullProperties(Object source, Object target) {
    String[] names = getNullPropertyNames(source);

    if (source != null
        && target != null
        && names != null
        && names.length > 0) {
      // Copy non null properties
      BeanUtils.copyProperties(source, target, names);
    }
  }

  public static String[] getNullPropertyNames(Object source) {
    if (source == null) {
      return new String[0];
    }

    // Create a BeanWrapper for the source object
    final BeanWrapper src = new BeanWrapperImpl(source);

    // Get all the properties of the source object
    PropertyDescriptor[] pds = src.getPropertyDescriptors();

    // Create a set to hold the names of the properties that are null
    Set<String> emptyNames = new HashSet<String>();

    // Iterate through the properties
    for (PropertyDescriptor pd : pds) {
      String name = pd.getName();

      if (name.isEmpty()) {
        continue;
      }

      // Get the value of the property
      Object srcValue = src.getPropertyValue(name);

      if (srcValue == null) {
        // Add the name of the property to the set
        emptyNames.add(pd.getName());
      }
    }

    // Convert the set to an array of strings
    String[] result = new String[emptyNames.size()];

    // Return the array of strings containing the names of the properties that are
    // null
    return emptyNames.toArray(result);
  }
}
