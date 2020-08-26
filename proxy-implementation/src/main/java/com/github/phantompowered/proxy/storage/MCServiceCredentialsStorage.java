/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.phantompowered.proxy.storage;

import com.github.phantompowered.proxy.api.database.DatabaseProvidedStorage;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.session.MCServiceCredentials;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MCServiceCredentialsStorage extends DatabaseProvidedStorage<MCServiceCredentials> {

    public MCServiceCredentialsStorage(ServiceRegistry registry) {
        super(registry, "credentials", MCServiceCredentials.class);
    }

    @Override
    public @NotNull Collection<MCServiceCredentials> getAll() {
        return super.getAll();
    }

    @Override
    public MCServiceCredentials get(String key) {
        return super.get(key);
    }

    @Override
    public void delete(String key) {
        super.delete(key);
    }

    @Override
    public void update(String key, MCServiceCredentials value) {
        super.update(key, value);
    }

    @Override
    public void insert(String key, MCServiceCredentials value) {
        super.insert(key, value);
    }
}
