CREATE INDEX IF NOT EXISTS idx_subscriptions_user ON subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_rates_pair ON exchange_rate(base, symbol);
CREATE INDEX IF NOT EXISTS idx_rates_hist_pair_asof ON exchange_rate_history(base, symbol, as_of);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);