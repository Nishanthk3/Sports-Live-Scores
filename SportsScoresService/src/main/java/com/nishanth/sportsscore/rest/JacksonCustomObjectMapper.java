package com.nishanth.sportsscore.rest;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator.Feature;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;

public class JacksonCustomObjectMapper extends ObjectMapper {


	public JacksonCustomObjectMapper() {
		setOptions();
	}

	@SuppressWarnings("deprecation")
	private void setOptions() {
		AnnotationIntrospector introspector = new JacksonAnnotationIntrospector();  
		this.configure(Feature.QUOTE_FIELD_NAMES, true);
		this.configure(Feature.QUOTE_NON_NUMERIC_NUMBERS, true);

		this.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
		this.configure(DeserializationConfig.Feature.USE_ANNOTATIONS, true);
		this.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.configure(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING, true);
		this.configure(DeserializationConfig.Feature.USE_GETTERS_AS_SETTERS, true);
		this.setDeserializationConfig(
				this.getDeserializationConfig().withAnnotationIntrospector(introspector));

		this.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		this.configure(SerializationConfig.Feature.USE_ANNOTATIONS, true);
		this.configure(SerializationConfig.Feature.REQUIRE_SETTERS_FOR_GETTERS, true);
		this.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
		this.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES, false);
		this.configure(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING, false);
		this.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		this.setSerializationConfig(
				this.getSerializationConfig().withAnnotationIntrospector(introspector));
	}
}