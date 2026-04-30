CREATE TABLE IF NOT EXISTS customer (
    customer_id SERIAL PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    address     TEXT,
    phone       VARCHAR(20),
    email       VARCHAR(120)
);

CREATE TABLE IF NOT EXISTS vehicle (
    vehicle_id          SERIAL PRIMARY KEY,
    registration_number VARCHAR(20)  NOT NULL UNIQUE,
    make                VARCHAR(60)  NOT NULL,
    model               VARCHAR(60)  NOT NULL,
    year                INT          NOT NULL,
    color               VARCHAR(40),
    chassis_number      VARCHAR(60),
    owner_id            INT REFERENCES customer(customer_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS service_record (
    service_id   SERIAL PRIMARY KEY,
    vehicle_id   INT REFERENCES vehicle(vehicle_id) ON DELETE CASCADE,
    service_date DATE          NOT NULL,
    service_type VARCHAR(80)   NOT NULL,
    description  TEXT,
    cost         NUMERIC(10,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS customer_query (
    query_id      SERIAL PRIMARY KEY,
    customer_id   INT REFERENCES customer(customer_id)  ON DELETE CASCADE,
    vehicle_id    INT REFERENCES vehicle(vehicle_id)    ON DELETE CASCADE,
    query_date    DATE NOT NULL,
    query_text    TEXT NOT NULL,
    response_text TEXT
);

CREATE TABLE IF NOT EXISTS police_report (
    report_id    SERIAL PRIMARY KEY,
    vehicle_id   INT REFERENCES vehicle(vehicle_id) ON DELETE CASCADE,
    report_date  DATE         NOT NULL,
    report_type  VARCHAR(60)  NOT NULL,
    description  TEXT,
    officer_name VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS violation (
    violation_id   SERIAL PRIMARY KEY,
    vehicle_id     INT REFERENCES vehicle(vehicle_id) ON DELETE CASCADE,
    violation_date DATE           NOT NULL,
    violation_type VARCHAR(80)    NOT NULL,
    fine_amount    NUMERIC(10,2)  NOT NULL,
    status         VARCHAR(10)    NOT NULL DEFAULT 'Unpaid'
        CHECK (status IN ('Paid','Unpaid'))
);

CREATE TABLE IF NOT EXISTS insurance (
    insurance_id    SERIAL PRIMARY KEY,
    vehicle_id      INT REFERENCES vehicle(vehicle_id) ON DELETE CASCADE,
    provider        VARCHAR(120)  NOT NULL,
    policy_number   VARCHAR(80)   NOT NULL UNIQUE,
    start_date      DATE          NOT NULL,
    expiry_date     DATE          NOT NULL,
    coverage_type   VARCHAR(60)   NOT NULL,
    premium_amount  NUMERIC(10,2) NOT NULL DEFAULT 0,
    status          VARCHAR(20)   NOT NULL DEFAULT 'Active'
        CHECK (status IN ('Active','Expired','Cancelled'))
);

CREATE TABLE IF NOT EXISTS app_user (
    user_id       SERIAL PRIMARY KEY,
    username      VARCHAR(60)  NOT NULL UNIQUE,
    password_hash VARCHAR(64)  NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER'
        CHECK (role IN ('ADMIN','USER')),
    active        BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ── VIEWS ─────────────────────────────────────────────────────

CREATE OR REPLACE VIEW vw_vehicle_details AS
SELECT  v.vehicle_id,
        v.registration_number,
        v.make,
        v.model,
        v.year,
        v.color,
        v.chassis_number,
        v.owner_id,
        c.name    AS owner_name,
        c.phone   AS owner_phone,
        c.email   AS owner_email,
        c.address AS owner_address
FROM    vehicle v
LEFT JOIN customer c ON c.customer_id = v.owner_id;

CREATE OR REPLACE VIEW vw_unpaid_violations AS
SELECT  vl.violation_id,
        v.registration_number,
        v.make,
        v.model,
        c.name          AS owner_name,
        vl.violation_date,
        vl.violation_type,
        vl.fine_amount
FROM    violation vl
JOIN    vehicle   v  ON v.vehicle_id  = vl.vehicle_id
LEFT JOIN customer c ON c.customer_id = v.owner_id
WHERE   vl.status = 'Unpaid';

CREATE OR REPLACE VIEW vw_active_insurance AS
SELECT  i.insurance_id,
        v.registration_number,
        v.make,
        v.model,
        c.name         AS owner_name,
        i.provider,
        i.policy_number,
        i.start_date,
        i.expiry_date,
        i.coverage_type,
        i.premium_amount,
        i.status
FROM    insurance i
JOIN    vehicle   v ON v.vehicle_id  = i.vehicle_id
LEFT JOIN customer c ON c.customer_id = v.owner_id
WHERE   i.status = 'Active';

-- ── STORED PROCEDURES ─────────────────────────────────────────

CREATE OR REPLACE PROCEDURE sp_register_vehicle(
    p_reg VARCHAR, p_make VARCHAR, p_model VARCHAR,
    p_year INT, p_color VARCHAR, p_chassis VARCHAR, p_owner INT
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO vehicle(registration_number, make, model, year, color, chassis_number, owner_id)
    VALUES (p_reg, p_make, p_model, p_year, p_color, p_chassis, p_owner);
END;
$$;

CREATE OR REPLACE PROCEDURE sp_pay_violation(p_id INT)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE violation SET status = 'Paid' WHERE violation_id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_add_insurance(
    p_vehicle_id INT, p_provider VARCHAR, p_policy VARCHAR,
    p_start DATE, p_expiry DATE, p_coverage VARCHAR, p_premium NUMERIC
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO insurance(vehicle_id, provider, policy_number, start_date,
                          expiry_date, coverage_type, premium_amount)
    VALUES (p_vehicle_id, p_provider, p_policy, p_start, p_expiry, p_coverage, p_premium);
END;
$$;

-- ── SEED ADMIN ────────────────────────────────────────────────
INSERT INTO app_user(username, password_hash, role)
VALUES ('admin', 'ac9689e2272427085e35b9d3e3e8bed88cb3434828b43b86fc0596cad4c6e270', 'ADMIN')
ON CONFLICT (username) DO UPDATE
    SET password_hash = EXCLUDED.password_hash,
        active = TRUE;
