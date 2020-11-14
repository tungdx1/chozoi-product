package com.chozoi.product.domain.entities.postgres;

import java.io.IOException;
import java.io.InputStream;

public abstract class EventContent extends InputStream {
    @Override
    public int read() throws IOException {
        return 0;
    }
}
