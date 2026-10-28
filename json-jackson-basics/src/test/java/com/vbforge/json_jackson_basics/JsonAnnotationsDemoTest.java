package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.vbforge.json_jackson_basics.JsonAnnotationsDemo.Coordinates;
import com.vbforge.json_jackson_basics.JsonAnnotationsDemo.Member;
import com.vbforge.json_jackson_basics.JsonAnnotationsDemo.Profile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JsonAnnotationsDemoTest — verifies every concept shown in {@link JsonAnnotationsDemo}.
 *
 * <p>These tests are always GREEN.
 */
@DisplayName("JsonAnnotationsDemo — API contracts")
class JsonAnnotationsDemoTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static JsonNode tree(String json) throws Exception {
        return MAPPER.readTree(json);
    }

    private static Member member(int id, String userName, String email) {
        Member m = new Member();
        m.setId(id);
        m.setUserName(userName);
        m.setEmail(email);
        return m;
    }

    // ── 1. Rename and order ───────────────────────────────────────────────────

    @Nested
    @DisplayName("1. @JsonProperty / @JsonPropertyOrder")
    class RenameTest {

        @Test
        @DisplayName("@JsonProperty renames the key on output")
        void renameOnWrite() throws Exception {
            String json = MAPPER.writeValueAsString(member(1, "ann", "ann@example.com"));
            assertThat(tree(json).has("user_name")).isTrue();
            assertThat(tree(json).has("userName")).isFalse();
        }

        @Test
        @DisplayName("@JsonPropertyOrder fixes key order (unlisted keys follow)")
        void order() throws Exception {
            String json = MAPPER.writeValueAsString(member(1, "ann", "ann@example.com"));
            assertThat(json).isEqualTo(
                    "{\"id\":1,\"user_name\":\"ann\",\"email\":\"ann@example.com\",\"nickname\":null}");
        }

        @Test
        @DisplayName("the renamed key is read back into the Java property")
        void renameOnRead() throws Exception {
            Member back = MAPPER.readValue("{\"id\":2,\"user_name\":\"bob\"}", Member.class);
            assertThat(back.getId()).isEqualTo(2);
            assertThat(back.getUserName()).isEqualTo("bob");
        }

        @Test
        @DisplayName("the old Java name is no longer accepted on input")
        void oldNameRejected() {
            assertThatThrownBy(() -> MAPPER.readValue("{\"userName\":\"bob\"}", Member.class))
                    .isInstanceOf(UnrecognizedPropertyException.class);
        }
    }

    // ── 2. Hiding data ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. @JsonIgnore vs WRITE_ONLY")
    class IgnoreTest {

        @Test
        @DisplayName("@JsonIgnore and WRITE_ONLY properties are never written")
        void notWritten() throws Exception {
            Member m = member(1, "ann", "ann@example.com");
            m.setInternalNote("vip");
            m.setPassword("s3cret");
            JsonNode json = tree(MAPPER.writeValueAsString(m));
            assertThat(json.has("internalNote")).isFalse();
            assertThat(json.has("password")).isFalse();
        }

        @Test
        @DisplayName("WRITE_ONLY property is still read from input")
        void writeOnlyIsRead() throws Exception {
            Member in = MAPPER.readValue("{\"id\":3,\"password\":\"pw\"}", Member.class);
            assertThat(in.getPassword()).isEqualTo("pw");
        }

        @Test
        @DisplayName("@JsonIgnore property is skipped silently on input")
        void ignoredIsSkipped() throws Exception {
            Member in = MAPPER.readValue("{\"id\":3,\"internalNote\":\"hack\"}", Member.class);
            assertThat(in.getInternalNote()).isNull();
        }
    }

    // ── 3. Include rules ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. @JsonInclude")
    class IncludeTest {

        @Test
        @DisplayName("by default null is written; NON_NULL and NON_EMPTY drop it")
        void sparse() throws Exception {
            Profile sparse = new Profile(null, null, Collections.<String>emptyList());
            assertThat(MAPPER.writeValueAsString(sparse)).isEqualTo("{\"city\":null}");
        }

        @Test
        @DisplayName("NON_NULL keeps an empty string; NON_EMPTY keeps a non-empty list")
        void full() throws Exception {
            Profile full = new Profile(null, "", new ArrayList<>(Collections.singletonList("a")));
            assertThat(MAPPER.writeValueAsString(full)).isEqualTo("{\"city\":null,\"bio\":\"\",\"tags\":[\"a\"]}");
        }

        @Test
        @DisplayName("a mapper-wide rule applies without annotations")
        void mapperWide() throws Exception {
            ObjectMapper nonNull = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
            assertThat(nonNull.writeValueAsString(member(1, "ann", "ann@example.com")))
                    .isEqualTo("{\"id\":1,\"user_name\":\"ann\",\"email\":\"ann@example.com\"}");
        }
    }

    // ── 4. Aliases ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. @JsonAlias")
    class AliasTest {

        @Test
        @DisplayName("every alias fills the same property")
        void aliases() throws Exception {
            for (String key : new String[]{"email", "mail", "email_address"}) {
                Member m = MAPPER.readValue("{\"" + key + "\":\"a@x.io\"}", Member.class);
                assertThat(m.getEmail()).as(key).isEqualTo("a@x.io");
            }
        }

        @Test
        @DisplayName("output always uses the primary name")
        void primaryOnWrite() throws Exception {
            JsonNode json = tree(MAPPER.writeValueAsString(member(1, "ann", "a@x.io")));
            assertThat(json.has("email")).isTrue();
            assertThat(json.has("mail")).isFalse();
        }
    }

    // ── 5. @JsonCreator ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. @JsonCreator")
    class CreatorTest {

        @Test
        @DisplayName("an immutable class is read through its annotated constructor")
        void readImmutable() throws Exception {
            Coordinates c = MAPPER.readValue("{\"lon\":30.52,\"lat\":50.45}", Coordinates.class);
            assertThat(c.getLat()).isEqualTo(50.45);
            assertThat(c.getLon()).isEqualTo(30.52);
        }

        @Test
        @DisplayName("it is written with the same property names")
        void writeImmutable() throws Exception {
            String json = MAPPER.writeValueAsString(new Coordinates(50.45, 30.52));
            assertThat(tree(json)).isEqualTo(tree("{\"lat\":50.45,\"lon\":30.52}"));
        }
    }

    // ── Demo runs end-to-end ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Demo main()")
    class MainRunsTest {

        @Test
        @DisplayName("runs every section without throwing")
        void runs() {
            assertThatCode(() -> JsonAnnotationsDemo.main(new String[0])).doesNotThrowAnyException();
        }
    }
}
