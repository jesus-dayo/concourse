/*
 * Copyright (c) 2013-2016 Cinchapi Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cinchapi.concourse.thrift;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.cinchapi.concourse.util.Convert;
import com.cinchapi.concourse.util.Random;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Unit tests for {@link ComplexTObject}.
 * 
 * @author Jeff Nelson
 */
public class ComplexTObjectTest {

    @Test
    public void testSerializeString() {
        String expected = Random.getString();
        String actual = ComplexTObject.fromJavaObject(expected).getJavaObject();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSerializePrimitive() {
        Object expected = Random.getObject();
        Object actual = ComplexTObject.fromJavaObject(expected).getJavaObject();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSerializeListBasic() {
        List<? extends Object> expected = Lists.<Object> newArrayList(1, 2, 3,
                4, 5, 6, 7, 8, "9");
        List<? extends Object> actual = ComplexTObject.fromJavaObject(expected)
                .getJavaObject();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSerializeList() {
        int count = Random.getScaleCount();
        List<Object> expected = Lists.newArrayListWithCapacity(count);
        for (int i = 0; i < count; ++i) {
            expected.add(Random.getObject());
        }
        List<Object> actual = ComplexTObject.fromJavaObject(expected)
                .getJavaObject();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSerializeSet() {
        int count = Random.getScaleCount();
        Set<Object> expected = Sets.newHashSet();
        for (int i = 0; i < count; ++i) {
            expected.add(Random.getObject());
        }
        Set<Object> actual = ComplexTObject.fromJavaObject(expected)
                .getJavaObject();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSerializeListOfLists() {
        List<? extends Object> expected = Lists.<Object> newArrayList("1",
                true, 1, Lists.<Object> newArrayList(1, 2, 3, "4"),
                Lists.newArrayList(1, 2), Sets.<Object> newHashSet("1", true));
        List<? extends Object> actual = ComplexTObject.fromJavaObject(expected)
                .getJavaObject();;
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSerializeSetOfSets() {
        Set<? extends Object> expected = Sets.<Object> newHashSet("1", true, 1,
                Sets.<Object> newHashSet(1, 2, 3, "4"), Sets.newHashSet(1, 2),
                Lists.<Object> newArrayList("1", true));
        Set<? extends Object> actual = ComplexTObject.fromJavaObject(expected)
                .getJavaObject();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSerializeTreeSet() {
        TreeSet<Integer> expected = Sets.newTreeSet();
        expected.add(3);
        expected.add(1);
        expected.add(2);
        expected.add(4);
        Set<Integer> actual = ComplexTObject.fromJavaObject(expected)
                .getJavaObject();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSerializeMap() {
        Map<Object, Object> expected = Maps.newHashMap();
        int count = Random.getScaleCount();
        for (int i = 0; i < count; ++i) {
            Object key = Random.getObject();
            Object value = Random.getObject();
            expected.put(key, value);
        }
        Map<Object, Object> actual = ComplexTObject.fromJavaObject(expected)
                .getJavaObject();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSerializeTObject() {
        TObject expected = Convert.javaToThrift(Random.getObject());
        ComplexTObject complex = ComplexTObject.fromJavaObject(expected);
        Assert.assertEquals(expected, complex.getJavaObject());
    }

}