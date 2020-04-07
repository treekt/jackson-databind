package com.fasterxml.jackson.failing;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class Inject2678Test extends BaseMapTest
{
    protected static class Some {
        private final String field1;

        @JacksonInject(value = "defaultValueForField2", useInput = OptBoolean.TRUE)
        private final String field2;

        public Some(@JsonProperty("field1") final String field1,
                  @JsonProperty("field2") @JacksonInject(value = "defaultValueForField2",
                            useInput = OptBoolean.TRUE) final String field2) {
             this.field1 = Objects.requireNonNull(field1);
             this.field2 = Objects.requireNonNull(field2);
        }

        public String getField1() {
             return field1;
        }

        public String getField2() {
             return field2;
        }
    }

    public void testReadValueInjectables() throws Exception {
        final InjectableValues injectableValues =
                  new InjectableValues.Std().addValue("defaultValueForField2", "somedefaultValue");
        final ObjectMapper mapper = JsonMapper.builder()
                .injectableValues(injectableValues)
                .build();

        final Some actualValueMissing = mapper.readValue("{\"field1\": \"field1value\"}", Some.class);
        assertEquals(actualValueMissing.getField1(), "field1value");
        assertEquals(actualValueMissing.getField2(), "somedefaultValue");

        final Some actualValuePresent = mapper.readValue(
                "{\"field1\": \"field1value\", \"field2\": \"field2value\"}", Some.class);
        assertEquals(actualValuePresent.getField1(), "field1value");
        assertEquals(actualValuePresent.getField2(), "somedefaultValue");

        // if I comment @JacksonInject that is next to the property the valid assert is the correct one:
        assertEquals(actualValuePresent.getField2(), "field2value");
   }
}
