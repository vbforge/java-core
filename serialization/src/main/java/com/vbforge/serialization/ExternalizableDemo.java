package com.vbforge.serialization;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

/**
 * ExternalizableDemo — full manual control of the serialized format with {@link Externalizable}.
 *
 * <p>Read top-to-bottom. Compare every section with {@link ObjectStreamsDemo} and
 * {@link TransientAndVersionDemo}, where the JVM did the work automatically.
 *
 * <p>Topics:
 * <ol>
 *   <li>Externalizable basics — {@code writeExternal} / {@code readExternal} and the constructor call</li>
 *   <li>Stream size — Externalizable vs default Serializable</li>
 *   <li>The public no-arg constructor rule</li>
 *   <li>Custom format and manual versioning</li>
 * </ol>
 */
public class ExternalizableDemo {

    public static void main(String[] args) throws Exception {
        externalizableBasicsDemo();
        System.out.println("---");
        sizeComparisonDemo();
        System.out.println("---");
        noArgConstructorRuleDemo();
        System.out.println("---");
        customFormatVersioningDemo();
    }

    // ── Model classes used by the demos (public so tests can reuse them) ──────

    /**
     * Externalizable: YOU write and read every field. Nothing is automatic —
     * not even {@code transient} matters here, because the JVM does not look at fields at all.
     */
    public static class ExternalizableGeoPoint implements Externalizable {
        public static final String DEFAULT_LABEL = "label-from-no-arg-constructor";
        private static final long serialVersionUID = 1L;

        private int latitude;
        private int longitude;
        private int altitude;
        private String label;               // NOT written by writeExternal

        /** REQUIRED: public, no arguments. The JVM calls it before readExternal. */
        public ExternalizableGeoPoint() {
            this.label = DEFAULT_LABEL;
        }

        public ExternalizableGeoPoint(int latitude, int longitude, int altitude, String label) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
            this.label = label;
        }

