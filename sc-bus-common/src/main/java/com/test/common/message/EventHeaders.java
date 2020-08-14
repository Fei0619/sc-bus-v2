package com.test.common.message;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

/**
 * @author 费世程
 * @date 2020/8/13 14:48
 */
public class EventHeaders implements Map<String, Set<String>>, Serializable, Cloneable {

  private final Map<String, Set<String>> targetMap;

  public static EventHeaders create() {
    return new EventHeaders();
  }

  private EventHeaders() {
    this.targetMap = new LinkedHashMap<>();
  }

  public EventHeaders add(String key, String value) {
    Set<String> values = this.targetMap.computeIfAbsent(key, k -> new LinkedHashSet<>());
    values.add(value);
    return this;
  }

  public EventHeaders addAll(String key, Collection<String> values) {
    Set<String> currentValues = this.targetMap.computeIfAbsent(key, k -> new LinkedHashSet<>());
    currentValues.addAll(values);
    return this;
  }

  @Override
  public int size() {
    return targetMap.size();
  }

  @Override
  public boolean isEmpty() {
    return targetMap.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return targetMap.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return targetMap.containsValue(value);
  }

  @Override
  public Set<String> get(Object key) {
    return targetMap.get(key);
  }

  @Nullable
  @Override
  public Set<String> put(String key, Set<String> value) {
    return targetMap.putIfAbsent(key, value);
  }

  @Override
  public Set<String> remove(Object key) {
    return targetMap.remove(key);
  }

  @Override
  public void putAll(@NotNull Map<? extends String, ? extends Set<String>> m) {
    targetMap.putAll(m);
  }

  @Override
  public void clear() {
    targetMap.clear();
  }

  @NotNull
  @Override
  public Set<String> keySet() {
    return targetMap.keySet();
  }

  @NotNull
  @Override
  public Collection<Set<String>> values() {
    return targetMap.values();
  }

  @NotNull
  @Override
  public Set<Entry<String, Set<String>>> entrySet() {
    return targetMap.entrySet();
  }

}
