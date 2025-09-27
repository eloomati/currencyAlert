package io.mhetko.datagatherer.provider.exception;

public class ProviderRateLimitException extends RuntimeException {
    public ProviderRateLimitException(String msg) { super(msg); }
}
