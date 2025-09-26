CREATE INDEX IF NOT EXISTS idx_gatherer_last_rates_pair ON gatherer_last_rates(base_id, target_id);
CREATE INDEX IF NOT EXISTS idx_rate_hist_pair_asof ON exchange_rate_history(base, symbol, as_of);
