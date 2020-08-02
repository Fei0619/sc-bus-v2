package com.test.common.json

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode


fun <T> T.toJsonString(ignoreNull: Boolean = true, pretty: Boolean = false) =
    JsonUtils.toJsonString(this, ignoreNull, pretty)

fun <T> String.parseJson(type: TypeReference<T>) = JsonUtils.parseJson(this, type)

fun <T> String.parseJson(clazz: Class<T>) = JsonUtils.parseJson(this, clazz)

fun <T> String.parseJson(parametrized: Class<out Any>, parameterClass: Class<out Any>): T {
  return JsonUtils.parseJson<T>(this, parametrized, parameterClass)
}

fun <T> String.parseJson(
  parametrized: Class<out Any>,
  parameterClass1: Class<out Any>,
  parameterClass2: Class<out Any>
): T {
  return JsonUtils.parseJson<T>(this, parametrized, parameterClass1, parameterClass2)
}

@Suppress("unused")
fun <E> String.parseJsonToList(clazz: Class<E>) = JsonUtils.parseArray(this, clazz)

fun <E> String.parseJsonToSet(clazz: Class<E>) = JsonUtils.parseSet(this, clazz)

fun <K, V> String.parseJsonToMap(
  keyClass: Class<K>,
  valueClass: Class<V>,
  mapClass: Class<out Map<out Any, Any>> = HashMap::class.java
): Map<K, V> {
  return JsonUtils.parseMap(this, keyClass, valueClass, mapClass)
}

fun String.readJsonTree(): JsonNode = JsonUtils.ignoreNullMapper.readTree(this)