        public int getLatitude()  { return latitude; }
        public int getLongitude() { return longitude; }
        public int getAltitude()  { return altitude; }
        public String getLabel()  { return label; }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(latitude);
            out.writeInt(longitude);
            out.writeInt(altitude);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException {
            latitude = in.readInt();        // SAME order as writeExternal
            longitude = in.readInt();
            altitude = in.readInt();
        }
    }

    /** The same data with default Serializable — {@code label} is transient to keep the comparison fair. */
    public static class SerializableGeoPoint implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int latitude;
        private final int longitude;
        private final int altitude;
        private final transient String label;

        public SerializableGeoPoint(int latitude, int longitude, int altitude, String label) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
            this.label = label;
        }

        public int getLatitude()  { return latitude; }
        public int getLongitude() { return longitude; }
        public int getAltitude()  { return altitude; }
        public String getLabel()  { return label; }
    }

    /** Broken: Externalizable but with NO public no-arg constructor. */
    public static class NoDefaultCtorExternal implements Externalizable {
        private static final long serialVersionUID = 1L;

        private int value;

        public NoDefaultCtorExternal(int value) { this.value = value; }

        public int getValue() { return value; }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException { out.writeInt(value); }

        @Override
        public void readExternal(ObjectInput in) throws IOException { value = in.readInt(); }
    }

    /** Broken: has a no-arg constructor, but it is package-private instead of public. */
    public static class HiddenCtorExternal implements Externalizable {
        private static final long serialVersionUID = 1L;

        private int value;

        HiddenCtorExternal() { }                                       // not public!

        public HiddenCtorExternal(int value) { this.value = value; }

        public int getValue() { return value; }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException { out.writeInt(value); }

        @Override
        public void readExternal(ObjectInput in) throws IOException { value = in.readInt(); }
    }

    /** Externalizable with its own format version byte, so old streams stay readable. */
    public static class UserProfile implements Externalizable {
        public static final int CURRENT_VERSION = 2;
        public static final String UNKNOWN_EMAIL = "unknown";
        private static final long serialVersionUID = 1L;

        private String name;
        private String email;
        private int formatVersion = CURRENT_VERSION;   // which layout writeExternal produces

        public UserProfile() { }

        public UserProfile(String name, String email) {
            this.name = name;
            this.email = email;
        }

        /** Creates an object that writes the OLD version-1 layout (name only). */
        static UserProfile legacyV1(String name) {
            UserProfile profile = new UserProfile(name, null);
            profile.formatVersion = 1;
            return profile;
        }

        public String getName()  { return name; }
        public String getEmail() { return email; }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeByte(formatVersion);          // version FIRST, so readers know what follows
            out.writeUTF(name);
            if (formatVersion >= 2) {
                out.writeUTF(email);
            }
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException {
            int version = in.readByte();
            name = in.readUTF();
            email = (version >= 2) ? in.readUTF() : UNKNOWN_EMAIL;
        }
    }

    // ── 1. Externalizable basics ──────────────────────────────────────────────

    /**
     * {@link Externalizable} extends {@link Serializable} and replaces the automatic
     * mechanism with two methods you write yourself:
     * <pre>
     *   void writeExternal(ObjectOutput out)   // write the state you care about
     *   void readExternal(ObjectInput in)      // read it back, same order
     * </pre>
     *
     * <p>WHY this matters: you decide EXACTLY what goes into the stream and in which layout —
     * useful for compact formats, for backward compatibility, or for fields that default
     * serialization cannot handle.
     *
     * <p>The big difference from plain Serializable: on deserialization the JVM calls the
     * class's PUBLIC NO-ARG CONSTRUCTOR first and then {@code readExternal}. Anything you did
     * not write comes from that constructor — not from the original object.
     *
     * <p>Common gotcha: with Serializable a skipped field becomes {@code null}/{@code 0}
     * (no constructor runs); with Externalizable it becomes whatever the constructor assigned.
     */
    static void externalizableBasicsDemo() throws IOException, ClassNotFoundException {
        System.out.println("=== Externalizable basics ===");

        ExternalizableGeoPoint original = new ExternalizableGeoPoint(50, 30, 179, "original-label");
        ExternalizableGeoPoint copy = ObjectStreamsDemo.roundTrip(original);
        System.out.println("lat / lon / alt (written)   : " + copy.getLatitude() + " / " + copy.getLongitude() + " / " + copy.getAltitude());
        System.out.println("label (not written)         : " + copy.getLabel());       // label-from-no-arg-constructor

        SerializableGeoPoint plain = new SerializableGeoPoint(50, 30, 179, "original-label");
        SerializableGeoPoint plainCopy = ObjectStreamsDemo.roundTrip(plain);
        System.out.println("Serializable, transient     : " + plainCopy.getLabel()); // null — no constructor ran

        System.out.println("Externalizable is Serializable: " + (original instanceof Serializable)); // true
    }

    // ── 2. Stream size ────────────────────────────────────────────────────────

    /**
     * Default serialization writes a description of every field (type + name) into the
     * stream next to the values. Externalizable writes only the bytes YOU write.
     *
     * <p>WHY this matters: for classes with many fields, or millions of small objects,
     * the metadata overhead is significant.
     *
     * <p>Common gotcha: the saving costs you maintenance. Every field you add must be
     * written AND read by hand, and forgetting one is a silent bug.
     */
    static void sizeComparisonDemo() throws IOException {
        System.out.println("=== Stream size ===");

        int externalSize = ObjectStreamsDemo.toBytes(new ExternalizableGeoPoint(50, 30, 179, "x")).length;
        int defaultSize  = ObjectStreamsDemo.toBytes(new SerializableGeoPoint(50, 30, 179, "x")).length;

        System.out.println("Externalizable stream       : " + externalSize + " bytes");
        System.out.println("Default Serializable stream : " + defaultSize + " bytes");
        System.out.println("Externalizable is smaller   : " + (externalSize < defaultSize)); // true
    }

    // ── 3. The public no-arg constructor rule ─────────────────────────────────

    /**
     * Because Externalizable deserialization starts by calling the no-arg constructor, that
     * constructor must exist AND be {@code public}.
     *
     * <p>WHY this matters: the failure is easy to miss — the class compiles, and
     * serialization succeeds. Only deserialization throws {@link InvalidClassException}
     * ("no valid constructor").
     *
     * <p>Common gotcha: a package-private or private no-arg constructor is not enough.
     */
    static void noArgConstructorRuleDemo() throws IOException, ClassNotFoundException {
        System.out.println("=== Public no-arg constructor rule ===");

        byte[] noCtor = ObjectStreamsDemo.toBytes(new NoDefaultCtorExternal(1));
        byte[] hidden = ObjectStreamsDemo.toBytes(new HiddenCtorExternal(2));
        System.out.println("both classes serialize fine : " + noCtor.length + " and " + hidden.length + " bytes");

        try {
            ObjectStreamsDemo.fromBytes(noCtor);
        } catch (InvalidClassException e) {
            System.out.println("no no-arg constructor       : InvalidClassException -> " + e.getMessage());
        }
        try {
            ObjectStreamsDemo.fromBytes(hidden);
        } catch (InvalidClassException e) {
            System.out.println("non-public no-arg constructor: InvalidClassException -> " + e.getMessage());
        }
    }

    // ── 4. Custom format and manual versioning ────────────────────────────────

    /**
     * Since YOU own the layout, you can version it yourself: write a format number first,
     * and let {@code readExternal} branch on it. Old streams keep working after the class
     * gains new fields.
     *
     * <p>WHY this matters: default serialization offers only the {@code serialVersionUID}
     * check — it matches or the read fails. A version byte of your own gives you finer control.
     *
     * <p>Common gotcha: a version byte only helps if it is there from the start. Streams
     * written before you introduced it cannot be told apart.
     */
    static void customFormatVersioningDemo() throws IOException, ClassNotFoundException {
        System.out.println("=== Custom format and versioning ===");

        UserProfile current = ObjectStreamsDemo.roundTrip(new UserProfile("alice", "alice@example.com"));
        System.out.println("v2 stream : " + current.getName() + ", " + current.getEmail());   // alice, alice@example.com

        UserProfile legacy = (UserProfile) ObjectStreamsDemo.fromBytes(
                ObjectStreamsDemo.toBytes(UserProfile.legacyV1("bob")));
        System.out.println("v1 stream : " + legacy.getName() + ", " + legacy.getEmail());    // bob, unknown
    }
}
