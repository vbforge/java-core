package com.vbforge.mockito_and_tdd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ArgumentCaptorsDemo — annotated reference for {@code ArgumentCaptor}: grabbing the
 * objects a collaborator was <em>called with</em> so you can inspect them.
 *
 * <p>As in {@code MockitoBasicsDemo}, Mockito is test-scoped, so each section here
 * runs the system under test against a hand-written recorder (which is exactly what an
 * {@code ArgumentCaptor} is), and the live Mockito code is in
 * {@code ArgumentCaptorsDemoTest}.
 *
 * <p>Topics:
 * <ol>
 *   <li>Capturing one argument — the hand-written recorder behind {@code captor.getValue()}</li>
 *   <li>Capturing many calls — {@code getAllValues()}</li>
 *   <li>Captor vs matcher — {@code argThat} for a yes/no check, a captor for rich assertions</li>
 *   <li>Gotcha — a captor stores references, not snapshots</li>
 * </ol>
 *
 * <p>The system under test is {@link OrderService}: it <em>builds</em> an {@link Order}
 * internally and hands it to a repository. The test never sees that object — unless it
 * captures it.
 */
public class ArgumentCaptorsDemo {

    public static void main(String[] args) {
        captureOneDemo();
        System.out.println("---");
        captureManyDemo();
        System.out.println("---");
        captorVsMatcherDemo();
        System.out.println("---");
        referenceNotSnapshotDemo();
    }

    // ── Domain ────────────────────────────────────────────────────────────────

    /** One line of an order. {@code lineTotalCents()} = quantity × unit price. */
    public static final class OrderLine {
        private final String sku;
        private final int quantity;
        private final long unitCents;

        public OrderLine(String sku, int quantity, long unitCents) {
            this.sku = sku;
            this.quantity = quantity;
            this.unitCents = unitCents;
        }

