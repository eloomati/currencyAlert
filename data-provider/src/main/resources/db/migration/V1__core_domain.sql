CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY,
                                     email TEXT UNIQUE NOT NULL,
                                     password_hash TEXT NOT NULL,
                                     created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                     is_active BOOLEAN NOT NULL DEFAULT true,
                                     role TEXT NOT NULL DEFAULT 'USER'
);


CREATE TABLE IF NOT EXISTS subscriptions (
                                             id UUID PRIMARY KEY,
                                             user_id UUID NOT NULL REFERENCES users(id),
                                             symbol TEXT NOT NULL,
                                             threshold_percent NUMERIC(6,3) NOT NULL,
                                             direction TEXT NOT NULL CHECK (direction IN ('UP','DOWN','ANY')),
                                             is_active BOOLEAN NOT NULL DEFAULT true,
                                             created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);


CREATE TABLE IF NOT EXISTS exchange_rate (
                                             id UUID PRIMARY KEY,
                                             base TEXT NOT NULL,
                                             symbol TEXT NOT NULL,
                                             rate NUMERIC(18,8) NOT NULL,
                                             as_of TIMESTAMPTZ NOT NULL,
                                             created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                             UNIQUE(base, symbol)
);


CREATE TABLE IF NOT EXISTS exchange_rate_history (
                                                     id UUID PRIMARY KEY,
                                                     base TEXT NOT NULL,
                                                     symbol TEXT NOT NULL,
                                                     rate NUMERIC(18,8) NOT NULL,
                                                     as_of TIMESTAMPTZ NOT NULL,
                                                     ingested_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                     UNIQUE(base, symbol, as_of)
);


CREATE TABLE IF NOT EXISTS notifications (
                                             id UUID PRIMARY KEY,
                                             user_id UUID NOT NULL REFERENCES users(id),
                                             symbol TEXT NOT NULL,
                                             change_percent NUMERIC(8,4) NOT NULL,
                                             direction TEXT NOT NULL,
                                             rate_before NUMERIC(18,8) NOT NULL,
                                             rate_after NUMERIC(18,8) NOT NULL,
                                             triggered_at TIMESTAMPTZ NOT NULL,
                                             sent_at TIMESTAMPTZ NULL,
                                             channel TEXT NOT NULL DEFAULT 'EMAIL'
);


CREATE TABLE IF NOT EXISTS message_outbox (
                                              id UUID PRIMARY KEY,
                                              aggregate_type TEXT NOT NULL,
                                              aggregate_id UUID NOT NULL,
                                              payload JSONB NOT NULL,
                                              created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                              sent_at TIMESTAMPTZ NULL
);