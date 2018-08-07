/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.empire.commons;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains common functions for comparing and converting values of type Object. 
 * 
 */
public final class ObjectUtils
{
    /**
     * This class explicitly defines that an Object has not been assigned a value.<BR>
     * This may be used in cases where the value of null may be a valid value.
     */
    private static final class NoValue implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private NoValue()
        { /* dummy */
        }
        @Override
        public String toString()
        {
        	return "[NO-VALUE]";
        }
    }
    
    /**
     * Constant that defines a object of type NoValue.
     * This may be used in cases where the value of null is a valid value.
     */
    public static final NoValue NO_VALUE = new NoValue();
    
    // Logger
    private static final Logger log = LoggerFactory.getLogger(ObjectUtils.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String DATETIME_FORMAT = "yyyy-MM-dd hh:mm:ss";

    private ObjectUtils()
    {
        // Static Function only
        // No instance may be created
    }
    
    /**
     * Checks whether an object has no value.
     * A Object is empty if and only if the Object is null or if its an empty string. 
     * @param o the object to check
     * @return true if the Object is null or if its an empty string. 
     */
    public static boolean isEmpty(Object o)
    {
        if (o==null || o==ObjectUtils.NO_VALUE)
            return true;
        if ((o instanceof String) && ((String)o).length()==0)
            return true;
        // not empty
        return false;
    }
    
    /**
     * Compares two objects for equality 
     * 
     * @param o1 the first object
     * @param o2 the second object
     * 
     * @return true if both objects are equal or false otherwise
     */
    @SuppressWarnings("unchecked")
    public static boolean compareEqual(Object o1, Object o2)
    {
    	// simple case
    	if (o1==o2)
    		return true;
        // Check for Empty Values
        if (isEmpty(o1))
            return isEmpty(o2);
        if (isEmpty(o2))
            return isEmpty(o1);
        // Check classes
        if (o1.getClass().equals(o2.getClass()))
        {   // Check if object implements comparable
            if (o1 instanceof Comparable)
                return (((Comparable<Object>)o1).compareTo(o2)==0);
            else
                return o1.equals(o2);
        }
        // Classes don't match
        // Use equal check first
        if (o1.equals(o2) || o2.equals(o1))
            return true;
        // Compare Numbers
        if (o1 instanceof Number && o2 instanceof Number)
        {   // boolean test = obj1.equals(obj2);
            double d1 = ((Number)o1).doubleValue();
            double d2 = ((Number)o2).doubleValue(); 
            return (d1==d2);
        }
        // Enum
        if (o1.getClass().isEnum())
        {   // Special enum handling   
            if (o2 instanceof Number)
                return ((Enum<?>)o1).ordinal()==((Number)o2).intValue();
            else
                return ((Enum<?>)o1).name().equals(o2);
        }
        else if (o2.getClass().isEnum())
        {   // Special enum handling   
            if (o1 instanceof Number)
                return ((Enum<?>)o2).ordinal()==((Number)o1).intValue();
            else
                return ((Enum<?>)o2).name().equals(o1);
        }
        // Compare Strings
        return o1.toString().equals(o2.toString());
    }

    /**
     * Compares two object arrrays for equality
     *
     * @param array1    the first array
     * @param array2    the second array
     *
     * @return true if both arrays are equal or false otherwise
     */
    public static boolean compareEqual(Object[] array1, Object[] array2)
    {   // Compare Length
        int len1 = (array1!=null ? array1.length : 0);
        int len2 = (array2!=null ? array2.length : 0);
        if (len1!= len2)
            return false;
        // Compare Key Values
        for (int i = 0; i < len1; i++)
        {   // Check String Values
            if (!ObjectUtils.compareEqual(array1[i], array2[i]))
                return false;
        }
        return true;
    }
    
    /**
     * Checks whether a preferred value is valid and returns an alternative value if not.
     * @param <T> the type of the values
     * @param preferred the preferred return value
     * @param alternative the alternative return value used if the preferred value is null 
     * @return the preferred value if it is not null or the alternative value otherwise
     */
    public static <T> T coalesce(T preferred, T alternative)
    {
        return (preferred!=null ? preferred : alternative);
    }

    /**
     * converts an object to an integer. If conversion is not possible, an error is thrown
     * @param v the value to convert
     * @return the integer value
     */
    public static int toInteger(Object v)
    {
        if (ObjectUtils.isEmpty(v))
            return 0;
        if (v instanceof Number)
            return ((Number)v).intValue();
        // Try to convert
        String str = v.toString();
        return Integer.parseInt(str);
    }
    
    /**
     * Converts an object value to an integer.
     * <P>
     * If the object value supplied is null or if conversion is not possible then the default value is returned.
     * @param v the obect to convert
     * @param defValue the default value if o is null or conversion is not possible 
     * @return the Integer value of o or a default value
     */
    public static int getInteger(Object v, int defValue)
    {
        // Check empty
        if (ObjectUtils.isEmpty(v))
            return defValue;
        // Try to convert
        try
        {   // Try to convert
            return toInteger(v);
        } catch (Exception e)
        {
        	log.warn(String.format("Cannot convert value [%s] to int!", v));
            return defValue;
        }
    }
    
    /**
     * Converts an object value to an integer.
     * <P>
     * If the object value supplied is null or if conversion is not possible then 0 is returned.
     * @param v the object value to convert
     * @return the Integer value of o or 0
     */
    public static int getInteger(Object v)
    {
        return getInteger(v, 0); 
    }

    /**
     * converts an object to a long. If conversion is not possible, an error is thrown
     * @param v the value to convert
     * @return the long value
     */
    public static long toLong(Object v)
    {
        if (ObjectUtils.isEmpty(v))
            return 0;
        if (v instanceof Number)
            return ((Number)v).longValue();
        // Try to convert
        String str = v.toString();
        return Long.parseLong(str);
    }
    
    /**
     * Converts an object value to a long.
     * <P>
     * If the object value supplied is null or if conversion is not possible then the default value is returned.
     * @param v the obect to convert
     * @param defValue the default value if o is null or conversion is not possible 
     * @return the Integer value of o or a default value
     */
    public static long getLong(Object v, long defValue)
    {
        // Check empty
        if (ObjectUtils.isEmpty(v))
            return defValue;
        // Try to convert
        try
        {   // Try to convert
            return toLong(v);
        } catch (Exception e)
        {
        	log.warn(String.format("Cannot convert value [%s] to long!", v));
            return defValue;
        }
    }
    
    /**
     * Converts an object value to a long.
     * <P>
     * If the object value supplied is null or if conversion is not possible then 0 is returned.
     * @param v the object value to convert
     * @return the Long value of o or 0
     */
    public static long getLong(Object v)
    {
        return getLong(v, 0); 
    }

    /**
     * converts an object to a double. If conversion is not possible, an error is thrown
     * @param v the value to convert
     * @return the double value
     */
    public static double toDouble(Object v)
    {
        // Get Double value
        if (ObjectUtils.isEmpty(v))
            return 0.0;
        if (v instanceof Number)
            return ((Number)v).doubleValue();
        // parse String for Integer value
        String val = v.toString(); 
        return Double.parseDouble(val);
    }
    
    /**
     * Converts an object value to a double.
     * <P>
     * If the object value supplied is null or if conversion is not possible then defValue is returned.
     * @param v the object value to convert
     * @param defValue the default value
     * @return the Long value of o or defValue
     */
    public static double getDouble(Object v, double defValue)
    {
        // Check empty
        if (ObjectUtils.isEmpty(v))
            return defValue;
        try
        {   // Try to convert
            return toDouble(v);
        } catch (Exception e)
        {
            log.warn(String.format("Cannot convert value [%s] to double!", v));
            return defValue;
        }
    }

    /**
     * Converts an object value to a double.
     * <P>
     * If the object value supplied is null or if conversion is not possible then 0.0 is returned.
     * @param v the object value to convert
     * @return the Long value of o or 0
     */
    public static double getDouble(Object v)
    {
        return getDouble(v, 0.0);
    }

    /**
     * converts an object to a decimal. If conversion is not possible, an error is thrown
     * @param v the value to convert
     * @return the decimal value
     */
    public static BigDecimal toDecimal(Object v)
    {
        // Get Double value
        if (ObjectUtils.isEmpty(v))
            return null;
        if (v instanceof BigDecimal)
            return ((BigDecimal)v);
        // Find a suitable converter
        if (v instanceof Number)
        {   // check other number types
            if (v instanceof BigInteger)
                return new BigDecimal((BigInteger)v);
            if (v instanceof Integer)
                return BigDecimal.valueOf(((Number)v).intValue());
            if (v instanceof Long)
                return BigDecimal.valueOf(((Number)v).longValue());
            // Default: convert via double
            return BigDecimal.valueOf(((Number)v).doubleValue());
        }
        // parse String for Integer value
        // Last-Chance > Try a string conversion
        return new BigDecimal(v.toString());
    }
    
    /**
     * Converts an object value to a BigDecimal.
     * <P>
     * If the object value supplied is null or if conversion is not possible then defValue is returned.
     * @param v the object value to convert
     * @param defValue the default value
     * @return the BigDecimal value of v or defValue
     */
    public static BigDecimal getDecimal(Object v, BigDecimal defValue)
    {
        // Check empty
        if (ObjectUtils.isEmpty(v))
            return defValue;
        try
        {   // Try to convert
            return toDecimal(v);
        } catch (Exception e)
        {   // Error
            log.warn(String.format("Cannot convert value [%s] to BigDecimal!", v));
            return defValue;
        }
    }

    /**
     * Converts an object value to a BigDecimal.
     * <P>
     * If the object value supplied is null or if conversion is not possible then 0.0 is returned.
     * @param v the object value to convert
     * @return the Long value of o or 0
     */
    public static BigDecimal getDecimal(Object v)
    {
        return getDecimal(v, BigDecimal.ZERO);
    }
    
    /**
     * Converts an object value to a boolean.
     * <P>
     * If the object value supplied is null or if conversion is not possible then false is returned.
     * @param v the object to convert
     * @return the boolean value of o (true or false)
     */
    public static boolean getBoolean(Object v)
    {
        // Get Boolean value
        if (ObjectUtils.isEmpty(v))
            return false;
        if (v instanceof Boolean)
            return ((Boolean)v).booleanValue();
        if (v instanceof Number)
            return (((Number)v).intValue()!=0);
        // parse String for boolean value
        String  val = v.toString(); 
        return (val.equalsIgnoreCase("Y") || val.equalsIgnoreCase("true"));
    }
    
    /**
     * Converts an object value to a Date.
     * <P>
     * If the object value supplied is null or if conversion is not possible then null is returned.
     * @param v the object to convert
     * @param locale the locale used for conversion
     * @return the Date value of o or null
     */
    public static Date getDate(Object v, Locale locale)
    {
        // Get DateTime value
        if (ObjectUtils.isEmpty(v))
            return null;
        if (v instanceof Date)
            return ((Date)v);
        // Get Calendar
        if (v instanceof Number)
        {   // Get Date from a number
            long l = ((Number)v).longValue();
            if (l==0)
                return DateUtils.getDateNow();
            // Year or full date/time?
            /*
            if (l<10000)
            {   // Year only
                Calendar calendar = Calendar.getInstance(getSafeLocale(locale));
                calendar.set((int)l, 1, 1);
                return calendar.getTime();
            }
            */
            // Date from long
            return new Date(l);
        }
        // Try to parse
        return DateUtils.parseDate(v.toString(), locale);
    }
    
    /**
     * Converts an object value to a Date.
     * <P>
     * @param v the object to convert
     * @return the Date value of o or null
     */
    public static Date getDate(Object v)
    {
        // Get DateTime value
        if (ObjectUtils.isEmpty(v))
            return null;
        if (v instanceof java.util.Date)
            return ((java.util.Date)v);
        // Convert from String
        try
        {   String str = v.toString();
            if (str.length() > 10)
                return new SimpleDateFormat(DATETIME_FORMAT).parse(str);
            else
                return new SimpleDateFormat(DATE_FORMAT).parse(str);
        } catch (Exception e)
        {
            log.error("Cannot convert value to date!", e);
            return null;
        }
    }
    
    /**
     * Formats a given date object to a standard date string.
     * The date string is locale independent and has the follwowing format:
     *  "yyyy-MM-dd hh:mm:ss"      
     * 
     * @param date the date to be formated
     * @param withTime indicates whether the date string should include the time or not
     * @return the date string
     */
    public static String formatDate(Date date, boolean withTime)
    {
    	if (withTime)
    		return new SimpleDateFormat(DATETIME_FORMAT).format(date);
    	else
    		return new SimpleDateFormat(DATE_FORMAT).format(date);
    }
    
    /**
     * Generic conversion function that will convert a object to another value type.
     * 
     * @param <T> the type to convert to
     * @param c the class type to convert to
     * @param v the object to convert
     * 
     * @return the Date value of o or null
     * 
     * @throws ClassCastException if the object is not null and is not assignable to the type T.
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Class<T> c, Object v)
        throws ClassCastException
    {
        if (v==null || c.isInstance(v))
            return (T)v;
        // Get Class form Primitive Type
        if (c.isPrimitive())
        {   // Get's the Java Class representing the primitive type
            c = getPrimitiveWrapper(c);
        }    
        // Convert
        if (c.isAssignableFrom(Boolean.class))
            return c.cast(getBoolean(v));
        if (c.isAssignableFrom(Integer.class))
            return c.cast(getInteger(v));
        if (c.isAssignableFrom(Long.class))
            return c.cast(getLong(v));
        if(c.isAssignableFrom(Double.class))
        	return c.cast(getDouble(v));
        if (c.isAssignableFrom(String.class))
            return c.cast(v.toString());
        // other
        return c.cast(v);
    }

    public static boolean isAssignmentCompatible(Class<?> target, Class<?> source)
    {
        // try plain assignment
        if (target.isAssignableFrom(source))
            return true;
        // Get Class form Primitive Type
        if (source.isPrimitive())
        {   // Get's the Java Class representing the primitive type
            source = getPrimitiveWrapper(source);
            if (source == null)
                return false;
            if (target.isAssignableFrom(source))
                return true;
        }
        // Get Class form Primitive Type
        if (target.isPrimitive())
        {   // Get's the Java Class representing the primitive type
            target = getPrimitiveWrapper(target);
            if (target == null)
                return false;
            if (target.isAssignableFrom(source))
                return true;
        }
        // Assume all numeric types can be converted to target class
        Class<Number> numberClass = Number.class;
        if (numberClass.isAssignableFrom(target) &&
            numberClass.isAssignableFrom(source))
        {   // Both are numeric
            return true;
        }
        // Special case: Allow character to string assignment
        if (source==Character.class && target==String.class)
        {
            return true;
        }    
        // Not compatible
        return false;
    }

    @SuppressWarnings("rawtypes")
    private static Class getPrimitiveWrapper(Class primitiveType) {
        // does anyone know a better strategy than comparing names?
        if (boolean.class.equals(primitiveType)) {
            return Boolean.class;
        } else if (float.class.equals(primitiveType)) {
            return Float.class;
        } else if (long.class.equals(primitiveType)) {
            return Long.class;
        } else if (int.class.equals(primitiveType)) {
            return Integer.class;
        } else if (short.class.equals(primitiveType)) {
            return Short.class;
        } else if (byte.class.equals(primitiveType)) {
            return Byte.class;
        } else if (double.class.equals(primitiveType)) {
            return Double.class;
        } else if (char.class.equals(primitiveType)) {
            return Character.class;
        } else {
            
            return null;
        }
    }
    
    /**
     * Generic conversion function that will convert a list to another list type.
     * 
     * @param <T> the type of elements
     * @param t the type class
     * @param source the source collection
     * 
     * @return the new list type
     */
    public static <T> List<T> convert(Class<T> t, Collection<? extends T> source)
    {
        List<T> target = new ArrayList<T>();
        target.addAll(source);
        return target;
    }
    
    /**
     * Converts an Object array to a String array.
     * @param objArray the object array to convert
     * @param defValue default value which will be set for all null objects 
     * @return the String array
     */
    public static String[] toStringArray(Object[] objArray, String defValue)
    {
        if (objArray==null)
            return null;
        String[] strArray = new String[objArray.length];
        for (int i=0; i<objArray.length; i++)
        {
            if (objArray[i]!=null)
                strArray[i]=objArray[i].toString();
            else 
                strArray[i]=defValue;
        }
        return strArray;
    }
    
    /**
     * returns wheter or not a array contains a certain item
     * 
     * @param <T> the ype of the objects in the array
     * @param array the array to search
     * @param item the item to search for
     * 
     * @return true if the array contains the item or false otherwise
     */
    public static <T> boolean contains(T[] array, T item)
    {
        if (array==null)
            return false;
        for (int i=0; i<array.length; i++)
        {
            if (array[i]==item)
                return true;
            if (array[i]!=null && array[i].equals(item))
                return true;
        }
        return false;
    }
    
}
