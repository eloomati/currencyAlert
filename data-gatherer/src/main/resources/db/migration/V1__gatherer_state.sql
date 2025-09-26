CREATE TABLE IF NOT EXISTS currencies (
                                          id SERIAL PRIMARY KEY,
                                          code TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS gatherer_last_rates (
                                                   id SERIAL PRIMARY KEY,
                                                   base_id INTEGER NOT NULL REFERENCES currencies(id),
                                                   target_id INTEGER NOT NULL REFERENCES currencies(id),
                                                   rate NUMERIC(18,8) NOT NULL,
                                                   as_of TIMESTAMPTZ NOT NULL,
                                                   updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                   UNIQUE (base_id, target_id)
);