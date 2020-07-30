package com.test.common.json

import com.test.common.date.DateFormatter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.test.common.date.DatePattern
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * JSON工具类
 * 依赖jackson
 *
 * @author 宋志宗
 * @date 2019-01-30 09:51
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object JsonUtils {

  val javaTimeModule: SimpleModule = JavaTimeModule()
      .addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateFormatter.of(DatePattern.yyyy_MM_dd_HH_mm_ss)))
      .addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DateFormatter.of(DatePattern.yyyy_MM_dd_HH_mm_ss)))
      .addSerializer(LocalDate::class.java, LocalDateSerializer(DateFormatter.of(DatePattern.yyyy_MM_dd)))
      .addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateFormatter.of(DatePattern.yyyy_MM_dd)))
      .addSerializer(LocalTime::class.java, LocalTimeSerializer(DateFormatter.of(DatePattern.HH_mm_ss)))
      .addDeserializer(LocalTime::class.java, LocalTimeDeserializer(DateFormatter.of(DatePattern.HH_mm_ss)))

  private val mapper: ObjectMapper = ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .registerModule(javaTimeModule)
      .findAndRegisterModules()

  internal val ignoreNullMapper: ObjectMapper = ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)
      .registerModule(javaTimeModule)
      .findAndRegisterModules()


  /**
   * 将对象转换为jsonString
   */
  @JvmOverloads
  fun <T> toJsonString(t: T, ignoreNull: Boolean = true, pretty: Boolean = false): String {
    val writer = if (ignoreNull) {
      ignoreNullMapper
    } else {
      mapper
    }
    return if (pretty) {
      writer.writerWithDefaultPrettyPrinter().writeValueAsString(t)
    } else {
      writer.writeValueAsString(t)
    }
  }

  /**
   * 将json字符串[jsonString]解析为类型[clazz]的实例
   */
  fun <T> parseJson(jsonString: String, clazz: Class<T>): T = ignoreNullMapper.readValue(jsonString, clazz)

  fun <T> parseJson(jsonString: String, javaType: JavaType): T = ignoreNullMapper.readValue(jsonString, javaType)

  /**
   * 将json字符串[jsonString]解析为[type]对应的实例
   */
  fun <T> parseJson(jsonString: String, type: TypeReference<T>): T = ignoreNullMapper.readValue(jsonString, type)

  /**
   * 将json字符串[jsonString]解析为对象
   * 例如需要解析一个Result<User>对象:
   * val result: Result<User> = JsonUtils.parseJson(jsonString, Result::class.java, User::class.java)
   *
   * @param jsonString json字符串
   * @param parametrized 解析后的对象类型 如上例中的Result
   * @param parameterClass 解析后的对象的泛型类型 如上例中的User
   */
  fun <T> parseJson(jsonString: String, parametrized: Class<out Any>, parameterClass: Class<out Any>): T {
    val javaType = ignoreNullMapper.typeFactory.constructParametricType(parametrized, parameterClass)
    return ignoreNullMapper.readValue(jsonString, javaType)
  }

  /**
   * 将json字符串[jsonString]解析为对象
   * 例如需要解析一个Result<User>对象:
   * val result: Result<List<User>> = JsonUtils.parseJson(jsonString, Result::class.java, List::class.java, User::class.java)
   *
   * @param jsonString json字符串
   * @param parametrized 解析后的对象类型 如上例中的Result
   * @param parameterClass 解析后的对象的泛型类型 如上例中的List
   * @param parameterClass2 解析后的对象的泛型类型 如上例中的User
   */
  fun <T> parseJson(
      jsonString: String, parametrized: Class<out Any>,
      parameterClass: Class<out Any>, parameterClass2: Class<out Any>
  ): T {
    ignoreNullMapper.reader()
    val typeFactory = ignoreNullMapper.typeFactory
    val javaType = typeFactory.constructParametricType(parameterClass, parameterClass2)
    val type = typeFactory.constructParametricType(parametrized, javaType)
    return ignoreNullMapper.readValue(jsonString, type)
  }

  /**
   * 将json字符串[jsonString]解析为类[clazz]的实例数组
   */
  fun <E> parseArray(jsonString: String, clazz: Class<E>): List<E> {
    val javaType = ignoreNullMapper.typeFactory.constructParametricType(List::class.java, clazz)
    return ignoreNullMapper.readValue(jsonString, javaType)
  }

  fun <E> parseSet(jsonString: String, clazz: Class<E>): Set<E> {
    val javaType = ignoreNullMapper.typeFactory.constructParametricType(HashSet::class.java, clazz)
    return ignoreNullMapper.readValue(jsonString, javaType)
  }

  @JvmOverloads
  fun <K, V> parseMap(
      jsonString: String?,
      keyClass: Class<K>,
      valueClass: Class<V>,
      mapClass: Class<out Map<out Any, Any>> = HashMap::class.java
  ): Map<K, V> {
    return if (jsonString?.isBlank() != false) {
      emptyMap()
    } else {
      val type = ignoreNullMapper.typeFactory.constructMapType(mapClass, keyClass, valueClass)
      return ignoreNullMapper.readValue(jsonString, type)
    }
  }

  fun <T> parsePackMap(
      jsonString: String,
      packClass: Class<out Any>,
      keyClass: Class<out Any>,
      valueClass: Class<out Any>,
      mapClass: Class<out Map<out Any, Any>> = HashMap::class.java
  ): T {
    val mapType = ignoreNullMapper.typeFactory.constructMapType(mapClass, keyClass, valueClass)
    val javaType = ignoreNullMapper.typeFactory.constructParametricType(packClass, mapType)
    return ignoreNullMapper.readValue(jsonString, javaType)
  }

  fun readTree(jsonString: String): JsonNode {
    return ignoreNullMapper.readTree(jsonString)
  }


  fun getJavaType(type: Type): JavaType {
    return if (type is ParameterizedType) {
      val actualTypeArguments = type.actualTypeArguments
      val rowClass = type.rawType as Class<*>
      val javaTypes = arrayOfNulls<JavaType>(actualTypeArguments.size)
      for (i in actualTypeArguments.indices) {
        javaTypes[i] = getJavaType(actualTypeArguments[i])
      }
      TypeFactory.defaultInstance().constructParametricType(rowClass, *javaTypes)
    } else {
      val cla = type as Class<*>
      TypeFactory.defaultInstance().constructParametricType(cla, *arrayOfNulls<JavaType>(0))
    }
  }
}