/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Bertrand Martel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.android.iotf.handler;

import fr.bmartel.android.iotf.constant.MessageFormat;
import fr.bmartel.android.iotf.constant.QosPolicy;
import fr.bmartel.android.iotf.listener.IMessageCallback;

/**
 * Generic interface for all handler
 *
 * @author Bertrand Martel
 */
public interface IHandler {

    void addIotCallback(IMessageCallback callback);

    void removeCallback(IMessageCallback callback);

    void connect();

    boolean isConnected();

    void disconnect();

    void setMessageFormat(MessageFormat format);

    void setQos(QosPolicy qos);

    void setKeepAlive(int keepAlive);

    void setConnectionTimeout(int timeout);

    void setCleanSession(boolean resetSession);

    void setSSL(boolean useSSL);
}
