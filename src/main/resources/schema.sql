-- Seating Service Schema

CREATE TABLE IF NOT EXISTS seat_reservations (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    order_reference VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_reservation_event_seat ON seat_reservations(event_id, seat_id);
CREATE INDEX IF NOT EXISTS idx_reservation_order ON seat_reservations(order_reference);
CREATE INDEX IF NOT EXISTS idx_reservation_status ON seat_reservations(status);
CREATE INDEX IF NOT EXISTS idx_reservation_expires ON seat_reservations(expires_at);
