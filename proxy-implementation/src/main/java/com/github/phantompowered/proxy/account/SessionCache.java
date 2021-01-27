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
package com.github.phantompowered.proxy.account;

import com.github.phantompowered.proxy.api.database.DatabaseProvidedStorage;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;

// TODO Finish session cache
public class SessionCache extends DatabaseProvidedStorage<CachedUserAuthentication> {

    public SessionCache(ServiceRegistry registry) {
        super(registry, "internal_minecraft_session_cache", CachedUserAuthentication.class);
    }

    public void cache(String email, String password, CachedUserAuthentication authentication) {
        //super.insertOrUpdate(email + ":" + password, authentication);
    }

    public CachedUserAuthentication getCachedAuthentication(String email, String password) {
        return null;
        //return super.get(email + ":" + password);
    }

    public void remove(String email, String password) {
        //super.delete(email + ":" + password);
    }

}
