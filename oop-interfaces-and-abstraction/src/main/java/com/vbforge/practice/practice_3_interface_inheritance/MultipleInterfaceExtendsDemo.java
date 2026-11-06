package com.vbforge.practice.practice_3_interface_inheritance;

/**
 * An interface can extend SEVERAL interfaces (interface A extends B, C).
 * One implementer then satisfies all three contracts and can be viewed through any of them.
 */
public class MultipleInterfaceExtendsDemo {

    interface JsonSerializable { String serialize(); }

    interface Auditable { String getAuditInfo(); }

    interface StorageItem extends JsonSerializable, Auditable {
        String getId();
    }

    static class DocItem implements StorageItem {
        private final String id;

        DocItem(String id) { this.id = id; }

        @Override public String getId()        { return id; }
        @Override public String serialize()    { return "{id:" + id + "}"; }
        @Override public String getAuditInfo() { return "DocItem#" + id + " accessed"; }
    }

    public static void main(String[] args) {
        StorageItem item = new DocItem("DOC-001");
        System.out.println("id:        " + item.getId());
        System.out.println("serialize: " + item.serialize());
        System.out.println("auditInfo: " + item.getAuditInfo());

        Auditable asAuditable = item;           // narrower view of the same object
        System.out.println("as Auditable: " + asAuditable.getAuditInfo());
    }
}