        public String getSku()       { return sku; }
        public int getQuantity()     { return quantity; }
        public long getUnitCents()   { return unitCents; }
        public long lineTotalCents() { return quantity * unitCents; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OrderLine)) return false;
            OrderLine other = (OrderLine) o;
            return quantity == other.quantity && unitCents == other.unitCents && sku.equals(other.sku);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sku, quantity, unitCents);
        }

        @Override
        public String toString() {
            return quantity + "x" + sku + "@" + unitCents;
        }
    }

    /** An immutable order. The total is stored, not recomputed. */
    public static final class Order {
        private final String customerId;
        private final List<OrderLine> lines;
        private final long totalCents;

        public Order(String customerId, List<OrderLine> lines, long totalCents) {
            this.customerId = customerId;
            this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
            this.totalCents = totalCents;
        }

        public String getCustomerId()  { return customerId; }
        public List<OrderLine> getLines() { return lines; }
        public long getTotalCents()    { return totalCents; }

        @Override
        public String toString() {
            return "Order[" + customerId + ", " + lines + ", total=" + totalCents + "]";
        }
    }

    public interface OrderRepository {
        void save(Order order);
    }

    public interface EventPublisher {
        void publish(String topic, String payload);
    }

    public interface AuditLog {
        void record(List<String> events);
    }

    /** The system under test: builds the order itself, returns nothing. */
    public static class OrderService {
        private final OrderRepository repository;
        private final EventPublisher publisher;

        public OrderService(OrderRepository repository, EventPublisher publisher) {
            this.repository = repository;
            this.publisher = publisher;
        }

        public void placeOrder(String customerId, List<OrderLine> lines) {
            if (lines.isEmpty()) {
                throw new IllegalArgumentException("order needs at least one line");
            }
            long total = 0;
            for (OrderLine line : lines) {
                total += line.lineTotalCents();
            }
            repository.save(new Order(customerId, lines, total));
            publisher.publish("orders.placed", customerId + ":" + total);
        }

        public void placeBatch(String customerId, List<List<OrderLine>> batch) {
            for (List<OrderLine> lines : batch) {
                placeOrder(customerId, lines);
            }
        }
    }

    /**
     * Groups events into batches of two and sends each batch to the audit log.
     * Deliberately reuses one buffer — the gotcha lives here.
     */
    public static class BatchAuditor {
        private final AuditLog log;

        public BatchAuditor(AuditLog log) {
            this.log = log;
        }

        /** Hands the log the SAME list object every time, clearing it between batches. */
        public void auditReusingBuffer(List<String> events) {
            List<String> buffer = new ArrayList<>();
            for (String event : events) {
                buffer.add(event);
                if (buffer.size() == 2) {
                    log.record(buffer);
                    buffer.clear();
                }
            }
            if (!buffer.isEmpty()) {
                log.record(buffer);
            }
        }

        /** Same batching, but each call gets its own copy. */
        public void auditWithCopies(List<String> events) {
            List<String> buffer = new ArrayList<>();
            for (String event : events) {
                buffer.add(event);
                if (buffer.size() == 2) {
                    log.record(new ArrayList<>(buffer));
                    buffer.clear();
                }
            }
            if (!buffer.isEmpty()) {
                log.record(new ArrayList<>(buffer));
            }
        }
    }

    // ── Hand-made recorders (what ArgumentCaptor does for you) ────────────────

    /** Remembers every Order it was asked to save — a hand-written captor. */
    public static class RecordingOrderRepository implements OrderRepository {
        private final List<Order> saved = new ArrayList<>();

        @Override
        public void save(Order order) {
            saved.add(order);
        }

        public List<Order> saved() {
            return Collections.unmodifiableList(saved);
        }
    }

    /** Remembers every (topic, payload) pair. */
    public static class RecordingEventPublisher implements EventPublisher {
        private final List<String> topics = new ArrayList<>();
        private final List<String> payloads = new ArrayList<>();

        @Override
        public void publish(String topic, String payload) {
            topics.add(topic);
            payloads.add(payload);
        }

        public List<String> topics()   { return Collections.unmodifiableList(topics); }
        public List<String> payloads() { return Collections.unmodifiableList(payloads); }
    }

    /** Stores the references it receives — exactly like ArgumentCaptor does. */
    public static class RecordingAuditLog implements AuditLog {
        private final List<List<String>> received = new ArrayList<>();

        @Override
        public void record(List<String> events) {
            received.add(events);
        }

        public List<List<String>> received() {
            return Collections.unmodifiableList(received);
        }
    }

    // ── 1. Capturing one argument ─────────────────────────────────────────────

    /**
     * {@code placeOrder} returns {@code void} and builds the {@link Order} itself, so
     * there is no return value to assert on. The only way to look at the order is to
     * intercept it on its way into {@code repository.save(...)}.
     *
     * <p>Mockito:
     * <pre>
     *   ArgumentCaptor&lt;Order&gt; captor = ArgumentCaptor.forClass(Order.class);
     *   verify(repository).save(captor.capture());   // capture() goes INSIDE verify
     *   Order saved = captor.getValue();             // then assert on it with AssertJ
     * </pre>
     *
     * <p>WHY this matters: it separates <em>"was it called?"</em> from <em>"was it called
     * with the right content?"</em> — and lets you use rich assertions (field by field,
     * with readable failure messages) instead of one opaque {@code equals} match.
     *
     * <p>Common gotcha: call {@code capture()} only in {@code verify(...)}. Capturing in
     * a {@code when(...)} stub is unsupported and gives confusing results.
     */
    static void captureOneDemo() {
        System.out.println("=== Capture one ===");

        RecordingOrderRepository repository = new RecordingOrderRepository();
        RecordingEventPublisher publisher = new RecordingEventPublisher();
        OrderService service = new OrderService(repository, publisher);

        service.placeOrder("cust-1", Arrays.asList(
                new OrderLine("pen", 3, 150), new OrderLine("ink", 1, 250)));

        Order saved = repository.saved().get(0);
        System.out.println("customer      : " + saved.getCustomerId());   // cust-1
        System.out.println("total         : " + saved.getTotalCents());   // 700
        System.out.println("topic         : " + publisher.topics().get(0));
        System.out.println("payload       : " + publisher.payloads().get(0));  // cust-1:700
    }

    // ── 2. Capturing many calls ───────────────────────────────────────────────

    /**
     * When the mock is called several times, one captor collects every argument.
     *
     * <p>Mockito:
     * <pre>
     *   verify(repository, times(2)).save(captor.capture());
     *   captor.getAllValues();   // every captured value, in call order
     *   captor.getValue();       // the LAST captured value only
     * </pre>
     *
     * <p>WHY this matters: loops and batch operations call a collaborator repeatedly;
     * you usually care about all of the calls and their order.
     *
     * <p>Time complexity: n/a. Common gotcha: the verification count must match the
     * number of calls — {@code verify(repository).save(captor.capture())} on a mock
     * called twice fails with {@code TooManyActualInvocations}.
     */
    static void captureManyDemo() {
        System.out.println("=== Capture many ===");

        RecordingOrderRepository repository = new RecordingOrderRepository();
        OrderService service = new OrderService(repository, new RecordingEventPublisher());

        List<List<OrderLine>> batch = new ArrayList<>();
        batch.add(Collections.singletonList(new OrderLine("pen", 2, 100)));
        batch.add(Collections.singletonList(new OrderLine("ink", 1, 900)));
        service.placeBatch("cust-1", batch);

        for (Order order : repository.saved()) {
            System.out.println("saved         : " + order);
        }
        System.out.println("count         : " + repository.saved().size());   // 2
    }

    // ── 3. Captor vs matcher ──────────────────────────────────────────────────

    /**
     * Two ways to check an argument you cannot build with {@code equals}:
     * <pre>
     *   verify(repository).save(argThat(o -&gt; o.getTotalCents() == 700));   // yes/no check
     *
     *   verify(repository).save(captor.capture());                          // inspect freely
     *   assertThat(captor.getValue().getLines()).hasSize(2);
     * </pre>
     * {@code @Captor ArgumentCaptor<List<String>> captor;} (with
     * {@code MockitoExtension}) is the annotation form — it is the only clean way to
     * capture a generic type such as {@code List<String>}, because
     * {@code forClass(List.class)} cannot carry the type parameter.
     *
     * <p>WHY this matters: {@code argThat} answers a single boolean and reports little
     * when it fails. A captor hands you the object, so a failing assertion can say
     * exactly which field was wrong.
     *
     * <p>Common gotcha: if you use one matcher in a call, every argument of that call
     * must be a matcher too ({@code eq("x")}, {@code argThat(...)}, {@code captor.capture()}).
     */
    static void captorVsMatcherDemo() {
        System.out.println("=== Captor vs matcher ===");

        RecordingOrderRepository repository = new RecordingOrderRepository();
        OrderService service = new OrderService(repository, new RecordingEventPublisher());
        service.placeOrder("cust-1", Collections.singletonList(new OrderLine("pen", 7, 100)));

        // "matcher" style: a boolean question
        boolean matches = repository.saved().get(0).getTotalCents() == 700;
        System.out.println("total == 700  : " + matches);

        // "captor" style: the object itself, free for any assertion
        Order captured = repository.saved().get(0);
        System.out.println("first sku     : " + captured.getLines().get(0).getSku());
        System.out.println("quantity      : " + captured.getLines().get(0).getQuantity());
    }

    // ── 4. Gotcha: references, not snapshots ──────────────────────────────────

    /**
     * An {@code ArgumentCaptor} stores the <em>reference</em> it was given, not a copy of
     * the object's state at call time. If the SUT keeps mutating that object after the
     * call, your assertions see the final state — not what the collaborator received.
     *
     * <p>Below, {@code auditReusingBuffer} passes the same list object to the log twice
     * and clears it in between. Both "captured" values are the same list, showing only
     * what is left at the end. {@code auditWithCopies} fixes it by passing a copy.
     *
     * <p>WHY this matters: the failure looks like a captor bug ("why is my first batch
     * empty?") but is really an aliasing bug in the production code — arguably one the
     * captor just caught.
     *
     * <p>Common gotcha: also, {@code captor.getValue()} on a captor that captured
     * nothing throws {@code MockitoException} ("No argument value was captured!").
     */
    static void referenceNotSnapshotDemo() {
        System.out.println("=== References, not snapshots ===");

        List<String> events = Arrays.asList("a", "b", "c");

        RecordingAuditLog shared = new RecordingAuditLog();
        new BatchAuditor(shared).auditReusingBuffer(events);
        System.out.println("reusing buffer: " + shared.received());                       // [[c], [c]]
        System.out.println("same object   : " + (shared.received().get(0) == shared.received().get(1)));

        RecordingAuditLog copies = new RecordingAuditLog();
        new BatchAuditor(copies).auditWithCopies(events);
        System.out.println("with copies   : " + copies.received());                       // [[a, b], [c]]
    }
}
